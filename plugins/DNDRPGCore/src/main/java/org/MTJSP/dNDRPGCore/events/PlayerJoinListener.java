package org.MTJSP.dNDRPGCore.events;
import org.MTJSP.dNDRPGCore.RPGPlugin;
import org.MTJSP.dNDRPGCore.data.PlayerData;
import org.MTJSP.dNDRPGCore.data.PlayerDataManager;
import org.MTJSP.dNDRPGCore.gui.CharacterCreationGUI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final RPGPlugin plugin;

    public PlayerJoinListener(RPGPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerDataManager dataManager = plugin.getPlayerDataManager();

        // 加载玩家数据
        PlayerData data = dataManager.getPlayerData(player.getUniqueId());

        // 如果玩家没有完成角色创建
        if (!data.isCreationCompleted()) {
            player.sendMessage(ChatColor.GOLD + "欢迎来到服务器！请先创建你的角色。");
            CharacterCreationGUI.open(player);
        }
    }
}