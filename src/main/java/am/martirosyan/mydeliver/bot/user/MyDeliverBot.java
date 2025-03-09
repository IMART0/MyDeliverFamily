package am.martirosyan.mydeliver.bot.user;
import am.martirosyan.mydeliver.bot.user.handler.OrderHandler;
import am.martirosyan.mydeliver.bot.user.handler.MainMenuHandler;
import am.martirosyan.mydeliver.bot.user.handler.RegistrationHandler;
import am.martirosyan.mydeliver.bot.user.util.MessageSender;
import am.martirosyan.mydeliver.bot.user.util.UserCart;
import am.martirosyan.mydeliver.bot.user.util.UserMessage;
import am.martirosyan.mydeliver.bot.user.util.UserStateManager;
import am.martirosyan.mydeliver.service.CategoryService;
import am.martirosyan.mydeliver.service.MenuItemService;
import am.martirosyan.mydeliver.service.UserService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyDeliverBot extends TelegramLongPollingBot {
    private final String NAME;
    private final String TOKEN;

    private final UserStateManager userStateManager;
    private final MessageSender messageSender;
    private final Map<Long, UserCart> userCarts;

    private final RegistrationHandler registrationHandler;
    private final OrderHandler orderHandler;
    private final MainMenuHandler mainMenuHandler;

    public MyDeliverBot(String name, String token, UserService userService,
                        CategoryService categoryService, MenuItemService menuItemService) {
        this.NAME = name;
        this.TOKEN = token;

        this.userStateManager = new UserStateManager();
        this.messageSender = new MessageSender(this, menuItemService);
        this.userCarts = new ConcurrentHashMap<>();

        this.registrationHandler = new RegistrationHandler(
                messageSender,
                userStateManager,
                userCarts,
                userService
        );
        this.orderHandler = new OrderHandler(
                messageSender,
                userCarts,
                categoryService,
                menuItemService
        );
        this.mainMenuHandler = new MainMenuHandler(
                messageSender,
                userStateManager,
                categoryService
        );
    }

    @Override
    public void onUpdateReceived(Update update) {
        long chatId = update.hasMessage() ? update.getMessage().getChatId() :
                update.hasCallbackQuery() ? update.getCallbackQuery().getMessage().getChatId() : -1;

        if (chatId == -1) return;

        switch (userStateManager.getUserState(chatId)) {
            case START -> {
                if (update.hasMessage())
                    registrationHandler.handleStartCommand(chatId);
                else
                    sendErrorMessage(chatId);
            }
            case REGISTRATION_PHONE -> registrationHandler.handleContact(update.getMessage());
            case REGISTRATION_BIRTHDATE -> {
                if (update.hasMessage())
                    registrationHandler.handleBirthDateInput(update.getMessage());
                else
                    sendErrorMessage(chatId);
            }
            case MAIN_MENU, SELECT_CATEGORY -> {
                if (update.hasMessage()) {
                    mainMenuHandler.handleMainMenu(update.getMessage());
                }
                else if (update.hasCallbackQuery()) {
                    orderHandler.handleCategorySelection(update.getCallbackQuery());
                }
                else
                    sendErrorMessage(chatId);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return NAME;
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    private void sendErrorMessage(long chatId) {
        messageSender.sendMessage(chatId, UserMessage.ERROR_MESSAGE);
    }
}
