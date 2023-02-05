package myclass.jails;

import myclass.jails.accesories.SpawnUtil;
import myclass.jails.accesories.TitleApi;
import myclass.jails.accesories.Utils;
import myclass.jails.command.SetPrisionerJail;
import myclass.jails.command.SetPrisionerReleased;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.libs.org.ibex.nestedvm.util.Seekable;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public final class Main extends JavaPlugin implements Listener, CommandExecutor{

    public Seekable.File location;
    public FileConfiguration spawnCoords;

    public static Main instance;
    public Map<UUID, Location> frozenPlayers = new HashMap<>();
    public Map<UUID, ItemStack> helmets = new HashMap<>();
    public Map<UUID, BukkitTask> tasks = new HashMap<>();
    private Main plugin;

    public Location jailLocation;
    public Location releasedLocation;

    @Override
    public void onEnable() {

        Utils.log("&e╔═╗╔═╗");
        Utils.log("&e╚═╗║       -&6MoonHub &7- &aOnline");
        Utils.log("&e╚═╝╚═╝");

        Main.instance = this;

        /* Loaded Class */
        loadListener();
        loadCommand();
        saveDefaultConfig();
        SpawnUtil.getManager().setupFiles();
        SpawnUtil.getManager().reloadConfig();

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
        getCommand("setjail").setExecutor(new SetPrisionerJail(this));
        getCommand("setreleased").setExecutor(new SetPrisionerReleased(this));

        Utils.log("&7(&6SuitCosmetics&7) &aCommands Loaded...");

    }
    public static Main getInstance() {
        return Main.instance;
    }

    @EventHandler
    public void onPlayerFreezeJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (frozenPlayers.containsKey(player.getUniqueId())) {
            playFreezeAnimation(player, 10, getConfig().getInt("PARTICLES.AMOUNT"), 1, 0.05f);
            player.teleport(jailLocation);
        }
    }

    @EventHandler
    public void freezeEvent(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (!frozenPlayers.containsKey(e.getPlayer().getUniqueId())) {
            return;
        }
        if (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockZ() != e.getTo().getBlockZ()) {
            TitleApi.sendTitle(player, 10, 30, 10, ChatColor.translateAlternateColorCodes('&', getConfig().getString("TITLES.FROZED.TITLE")), getConfig().getString("TITLES.FROZED.SUBTITLE"));
            e.getPlayer().teleport(frozenPlayers.get(player.getUniqueId()));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("FROZZED-MESSAGE")));
            player.teleport(jailLocation);
        }
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
            Player target = Bukkit.getPlayer(targetName);

            if (frozenPlayers.containsKey(target.getUniqueId())) {
                frozenPlayers.remove(target.getUniqueId());
                TitleApi.sendTitle(target, 10, 30, 10, ChatColor.translateAlternateColorCodes('&', getConfig().getString("TITLES.UNFROZED.TITLE")), getConfig().getString("TITLES.UNFROZED.SUBTITLE"));
                cuandoDesfrozea(target);
                player.teleport(releasedLocation);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("PREFIX") + getConfig().getString("STAFF-UNFROZZED-MESSAGE") + getConfig().getString("COLOR-TARGET") + targetName));

            } else {
                frozenPlayers.put(target.getUniqueId(), target.getLocation().clone());
                playFreezeAnimation(target, 10, getConfig().getInt("PARTICLES.AMOUNT"), 1, 0.05f);
                cuandoFrozea(target);
                player.teleport(jailLocation);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("PREFIX") + getConfig().getString("STAFF-FROZZED-MESSAGE") + getConfig().getString("COLOR-TARGET") + targetName));
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
        if (player.getInventory().getHelmet() != null) {
            helmets.put(player.getUniqueId(), player.getInventory().getHelmet());
        }
        player.getInventory().setHelmet(new ItemStack(Material.PACKED_ICE));
        player.teleport(jailLocation);
        player.getPlayer().setGameMode(GameMode.ADVENTURE);
    }

    public void cuandoDesfrozea(Player player) {
        player.getInventory().setHelmet(null);
        if (player.getInventory().getHelmet() != null) {
            helmets.put(player.getUniqueId(), player.getInventory().getHelmet());
        }
        player.getInventory().setHelmet(new ItemStack(Material.AIR));
        player.getPlayer().setGameMode(GameMode.SURVIVAL);
        player.teleport(releasedLocation);
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