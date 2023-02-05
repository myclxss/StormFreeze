package myclass.jails.command;

import myclass.jails.Main;
import myclass.jails.accesories.SpawnUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;


public class SetPrisionerJail implements CommandExecutor {

    public File location;
    public FileConfiguration spawnCoords;
    public String MainConfig;

    private final Main plugin;

    public SetPrisionerJail(Main plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("setjail")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Este comando solo puede ser usado por un jugador");
                return true;
            }
            Player player = (Player) sender;

            SpawnUtil spawnCoords = SpawnUtil.getManager();

            Main.instance.jailLocation = player.getLocation();
            spawnCoords.getConfig().set("jail.world", Main.instance.jailLocation.getWorld().getName());
            spawnCoords.getConfig().set("jail.x", Main.instance.jailLocation.getX());
            spawnCoords.getConfig().set("jail.y", Main.instance.jailLocation.getY());
            spawnCoords.getConfig().set("jail.z", Main.instance.jailLocation.getZ());
            spawnCoords.getConfig().set("jail.yaw", Main.instance.jailLocation.getYaw());
            spawnCoords.getConfig().set("jail.pitch", Main.instance.jailLocation.getPitch());
            spawnCoords.saveConfig();
            sender.sendMessage("La ubicación de la cárcel ha sido establecida en " + Main.instance.jailLocation.getX() + "," + Main.instance.jailLocation.getY() + "," + Main.instance.jailLocation.getZ());
            return true;
        }
        return false;
    }
}
