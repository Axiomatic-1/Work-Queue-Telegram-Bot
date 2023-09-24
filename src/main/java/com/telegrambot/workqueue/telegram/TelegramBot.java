package com.telegrambot.workqueue.telegram;

import com.telegrambot.workqueue.config.BotConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    public final Map<String, Integer> map = new HashMap<>();


    @Override
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();

            SendMessage message = new SendMessage(); // Create a message object object
            message.setChatId(chat_id);
            message.setText(message_text);
            try {
                execute(message); // Sending our message object to user
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = "Hi, " + name + ", nice to meet you!" + "\n" +
                "Enter the currency whose official exchange rate" + "\n" +
                "you want to know in relation to BYN." + "\n" +
                "For example: USD";
        sendMessage(chatId, answer);
    }


    private void sendMessage(Long chatId, String textToSend){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {

        }
    }

}
