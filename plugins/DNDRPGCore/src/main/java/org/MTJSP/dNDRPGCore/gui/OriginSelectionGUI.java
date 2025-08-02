package org.MTJSP.dNDRPGCore.gui;


import org.MTJSP.dNDRPGCore.RPGPlugin;
import org.MTJSP.dNDRPGCore.data.OriginData;
import org.MTJSP.dNDRPGCore.data.PlayerData;
import org.MTJSP.dNDRPGCore.utils.ConfigManager;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OriginSelectionGUI {

    public static void open(Player player, String className) {
        ConfigManager configManager = RPGPlugin.getInstance().getConfigManager();
        Map<String, OriginData> origins = configManager.getOriginsConfig().getOriginsByClass(className);

        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.BLUE + "选择出身 - " + className);

        int slot = 10;
        for (Map.Entry<String, OriginData> entry : origins.entrySet()) {
            OriginData origin = entry.getValue();

            ItemStack originItem = new ItemStack(Material.PAPER);
            ItemMeta meta = originItem.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + origin.getName());

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + origin.getDescription());
            lore.add("");

            // 属性加成
            lore.add(ChatColor.GOLD + "属性加成:");
            origin.getAttributeBonuses().forEach((attr, bonus) -> {
                lore.add(ChatColor.GRAY + "  " + attr + ": " + ChatColor.GREEN + "+" + bonus);
            });

            // 初始物品
            lore.add("");
            lore.add(ChatColor.GOLD + "初始物品:");
            origin.getStartingItems().forEach(item -> {
                lore.add(ChatColor.GRAY + "  - " + item);
            });

            lore.add("");
            lore.add(ChatColor.YELLOW + "点击选择");

            meta.setLore(lore);
            originItem.setItemMeta(meta);

            gui.setItem(slot, originItem);
            slot += 2;
        }

        player.openInventory(gui);
    }

    public static class GUIListener implements Listener {
        private final RPGPlugin plugin;

        public GUIListener(RPGPlugin plugin) {
            this.plugin = plugin;
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            if (!event.getView().getTitle().startsWith("选择出身")) return;

            event.setCancelled(true);

            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) return;

            String originName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
            PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());

            // 应用出身加成
            OriginData origin = plugin.getConfigManager().getOriginsConfig().getOrigin(originName);
            if (origin != null) {
                data.setOrigin(originName);
                origin.getAttributeBonuses().forEach(data::addAttributeBonus);

                // 给予初始物品
                origin.getStartingItems().forEach(item -> {
                    // 这里简化处理，实际需要解析物品
                    player.getInventory().addItem(new ItemStack(Material.valueOf(item)));
                });

                // 打开属性分配界面
                AttributeAllocationGUI.open(player);
            }
        }
    }
}