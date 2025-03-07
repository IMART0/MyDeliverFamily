package am.martirosyan.mydeliver.bot.user;

import am.martirosyan.mydeliver.model.Category;
import am.martirosyan.mydeliver.model.MenuItem;
import am.martirosyan.mydeliver.service.CategoryService;
import am.martirosyan.mydeliver.service.MenuItemService;
import am.martirosyan.mydeliver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class MyDeliverBot extends TelegramLongPollingBot {
    private final Logger logger = LoggerFactory.getLogger(MyDeliverBot.class);

    private final String NAME;
    private final String TOKEN;
    private UserState state = UserState.START;

    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private MenuItemService menuItemService;

    private String phone;
    private String name;

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

        if (update.hasMessage()) {
            Message message = update.getMessage();

            if (message.hasText() && message.getText().equals("/state")) {
                sendMessage(message.getChatId(), state.toString());
            }

            if (message.hasText() && message.getText().equals("/start")) {
                state = UserState.START;
            }
        }

        switch (state) {
            case START -> {
                if (update.hasMessage())
                    handleStartCommand(update.getMessage());
            }
            case REGISTRATION_PHONE -> {
                if (update.hasMessage())
                    handleContact(update.getMessage());
            }
            case REGISTRATION_BIRTHDATE -> {
                if (update.hasMessage())
                    handleBirthDateInput(update.getMessage());
            }
            case MAIN_MENU -> {
                if (update.hasMessage())
                    handleMainMenu(update.getMessage());
                if (update.hasCallbackQuery()) {
                    CallbackQuery callbackQuery = update.getCallbackQuery();

                    if (callbackQuery.getData().matches("CATEGORY_\\d+")) {
                        long chatId = callbackQuery.getMessage().getChatId();
                        long categoryId = Long.parseLong(callbackQuery.getData().substring(9));
                        Category category = categoryService.getById(categoryId);
                        if (category != null) {
                            sendCategoryMenu(chatId, categoryId, 0);
                            state = UserState.SELECT_CATEGORY;
                        }
                    }
                }
            }
        }
    }

    private void handleStartCommand(Message message) {
        long chatId = message.getChatId();
        sendMessage(chatId, UserMessage.WELCOME_MESSAGE);
        if (!userService.isRegistered(chatId)) {
            sendRequestPhoneMessage(chatId);
            state = UserState.REGISTRATION_PHONE;
        } else {
            state = UserState.MAIN_MENU;
            sendMainMenuKeyboardMessage(chatId);
        }

    }

    private void handleContact(Message message) {
        long chatId = message.getChatId();
        if (message.hasContact() && message.getContact().getUserId().equals(chatId)) {
            phone = message.getContact().getPhoneNumber();
            name = message.getContact().getFirstName();
            removeKeyboard(chatId, UserMessage.BIRTHDAY_REQUEST_MESSAGE);
            state = UserState.REGISTRATION_BIRTHDATE;
        } else {
            sendRequestPhoneMessage(chatId);
        }
    }


    private void handleBirthDateInput(Message message) {
        long chatId = message.getChatId();
        String birthDate = message.getText();

        if (birthDate.matches("\\d{2}\\.\\d{2}\\.\\d{4}") &&
                userService.register(chatId, name, phone, birthDate) != null) {
            sendMessage(chatId, UserMessage.REGISTRATION_SUCCESS_MESSAGE);
            state = UserState.MAIN_MENU;
            sendMainMenuKeyboardMessage(chatId);
        } else {
            sendMessage(chatId, UserMessage.MISSING_BIRTHDAY_MESSAGE);
        }
    }

    private void handleMainMenu(Message message) {
        if (message.hasText()) {
            String messageText = message.getText();
            if (messageText.equals("🍔 Сделать заказ")) {
                sendCategoriesInlineKeyboard(message.getChatId());
            }
        }
    }

    private void sendCategoriesInlineKeyboard(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите категорию");

        List<Category> categories = categoryService.getAll();

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        while (!categories.isEmpty()) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int rowNum = 0; rowNum < 2; rowNum++) {
                if (!categories.isEmpty()) {
                    Category category = categories.remove(0);
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    button.setText(category.getName());
                    button.setCallbackData("CATEGORY_" + category.getId());

                    row.add(button);
                }
            }
            rows.add(row);
        }

        keyboardMarkup.setKeyboard(rows);
        message.setReplyMarkup(keyboardMarkup);
        executeMessage(message);
    }

    private void sendCategoryMenu(long chatId, long categoryId, int page) {
        int pageSize = 5; // Количество элементов на странице
        List<MenuItem> menuItems = menuItemService.getByCategoryId(categoryId);

        int totalPages = (int) Math.ceil((double) menuItems.size() / pageSize);
        page = Math.max(0, Math.min(page, totalPages - 1)); // Ограничиваем границы страниц

        int start = page * pageSize;
        int end = Math.min(start + pageSize, menuItems.size());
        List<MenuItem> pageItems = menuItems.subList(start, end);

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Кнопки с элементами меню
        for (MenuItem item : pageItems) {
            InlineKeyboardButton button = new InlineKeyboardButton(item.getName());
            button.setCallbackData("MENUITEM_" + item.getId());
            rows.add(Collections.singletonList(button));
        }

        // Кнопки для пагинации
        List<InlineKeyboardButton> paginationRow = createPaginationButtons(
                "CATEGORY_PAGE_",
                page,
                totalPages
        );

        rows.add(paginationRow);
        keyboard.setKeyboard(rows);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите блюдо:");
        message.setReplyMarkup(keyboard);

        executeMessage(message);
    }

    private static List<InlineKeyboardButton> createPaginationButtons(String callback, int page, int totalPages) {
        List<InlineKeyboardButton> paginationRow = new ArrayList<>();

        InlineKeyboardButton prevButton = new InlineKeyboardButton("⬅️");
        prevButton.setCallbackData(callback + (page - 1));

        InlineKeyboardButton pageIndicator = new InlineKeyboardButton((page + 1) + "/" + totalPages);
        pageIndicator.setCallbackData(callback + "INFO");

        InlineKeyboardButton nextButton = new InlineKeyboardButton("➡️");
        nextButton.setCallbackData(callback + (page + 1));

        if (page > 0) paginationRow.add(prevButton);
        paginationRow.add(pageIndicator);
        if (page < totalPages - 1) paginationRow.add(nextButton);
        return paginationRow;
    }

    private void sendMainMenuKeyboardMessage(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(UserMessage.MAIN_MENU_MESSAGE);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add("🍔 Сделать заказ");

        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add("📋 Мои заказы");

        KeyboardRow thirdRow = new KeyboardRow();
        thirdRow.add("📞 Служба поддержки");
        thirdRow.add("\uD83C\uDF81 Бонусы");
        thirdRow.add("\uD83D\uDC64 Профиль");

        keyboardMarkup.setKeyboard(List.of(firstRow, secondRow, thirdRow));
        keyboardMarkup.setResizeKeyboard(true);

        message.setReplyMarkup(keyboardMarkup);

        executeMessage(message);
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

    private void removeKeyboard(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        // Убираем клавиатуру
        ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();
        keyboardRemove.setRemoveKeyboard(true);

        message.setReplyMarkup(keyboardRemove);
        executeMessage(message);
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        executeMessage(message);
    }

    // Метод для удаления сообщения
    private void deleteMessage(long chatId, int messageId) {
        try {
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(messageId);
            execute(deleteMessage); // Удаляем сообщение
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
