package am.martirosyan.mydeliver.bot.user.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserCart {

    private final Map<Long, Integer> menuItems;

    public UserCart() {
        menuItems = new ConcurrentHashMap<>();
    }

    public int getItemCount(Long id) {
        if (!menuItems.containsKey(id)) {
            menuItems.put(id, 0);
        }
        return menuItems.get(id);
    }

    public void increaseQuantity(long menuItemId) {
        menuItems.put(menuItemId, menuItems.get(menuItemId) + 1);
    }

    public boolean decreaseQuantity(long menuItemId) {
        if (menuItems.get(menuItemId) > 0) {
            menuItems.put(menuItemId, menuItems.get(menuItemId) - 1);
            return true;
        }
        return false;
    }
}
