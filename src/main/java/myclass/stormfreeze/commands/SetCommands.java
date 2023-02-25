package myclass.stormfreeze.commands;

import myclass.stormfreeze.Main;
import myclass.stormfreeze.accesories.Utils;
import myclass.stormfreeze.listener.SetLocations;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetCommands implements CommandExecutor {

    private final Main plugin;

    public SetCommands(Main plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player))
            return true;

        Player player = (Player) sender;

        if (args.length <= 0) {
            player.sendMessage(Utils.color("&r"));
            player.sendMessage(Utils.color("&7&m-----------------------------------"));
            player.sendMessage(Utils.color("&r"));
            player.sendMessage(Utils.color("&8» &6Plugin: &fStormFreeze"));
            player.sendMessage(Utils.color("&8» &6Developer: &fmyclass"));
            player.sendMessage(Utils.color("&8» &6Web page: &fhttps://myclxss.online"));
            player.sendMessage(Utils.color("&r"));
            player.sendMessage(Utils.color("&7&m-----------------------------------"));
            player.sendMessage(Utils.color("&r"));
            return true;
        }

        if (args[0].equalsIgnoreCase("setlocations")) {
            if (player.hasPermission("stormfreeze.admin")){
                SetLocations inv = new SetLocations();
                inv.createSetLocations(player);
                return true;
            }else {
                player.sendMessage(Utils.color(Main.instance.getConfig().getString("CHAT.NO-PERMISSIONS")));
            }
        }
        return false;
    }
}
