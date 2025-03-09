package am.martirosyan.mydeliver.bot.user.handler;

import am.martirosyan.mydeliver.bot.user.util.MessageSender;
import am.martirosyan.mydeliver.bot.user.util.UserState;
import am.martirosyan.mydeliver.bot.user.util.UserStateManager;
import am.martirosyan.mydeliver.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Message;

@RequiredArgsConstructor
public class MainMenuHandler {
    private final MessageSender messageSender;
    private final UserStateManager userStateManager;

    private final CategoryService categoryService;

    public void handleMainMenu(Message message) {
        if (message.hasText()) {
            String messageText = message.getText();
            if (messageText.equals("🍔 Сделать заказ")) {
                messageSender.sendCategoriesInlineKeyboard(message.getChatId(), categoryService.getAll());
                userStateManager.setUserState(message.getChatId(), UserState.SELECT_CATEGORY);
            }
        }
    }

}
