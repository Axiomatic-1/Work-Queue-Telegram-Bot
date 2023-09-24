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

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {
    public static final String ADD_TASK = "/add";
    public static final String CLEAR = "/clear";
    public static final String REPORT = "/report";
    public static final String MULTIPLE_ADD = "/multitask";

    private final BotConfig botConfig;
    private final TrackerQueueService trackerService;

    @Override
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            String msgTxt = update.getMessage().getText();
            String command = prepareCommand(msgTxt);
            String name = update.getMessage().getFrom().getFirstName();
            long chatId = update.getMessage().getChatId();

            switch (command) {
                case ADD_TASK -> {
                    String task = prepareTask(msgTxt);
                    if(task.isEmpty()) {
                        return;
                    }
                    String ans = trackerService.addTask(task, name, chatId);
                    sendMessage(chatId, ans);
                }
                case MULTIPLE_ADD -> {
                    String[] tasks = msgTxt.substring(9).split("~");
                    String ans = "";
                    for (String task : tasks) {
                        ans = trackerService.addTask(task, name, chatId);
                    }
                    sendMessage(chatId, ans);
                }
                case CLEAR -> sendMessage(chatId,trackerService.clearTasks(chatId));
                case REPORT -> sendMessage(chatId, trackerService.getReport(chatId));
            }
        }
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
