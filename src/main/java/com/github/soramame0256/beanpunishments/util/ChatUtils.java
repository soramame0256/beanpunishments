package com.github.soramame0256.beanpunishments.util;

import com.github.soramame0256.beanpunishments.BeanPunishments;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatUtils {
    public static void broadcastTranslated(String key, String... args){
        BeanPunishments.getInstance().getServer().getOnlinePlayers().forEach(a->messageTranslated(a,key,args));
    }
    public static void messageTranslated(Player p, String key, String... args){
        p.sendMessage(coloredTranslated(p,key,args));
    }
    public static String coloredTranslated(Player p, String key, String... args){
        return ChatColor.translateAlternateColorCodes('&',BeanPunishments.getTranslator().translate(p.getLocale(),key, args));
    }
}
