package am.martirosyan.mydeliver.bot.user.util;

import am.martirosyan.mydeliver.model.Category;
import am.martirosyan.mydeliver.model.MenuItem;
import am.martirosyan.mydeliver.service.MenuItemService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class MessageSender {
    private final Logger logger = LoggerFactory.getLogger(MessageSender.class);
    private final AbsSender bot;
    private final Map<Long, Integer> previousMessageId = new ConcurrentHashMap<>();

    private final MenuItemService menuItemService;

    public void sendMainMenuKeyboardMessage(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(UserMessage.MAIN_MENU_MESSAGE);

        message.setReplyMarkup(UserKeyboardFactory.mainMenuKeyboard());

        executeMessage(message);
    }

    public void sendCategoriesInlineKeyboard(long chatId, List<Category> categories) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите категорию");

        message.setReplyMarkup(UserKeyboardFactory.categoryInlineKeyboard(categories));
        executeMessage(message);
    }

    public void sendCategoryMenu(long chatId, long categoryId, int page) {
        int pageSize = 5; // Количество элементов на странице
        List<MenuItem> menuItems = menuItemService.getByCategoryId(categoryId);

        int totalPages = (int) Math.ceil((double) menuItems.size() / pageSize);
        page = Math.max(0, Math.min(page, totalPages - 1)); // Ограничиваем границы страниц

        List<MenuItem> pageItems = menuItemService.getByCategoryId(categoryId, page);

        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setText("Выберите блюдо:");
        message.setMessageId(previousMessageId.get(chatId));
        message.setReplyMarkup(UserKeyboardFactory.menuItemInlineKeyboard(pageItems, categoryId, page, totalPages));

        editMessage(message);
    }

    public void sendMenuItemDetails(long chatId, MenuItem menuItem, UserCart userCart) {
        deletePreviousMessage(chatId);
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(new InputFile("https://image.pngaaa.com/898/195898-middle.png")); // URL или путь к файлу

        // Форматируем текст в Markdown
        String text = String.format(
                "*%s*\n\n_%s_\n\nЦена: *%.2f₽*",
                menuItem.getName(), menuItem.getDescription(), menuItem.getPrice()
        );
        sendPhoto.setCaption(text);
        sendPhoto.setParseMode("Markdown");

        // Добавляем кнопку "Добавить в корзину"
        sendPhoto.setReplyMarkup(UserKeyboardFactory.addToCartKeyboard(menuItem.getId(),
                userCart.getItemCount(menuItem.getId())));

        executeMessage(sendPhoto);

    }

    public void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        executeMessage(message);
    }

    public void deletePreviousMessage(long chatId) {
        try {
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(getPreviousMessageId(chatId));
            bot.execute(deleteMessage);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
        }
    }

    public void editMessage(EditMessageText message) {
        try {
            message.setMessageId(getPreviousMessageId(Long.parseLong(message.getChatId())));
            bot.execute(message);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
        }
    }

    public void sendRequestPhoneMessage(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(UserMessage.REGISTRATION_MESSAGE);
        message.setReplyMarkup(UserKeyboardFactory.phoneRequestKeyboard());
        executeMessage(message);
    }

    public void executeMessage(SendPhoto message) {
        try {
            int messageId = bot.execute(message).getMessageId();
            previousMessageId.put(Long.valueOf(message.getChatId()), messageId);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
        }
    }

    public void executeMessage(SendMessage message) {
        try {
            int messageId = bot.execute(message).getMessageId();
            previousMessageId.put(Long.valueOf(message.getChatId()), messageId);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
        }
    }

    public Integer getPreviousMessageId(long chatId) {
        return previousMessageId.get(chatId);
    }
}
