package com.github.soramame0256.beanpunishments;

import com.github.soramame0256.beanpunishments.util.ChatUtils;
import com.github.soramame0256.beanpunishments.util.FileUtils;
import com.github.soramame0256.beanpunishments.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PunishmentManager {
    private final Map<UUID, BanStatus> banMap;

    public PunishmentManager(Plugin pl){
        banMap = new HashMap<>();
        Connection conn = null;
        try{
            Instant inst = Instant.now();
            conn = DriverManager.getConnection("jdbc:sqlite:beanpunishments.db");
            Statement statement = conn.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate("create table if not exists ban (uuid text not null primary key, start text, end text, reason text, enforcer text)");
            ResultSet rs = statement.executeQuery("select * from ban");
            while(rs.next()){
                banMap.put(UUID.fromString(rs.getString("uuid")),new BanStatus(inst.getEpochSecond()<Long.parseLong(rs.getString("end")),rs.getString("reason"),Long.parseLong(rs.getString("start")),Long.parseLong(rs.getString("end")),Long.parseLong(rs.getString("end"))==Long.MAX_VALUE,UUID.fromString(rs.getString("enforcer"))));
            }
            statement.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean isBanned(OfflinePlayer p){
        if(!banMap.containsKey(p.getUniqueId())) return false;
        update(p);
        return banMap.containsKey(p.getUniqueId());
    }
    public BanStatus getBanStatus(OfflinePlayer p){
        return banMap.get(p.getUniqueId());
    }
    public List<OfflinePlayer> getBannedPlayers(){
        return banMap.keySet().stream()
                .map(u->BeanPunishments.getInstance().getServer().getOfflinePlayer(u))
                .collect(Collectors.toList());
    }
    public void ban(OfflinePlayer p, long time, String reason, CommandSender enforcer, boolean permanent){
        if(isBanned(p)) pardon(p,"update", enforcer.getName());
        long start = Instant.now().getEpochSecond();
        long end = start+time;
        if(permanent) end=Long.MAX_VALUE;
        String uuidEnforcer = enforcer instanceof Player ? ((Player) enforcer).getUniqueId().toString() : "00000000-0000-0000-0000-000000000000";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:beanpunishments.db");
            PreparedStatement ps = conn.prepareStatement("insert into ban (uuid, start, end, reason, enforcer) values (?,?,?,?,?)");
            ps.setString(1,p.getUniqueId().toString());
            ps.setString(2,String.valueOf(start));
            ps.setString(3,String.valueOf(end));
            ps.setString(4,reason);
            ps.setString(5,uuidEnforcer);
            ps.executeUpdate();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (Config.isLoggingOn())FileUtils.log(enforcer.getName() + " banned " + p.getName() + "(" + p.getUniqueId() + ") because of " + reason + " for " + TimeUtils.getFormattedTime(time), Config.getLoggingFile());
        ChatUtils.broadcastTranslated("punish.ban.spoken",p.getName(),p.getUniqueId().toString(), TimeUtils.getFormattedTime(time),reason,enforcer.getName());
        banMap.put(p.getUniqueId(),new BanStatus(true,reason,start,end,permanent,UUID.fromString(uuidEnforcer)));
        if(p.isOnline()) kick(p, reason,enforcer);
    }
    public void kick(OfflinePlayer p, String reason, CommandSender enforcer){
        if(p.isOnline() && isBanned(p)){
            p.getPlayer().kickPlayer(ChatColor.translateAlternateColorCodes('&',BeanPunishments.getTranslator().translate(p.getPlayer().getLocale(),"punish.banned",reason, enforcer.getName(), Timestamp.from(Instant.ofEpochSecond(getBanStatus(p).getEnd())).toString())));
        }else if(p.isOnline()){
            if (Config.isLoggingOn()) FileUtils.log(enforcer.getName() + " kicked " + p.getName() + "(" + p.getUniqueId() + ") because of " + reason, Config.getLoggingFile());
            p.getPlayer().kickPlayer(ChatColor.translateAlternateColorCodes('&',BeanPunishments.getTranslator().translate(p.getPlayer().getLocale(),"punish.kicked",reason, enforcer.getName())));
        }
    }
    public void pardon(OfflinePlayer p,String reason, String enforcer){
        if(banMap.containsKey(p.getUniqueId())){
            Connection conn;
            try {
                conn = DriverManager.getConnection("jdbc:sqlite:beanpunishments.db");
                PreparedStatement ps = conn.prepareStatement("delete from ban where uuid = ?");
                ps.setString(1,p.getUniqueId().toString());
                ps.executeUpdate();
                ps.close();
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (Config.isLoggingOn()) FileUtils.log(enforcer + " pardoned " + p.getName() + "(" + p.getUniqueId() + ") because of " + reason, Config.getLoggingFile());
            banMap.remove(p.getUniqueId());
        }
    }
    private void update(OfflinePlayer p){
        if(banMap.get(p.getUniqueId()).getEnd()<=Instant.now().getEpochSecond()) pardon(p,"Period ends", "Console");
    }
}
