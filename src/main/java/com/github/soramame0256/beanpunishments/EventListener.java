package com.github.soramame0256.beanpunishments;

import com.github.soramame0256.beanpunishments.util.ChatUtils;
import com.github.soramame0256.beanpunishments.util.PlayerUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.sql.Timestamp;
import java.time.Instant;

public class EventListener implements Listener {
    @EventHandler
    public void onConnect(PlayerLoginEvent e){
        PunishmentManager pm=BeanPunishments.getPunishmentManager();
        if(pm.isBanned(e.getPlayer())){
            PunishmentManager.BanStatus bs = pm.getBanStatus(e.getPlayer());
            String timeStamp = bs.getEnd() > 999999999 ? (bs.getEnd()==Long.MAX_VALUE ? "permanent" : "effective permanent") : Timestamp.from(Instant.ofEpochSecond(bs.getEnd())).toString();
            e.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                    ChatUtils.coloredTranslated(e.getPlayer(),"punish.banned",bs.getReason(), PlayerUtils.getName(bs.getEnforcer()), timeStamp)
            );
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e){
        PunishmentManager pm=BeanPunishments.getPunishmentManager();
        if(pm.isMuted(e.getPlayer())){
            PunishmentManager.MuteStatus ms = pm.getMuteStatus(e.getPlayer());
            String timeStamp = ms.getEnd() > 999999999 ? (ms.getEnd()==Long.MAX_VALUE ? "permanent" : "effective permanent") : Timestamp.from(Instant.ofEpochSecond(ms.getEnd())).toString();
            if(ms.getEnd()==Long.MAX_VALUE){
                ChatUtils.messageTranslated(e.getPlayer(), "punish.muted.permanent");
            }else{
                ChatUtils.messageTranslated(e.getPlayer(), "punish.muted.temporally", timeStamp);
            }
            e.setCancelled(true);
        }
    }
}
