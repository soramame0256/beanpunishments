package com.github.soramame0256.beanpunishments.command;

import com.github.soramame0256.beanpunishments.BeanPunishments;
import com.github.soramame0256.beanpunishments.util.ChatUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UnmuteCmd extends CommandBase{
    public UnmuteCmd() {
        super("beanunmute");
        setPermission("beanpunishments.command.mute.remove");
        setUsage("/beanunmute <player> [reason]");
        setDescription("Unmutes muted player");
        setAliases(new ArrayList<String>(){{add("unmute");}});
    }
    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if(args.length<=1) {
            return BeanPunishments.getPunishmentManager().getMutedPlayers().stream()
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
        String locale = sender instanceof Player ? ((Player) sender).getLocale() : "en_us";
        OfflinePlayer target = getPlugin().getServer().getOfflinePlayer(args[0]);
        if (BeanPunishments.getPunishmentManager().isMuted(target)){
            BeanPunishments.getPunishmentManager().unmute(target,reason,sender.getName());
            sendMessage(sender, BeanPunishments.getTranslator().translate(locale,"punish.unmute.execute",args[0],reason));
            if(target.isOnline()) ChatUtils.messageTranslated(target.getPlayer(), "punish.command.unmuted");
        }
        return true;
    }
}
