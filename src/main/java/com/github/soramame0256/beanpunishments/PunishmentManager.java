package com.github.soramame0256.beanpunishments;

import com.github.soramame0256.beanpunishments.util.ChatUtils;
import com.github.soramame0256.beanpunishments.util.FileUtils;
import com.github.soramame0256.beanpunishments.util.TimeUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class PunishmentManager {
    private final Map<UUID, BanStatus> banMap;
    private final Map<UUID, Double> pointsMap;
    private final Map<UUID, MuteStatus> muteMap;
    public PunishmentManager(Plugin pl){
        banMap = new HashMap<>();
        pointsMap = new HashMap<>();
        muteMap = new HashMap<>();
        Connection conn;
        try{
            Instant inst = Instant.now();
            conn = DriverManager.getConnection("jdbc:sqlite:beanpunishments.db");
            Statement statement = conn.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate("create table if not exists ban (uuid text not null primary key, start integer, end integer, reason text, enforcer text)");
            statement.executeUpdate("create table if not exists warning (uuid text not null primary key, point real)");
            statement.executeUpdate("create table if not exists mute (uuid text not null primary key, end integer, reason text, enforcer text)");
            ResultSet rs = statement.executeQuery("select * from ban");
            while(rs.next()){
                banMap.put(UUID.fromString(rs.getString("uuid")), new BanStatus(rs.getString("reason"), rs.getLong("start"), rs.getLong("end"), rs.getLong("end") == Long.MAX_VALUE, UUID.fromString(rs.getString("enforcer"))));
            }
            rs = statement.executeQuery("select * from warning");
            while(rs.next()){
                pointsMap.put(UUID.fromString(rs.getString("uuid")),rs.getDouble("point"));
            }
            rs = statement.executeQuery("select * from mute");
            while(rs.next()){
                muteMap.put(UUID.fromString(rs.getString("uuid")),new MuteStatus(rs.getString("reason"),rs.getLong("end"),rs.getLong("end")==Long.MAX_VALUE, UUID.fromString("enforcer")));
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
    public boolean isMuted(OfflinePlayer p){
        if(!muteMap.containsKey(p.getUniqueId())) return false;
        update(p);
        return muteMap.containsKey(p.getUniqueId());
    }
    public BanStatus getBanStatus(OfflinePlayer p){
        return banMap.get(p.getUniqueId());
    }
    public List<OfflinePlayer> getBannedPlayers(){
        return banMap.keySet().stream()
                .map(u->BeanPunishments.getInstance().getServer().getOfflinePlayer(u))
                .collect(Collectors.toList());
    }
    public double getPoint(OfflinePlayer p){
        return pointsMap.getOrDefault(p.getUniqueId(), Config.getInitialPoint());
    }
    public void warn(OfflinePlayer p, double point, String reason, CommandSender enforcer){
        double currentlyPoint = getPoint(p)-point;
        new Thread(()->{
            Connection conn;
            try {
                conn = DriverManager.getConnection("jdbc:sqlite:beanpunishments.db");
                PreparedStatement ps;
                if(pointsMap.containsKey(p.getUniqueId())){
                    ps = conn.prepareStatement("update warning set point = ? where uuid = ?");
                }else {
                    ps = conn.prepareStatement("insert into warning (point, uuid) values (?,?)");
                }
                ps.setDouble(1,currentlyPoint);
                ps.setString(2,p.getUniqueId().toString());
                ps.executeUpdate();
                ps.close();
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).start();
        pointsMap.put(p.getUniqueId(), currentlyPoint);
        ChatUtils.broadcastTranslated("punish.warned", p.getName(), String.valueOf(point), reason, enforcer.getName(), String.valueOf(currentlyPoint));
        if (Config.isLoggingOn())FileUtils.log(enforcer.getName() + " warned " + p.getName() + "(" + p.getUniqueId() + ") because of " + reason + " and removed " + point + " points", Config.getLoggingFile());
    }
    public void ban(OfflinePlayer p, long time, String reason, CommandSender enforcer, boolean permanent){
        if(isBanned(p)) pardon(p,"update", enforcer.getName());
        long start = Instant.now().getEpochSecond();
        long end = permanent ? Long.MAX_VALUE : start+time;
        String uuidEnforcer = enforcer instanceof Player ? ((Player) enforcer).getUniqueId().toString() : "00000000-0000-0000-0000-000000000000";
        new Thread(()->{
            try {
                Connection conn;
                conn = DriverManager.getConnection("jdbc:sqlite:beanpunishments.db");
                PreparedStatement ps = conn.prepareStatement("insert into ban (uuid, start, end, reason, enforcer) values (?,?,?,?,?)");
                ps.setString(1,p.getUniqueId().toString());
                ps.setLong(2,start);
                ps.setLong(3,end);
                ps.setString(4,reason);
                ps.setString(5,uuidEnforcer);
                ps.executeUpdate();
                ps.close();
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).start();
        if (Config.isLoggingOn())FileUtils.log(enforcer.getName() + " banned " + p.getName() + "(" + p.getUniqueId() + ") because of " + reason + " for " + (permanent ? "permanent" : TimeUtils.getFormattedTime(time)), Config.getLoggingFile());
        ChatUtils.broadcastTranslated("punish.ban.spoken",p.getName(),p.getUniqueId().toString(), permanent ? "permanent" : TimeUtils.getFormattedTime(time),reason,enforcer.getName());
        banMap.put(p.getUniqueId(), new BanStatus(reason, start, end, permanent, UUID.fromString(uuidEnforcer)));
        if(p.isOnline()) kick(p, reason,enforcer);
    }
    public void kick(OfflinePlayer p, String reason, CommandSender enforcer){
        String timeStamp = getBanStatus(p).getEnd() > 999999999 ? (getBanStatus(p).getEnd()==Long.MAX_VALUE ? "permanent" : "effective permanent") : Timestamp.from(Instant.ofEpochSecond(getBanStatus(p).getEnd())).toString();
        if(p.isOnline() && isBanned(p)){
            p.getPlayer().kickPlayer(ChatUtils.coloredTranslated(p.getPlayer(),"punish.banned",reason, enforcer.getName(), timeStamp));
        }else if(p.isOnline()){
            if (Config.isLoggingOn()) FileUtils.log(enforcer.getName() + " kicked " + p.getName() + "(" + p.getUniqueId() + ") because of " + reason, Config.getLoggingFile());
            p.getPlayer().kickPlayer(ChatUtils.coloredTranslated(p.getPlayer(),"punish.kicked",reason, enforcer.getName()));
        }
    }
    public void pardon(OfflinePlayer p,String reason, String enforcer){
        if(banMap.containsKey(p.getUniqueId())){
            new Thread(()-> {
                Connection conn;
                try {
                    conn = DriverManager.getConnection("jdbc:sqlite:beanpunishments.db");
                    PreparedStatement ps = conn.prepareStatement("delete from ban where uuid = ?");
                    ps.setString(1, p.getUniqueId().toString());
                    ps.executeUpdate();
                    ps.close();
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }).start();
            if (Config.isLoggingOn()) FileUtils.log(enforcer + " pardoned " + p.getName() + "(" + p.getUniqueId() + ") because of " + reason, Config.getLoggingFile());
            banMap.remove(p.getUniqueId());
        }
    }
    public void mute(OfflinePlayer p, String reason, long time, CommandSender enforcer, boolean permanent){
        if(muteMap.containsKey(p.getUniqueId())) unmute(p,"update", enforcer.getName());
        long start = Instant.now().getEpochSecond();
        long end = permanent ? Long.MAX_VALUE : start+time;
        String uuidEnforcer = enforcer instanceof Player ? ((Player) enforcer).getUniqueId().toString() : "00000000-0000-0000-0000-000000000000";
        new Thread(()->{
            try {
                Connection conn;
                conn = DriverManager.getConnection("jdbc:sqlite:beanpunishments.db");
                PreparedStatement ps = conn.prepareStatement("insert into mute (uuid, end, reason, enforcer) values (?,?,?,?)");
                ps.setString(1,p.getUniqueId().toString());
                ps.setLong(2,end);
                ps.setString(3,reason);
                ps.setString(4,uuidEnforcer);
                ps.executeUpdate();
                ps.close();
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).start();
        if (Config.isLoggingOn())FileUtils.log(enforcer.getName() + " muted " + p.getName() + "(" + p.getUniqueId() + ") because of " + reason + " for " + (permanent ? "permanent" : TimeUtils.getFormattedTime(time)), Config.getLoggingFile());
        ChatUtils.broadcastTranslated("punish.mute.spoken",p.getName(), permanent ? "permanent" : TimeUtils.getFormattedTime(time),reason,enforcer.getName());
        muteMap.put(p.getUniqueId(), new MuteStatus(reason, end, permanent, UUID.fromString(uuidEnforcer)));

    }
    public void unmute(OfflinePlayer p, String reason, String enforcer){
        if(muteMap.containsKey(p.getUniqueId())){
            new Thread(()-> {
                Connection conn;
                try {
                    conn = DriverManager.getConnection("jdbc:sqlite:beanpunishments.db");
                    PreparedStatement ps = conn.prepareStatement("delete from mute where uuid = ?");
                    ps.setString(1, p.getUniqueId().toString());
                    ps.executeUpdate();
                    ps.close();
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }).start();
            if (Config.isLoggingOn()) FileUtils.log(enforcer + " unmuted " + p.getName() + "(" + p.getUniqueId() + ") because of " + reason, Config.getLoggingFile());
            muteMap.remove(p.getUniqueId());
        }
    }
    private void update(OfflinePlayer p){
        if(banMap.containsKey(p.getUniqueId()) && banMap.get(p.getUniqueId()).getEnd()<=Instant.now().getEpochSecond()) pardon(p,"Period ends", "Console");
        if(muteMap.containsKey(p.getUniqueId())&& muteMap.get(p.getUniqueId()).getEnd()<=Instant.now().getEpochSecond()) unmute(p,"Period ends", "Console");
    }

    public List<OfflinePlayer> getMutedPlayers(){
        return muteMap.keySet().stream()
                .map(u->BeanPunishments.getInstance().getServer().getOfflinePlayer(u))
                .collect(Collectors.toList());
    }

    static class MuteStatus{
        private final String reason;
        private final long end;
        private final boolean permanent;
        private final UUID enforcer;
        public MuteStatus(String reason, long end, boolean permanent, UUID enforcer){
            this.reason = reason;
            this.end = end;
            this.permanent = permanent;
            this.enforcer = enforcer;
        }
        public long getEnd(){
            return end;
        }
        public String getReason(){
            return reason;
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
    static class BanStatus {
        private final String reason;
        private final long start;
        private final long end;
        private final boolean permanent;
        private final UUID enforcer;
        public BanStatus(String reason, long start, long end, boolean permanent, UUID enforcer){
            this.reason = reason;
            this.start = start;
            this.end = end;
            this.permanent = permanent;
            this.enforcer = enforcer;
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
}
