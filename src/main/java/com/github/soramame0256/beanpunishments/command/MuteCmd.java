package com.github.soramame0256.beanpunishments.command;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class MuteCmd extends CommandBase {
    public MuteCmd() {
        super("beanmute");
        setPermission("beanpunishments.command.mute");
        setUsage("/beanmute <player> [time=permanent] [reason]");
        setDescription("mutes player");
        setAliases(new ArrayList<String>(){{add("mute");}});
    }

    @Override
    public boolean run(CommandSender sender, String commandLabel, String[] args) {

        return false;
    }
}
