package com.example.assistentforday.service;

import com.example.assistentforday.entity.Currency;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class CurrencyModeService {

    private HashMap<Long,Currency> originalCurrencyList = new HashMap<>();
    private HashMap<Long,Currency> targetCurrencyList = new HashMap<>();

    public Currency getOriginalCurrency(long chatId) {
        if (originalCurrencyList.containsKey(chatId)) {
            return originalCurrencyList.get(chatId);
        } else {
            return Currency.USD;
        }
    }

    public void setOriginalCurrency(long chatId, Currency originalCurrency) {
        originalCurrencyList.put(chatId,originalCurrency);
    }

    public Currency getTargetCurrency(long chatId) {
        if (targetCurrencyList.containsKey(chatId)) {
            return targetCurrencyList.get(chatId);
        } else {
            return Currency.USD;
        }
    }

    public void setTargetCurrency(long chatId, Currency targetCurrency) {
        targetCurrencyList.put(chatId,targetCurrency);
    }
}
