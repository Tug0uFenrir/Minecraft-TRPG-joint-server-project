package org.MTJSP.dNDRPGCore.gui;

import org.MTJSP.dNDRPGCore.RPGPlugin;
import org.MTJSP.dNDRPGCore.data.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class CharacterCreationGUI {
    public static void open(Player player) {
        Inventory gui = Bukkit.createInventory(null,27, ChatColor.BLUE+ "角色创建 - 选择职业");

        //战士职业按钮
        ItemStack warriorItem = new ItemStack(Material.IRON_SWORD);
        ItemMeta warriorMeta = warriorItem.getItemMeta();
        warriorMeta.setDisplayName(ChatColor.RED + "战士");
        warriorMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "主属性: " + ChatColor.RED + "力量",
                ChatColor.GRAY + "擅长近战，拥有强大的生存能力",
                "",
                ChatColor.YELLOW + "点击选择"
        ));
        warriorItem.setItemMeta(warriorMeta);
        gui.setItem(11, warriorItem);
        //法师职业按钮
        ItemStack mageItem = new ItemStack(Material.BLAZE_ROD);
        ItemMeta mageMeta = mageItem.getItemMeta();
        mageMeta.setDisplayName(ChatColor.AQUA + "法师");
        mageMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "主属性: " + ChatColor.AQUA + "智力",
                ChatColor.GRAY + "擅长远程法术，爆发力强但生存能力弱",
                "",
                ChatColor.YELLOW + "点击选择"
        ));
        mageItem.setItemMeta(mageMeta);
        gui.setItem(13, mageItem);
        // 吟游诗人职业按钮
        ItemStack bardItem = new ItemStack(Material.MUSIC_DISC_CAT);
        ItemMeta bardMeta = bardItem.getItemMeta();
        bardMeta.setDisplayName(ChatColor.GOLD + "吟游诗人");
        bardMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "主属性: " + ChatColor.GOLD + "魅力",
                ChatColor.GRAY + "擅长支援队友，拥有多样的辅助能力",
                "",
                ChatColor.YELLOW + "点击选择"
        ));
        bardItem.setItemMeta(bardMeta);
        gui.setItem(15, bardItem);

        player.openInventory(gui);
    }

    //GUI监听事件
    public static class GUIListener implements Listener {
        private final RPGPlugin plugin;

        public GUIListener(RPGPlugin plugin) {
            this.plugin = plugin;
        }
        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            if (!event.getView().getTitle().contains("角色创建"))
                return;
            event.setCancelled(true);
            if (!(event.getWhoClicked() instanceof Player))
                return;
            Player player = (Player) event.getWhoClicked();

            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta())
                return;
            String className = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
            PlayerDataManager dataManager = plugin.getPlayerDataManager();
            switch (className) {
                case "战士":
                    dataManager.getPlayerData(player.getUniqueId()).setPlayerClass("warrior");
                    OriginSelectionGUI.open(player, "warrior");
                    break;
                case "法师":
                    dataManager.getPlayerData(player.getUniqueId()).setPlayerClass("mage");
                    OriginSelectionGUI.open(player, "mage");
                    break;
                case "吟游诗人":
                    dataManager.getPlayerData(player.getUniqueId()).setPlayerClass("bard");
                    OriginSelectionGUI.open(player, "bard");
                    break;
            }
        }


    }

}
