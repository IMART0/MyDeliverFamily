package am.martirosyan.mydeliver.bot.user.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserStateManager {
    private final Map<Long, UserState> userStates = new ConcurrentHashMap<>();

    public UserState getUserState(long chatId) {
        return userStates.getOrDefault(chatId, UserState.START);
    }

    public void setUserState(long chatId, UserState state) {
        userStates.put(chatId, state);
    }
}
