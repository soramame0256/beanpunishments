package com.github.soramame0256.beanpunishments.command;


import com.github.soramame0256.beanpunishments.BeanPunishments;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PardonCmd extends CommandBase {
    public PardonCmd() {
        super("beanpardon");
        setPermission("beanpunishments.command.ban.remove");
        setUsage("/beanpardon <player> (reason)");
    }
    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if(args.length<=1) {
            return BeanPunishments.getPunishmentManager().getBannedPlayers().stream()
                    .map(OfflinePlayer::getName)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public boolean run(CommandSender sender, String commandLabel, String[] args) {
        if(args.length == 0) return false;
        StringBuilder reasonSb = new StringBuilder();
        if(args.length==1){
            reasonSb.append("No reason");
        }else{
            for(int i=1; i<args.length; i++){
                reasonSb.append(args[i]).append(" ");
            }
        }
        String reason = reasonSb.toString().trim();
        if (BeanPunishments.getPunishmentManager().isBanned(getPlugin().getServer().getOfflinePlayer(args[0]))){
            BeanPunishments.getPunishmentManager().pardon(getPlugin().getServer().getOfflinePlayer(args[0]),reason,sender.getName());
        }
        return true;
    }
}
