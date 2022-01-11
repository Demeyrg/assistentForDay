package com.example.assistentforday;

import com.example.assistentforday.service.CommandHandlerService;
import com.example.assistentforday.service.CurrencyService;
import com.example.assistentforday.service.TextHandlerService;
import com.example.assistentforday.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class AssistantForDayBot extends TelegramLongPollingBot {

    @Autowired
    private CommandHandlerService commandHandler;

    @Autowired
    private TextHandlerService textHandler;

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private CurrencyService currencyService;

    @Override
    public String getBotUsername() {
        return "@AssistantForDayBot";
    }

    @Override
    public String getBotToken() {
        return "5057963868:AAFuQnRU908cVdulqMZng1nmXxF1ZwBX030";
    }

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage sendMessage = new SendMessage();
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasEntities()) {
                sendMessage = commandHandler.commandProcessing(message);
            } else if (message.hasText()) {
                sendMessage = textHandler.textHandling(message);
            } else if (message.hasLocation()) {
                sendMessage = weatherService.getWeatherInfoByCoordinates(message);
            }
            startExecutionSendMessage(sendMessage);
        } else if (update.hasCallbackQuery()) {
            Message message = currencyService.handleCallback(update.getCallbackQuery());
            startExecutionInlineKeyboard(message);
        }
    }

    private void startExecutionSendMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void startExecutionInlineKeyboard(Message message) {
        try {
            execute(
                    EditMessageReplyMarkup
                            .builder()
                            .chatId(message.getChatId().toString())
                            .messageId(message.getMessageId())
                            .replyMarkup(currencyService.getCurrencySelectionKeyboard(message.getChatId()))
                            .build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
