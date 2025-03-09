package am.martirosyan.mydeliver.config;

import am.martirosyan.mydeliver.bot.MyDeliverAdminBot;
import am.martirosyan.mydeliver.bot.user.MyDeliverBot;
import am.martirosyan.mydeliver.bot.MyDeliverCashierBot;
import am.martirosyan.mydeliver.bot.MyDeliverCourierBot;
import am.martirosyan.mydeliver.properties.BotsProperties;
import am.martirosyan.mydeliver.service.CategoryService;
import am.martirosyan.mydeliver.service.MenuItemService;
import am.martirosyan.mydeliver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@RequiredArgsConstructor
public class BotsConfig {

    @Bean
    public BotsProperties botProperties() {
        return new BotsProperties();
    }

    @Bean
    public TelegramBotsApi telegramBotsApi(MyDeliverBot myDeliverBot,
                                           MyDeliverCashierBot cashierBot,
                                           MyDeliverAdminBot adminBot,
                                           MyDeliverCourierBot courierBot) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(myDeliverBot);
        botsApi.registerBot(cashierBot);
        botsApi.registerBot(adminBot);
        botsApi.registerBot(courierBot);
        return botsApi;
    }

    @Bean
    public MyDeliverBot myDeliverBot(UserService userService, CategoryService categoryService, MenuItemService menuItemService) {
        return new MyDeliverBot(
                botProperties().getMyDeliverBot().getName(),
                botProperties().getMyDeliverBot().getToken(),
                userService,
                categoryService,
                menuItemService
                );
    }

    @Bean
    public MyDeliverCashierBot cashierBot() {
        return new MyDeliverCashierBot(
                botProperties().getCashierBot().getName(),
                botProperties().getCashierBot().getToken()
                );
    }

    @Bean
    public MyDeliverAdminBot adminBot() {
        return new MyDeliverAdminBot(
                botProperties().getAdminBot().getName(),
                botProperties().getAdminBot().getToken()
                );
    }

    @Bean
    public MyDeliverCourierBot courierBot() {
        return new MyDeliverCourierBot(
                botProperties().getCourierBot().getName(),
                botProperties().getCourierBot().getToken()
                );
    }
}
