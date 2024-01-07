package com.github.soramame0256.beanpunishments.command;

import com.github.soramame0256.beanpunishments.BeanPunishments;
import com.github.soramame0256.beanpunishments.util.ChatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarningCmd extends CommandBase {
    protected WarningCmd(String name) {
        super(name);
        setPermission("beanpunishments.command.warning");
        setUsage("/beanwarning <player> <point> (reason)");
    }

    @Override
    public boolean run(CommandSender sender, String commandLabel, String[] args) {
        if(args.length<2) return false;
        StringBuilder reasonSb = new StringBuilder();
        if(args.length==2){
            reasonSb.append("No reason");
        }else{
            for(int i=2; i<args.length; i++){
                reasonSb.append(args[i]).append(" ");
            }
        }
        String reason = reasonSb.toString().trim();
        BeanPunishments.getPunishmentManager().warn(getPlugin().getServer().getOfflinePlayer(args[0]),Double.parseDouble(args[1]),reason,sender);
        String locale = sender instanceof Player ? ((Player) sender).getLocale() : "en_us";
        sendMessage(sender, BeanPunishments.getTranslator().translate(locale,"punish.warn.execute",args[0], reason, args[1]));
        return true;
    }
}
