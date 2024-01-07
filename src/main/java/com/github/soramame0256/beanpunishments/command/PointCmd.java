package com.github.soramame0256.beanpunishments.command;

import org.bukkit.command.CommandSender;

public class PointCmd extends CommandBase{

    protected PointCmd(String name) {
        super(name);
        setUsage("/beanpoints (player)");
    }

    @Override
    public boolean run(CommandSender sender, String commandLabel, String[] args) {
        return false;
    }
}
