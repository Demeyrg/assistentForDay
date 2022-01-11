package com.example.assistentforday.service;

import com.example.assistentforday.entity.ButtonsValue;
import com.example.assistentforday.entity.Currency;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Service
public class CurrencyService {

    @Autowired
    private CurrencyModeService currencyModeService;

    private HashMap<Currency,Double> currencyList = new HashMap<>();

    public ReplyKeyboard getCurrencyMainKeyboard() {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setSelective(true);
        keyboard.setOneTimeKeyboard(true);
        keyboard.setKeyboard(fillCurrencyMainKeyboard());
        return keyboard;
    }

    public InlineKeyboardMarkup getCurrencySelectionKeyboard(long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        Currency originalCurrency = currencyModeService.getOriginalCurrency(chatId);
        Currency targetCurrency = currencyModeService.getTargetCurrency(chatId);
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        for(Currency currency : Currency.values()) {
            buttons.add(
                    Arrays.asList(
                            InlineKeyboardButton
                                    .builder()
                                    .text(getCurrencyButton(originalCurrency,currency))
                                    .callbackData("ORIGINAL:" + currency)
                                    .build(),
                            InlineKeyboardButton
                                    .builder()
                                    .text(getCurrencyButton(targetCurrency,currency))
                                    .callbackData("TARGET:" + currency)
                                    .build()));
        }
        inlineKeyboardMarkup.setKeyboard(buttons);
        return inlineKeyboardMarkup;
    }

    public Message handleCallback(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        String[] param = callbackQuery.getData().split(":");
        String action = param[0];
        Currency currency = Currency.valueOf(param[1]);
        switch (action) {
            case "ORIGINAL":
                currencyModeService.setOriginalCurrency(message.getChatId(),currency);
                break;
            case "TARGET":
                currencyModeService.setTargetCurrency(message.getChatId(),currency);
                break;
        }
        return message;
    }

    public String getCurrencyRate() {
        executeRequest();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Курсы валют по отношению к тенге: \n");
        stringBuffer.append(String.format("1$ Доллар: %.2f тг\n",currencyList.get(Currency.USD)));
        stringBuffer.append(String.format("1€ Евро: %.2f тг\n",currencyList.get(Currency.EUR)));
        stringBuffer.append(String.format("1₽ Рубль: %.2f тг\n",currencyList.get(Currency.RU)));
        return stringBuffer.toString();
    }

    public String currencyConversion(Message message) {
        double money = Double.valueOf(message.getText());
        Currency originalCurrency = currencyModeService.getOriginalCurrency(message.getChatId());
        Currency targetCurrency = currencyModeService.getTargetCurrency(message.getChatId());
        if(originalCurrency.equals(targetCurrency)) {
            return money + " " + originalCurrency.name() + " = " + money + " " + targetCurrency.name();
        } else {
            double divisionRatio = getDivisionRatio(originalCurrency, targetCurrency);
            return String.format("%.2f %s",money * divisionRatio, targetCurrency.name());
        }
    }

    private Double getDivisionRatio(Currency originalCurrency, Currency targetCurrency) {
        return currencyList.get(originalCurrency) / currencyList.get(targetCurrency);
    }

    private List<KeyboardRow> fillCurrencyMainKeyboard() {
        ArrayList<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow firstRow = new KeyboardRow();
        KeyboardRow secondRow = new KeyboardRow();
        firstRow.add(ButtonsValue.FIND_CURRENCY_RATE);
        firstRow.add(ButtonsValue.CONVERSION_SETTINGS);
        secondRow.add(ButtonsValue.BACK_TO_THE_MAIN_MENU);
        keyboard.add(firstRow);
        keyboard.add(secondRow);
        return keyboard;
    }

    private String getCurrencyButton(Currency saved, Currency current) {
        return saved == current ? current + " ✅" : current.name();
    }

    private void executeRequest() {
        String URL = getUrlContent("https://www.cbr-xml-daily.ru/daily_json.js");
        parseJSONCurrencyRateResponse(URL);
    }

    private String getUrlContent(String urlAddress) {
        StringBuffer content = new StringBuffer();
        try {
            URL url = new URL(urlAddress);
            URLConnection urlConn = url.openConnection();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    content.append(line);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    private void parseJSONCurrencyRateResponse(String text) {
        if (!text.isEmpty()) {
            JSONObject obj = new JSONObject(text);
            double ru = 100.0/obj.getJSONObject("Valute").getJSONObject("KZT").getDouble("Value");
            double usd = ru * obj.getJSONObject("Valute").getJSONObject("USD").getDouble("Value");
            double eur = ru * obj.getJSONObject("Valute").getJSONObject("EUR").getDouble("Value");
            currencyList.clear();
            currencyList.put(Currency.USD,usd);
            currencyList.put(Currency.EUR,eur);
            currencyList.put(Currency.RU,ru);
            currencyList.put(Currency.KZ,1.0);
        }
    }

}
