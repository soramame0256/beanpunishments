package com.github.soramame0256.beanpunishments;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.security.CodeSource;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class I18n {
    private Map<String, FileConfiguration> langs;
    public I18n(Plugin pl){
        langs = new HashMap<>();
        saveDefaultLanguage(pl);
        File langFolder = new File(pl.getDataFolder(),"lang");
        Arrays.stream(langFolder.listFiles())
                .filter(f->f.getName().endsWith(".yml"))
                .forEach(a-> langs.put(a.getName().replaceAll("\\.yml",""),YamlConfiguration.loadConfiguration(a)));

    }
    public String translate(String locale, String key, String... args){
        FileConfiguration lang = langs.getOrDefault(locale, langs.get("en_us"));
        String base = lang.getString(key);
        for(int i=1; i<=args.length; i++){
            base = base.replace("%"+i,args[i-1]);
        }
        return base;
    }
    private void saveDefaultLanguage(Plugin pl){
        CodeSource cs = PunishmentManager.class.getProtectionDomain().getCodeSource();
        File fl = new File(cs.getLocation().getFile());
        List<String> files = new ArrayList<>();
        System.out.println(fl);
        try {
            JarFile jar = new JarFile(fl);
            for (Enumeration<JarEntry> enumjar = jar.entries(); enumjar.hasMoreElements();) {
                JarEntry ent = enumjar.nextElement();
                if(ent.getName().startsWith("lang/")&&!ent.isDirectory()) files.add(ent.getName());
            }
            files.forEach(s->pl.saveResource(s, false));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
