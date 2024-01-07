package com.github.soramame0256.beanpunishments.command;

import com.github.soramame0256.beanpunishments.BeanPunishments;
import com.github.soramame0256.beanpunishments.util.TimeUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MuteCmd extends CommandBase {
    public MuteCmd() {
        super("beanmute");
        setPermission("beanpunishments.command.mute.add");
        setUsage("/beanmute <player> [time=permanent] [reason]");
        setDescription("mutes player");
        setAliases(new ArrayList<String>(){{add("mute");}});
    }
    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if(args.length<=1) {
            return getPlugin().getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.startsWith(args.length == 1 ? args[0] : ""))
                    .collect(Collectors.toList());
        } else if(args.length==2){
            return new ArrayList<String>(){
                {
                    add("permanent");
                    add("1s");
                    add("1m");
                    add("1h");
                    add("1d");
                    add("1M");
                    add("1y");
                }
            }.stream().filter(a->a.startsWith(args[1])).collect(Collectors.toList());
        } else if(args.length==3){
            return new ArrayList<String>(){
                {
                    add("Spamming");
                    add("HateSpeach");
                    add("SexualHarassment");
                }
            }.stream().filter(a->a.startsWith(args[2])).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public boolean run(CommandSender sender, String commandLabel, String[] args) {
        if(args.length==0) return false;
        String time = args.length==1 ? "permanent" : args[1];
        StringBuilder reasonSb = new StringBuilder();
        if(args.length==2){
            reasonSb.append("No reason");
        }else{
            for(int i=2; i<args.length; i++){
                reasonSb.append(args[i]).append(" ");
            }
        }
        String reason = reasonSb.toString().trim();
        String locale = sender instanceof Player ? ((Player) sender).getLocale() : "en_us";
        if (!time.equalsIgnoreCase("permanent") && !TimeUtils.isTimeFormat(time)) return false;
        if (time.equalsIgnoreCase("permanent")){
            BeanPunishments.getPunishmentManager().mute(getPlugin().getServer().getOfflinePlayer(args[0]), reason, 0, sender, true);
        }else{
            BeanPunishments.getPunishmentManager().mute(getPlugin().getServer().getOfflinePlayer(args[0]),reason,TimeUtils.getSecond(time),sender,false);
        }
        sendMessage(sender, BeanPunishments.getTranslator().translate(locale,"punish.mute.execute",args[0], time.equalsIgnoreCase("permanent") ? "permanent" : TimeUtils.getFormattedTime(TimeUtils.getSecond(time)), reason));
        return true;
    }
}
