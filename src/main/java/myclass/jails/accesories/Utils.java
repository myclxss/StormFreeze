package myclass.jails.accesories;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Utils {

    public static String color(final String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static void log(String in) {
        Bukkit.getConsoleSender().sendMessage(color(in));
    }
}
