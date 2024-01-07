package com.github.soramame0256.beanpunishments;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Config {
    private Plugin plugin;
    private FileConfiguration config;
    private static File loggingPath;
    private static boolean logging;
    private static double initialPoint;
    public Config(JavaPlugin pl){
        plugin = pl;
        reload();
    }
    public void reload(){
        initialize();
        config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));
        loggingPath = new File(config.getString("log-path"));
        logging = config.getBoolean("logging");
        initialPoint = config.getDouble("initial-point");
    }
    public void initialize(){
        plugin.saveDefaultConfig();
        config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));
        config.addDefault("log-path","plugins/BeanPunishments/punishment.log");
        config.addDefault("logging",true);
        config.addDefault("initial-point",100);
        config.options().copyDefaults(true);
        try {
            config.save(new File(plugin.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static File getLoggingFile(){
        return loggingPath;
    }
    public static boolean isLoggingOn(){
        return logging;
    }
    public static double getInitialPoint(){
        return initialPoint;
    }
}
