package com.github.soramame0256.beanpunishments.command;

import com.github.soramame0256.beanpunishments.BeanPunishments;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PointCmd extends CommandBase{

    public PointCmd() {
        super("beanpoints");
        setUsage("/beanpoints");
    }

    @Override
    public boolean run(CommandSender sender, String commandLabel, String[] args) {
        if(!(sender instanceof Player)) return false;
        sendMessage(BeanPunishments.getTranslator().translate(((Player) sender).getLocale(),"command.point.execute.self",String.valueOf(BeanPunishments.getPunishmentManager().getPoint((OfflinePlayer) sender))));
        return true;
    }
}
