package am.martirosyan.mydeliver.bot.user.handler;

import am.martirosyan.mydeliver.bot.user.util.MessageSender;
import am.martirosyan.mydeliver.bot.user.util.UserCart;
import am.martirosyan.mydeliver.model.MenuItem;
import am.martirosyan.mydeliver.service.CategoryService;
import am.martirosyan.mydeliver.service.MenuItemService;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.Map;

@RequiredArgsConstructor
public class OrderHandler {

    private final MessageSender messageSender;
    private final Map<Long, UserCart> userCarts;

    private final CategoryService categoryService;
    private final MenuItemService menuItemService;

    public void handleCategorySelection(CallbackQuery callbackQuery) {
        long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();

        if (data.matches("CATEGORY_\\d+_PAGE_\\d+")) {
            long categoryId = Long.parseLong(data.substring(9, data.indexOf("_PAGE_")));
            int page = Integer.parseInt(data.substring(data.indexOf("_PAGE_") + 6));
            if (callbackQuery.getMessage().hasPhoto()) {
                messageSender.deletePreviousMessage(chatId);
                messageSender.sendMessage(chatId, "Выберите блюдо:");
            }
            messageSender.sendCategoryMenu(callbackQuery.getMessage().getChatId(), categoryId, page);
        } else if (data.matches("MENUITEM_\\d+")) {
            long menuItemId = Long.parseLong(data.substring(9));
            MenuItem menuItem = menuItemService.getById(menuItemId);
            if (menuItem != null) {
                sendMenuItemDetails(chatId, menuItem, true);
            }
        } else if (data.equals("CATEGORIES")) {
            messageSender.deletePreviousMessage(chatId);
            messageSender.sendCategoriesInlineKeyboard(chatId, categoryService.getAll());
        } else if (data.matches("INCREASE_\\d+")) {
            long menuItemId = Long.parseLong(data.substring(9));
            userCarts.get(chatId).increaseQuantity(menuItemId);
            sendMenuItemDetails(chatId, menuItemService.getById(menuItemId), false);
        } else if (data.matches("DECREASE_\\d+")) {
            long menuItemId = Long.parseLong(data.substring(9));
            if (userCarts.get(chatId).decreaseQuantity(menuItemId)) {
                sendMenuItemDetails(chatId, menuItemService.getById(menuItemId), false);
            }
        }
    }

    private void sendMenuItemDetails(long chatId, MenuItem menuItem, boolean isNew) {
        if (isNew)
            messageSender.sendMenuItemDetails(chatId, menuItem, userCarts.get(chatId));
        else
            messageSender.editMenuItemCount(chatId, menuItem, userCarts.get(chatId));
    }
}
