package com.github.soramame0256.beanpunishments.command;

import org.bukkit.command.CommandSender;

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

        return true;
    }
}
