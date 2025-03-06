package am.martirosyan.mydeliver.bot.user;

import am.martirosyan.mydeliver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

@RequiredArgsConstructor
public class MyDeliverBot extends TelegramLongPollingBot {
    private final Logger logger = LoggerFactory.getLogger(MyDeliverBot.class);

    private final String NAME;
    private final String TOKEN;
    private UserState state = UserState.START;

    @Autowired
    private UserService userService;

    @Override
    public String getBotUsername() {
        return NAME;
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        switch (state) {
            case START -> {
                handleStartCommand(update.getMessage());
            }
            case REGISTRATION -> {
                handleContact(update.getMessage());
            }
        }
    }

    private void handleContact(Message message) {
        long chatId = message.getChatId();
        if (message.hasContact() && message.getContact().getUserId().equals(message.getChatId())) {
            String phone = message.getContact().getPhoneNumber();
            String name = message.getContact().getFirstName();
            message.getContact().getUserId();
            userService.registerUser(chatId, name, phone);
            sendMessage(chatId, UserMessage.REGISTRATION_SUCCESS_MESSAGE);
        } else sendRequestPhoneMessage(chatId);
    }

    private void handleStartCommand(Message message) {
        long chatId = message.getChatId();
        sendMessage(chatId, UserMessage.WELCOME_MESSAGE);
        if (!userService.isUserRegistered(chatId)) {
            sendRequestPhoneMessage(chatId);
            state = UserState.REGISTRATION;
        }
    }

    private void sendRequestPhoneMessage(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(UserMessage.REGISTRATION_MESSAGE);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        KeyboardButton button = new KeyboardButton("Отправить номер ☎️");
        button.setRequestContact(true);
        KeyboardRow row = new KeyboardRow();
        row.add(button);
        keyboardMarkup.setKeyboard(List.of(row));
        keyboardMarkup.setResizeKeyboard(true);

        message.setReplyMarkup(keyboardMarkup);
        executeMessage(message);
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        executeMessage(message);
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
