package am.martirosyan.mydeliver.config;

import am.martirosyan.mydeliver.bot.MyDeliverAdminBot;
import am.martirosyan.mydeliver.bot.user.MyDeliverBot;
import am.martirosyan.mydeliver.bot.MyDeliverCashierBot;
import am.martirosyan.mydeliver.bot.MyDeliverCourierBot;
import am.martirosyan.mydeliver.properties.BotsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@RequiredArgsConstructor
public class BotsConfig {
    private final BotsProperties botProperties;

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
    public MyDeliverBot myDeliverBot() {
        return new MyDeliverBot(
                botProperties.getMyDeliverBot().getName(),
                botProperties.getMyDeliverBot().getToken()
                );
    }

    @Bean
    public MyDeliverCashierBot cashierBot() {
        return new MyDeliverCashierBot(
                botProperties.getCashierBot().getName(),
                botProperties.getCashierBot().getToken()
                );
    }

    @Bean
    public MyDeliverAdminBot adminBot() {
        return new MyDeliverAdminBot(
                botProperties.getAdminBot().getName(),
                botProperties.getAdminBot().getToken()
                );
    }

    @Bean
    public MyDeliverCourierBot courierBot() {
        return new MyDeliverCourierBot(
                botProperties.getCourierBot().getName(),
                botProperties.getCourierBot().getToken()
                );
    }
}
