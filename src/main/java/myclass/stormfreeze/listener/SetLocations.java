package myclass.stormfreeze.listener;

import myclass.stormfreeze.accesories.SpawnUtil;
import myclass.stormfreeze.accesories.Utils;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.event.inventory.*;
import org.bukkit.*;
import org.bukkit.event.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class SetLocations implements Listener {

    public void createSetLocations(Player player) {
        Inventory inv = Bukkit.createInventory(null, 18, Utils.color("&e&lSet locations"));

        ItemStack safezone = new ItemStack(Material.EMERALD, 1);
        ItemMeta safezonemeta = safezone.getItemMeta();
        safezonemeta.setDisplayName(Utils.color("&aSet safezone location"));
        List<String> safezonelore = new ArrayList<String>();
        safezonelore.add(Utils.color("&r"));
        safezonelore.add(Utils.color("&7Coloca la zona donde los jugadores"));
        safezonelore.add(Utils.color("&7seran teletransportados al finalizar"));
        safezonelore.add(Utils.color("&7el congelamiento"));
        safezonelore.add(Utils.color("&r"));
        safezonelore.add(Utils.color("&eclick para colocar"));
        safezonemeta.setLore(safezonelore);
        safezone.setItemMeta(safezonemeta);
        inv.setItem(2, safezone);

        ItemStack jail = new ItemStack(101, 1);
        ItemMeta jailmeta = jail.getItemMeta();
        jailmeta.setDisplayName(Utils.color("&cSet jail location"));
        List<String> jaillore = new ArrayList<String>();
        jaillore.add(Utils.color("&r"));
        jaillore.add(Utils.color("&7Coloca la zona donde los jugadores"));
        jaillore.add(Utils.color("&7seran teletransportados al iniciar"));
        jaillore.add(Utils.color("&7el congelamiento"));
        jaillore.add(Utils.color("&r"));
        jaillore.add(Utils.color("&eclick para colocar"));
        jailmeta.setLore(jaillore);
        jail.setItemMeta(jailmeta);
        inv.setItem(6, jail);

        ItemStack close = new ItemStack(Material.BED, 1);
        ItemMeta closemeta = close.getItemMeta();
        closemeta.setDisplayName(Utils.color("&cCerrar"));
        close.setItemMeta(closemeta);
        inv.setItem(13, close);

        ItemStack blackpanel1 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        inv.setItem(0, blackpanel1);
        ItemStack blackpanel2 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        inv.setItem(1, blackpanel2);
        ItemStack blackpanel3 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        inv.setItem(3, blackpanel3);
        ItemStack blackpanel4 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        inv.setItem(4, blackpanel4);
        ItemStack blackpanel5 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        inv.setItem(5, blackpanel5);
        ItemStack blackpanel6 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        inv.setItem(7, blackpanel6);
        ItemStack blackpanel7 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        inv.setItem(8, blackpanel7);
        ItemStack blackpanel8 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        inv.setItem(9, blackpanel8);
        ItemStack blackpanel9 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        inv.setItem(10, blackpanel9);
        ItemStack blackpanel10 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        inv.setItem(11, blackpanel10);
        ItemStack blackpanel11 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        inv.setItem(12, blackpanel11);
        ItemStack blackpanel12 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        inv.setItem(14, blackpanel12);
        ItemStack blackpanel13 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        inv.setItem(15, blackpanel13);
        ItemStack blackpanel15 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        inv.setItem(16, blackpanel15);
        ItemStack blackpanel16 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        inv.setItem(17, blackpanel16);

        player.openInventory(inv);
    }

    @EventHandler
    public void ClickerInv(final InventoryClickEvent event) {
        final String name = Utils.color("&e&lSet locations");
        final String name2 = ChatColor.stripColor(name);
        if (ChatColor.stripColor(event.getView().getTitle()).equals(name2)) {
            if (event.getCurrentItem() == null || event.getSlotType() == null || event.getCurrentItem().getType() == Material.AIR) {
                event.setCancelled(true);
                return;
            }
            if (!event.getCurrentItem().hasItemMeta()) {
                event.setCancelled(true);
                return;
            }
            final Player player = (Player) event.getWhoClicked();
            SpawnUtil spawnCoords = SpawnUtil.getManager();
            event.setCancelled(true);
            if (event.getSlot() == 2) {
                spawnCoords.getConfig().set("SAFEZONE.WORLD", player.getLocation().getWorld().getName());
                spawnCoords.getConfig().set("SAFEZONE.X", Double.valueOf(player.getLocation().getX()));
                spawnCoords.getConfig().set("SAFEZONE.Y", Double.valueOf(player.getLocation().getY()));
                spawnCoords.getConfig().set("SAFEZONE.Z", Double.valueOf(player.getLocation().getZ()));
                spawnCoords.getConfig().set("SAFEZONE.YAW", Float.valueOf(player.getLocation().getYaw()));
                spawnCoords.getConfig().set("SAFEZONE.PITCH", Float.valueOf(player.getLocation().getPitch()));
                spawnCoords.saveConfig();
                player.sendMessage(Utils.color(String.format("&aThe safe zone was placed in: &eX&7: &e%.0f &eY&7: &e%.0f Z&7: &e%.0f", spawnCoords.getConfig().getDouble("SAFEZONE.X"), spawnCoords.getConfig().getDouble("SAFEZONE.Y"), spawnCoords.getConfig().getDouble("SAFEZONE.Z"))));
                player.closeInventory();
            }
            if (event.getSlot() == 6) {
                spawnCoords.getConfig().set("JAIL.WORLD", player.getLocation().getWorld().getName());
                spawnCoords.getConfig().set("JAIL.X", Double.valueOf(player.getLocation().getX()));
                spawnCoords.getConfig().set("JAIL.Y", Double.valueOf(player.getLocation().getY()));
                spawnCoords.getConfig().set("JAIL.Z", Double.valueOf(player.getLocation().getZ()));
                spawnCoords.getConfig().set("JAIL.YAW", Float.valueOf(player.getLocation().getYaw()));
                spawnCoords.getConfig().set("JAIL.PITCH", Float.valueOf(player.getLocation().getPitch()));
                spawnCoords.saveConfig();
                player.sendMessage(Utils.color(String.format("&aThe jail was placed in: &eX&7: &e%.0f &eY&7: &e%.0f Z&7: &e%.0f", spawnCoords.getConfig().getDouble("JAIL.X"), spawnCoords.getConfig().getDouble("JAIL.Y"), spawnCoords.getConfig().getDouble("JAIL.Z"))));

                player.closeInventory();
            }
            if (event.getSlot() == 13) {
                player.closeInventory();
            }
        }
    }
}
