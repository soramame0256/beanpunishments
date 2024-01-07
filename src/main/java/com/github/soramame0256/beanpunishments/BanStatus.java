package com.github.soramame0256.beanpunishments;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BanStatus {
    private final boolean banned;
    private final String reason;
    private final long start;
    private final long end;
    private final boolean permanent;
    private final UUID enforcer;
    public BanStatus(boolean banned, String reason, long start, long end, boolean permanent, UUID enforcer){
        this.banned = banned;
        this.reason = reason;
        this.start = start;
        this.end = end;
        this.permanent = permanent;
        this.enforcer = enforcer;
    }
    public boolean isBanned(){
        return banned;
    }
    public String getReason(){
        return reason;
    }
    public long getStart(){
        return start;
    }
    public long getEnd(){
        return end;
    }
    public boolean isPermanent(){
        return permanent;
    }
    public UUID getEnforcer(){
        return enforcer;
    }
    public Player getEnforcerPlayer(){
        if(enforcer.compareTo(UUID.fromString("00000000-0000-0000-0000-000000000000"))==0){
            return null;
        }
        return BeanPunishments.getInstance().getServer().getPlayer(enforcer);
    }
}
