package com.github.soramame0256.beanpunishments;

import com.github.soramame0256.beanpunishments.command.*;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class BeanPunishments extends JavaPlugin {
    //https://www.spigotmc.org/threads/how-to-register-commands-without-plugin-yml.441499/

    private static SimpleCommandMap commandMap;
    private SimplePluginManager pluginManager;
    private static BeanPunishments INSTANCE;
    private static Logger logger;
    private static I18n translator;
    private static Config config;
    private static PunishmentManager pManager;
    @Override
    public void onEnable() {
        INSTANCE = this;
        logger = getLogger();
        logger.log(Level.INFO,"First initialization completed!");
        logger.log(Level.INFO, "trying to register commands...");
        setupCommandMap();
        int cm = registerCommands(new BanCmd(), new PardonCmd(), new KickCmd(), new WarningCmd(), new PointCmd());
        logger.log(Level.INFO, "Successfully registered "+cm +" commands!");
        logger.log(Level.INFO, "Initializing subsystems...");
        config=new Config(this);
        translator=new I18n(this);
        pManager=new PunishmentManager(this);
        logger.log(Level.INFO, "Registering listeners...");
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        logger.log(Level.INFO, "Completed!");

    }
    public static I18n getTranslator(){
        return translator;
    }
    public static BeanPunishments getInstance(){
        return INSTANCE;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    public static PunishmentManager getPunishmentManager(){
        return pManager;
    }
    private int registerCommands(CommandBase... cmds){
        Arrays.stream(cmds).forEach(this::registerCommand);
        return cmds.length;
    }
    private void registerCommand(CommandBase cmd){
        commandMap.register("beanpunishments",cmd);
    }
    private void setupCommandMap(){
        pluginManager = (SimplePluginManager) this.getServer().getPluginManager();
        Field f = null;
        try {
            f = SimplePluginManager.class.getDeclaredField("commandMap");
        }catch(Exception e){
            e.printStackTrace();
        }
        f.setAccessible(true);
        try{
            commandMap= (SimpleCommandMap) f.get(pluginManager);
        }catch(Exception e){
            e.printStackTrace();
        }

    }
    public static SimpleCommandMap getCommandMap(){
        return commandMap;
    }
}
