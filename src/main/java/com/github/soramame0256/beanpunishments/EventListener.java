package com.github.soramame0256.beanpunishments;

import com.github.soramame0256.beanpunishments.util.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.sql.Timestamp;
import java.time.Instant;

public class EventListener implements Listener {
    @EventHandler
    public void onConnect(PlayerLoginEvent e){
        PunishmentManager pm=BeanPunishments.getPunishmentManager();
        if(pm.isBanned(e.getPlayer())){
            BanStatus bs = pm.getBanStatus(e.getPlayer());
            e.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                    ChatColor.translateAlternateColorCodes('&',
                            BeanPunishments.getTranslator().translate(e.getPlayer().getLocale(),"punish.banned",bs.getReason(), PlayerUtils.getName(bs.getEnforcer()),Timestamp.from(Instant.ofEpochSecond(bs.getEnd())).toString())
                    )
            );
        }
    }
}
