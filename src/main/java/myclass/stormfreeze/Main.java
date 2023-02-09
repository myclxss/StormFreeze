package myclass.stormfreeze;

import me.clip.placeholderapi.PlaceholderAPI;
import myclass.stormfreeze.accesories.SpawnUtil;
import myclass.stormfreeze.accesories.TitleApi;
import myclass.stormfreeze.accesories.Utils;
import myclass.stormfreeze.nuevo.SetGreenLocation;
import myclass.stormfreeze.nuevo.SetRedlocation;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.*;

public final class Main extends JavaPlugin implements Listener, CommandExecutor{


    public File location;
    public FileConfiguration spawnCoords;

    public static Main instance;
    public Map<UUID, Location> frozenPlayers = new HashMap<>();
    public Map<UUID, ItemStack> helmets = new HashMap<>();
    public Map<UUID, BukkitTask> tasks = new HashMap<>();

    public Map<UUID, UUID> freezeChat = new HashMap<>();

    private Main plugin;

    @Override
    public void onEnable() {

        Utils.log("&e╔═╗╔═╗");
        Utils.log("&e╚═╗║       -&6MoonHub &7- &aOnline");
        Utils.log("&e╚═╝╚═╝");

        Main.instance = this;

        /* Loaded Class */
        loadListener();
        loadCommand();
        SpawnUtil.getManager().setupFiles();
        SpawnUtil.getManager().reloadConfig();
        saveDefaultConfig();

    }
    @Override
    public void onDisable() {

        Utils.log("&e╔═╗╔═╗");
        Utils.log("&e╚═╗║       -&6MoonHub &7- &cOffline");
        Utils.log("&e╚═╝╚═╝");

        Main.instance = null;
        onStop();

    }
    public void loadListener() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        /* Global Listeners */
        getServer().getPluginManager().registerEvents(this, this);

