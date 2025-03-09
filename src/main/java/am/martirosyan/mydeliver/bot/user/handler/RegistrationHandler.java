package am.martirosyan.mydeliver.bot.user.handler;

import am.martirosyan.mydeliver.bot.user.util.*;
import am.martirosyan.mydeliver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class RegistrationHandler {

    private final MessageSender messageSender;
    private final UserStateManager userStateManager;
    private final Map<Long, UserCart> userCarts;

    private final UserService userService;


    private final Map<Long, String> phones = new ConcurrentHashMap<>();
    private final Map<Long, String> names = new ConcurrentHashMap<>();


    public void handleStartCommand(long chatId) {
        messageSender.sendMessage(chatId, UserMessage.WELCOME_MESSAGE);
        if (!userService.isRegistered(chatId)) {
            messageSender.sendRequestPhoneMessage(chatId);
            userStateManager.setUserState(chatId, UserState.REGISTRATION_PHONE);
        } else {
            if (!userCarts.containsKey(chatId))
                userCarts.put(chatId, new UserCart());
            userStateManager.setUserState(chatId, UserState.MAIN_MENU);
            messageSender.sendMainMenuKeyboardMessage(chatId);
        }
    }

    public void handleContact(Message message) {
        long chatId = message.getChatId();
        if (message.hasContact() && message.getContact().getUserId().equals(chatId)) {
            phones.put(chatId, message.getContact().getPhoneNumber());
            names.put(chatId, message.getContact().getFirstName());

            messageSender.sendMessage(chatId, UserMessage.BIRTHDAY_REQUEST_MESSAGE);
            userStateManager.setUserState(chatId, UserState.REGISTRATION_BIRTHDATE);
        } else {
            messageSender.sendRequestPhoneMessage(chatId);
        }
    }

    public void handleBirthDateInput(Message message) {
        long chatId = message.getChatId();
        String birthDate = message.getText();

        if (birthDate.matches("\\d{2}\\.\\d{2}\\.\\d{4}") &&
                userService.register(chatId, names.get(chatId),
                        phones.get(chatId), birthDate) != null) {
            if (!userCarts.containsKey(chatId))
                userCarts.put(chatId, new UserCart());
            messageSender.sendMessage(chatId, UserMessage.REGISTRATION_SUCCESS_MESSAGE);
            userStateManager.setUserState(chatId, UserState.MAIN_MENU);
            messageSender.sendMainMenuKeyboardMessage(chatId);

            names.remove(chatId);
            phones.remove(chatId);
        } else {
            messageSender.sendMessage(chatId, UserMessage.MISSING_BIRTHDAY_MESSAGE);
        }
    }
}
