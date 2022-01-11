package com.example.assistentforday.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import java.util.List;

@Service
public class CommandHandlerService {

    @Autowired
    private MainMenuService menuService;

    public SendMessage commandProcessing(Message message) {
        SendMessage sendMessage = new SendMessage();
        List<MessageEntity> entities = message.getEntities();
        for (MessageEntity entity : entities) {
            String text = entity.getText();
            if (text.startsWith("/start")) {
                sendMessage.setText("Что хотите узнать?");
                sendMessage.setReplyMarkup(menuService.getKeyboardMainMenu());
            }
        }
        sendMessage.setChatId(message.getChatId().toString());
        return sendMessage;
    }

}