        Utils.log("&7(&6SuitCosmetics&7) &aListener Loaded...");

    }
    public void loadCommand() {

        getCommand("freeze").setExecutor(this);
        getCommand("ss").setExecutor(this);
        getCommand("setred").setExecutor(new SetRedlocation(this));
        getCommand("setgreen").setExecutor(new SetGreenLocation(this));

        Utils.log("&7(&6SuitCosmetics&7) &aCommands Loaded...");

    }
    public static Main getInstance() {
        return Main.instance;
    }
    @EventHandler
    public void freezeEvent(PlayerMoveEvent e) {

        SpawnUtil spawnCoords = SpawnUtil.getManager();

        Player player = e.getPlayer();
        if (!frozenPlayers.containsKey(e.getPlayer().getUniqueId())) {
            return;
        }
        player.sendMessage(Utils.color(Main.instance.getConfig().getString("CHAT.FROZZED-MESSAGE")));
        World w = Bukkit.getServer().getWorld(spawnCoords.getConfig().getString("red.world"));
        double x = spawnCoords.getConfig().getDouble("red.x");
        double y = spawnCoords.getConfig().getDouble("red.y");
        double z = spawnCoords.getConfig().getDouble("red.z");
        float yaw = (float)spawnCoords.getConfig().getDouble("red.yaw");
        float pitch = (float)spawnCoords.getConfig().getDouble("red.pitch");
        Location loc = new Location(w, x, y, z, yaw, pitch);
        player.teleport(loc);

    }

    @EventHandler
    public void alRomper(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if (!frozenPlayers.containsKey(player.getUniqueId())) {
            return;
        }
        e.setCancelled(true);
        return;
    }

    @EventHandler
    public void alColocar(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        if (!frozenPlayers.containsKey(player.getUniqueId())) {
            return;
        }
        e.setCancelled(true);
        return;
    }

    @EventHandler
    public void alDropear(PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        if (!frozenPlayers.containsKey(player.getUniqueId())) {
            return;
        }
        e.setCancelled(true);
        return;
    }

    @EventHandler
    public void alMoverItem(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (!frozenPlayers.containsKey(player.getUniqueId())) {
            return;
        }
        e.setCancelled(true);
        player.updateInventory();
        return;
    }

    @EventHandler
    public void alGolpear(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (!frozenPlayers.containsKey(player.getUniqueId())) {
                return;
            }
            e.setCancelled(true);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("stormsfreeze.use.commands")) {
            player.sendMessage(Utils.color(Main.instance.getConfig().getString("CHAT.NO-PERMISSIONS")));
            return true;
        }


        if (args.length == 0) {
            player.sendMessage(Utils.color( "&7&m-----------------------------------"));
            player.sendMessage(Utils.color( "&r"));
            player.sendMessage(Utils.color( "&8» &6Plugin: &fFreeze"));
            player.sendMessage(Utils.color( "&8» &6Developer: &fAnhuar"));
            player.sendMessage(Utils.color( "&r"));
            player.sendMessage(Utils.color( "&8» &6Commands:"));
            player.sendMessage(Utils.color( "&r  &f/ss (nick)"));
            player.sendMessage(Utils.color( "&r  &f/freeze (nick)"));
            player.sendMessage(Utils.color( "&r"));
            player.sendMessage(Utils.color( "&7&m-----------------------------------"));
            return true;
        }

        String targetName = args[0];
        if (Bukkit.getOfflinePlayer(targetName).getPlayer() != null) {

            SpawnUtil spawnCoords = SpawnUtil.getManager();

            Player target = Bukkit.getPlayer(targetName);

            if (frozenPlayers.containsKey(target.getUniqueId())) {
                frozenPlayers.remove(target.getUniqueId());

                freezeChat.remove(freezeChat.get(target.getUniqueId()));
                freezeChat.remove(target.getUniqueId());

                TitleApi.sendTitle(target, 10, 30, 10, Utils.color(Main.instance.getConfig().getString("TITLES.UNFROZED.TITLE")), Main.instance.getConfig().getString("TITLES.UNFROZED.SUBTITLE"));
                cuandoDesfrozea(target);

                String staffUnFrozedMessage = Main.instance.getConfig().getString("CHAT.STAFF-UNFROZZED-MESSAGE");
                String replacedUnFrozedMessage = PlaceholderAPI.setPlaceholders(player, Utils.color(staffUnFrozedMessage));
                replacedUnFrozedMessage = replacedUnFrozedMessage.replaceAll("<target_name>", targetName);
                player.sendMessage(replacedUnFrozedMessage);

                World w = Bukkit.getServer().getWorld(spawnCoords.getConfig().getString("green.world"));
                double x = spawnCoords.getConfig().getDouble("green.x");
                double y = spawnCoords.getConfig().getDouble("green.y");
                double z = spawnCoords.getConfig().getDouble("green.z");
                float yaw = (float)spawnCoords.getConfig().getDouble("green.yaw");
                float pitch = (float)spawnCoords.getConfig().getDouble("green.pitch");
                Location loc2 = new Location(w, x, y, z, yaw, pitch);
                player.teleport(loc2);
                player.sendMessage(Utils.color(Main.instance.getConfig().getString("CHAT.CHAT-DELETED")));
            } else {
                frozenPlayers.put(target.getUniqueId(), target.getLocation().clone());

                freezeChat.put(player.getUniqueId(), target.getUniqueId());
                freezeChat.put(target.getUniqueId(), player.getUniqueId());

                playFreezeAnimation(target, 10, getConfig().getInt("PARTICLES.AMOUNT"), 1, 0.05f);
                cuandoFrozea(target);

                String staffFrozedMessage = Main.instance.getConfig().getString("CHAT.STAFF-FROZZED-MESSAGE");
                String replacedFrozedMessage = PlaceholderAPI.setPlaceholders(player, Utils.color(staffFrozedMessage));
                replacedFrozedMessage = replacedFrozedMessage.replaceAll("<target_name>", targetName);
                player.sendMessage(replacedFrozedMessage);

                World w = Bukkit.getServer().getWorld(spawnCoords.getConfig().getString("red.world"));
                double x = spawnCoords.getConfig().getDouble("red.x");
                double y = spawnCoords.getConfig().getDouble("red.y");
                double z = spawnCoords.getConfig().getDouble("red.z");
                float yaw = (float)spawnCoords.getConfig().getDouble("red.yaw");
                float pitch = (float)spawnCoords.getConfig().getDouble("red.pitch");
                Location loc = new Location(w, x, y, z, yaw, pitch);
                player.sendMessage(Utils.color(Main.instance.getConfig().getString("CHAT.CHAT-CREATED")));
                player.teleport(loc);
            }
        }
        return false;
    }

    public void playFreezeAnimation(Player player, int time, int particlesAmount, int particleAmount, float explosionStrength) {
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!frozenPlayers.containsKey(player.getUniqueId())) {
                    cancel();
                    return;
                }
                for (int i = 0; i < particlesAmount; i++) {
                    Location location = getRandomLocation(player.getLocation().add(0, 2.5, 0));
                    PacketPlayOutWorldParticles particlesPacket = new PacketPlayOutWorldParticles(EnumParticle.valueOf(getConfig().getString("PARTICLES.TYPE")), false, (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, explosionStrength, particleAmount, null);
                    Bukkit.getServer().getOnlinePlayers().forEach(online -> (((CraftPlayer) online).getHandle()).playerConnection.sendPacket(particlesPacket));
                }
            }
        }.runTaskTimer(this, 0, time);
        tasks.put(player.getUniqueId(), task);
    }

    public Location getRandomLocation(Location location) {
        Random random = new Random();
        double x = Double.parseDouble((int) location.getX() + "." + random.nextInt(10));
        double z = Double.parseDouble((int) location.getZ() + "." + random.nextInt(10));
        location.setX(x);
        location.setZ(z);
        return location;
    }

    public void cuandoFrozea(Player player) {

        SpawnUtil spawnCoords = SpawnUtil.getManager();

        if (player.getInventory().getHelmet() != null) {
            helmets.put(player.getUniqueId(), player.getInventory().getHelmet());
        }
        List<String> commandlist = Main.instance.getConfig().getStringList("COMMANDS.FORCE-EJECUTE");
        for (String s : commandlist) {
            Bukkit.dispatchCommand(player, s);
        }

        World w = Bukkit.getServer().getWorld(spawnCoords.getConfig().getString("red.world"));
        double x = spawnCoords.getConfig().getDouble("red.x");
        double y = spawnCoords.getConfig().getDouble("red.y");
        double z = spawnCoords.getConfig().getDouble("red.z");
        float yaw = (float)spawnCoords.getConfig().getDouble("red.yaw");
        float pitch = (float)spawnCoords.getConfig().getDouble("red.pitch");
        Location loc = new Location(w, x, y, z, yaw, pitch);
        player.teleport(loc);

        int data = Main.getInstance().getConfig().getInt("HEAD-ITEM.DATA");
        player.getInventory().setHelmet(new ItemStack(Material.getMaterial(getConfig().getString("HEAD-ITEM.TYPE")),1 ,(short) data));
        TitleApi.sendTitle(player, 10, 30, 10, Utils.color(Main.instance.getConfig().getString("TITLES.FROZED.TITLE")), Main.instance.getConfig().getString("TITLES.FROZED.SUBTITLE"));
        player.getPlayer().setGameMode(GameMode.ADVENTURE);
        player.sendMessage(Utils.color(Main.instance.getConfig().getString("CHAT.CHAT-CREATED")));
    }



    @EventHandler
    public void commandBlock(PlayerCommandPreprocessEvent event) {
        if (frozenPlayers.containsKey(event.getPlayer().getUniqueId())) {
            List<String> blocklist = Main.instance.getConfig().getStringList("COMMANDS.DENY-EJECUTE");
            for (String s : blocklist) {
                if (event.getMessage().startsWith(s)) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(Utils.color(Main.instance.getConfig().getString("CHAT.NO-PERMISSIONS")));
                }
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();

        if (freezeChat.containsKey(player.getUniqueId())) {
            UUID uuid = freezeChat.get(player.getUniqueId());
            Player target = Bukkit.getPlayer(uuid);


            String receivedMessage = Main.instance.getConfig().getString("CHAT.RECEIVED-FORMAT");
            String replacedMessage = PlaceholderAPI.setPlaceholders(event.getPlayer(), Utils.color(receivedMessage));
            replacedMessage = replacedMessage.replaceAll("<he_name>", player.getName());
            replacedMessage = replacedMessage.replaceAll("<you_name>", target.getName());
            replacedMessage = replacedMessage.replaceAll("<message>", event.getMessage());
            target.sendMessage(replacedMessage);

            String sendMessage = Main.instance.getConfig().getString("CHAT.SEND-FORMAT");
            String replacedMessage1 = PlaceholderAPI.setPlaceholders(event.getPlayer(), Utils.color(sendMessage));
            replacedMessage1 = replacedMessage1.replaceAll("<he_name>", player.getName());
            replacedMessage1 = replacedMessage1.replaceAll("<you_name>", target.getName());
            replacedMessage1 = replacedMessage1.replaceAll("<message>", event.getMessage());
            player.sendMessage(replacedMessage1);

            event.setCancelled(true);
        }
    }

    public void cuandoDesfrozea(Player player) {

        SpawnUtil spawnCoords = SpawnUtil.getManager();

        player.getInventory().setHelmet(null);
        if (player.getInventory().getHelmet() != null) {
            helmets.put(player.getUniqueId(), player.getInventory().getHelmet());
        }
        World w = Bukkit.getServer().getWorld(spawnCoords.getConfig().getString("green.world"));
        double x = spawnCoords.getConfig().getDouble("green.x");
        double y = spawnCoords.getConfig().getDouble("green.y");
        double z = spawnCoords.getConfig().getDouble("green.z");
        float yaw = (float)spawnCoords.getConfig().getDouble("green.yaw");
        float pitch = (float)spawnCoords.getConfig().getDouble("green.pitch");
        Location loc2 = new Location(w, x, y, z, yaw, pitch);
        player.teleport(loc2);
        player.getInventory().setHelmet(null);
        player.getPlayer().setGameMode(GameMode.SURVIVAL);
        player.sendMessage(Utils.color(Main.instance.getConfig().getString("CHAT.CHAT-DELETED")));

    }

    public void onStop() {
        for (Player online : Bukkit.getServer().getOnlinePlayers()) {
            if (helmets.containsKey(online.getUniqueId())) {
                online.getInventory().setHelmet(helmets.get(online.getUniqueId()));
                helmets.remove(online.getUniqueId());
            }
        }
    }

    @EventHandler
    public void cuandoDesconecta(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (frozenPlayers.containsKey(player.getUniqueId())) {
            tasks.get(player.getUniqueId()).cancel();
            tasks.remove(player.getUniqueId());

            frozenPlayers.remove(player.getUniqueId());
            freezeChat.remove(freezeChat.get(player.getUniqueId()));
            freezeChat.remove(player.getUniqueId());
            player.getInventory().setHelmet(null);

            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), Main.instance.getConfig().getString("BANNED-PLAYER.COMMAND") + " " + player.getName() + " " + Main.instance.getConfig().getString("BANNED-PLAYER.REASON"));
        }
    }
}