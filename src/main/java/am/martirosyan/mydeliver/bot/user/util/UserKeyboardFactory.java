package am.martirosyan.mydeliver.bot.user.util;

import am.martirosyan.mydeliver.model.Category;
import am.martirosyan.mydeliver.model.MenuItem;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserKeyboardFactory {

    public static ReplyKeyboardMarkup phoneRequestKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        KeyboardButton button = new KeyboardButton("Отправить номер ☎️");
        button.setRequestContact(true);
        KeyboardRow row = new KeyboardRow();
        row.add(button);
        keyboardMarkup.setKeyboard(List.of(row));
        keyboardMarkup.setResizeKeyboard(true);

        return keyboardMarkup;
    }

    public static InlineKeyboardMarkup menuItemInlineKeyboard(List<MenuItem> pageItems, long categoryId, int page, int totalPages) {
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
                "CATEGORY_%s_PAGE_".formatted(categoryId),
                page,
                totalPages
        );

        rows.add(paginationRow);
        keyboard.setKeyboard(rows);

        return keyboard;
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

    public static InlineKeyboardMarkup categoryInlineKeyboard(List<Category> categories) {
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
        return keyboardMarkup;
    }

    public static ReplyKeyboardMarkup mainMenuKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add("🍔 Сделать заказ");

        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add("📋 Мои заказы");
        secondRow.add("\uD83D\uDED2 Корзина");

        KeyboardRow thirdRow = new KeyboardRow();
        thirdRow.add("📞 Служба поддержки");
        thirdRow.add("\uD83C\uDF81 Бонусы");
        thirdRow.add("\uD83D\uDC64 Профиль");

        keyboardMarkup.setKeyboard(List.of(firstRow, secondRow, thirdRow));
        keyboardMarkup.setResizeKeyboard(true);

        return keyboardMarkup;
    }

    public static InlineKeyboardMarkup addToCartKeyboard(long menuItemId, int quantity) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        InlineKeyboardButton reduceButton = new InlineKeyboardButton("➖");
        reduceButton.setCallbackData("REDUCE_" + menuItemId);

        InlineKeyboardButton quantityButton = new InlineKeyboardButton(String.valueOf(quantity));
        quantityButton.setCallbackData("INFO");

        InlineKeyboardButton increaseButton = new InlineKeyboardButton("➕");
        increaseButton.setCallbackData("INCREASE_" + menuItemId);

        InlineKeyboardButton addButton = new InlineKeyboardButton("\uD83D\uDED2 Добавить в корзину \uD83D\uDED2");
        addButton.setCallbackData("ADD_TO_CART_%s_QUANTITY_%s".formatted(menuItemId, quantity));

        InlineKeyboardButton removeButton = new InlineKeyboardButton("❌ Убрать из корзины ❌");
        removeButton.setCallbackData("REMOVE_FROM_CART_" + menuItemId);

        List<InlineKeyboardButton> firstRow = List.of(reduceButton, quantityButton, increaseButton);

        List<InlineKeyboardButton> secondRow = List.of(addButton);

        List<InlineKeyboardButton> thirdRow = List.of(removeButton);

        keyboard.setKeyboard(List.of(firstRow, secondRow, thirdRow));
        return keyboard;
    }
}
