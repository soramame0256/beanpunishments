package com.github.soramame0256.beanpunishments.command;

import com.github.soramame0256.beanpunishments.BeanPunishments;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KickCmd extends CommandBase {
    public KickCmd() {
        super("beankick");
        setPermission("beanpunishments.command.kick");
        setUsage("/beankick <player> (reason)");
    }
    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if(args.length<=1) {
            return getPlugin().getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
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
        String locale = sender instanceof Player ? ((Player) sender).getLocale() : "en_us";
        if(getPlugin().getServer().getPlayer(args[0]).isOnline()) sendMessage(sender, BeanPunishments.getTranslator().translate(locale,"punish.kick.execute",args[0], reason));
        if(getPlugin().getServer().getPlayer(args[0]).isOnline()) BeanPunishments.getPunishmentManager().kick(getPlugin().getServer().getOfflinePlayer(args[0]),reason,sender);
        return true;
    }
}
