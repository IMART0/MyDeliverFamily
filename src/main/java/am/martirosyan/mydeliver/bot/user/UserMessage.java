package am.martirosyan.mydeliver.bot.user;

public class UserMessage {
    public final static String WELCOME_MESSAGE = """
            🎉 Добро пожаловать в MyDeliver! 🍔\s
            🚀Здесь вы можете заказать доставку еды из любимых ресторанов города🏙️🍕
            """;

    public final static String REGISTRATION_MESSAGE = """
           Для начала работы введите ваш номер телефона, используя клавиатуру📲
           """;

    public final static String BIRTHDAY_REQUEST_MESSAGE = """
            Введите свою дату рождения в формате ДД.ММ.ГГГГ
            для получения дополнительных бонусов🎂
            """;

    public final static String MISSING_BIRTHDAY_MESSAGE = """
            "Некорректный формат даты. Введите дату рождения в формате ДД.ММ.ГГГГ:"
            """;

    public static final String REGISTRATION_SUCCESS_MESSAGE = """
            ✅ Регистрация завершена!
            """;
    public static final String MAIN_MENU_MESSAGE = """
            Добро пожаловать в MyDeliver! 🍽️🚀\s
            Теперь вы можете оформить заказ и наслаждаться вкусной едой😋\s
            Чтобы начать, выберите действие на вашей клавиатуре👇
            """;
}
