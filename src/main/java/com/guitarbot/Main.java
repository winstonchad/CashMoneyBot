package com.guitarbot;

import com.guitarbot.model.Transaction;
import com.guitarbot.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class Main extends TelegramLongPollingBot {

    @Autowired
    private TransactionRepository transactionRepository;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    private final Map<Long, PendingTransaction> pendingTransactions = new HashMap<>();

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update){
        if(update.hasMessage() && update.getMessage().hasText()) {
            String userMessage = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if(pendingTransactions.containsKey(chatId)){
                PendingTransaction pending = pendingTransactions.remove(chatId);
                Transaction transaction = new Transaction();
                transaction.setAmount(pending.amount);
                transaction.setDescription(userMessage);
                transaction.setTimestamp(LocalDateTime.now());

                transactionRepository.save(transaction);
                System.out.println("Сохраняю транзакцию: " + transaction.getAmount() + " | " + transaction.getDescription());
                sendMessage(chatId, "Изменения сохранены!!");

                return;
            }

            if(userMessage.startsWith("+") || userMessage.startsWith("-")){
                try {
                    double amount = Double.parseDouble(userMessage);
                    pendingTransactions.put(chatId, new PendingTransaction(amount));
                    sendMessage(chatId, "Напиши описание");
                } catch (NumberFormatException e){
                    sendMessage(chatId, "Я не понмимаю");
                }
            } else if (userMessage.equals("/all")){
                StringBuilder response = new StringBuilder("Все транзакции:\n");
                transactionRepository.findAll().forEach(t ->{
                    response.append(t.getTimestamp()).
                            append("|").
                            append(t.getAmount())
                            .append(" | ")
                            .append(t.getDescription())
                            .append("\n");
                });
//                Добавить сюда команду /delete
                sendMessage(chatId, response.toString());
            } else{
                sendMessage(chatId, "Бля, я тебя не понимаю, нормально пиши, ты же сам меня создал, нахуя ты это сделал если не можешь нормально писать");
            }
        }
    }

    private void sendMessage(Long chatId, String text){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try{
            execute(message);
        }
        catch(TelegramApiException e){
            e.printStackTrace();
        }
    }

    private static class PendingTransaction{
        double amount;

        public PendingTransaction(double amount){
            this.amount = amount;
        }
    }
}