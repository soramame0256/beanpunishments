package com.github.soramame0256.beanpunishments.util;

import com.github.soramame0256.beanpunishments.BeanPunishments;
import org.bukkit.ChatColor;

public class ChatUtils {
    public static void broadcastTranslated(String key, String... args){
        BeanPunishments.getInstance().getServer().getOnlinePlayers().forEach(a->a.sendMessage(ChatColor.translateAlternateColorCodes('&',BeanPunishments.getTranslator().translate(a.getLocale(),key, args))));
    }
}
