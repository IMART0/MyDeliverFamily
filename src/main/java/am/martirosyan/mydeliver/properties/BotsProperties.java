package am.martirosyan.mydeliver.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "telegram")
public class BotsProperties {
    private BotConfig myDeliverBot;
    private BotConfig cashierBot;
    private BotConfig courierBot;
    private BotConfig adminBot;

    @Getter
    @Setter
    public static class BotConfig {
        private String name;
        private String token;
    }
}

