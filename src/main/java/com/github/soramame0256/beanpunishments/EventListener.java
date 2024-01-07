package com.github.soramame0256.beanpunishments;

import com.github.soramame0256.beanpunishments.util.ChatUtils;
import com.github.soramame0256.beanpunishments.util.PlayerUtils;
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
            String timeStamp = bs.getEnd() > 999999999 ? "permanent" : Timestamp.from(Instant.ofEpochSecond(bs.getEnd())).toString();
            e.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                    ChatUtils.coloredTranslated(e.getPlayer(),"punish.banned",bs.getReason(), PlayerUtils.getName(bs.getEnforcer()), timeStamp)
            );
        }
    }
}
