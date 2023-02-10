package myclass.stormfreeze.commands;

import myclass.stormfreeze.Main;
import myclass.stormfreeze.accesories.SpawnUtil;
import myclass.stormfreeze.accesories.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class SetCommands implements CommandExecutor {

    public File location;
    public FileConfiguration spawnCoords;
    public String MainConfig;
    private final Main plugin;

    public SetCommands(Main plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player))
            return true;

        Player player = (Player) sender;
        SpawnUtil spawnCoords = SpawnUtil.getManager();

        if (args.length < 2) {
                player.sendMessage(Utils.color(Main.instance.getConfig().getString("CHAT.NO-PERMISSIONS")));
                return true;
            }

            if (!args[0].equalsIgnoreCase("set")) {
                player.sendMessage(Utils.color("&8(&eStormFreeze8) &aUse &7/stormfreeze set &csafezone &7or &cjail"));
                return true;
            }
            if (args[1].equalsIgnoreCase("safezone")) {
                if (player.hasPermission("stormfreeze.admin")) {
                    spawnCoords.getConfig().set("SAFEZONE.WORLD", player.getLocation().getWorld().getName());
                    spawnCoords.getConfig().set("SAFEZONE.X", Double.valueOf(player.getLocation().getX()));
                    spawnCoords.getConfig().set("SAFEZONE.Y", Double.valueOf(player.getLocation().getY()));
                    spawnCoords.getConfig().set("SAFEZONE.Z", Double.valueOf(player.getLocation().getZ()));
                    spawnCoords.getConfig().set("SAFEZONE.YAW", Float.valueOf(player.getLocation().getYaw()));
                    spawnCoords.getConfig().set("SAFEZONE.PITCH", Float.valueOf(player.getLocation().getPitch()));
                    spawnCoords.saveConfig();
                    player.sendMessage(Utils.color(String.format("&8(&eStormFreeze&8) &aThe safe zone was placed in: &eX&7: &e%.0f &eY&7: &e%.0f Z&7: &e%.0f", spawnCoords.getConfig().getDouble("SAFEZONE.X"), spawnCoords.getConfig().getDouble("SAFEZONE.Y"), spawnCoords.getConfig().getDouble("SAFEZONE.Z"))));

                    return true;
                }
            }
            if (args[1].equalsIgnoreCase("jail")) {
                if (player.hasPermission("stormfreeze.admin")) {
                    spawnCoords.getConfig().set("JAIL.WORLD", player.getLocation().getWorld().getName());
                    spawnCoords.getConfig().set("JAIL.X", Double.valueOf(player.getLocation().getX()));
                    spawnCoords.getConfig().set("JAIL.Y", Double.valueOf(player.getLocation().getY()));
                    spawnCoords.getConfig().set("JAIL.Z", Double.valueOf(player.getLocation().getZ()));
                    spawnCoords.getConfig().set("JAIL.YAW", Float.valueOf(player.getLocation().getYaw()));
                    spawnCoords.getConfig().set("JAIL.PITCH", Float.valueOf(player.getLocation().getPitch()));
                    spawnCoords.saveConfig();
                    player.sendMessage(Utils.color(String.format("&8(&eStormFreeze&d8) &aThe jail was placed in: &eX&7: &e%.0f &eY&7: &e%.0f Z&7: &e%.0f", spawnCoords.getConfig().getDouble("JAIL.X"), spawnCoords.getConfig().getDouble("JAIL.Y"), spawnCoords.getConfig().getDouble("JAIL.Z"))));

                    return true;
                }
            }
        return false;
    }
}
