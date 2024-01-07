package com.github.soramame0256.beanpunishments.command;

import com.github.soramame0256.beanpunishments.BeanPunishments;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

public abstract class CommandBase extends Command implements PluginIdentifiableCommand {
    CommandSender sender;
    BeanPunishments pl = BeanPunishments.getInstance();
    protected CommandBase(String name) {
        super(name);
    }
    @Override
    public Plugin getPlugin() {
        return pl;
    }
    public abstract boolean run(CommandSender sender, String commandLabel, String[] args);
    @Override
    public boolean testPermission(CommandSender target) {
        if (testPermissionSilent(target)) return true;
        sendMessage(target, BeanPunishments.getTranslator().translate(target instanceof Player ? ((Player) target).getLocale() : "en_us","command.permission_denied",getPermission()).split("\n"));
        return false;
    }
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        this.sender = sender;
        boolean success;
        if (!testPermission(sender)) {
            return true;
        }
        try {
            success = run(sender,commandLabel,args);
        } catch (Throwable ex) {
            throw new CommandException("Unhandled exception executing command '" + commandLabel + "' in plugin " + pl.getDescription().getFullName(), ex);
        }

        if (!success && usageMessage.length() > 0) {
            for (String line : usageMessage.replace("<command>", commandLabel).split("\n")) {
                sender.sendMessage(line);
            }
        }

        return success;
    }
    protected void sendMessage(String... messages){
        sendMessage(this.sender, messages);
    }
    protected void sendMessage(CommandSender sender, String... messages){
        Arrays.stream(messages)
                .forEach(m->sender.sendMessage(ChatColor.translateAlternateColorCodes('&',m)));
    }
}
