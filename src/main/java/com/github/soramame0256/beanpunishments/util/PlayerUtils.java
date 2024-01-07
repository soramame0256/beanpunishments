package com.github.soramame0256.beanpunishments.util;

import com.github.soramame0256.beanpunishments.BeanPunishments;

import java.util.UUID;

public class PlayerUtils {
    public static String getName(UUID uuid){
        return uuid.toString().equals("00000000-0000-0000-0000-000000000000") ? "Console" : BeanPunishments.getInstance().getServer().getOfflinePlayer(uuid).getName();
    }
}
