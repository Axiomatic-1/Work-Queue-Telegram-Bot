package com.telegrambot.workqueue.telegram;

import com.telegrambot.workqueue.config.BotConfig;
import com.telegrambot.workqueue.service.TrackerQueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {
    public static final String ADD_TASK = "/add";
    public static final String SHOW = "/show";
    public static final String TASK_IS_ADDED_IN_QUEUE = "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n Таска добавлена в очередь \n";
    public static final String END_TITLE = "\"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n";
    private static final String MK_PC = "https://tenor.com/ru/view/monkey-stress-mad-gif-15327798";
    private final BotConfig botConfig;
    private final TrackerQueueService trackerService;

    @Override
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            String msgTxt = update.getMessage().getText();
            String command = prepareCommand(msgTxt);
            String task = prepareTask(msgTxt);
            String name = update.getMessage().getFrom().getFirstName();
            long chatId = update.getMessage().getChatId();

            switch (command) {
                case ADD_TASK -> {
                    Map<Integer, String> taskMap = trackerService.addTask(task, name, chatId);
                    StringBuilder taskList = new StringBuilder();
                    AtomicInteger count = new AtomicInteger(1);
                    String ans = buildTaskList(taskMap, taskList, count, MK_PC);
                    sendMessage(chatId, ans);
                }
                case SHOW -> {
                    log.info("");
                }
            }
        }
    }

    private static String buildTaskList(
            Map<Integer, String> taskMap,
            StringBuilder taskList,
            AtomicInteger count,
            String gif) {
        taskMap.values().forEach(taskDescription ->
                taskList.append(count.getAndIncrement())
                        .append(". ")
                        .append(taskDescription)
                        .append("\n")
        );
        return TASK_IS_ADDED_IN_QUEUE + taskList + END_TITLE + gif;
    }

    private String prepareTask(String msgTxt) {
        return msgTxt.substring(4);
    }

    private String prepareCommand(String msgTxt) {
        return msgTxt.toLowerCase().split(" ")[0];
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }


    private void sendMessage(Long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

}
