package com.example.assistentforday.service;

import com.example.assistentforday.entity.ButtonsValue;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Service
public class TextHandlerService {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private MainMenuService menuService;


    public SendMessage textHandling(Message message) {
        SendMessage sendMessage = new SendMessage();
        String text = message.getText();

        switch (text) {
            case ButtonsValue.WEATHER:
                sendMessage.setText("Выберите способ");
                sendMessage.setReplyMarkup(weatherService.getWeatherKeyboard());
                break;
            case ButtonsValue.CURRENCY:
                sendMessage.setText("Что вас интересует?");
                sendMessage.setReplyMarkup(currencyService.getCurrencyMainKeyboard());
                break;
            case ButtonsValue.ENTER_NAME_CITY:
                sendMessage.setText("Введите город по шаблону \n \"Город Москва\"");
                break;
            case ButtonsValue.CONVERSION_SETTINGS:
                sendMessage.setText("Выберите валюту для конвертации и введите сумму");
                sendMessage.setReplyMarkup(currencyService.getCurrencySelectionKeyboard(message.getChatId()));
                break;
            case ButtonsValue.FIND_CURRENCY_RATE:
                sendMessage.setText(currencyService.getCurrencyRate());
                break;
            case ButtonsValue.BACK_TO_THE_MAIN_MENU:
                sendMessage.setText("Что хотите узнать?");
                sendMessage.setReplyMarkup(menuService.getKeyboardMainMenu());
                break;
            case ButtonsValue.CREATOR:
                sendMessage.setText("Создал бота - Алейников Вячеслав");
                break;
            default:
                sendMessage = new SendMessage();
                sendMessage.setText("Возможно вы ввели неправильную команду");
        }

        if (isNumeric(text)) {
            sendMessage.setText(currencyService.currencyConversion(message));
        }

        if (text.length() > 5 && hasCity(text)) {
            String nameCity = text.substring(6);
            nameCity.trim();
            sendMessage.setText(weatherService.getWeatherInfoByCity(nameCity));
        }

        sendMessage.setChatId(message.getChatId().toString());
        return sendMessage;
    }

    private boolean isNumeric(String text) {
        try {
            Double.parseDouble(text);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    private boolean hasCity(String text) {
        String city = text.substring(0, 6);
        if (city.equalsIgnoreCase("город ")) {
            return true;
        }
        return false;
    }

}
