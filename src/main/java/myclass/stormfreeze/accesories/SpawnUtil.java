package myclass.stormfreeze.accesories;

import java.io.File;
import java.io.IOException;

import myclass.stormfreeze.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

public class SpawnUtil implements Listener {
    private final Main plugin = Main.getPlugin(Main.class);

    public File location;

    public YamlConfiguration spawnCoords;

    private static final SpawnUtil manager = new SpawnUtil();

    private SpawnUtil() {
    }

    public SpawnUtil(Main mainclass) {
    }

    public static SpawnUtil getManager() {
        return manager;
    }

    public void setupFiles() {
        this.location = new File(this.plugin.getDataFolder(), "locations.yml");
        if (!this.location.exists()) {
            this.location.getParentFile().mkdirs();
            this.plugin.saveResource("locations.yml", false);
        }
        this.spawnCoords = new YamlConfiguration();
        try {
            this.spawnCoords.load(this.location);
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        return this.spawnCoords;
    }

    public void saveConfig() {
        try {
            this.spawnCoords.save(this.location);
        } catch (IOException iOException) {
        }
    }

    public void reloadConfig() {
        this.spawnCoords = YamlConfiguration.loadConfiguration(this.location);
    }
}
