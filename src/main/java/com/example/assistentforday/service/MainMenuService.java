package com.example.assistentforday.service;

import com.example.assistentforday.entity.ButtonsValue;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;

@Service
public class MainMenuService {

    public ReplyKeyboard getKeyboardMainMenu() {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setSelective(true);
        keyboard.setOneTimeKeyboard(false);
        keyboard.setKeyboard(getFillKeyboard());
        return keyboard;
    }

    private ArrayList<KeyboardRow> getFillKeyboard() {

        ArrayList<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow firstRow = new KeyboardRow();
        KeyboardRow secondRow = new KeyboardRow();
        firstRow.add(ButtonsValue.WEATHER);
        firstRow.add(ButtonsValue.CURRENCY);
        secondRow.add(ButtonsValue.CREATOR);
        keyboard.add(firstRow);
        keyboard.add(secondRow);

        return keyboard;
    }
}
