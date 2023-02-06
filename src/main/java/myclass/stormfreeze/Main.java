package myclass.stormfreeze;

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
    public void onPlayerFreezeJoin(PlayerJoinEvent event) {

        SpawnUtil spawnCoords = SpawnUtil.getManager();

        Player player = event.getPlayer();
        if (frozenPlayers.containsKey(player.getUniqueId())) {
            playFreezeAnimation(player, 10, getConfig().getInt("PARTICLES.AMOUNT"), 1, 0.05f);
            World w = Bukkit.getServer().getWorld(spawnCoords.getConfig().getString("red.world"));
            double x = spawnCoords.getConfig().getDouble("red.x");
            double y = spawnCoords.getConfig().getDouble("red.y");
            double z = spawnCoords.getConfig().getDouble("red.z");
            float yaw = (float)spawnCoords.getConfig().getDouble("red.yaw");
            float pitch = (float)spawnCoords.getConfig().getDouble("red.pitch");
            Location loc = new Location(w, x, y, z, yaw, pitch);
            player.teleport(loc);
        }
    }

    @EventHandler
    public void freezeEvent(PlayerMoveEvent e) {

        SpawnUtil spawnCoords = SpawnUtil.getManager();

        Player player = e.getPlayer();
        if (!frozenPlayers.containsKey(e.getPlayer().getUniqueId())) {
            return;
        }
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("FROZZED-MESSAGE")));
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
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("CONSOLE-MESSAGE")));
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("stormsfreeze.use.commands")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("MESSAGE-NOT-PERMISSION")));
            return true;
        }


        if (args.length == 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&m-----------------------------------"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&r"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8» &6Plugin: &fFreeze"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8» &6Developer: &fAnhuar"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&r"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8» &6Commands:"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&r  &f/ss (nick)"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&r  &f/freeze (nick)"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&r"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&m-----------------------------------"));
            return true;
        }

        String targetName = args[0];
        if (Bukkit.getOfflinePlayer(targetName).getPlayer() != null) {

            SpawnUtil spawnCoords = SpawnUtil.getManager();

            Player target = Bukkit.getPlayer(targetName);

            if (frozenPlayers.containsKey(target.getUniqueId())) {
                frozenPlayers.remove(target.getUniqueId());
                TitleApi.sendTitle(target, 10, 30, 10, ChatColor.translateAlternateColorCodes('&', getConfig().getString("TITLES.UNFROZED.TITLE")), getConfig().getString("TITLES.UNFROZED.SUBTITLE"));
                cuandoDesfrozea(target);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("PREFIX") + getConfig().getString("STAFF-UNFROZZED-MESSAGE") + getConfig().getString("COLOR-TARGET") + targetName));
                World w = Bukkit.getServer().getWorld(spawnCoords.getConfig().getString("green.world"));
                double x = spawnCoords.getConfig().getDouble("green.x");
                double y = spawnCoords.getConfig().getDouble("green.y");
                double z = spawnCoords.getConfig().getDouble("green.z");
                float yaw = (float)spawnCoords.getConfig().getDouble("green.yaw");
                float pitch = (float)spawnCoords.getConfig().getDouble("green.pitch");
                Location loc2 = new Location(w, x, y, z, yaw, pitch);
                player.teleport(loc2);
            } else {
                frozenPlayers.put(target.getUniqueId(), target.getLocation().clone());
                playFreezeAnimation(target, 10, getConfig().getInt("PARTICLES.AMOUNT"), 1, 0.05f);
                cuandoFrozea(target);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("PREFIX") + getConfig().getString("STAFF-FROZZED-MESSAGE") + getConfig().getString("COLOR-TARGET") + targetName));
                World w = Bukkit.getServer().getWorld(spawnCoords.getConfig().getString("red.world"));
                double x = spawnCoords.getConfig().getDouble("red.x");
                double y = spawnCoords.getConfig().getDouble("red.y");
                double z = spawnCoords.getConfig().getDouble("red.z");
                float yaw = (float)spawnCoords.getConfig().getDouble("red.yaw");
                float pitch = (float)spawnCoords.getConfig().getDouble("red.pitch");
                Location loc = new Location(w, x, y, z, yaw, pitch);
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
        List<String> commandlist = Main.instance.getConfig().getStringList("COMMANDS-LIST");
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
        player.getInventory().setHelmet(new ItemStack(Material.PACKED_ICE));
        TitleApi.sendTitle(player, 10, 30, 10, ChatColor.translateAlternateColorCodes('&', getConfig().getString("TITLES.FROZED.TITLE")), getConfig().getString("TITLES.FROZED.SUBTITLE"));
        player.getPlayer().setGameMode(GameMode.ADVENTURE);
    }
    @EventHandler
    public void commandBlock(PlayerCommandPreprocessEvent event) {
        if (frozenPlayers.containsKey(event.getPlayer().getUniqueId())) {
            List<String> blocklist = Main.instance.getConfig().getStringList("COMMANDS-BLOCK");
            for (String s : blocklist) {
                if (event.getMessage().startsWith(s)) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("no tienes permisos xddddd");
                }
            }
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
        player.getInventory().setHelmet(new ItemStack(Material.AIR));
        player.getPlayer().setGameMode(GameMode.SURVIVAL);
        
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
        }
    }
}