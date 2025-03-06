package am.martirosyan.mydeliver.bot;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RequiredArgsConstructor
public class MyDeliverCourierBot extends TelegramLongPollingBot {
    private final Logger logger = LoggerFactory.getLogger(MyDeliverCourierBot.class);

    private final String NAME;
    private final String TOKEN;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String text = "Бот тут! Отправляю тебе что-то еще.";
            try {
                execute(new SendMessage(chatId, text));
            } catch (TelegramApiException e) {
                logger.error(e.getMessage());
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
}
