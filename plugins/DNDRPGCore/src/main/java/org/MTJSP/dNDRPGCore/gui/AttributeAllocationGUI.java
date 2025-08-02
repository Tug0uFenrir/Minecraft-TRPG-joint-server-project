package org.MTJSP.dNDRPGCore.gui;


import org.MTJSP.dNDRPGCore.RPGPlugin;
import org.MTJSP.dNDRPGCore.data.PlayerData;
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
import java.util.HashMap;
import java.util.Map;

public class AttributeAllocationGUI {

    private static final Map<Integer, String> SLOT_TO_ATTRIBUTE = new HashMap<>();
    static {
        SLOT_TO_ATTRIBUTE.put(10, "strength");
        SLOT_TO_ATTRIBUTE.put(12, "dexterity");
        SLOT_TO_ATTRIBUTE.put(14, "constitution");
        SLOT_TO_ATTRIBUTE.put(16, "wisdom");
        SLOT_TO_ATTRIBUTE.put(28, "intelligence");
        SLOT_TO_ATTRIBUTE.put(30, "charisma");
    }

    public static void open(Player player) {
        PlayerData data = RPGPlugin.getInstance().getPlayerDataManager().getPlayerData(player.getUniqueId());
        Inventory gui = Bukkit.createInventory(null, 45, ChatColor.BLUE + "分配属性点 (" + data.getRemainingPoints() + "点可用)");

        // 属性显示
        for (Map.Entry<Integer, String> entry : SLOT_TO_ATTRIBUTE.entrySet()) {
            int slot = entry.getKey();
            String attribute = entry.getValue();
            int value = data.getAttribute(attribute);

            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();

            String displayName = "";
            switch (attribute) {
                case "strength": displayName = ChatColor.RED + "力量"; break;
                case "dexterity": displayName = ChatColor.YELLOW + "敏捷"; break;
                case "constitution": displayName = ChatColor.GREEN + "体质"; break;
                case "wisdom": displayName = ChatColor.LIGHT_PURPLE + "感知"; break;
                case "intelligence": displayName = ChatColor.AQUA + "智力"; break;
                case "charisma": displayName = ChatColor.GOLD + "魅力"; break;
            }

            meta.setDisplayName(displayName);
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "当前值: " + ChatColor.WHITE + value,
                    "",
                    ChatColor.GREEN + "+1点",
                    ChatColor.RED + "-1点"
            ));

            item.setItemMeta(meta);
            gui.setItem(slot, item);
        }

        // 确认按钮
        ItemStack confirm = new ItemStack(Material.EMERALD);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName(ChatColor.GREEN + "确认属性分配");
        confirm.setItemMeta(confirmMeta);
        gui.setItem(40, confirm);

        player.openInventory(gui);
    }

    public static class GUIListener implements Listener {
        private final RPGPlugin plugin;

        public GUIListener(RPGPlugin plugin) {
            this.plugin = plugin;
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            if (!event.getView().getTitle().startsWith("分配属性点")) return;

            event.setCancelled(true);

            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();
            int slot = event.getRawSlot();

            PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());

            // 确认按钮
            if (slot == 40) {
                if (data.getRemainingPoints() == 0) {
                    player.closeInventory();
                    player.sendMessage(ChatColor.GREEN + "角色创建完成！");
                    data.setCreationCompleted(true);
                    plugin.getPlayerDataManager().savePlayerData(player.getUniqueId());
                } else {
                    player.sendMessage(ChatColor.RED + "你还有未分配的属性点！");
                }
                return;
            }

            // 属性调整
            String attribute = SLOT_TO_ATTRIBUTE.get(slot);
            if (attribute != null) {
                if (event.isLeftClick() && data.getRemainingPoints() > 0) {
                    data.increaseAttribute(attribute, 1);
                    data.decreaseRemainingPoints(1);
                } else if (event.isRightClick() && data.getAttribute(attribute) > 0) {
                    data.decreaseAttribute(attribute, 1);
                    data.increaseRemainingPoints(1);
                }

                // 刷新GUI
                open(player);
            }
        }
    }
}