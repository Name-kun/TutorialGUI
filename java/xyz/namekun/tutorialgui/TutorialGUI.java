package xyz.namekun.tutorialgui;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class TutorialGUI extends JavaPlugin {

    //config.ymlのメソッド
    public File configFile;
    public static FileConfiguration config;

    @Override
    public void onEnable() {
        TutorialGUICmd tutorialGUICmd = new TutorialGUICmd();
        getCommand("tutorialgui").setExecutor(tutorialGUICmd);
        getCommand("tutorialgui").setTabCompleter(tutorialGUICmd);
        getServer().getPluginManager().registerEvents(tutorialGUICmd, this);
        createFiles();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void createFiles() {
        configFile = new File(this.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            this.saveResource("config.yml", false);
        }
        config = new YamlConfiguration();

        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
