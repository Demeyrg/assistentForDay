package com.example.assistentforday.service;

import com.example.assistentforday.entity.ButtonsValue;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherService {

    private static final String API_WEATHER = "e1df48d7765d0301af6ca6aee27efea5";

    public ReplyKeyboard getWeatherKeyboard() {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setSelective(true);
        keyboard.setOneTimeKeyboard(true);
        keyboard.setKeyboard(fillKeyboard());
        return keyboard;
    }

    public SendMessage getWeatherInfoByCoordinates(Message message) {
        String URL = getUrlContent("https://api.openweathermap.org/data/2.5/weather?"
                + "lat=" + message.getLocation().getLatitude().intValue()
                + "&lon=" + message.getLocation().getLongitude().intValue()
                + "&appid=" + API_WEATHER
                + "&units=metric");
        String weatherInfo = parseJSONWeatherResponse(URL);
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), weatherInfo);
        return sendMessage;
    }

    public String getWeatherInfoByCity(String city) {
        String URL = getUrlContent("https://api.openweathermap.org/data/2.5/weather?"
                + "q=" + city
                + "&appid=" + API_WEATHER
                + "&units=metric");
        String result;
        if (URL.isEmpty()) {
            result = "Такого города нет ☹ \nВозможно вы ошиблись. ";
        } else {
             result = parseJSONWeatherResponse(URL);
        }
        return result;
    }

    private List<KeyboardRow> fillKeyboard() {
        ArrayList<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow firstRow = new KeyboardRow();
        KeyboardRow secondRow = new KeyboardRow();
        KeyboardButton button = new KeyboardButton("Отправить местоположение \uD83C\uDF0F");
        button.setRequestLocation(true);
        firstRow.add(ButtonsValue.ENTER_NAME_CITY);
        firstRow.add(button);
        secondRow.add(ButtonsValue.BACK_TO_THE_MAIN_MENU);
        keyboard.add(firstRow);
        keyboard.add(secondRow);
        return keyboard;
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

    private String parseJSONWeatherResponse(String text) {
        StringBuffer result = new StringBuffer();
        if (!text.isEmpty()) {
            JSONObject obj = new JSONObject(text);
            result.append("Температура\uD83C\uDF21: " + obj.getJSONObject("main").getDouble("temp") + "°\n");
            result.append("Ощущается как\uD83D\uDE44: " + obj.getJSONObject("main").getDouble("feels_like") + "°\n");
            result.append("Давление: " + obj.getJSONObject("main").getDouble("pressure") + " hPa\n");
            result.append("Влажность\uD83D\uDCA7: " + obj.getJSONObject("main").getDouble("humidity") + " %\n");
            result.append("Скорость ветра\uD83D\uDCA8: " + obj.getJSONObject("wind").getDouble("speed") + " м/с\n");
            result.append("На улице: " + obj.getJSONArray("weather").getJSONObject(0).getString("description"));
        }
        return result.toString();
    }

}
