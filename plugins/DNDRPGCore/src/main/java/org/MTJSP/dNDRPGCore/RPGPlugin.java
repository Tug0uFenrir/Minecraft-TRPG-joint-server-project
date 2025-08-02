package org.MTJSP.dNDRPGCore;

import org.MTJSP.dNDRPGCore.data.PlayerDataManager;
import org.MTJSP.dNDRPGCore.events.PlayerJoinListener;
import org.MTJSP.dNDRPGCore.events.SkillUseListener;
import org.MTJSP.dNDRPGCore.gui.CharacterCreationGUI;
import org.MTJSP.dNDRPGCore.skills.SkillManager;
import org.MTJSP.dNDRPGCore.utils.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class RPGPlugin extends JavaPlugin {

    private static RPGPlugin instance;
    private PlayerDataManager playerDataManager;
    private ConfigManager configManager;
    private SkillManager skillManager;

    @Override
    public void onEnable() {
        instance = this;

        // 初始化管理器
        configManager = new ConfigManager(this);
        playerDataManager = new PlayerDataManager(this);
        skillManager = new SkillManager(this);

        // 加载配置
        configManager.loadConfigs();

        // 注册事件
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new SkillUseListener(this), this);

        // 注册命令
        getCommand("rpg").setExecutor((sender, command, label, args) -> {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                CharacterCreationGUI.open(player);
                return true;
            }
            return false;
        });

        getLogger().info("RPG插件已启用！");
    }

    @Override
    public void onDisable() {
        playerDataManager.saveAllPlayerData();
        getLogger().info("RPG插件已禁用！");
    }

    public static RPGPlugin getInstance() {
        return instance;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public SkillManager getSkillManager() {
        return skillManager;
    }
}