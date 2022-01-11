package com.example.assistentforday.entity;

import org.springframework.stereotype.Component;

@Component
public final class ButtonsValue {
    //Main menu
    public static final String WEATHER ="Погода \uD83C\uDF24";
    public static final String CURRENCY ="Валюта \uD83C\uDFE6";
    public static final String CREATOR = "Создатель";

    //Weather
    public static final String ENTER_NAME_CITY ="Ввести город \uD83C\uDFD9";
    public static final String BACK_TO_THE_MAIN_MENU = "Вернуть в главное меню ⬅";

    //Currency
    public static final String FIND_CURRENCY_RATE = "Узнать курс валют \uD83D\uDCB9";
    public static final String CONVERSION_SETTINGS = "Изменить настройки конвертации ⚙";

}
