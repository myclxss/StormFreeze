package myclass.stormfreeze.nuevo;

import myclass.stormfreeze.Main;
import myclass.stormfreeze.accesories.SpawnUtil;
import myclass.stormfreeze.accesories.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class SetRedlocation implements CommandExecutor {

    public File location;
    public FileConfiguration spawnCoords;
    public String MainConfig;
    private final Main plugin;

    public SetRedlocation(Main plugin) {
        this.plugin = plugin;
    }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player))
            sender.sendMessage("This command cannot be executed from console");

        Player player = (Player) sender;

        SpawnUtil spawnCoords = SpawnUtil.getManager();

        if (!player.hasPermission("moonhub.setlocation")) {
            player.sendMessage("no pemrisos pe");
            return true;
        }
        spawnCoords.getConfig().set("red.world", player.getLocation().getWorld().getName());
        spawnCoords.getConfig().set("red.x", Double.valueOf(player.getLocation().getX()));
        spawnCoords.getConfig().set("red.y", Double.valueOf(player.getLocation().getY()));
        spawnCoords.getConfig().set("red.z", Double.valueOf(player.getLocation().getZ()));
        spawnCoords.getConfig().set("red.yaw", Float.valueOf(player.getLocation().getYaw()));
        spawnCoords.getConfig().set("red.pitch", Float.valueOf(player.getLocation().getPitch()));
        spawnCoords.saveConfig();
        player.sendMessage(Utils.color(String.format("&aLa jail fue colocada en: &eX&7: &e%.3f &eY&7: &e%.3f Z&7: &e%.3f", spawnCoords.getConfig().getDouble("red.x"), spawnCoords.getConfig().getDouble("red.y"), spawnCoords.getConfig().getDouble("red.z"))));

        return true;
    }
}
