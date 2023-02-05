package myclass.jails.command;

import myclass.jails.Main;
import myclass.jails.accesories.SpawnUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class SetPrisionerReleased implements CommandExecutor {

    public File location;
    public FileConfiguration spawnCoords;
    public String MainConfig;

    private final Main plugin;

    public SetPrisionerReleased(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("setreleased")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Este comando solo puede ser usado por un jugador");
                return true;
            }
            Player player = (Player) sender;

            SpawnUtil spawnCoords = SpawnUtil.getManager();

            Main.instance.releasedLocation = player.getLocation();
            spawnCoords.getConfig().set("released.world", Main.instance.releasedLocation.getWorld().getName());
            spawnCoords.getConfig().set("released.x", Main.instance.releasedLocation.getX());
            spawnCoords.getConfig().set("released.y", Main.instance.releasedLocation.getY());
            spawnCoords.getConfig().set("released.z", Main.instance.releasedLocation.getZ());
            spawnCoords.getConfig().set("released.yaw", Main.instance.releasedLocation.getYaw());
            spawnCoords.getConfig().set("released.pitch", Main.instance.releasedLocation.getPitch());
            spawnCoords.saveConfig();
            sender.sendMessage("La ubicación de la liberacion ha sido establecida en " + Main.instance.releasedLocation.getX() + "," + Main.instance.releasedLocation.getY() + "," + Main.instance.releasedLocation.getZ());
            return true;
        }
        return false;
    }
}
