package org.MTJSP.diceCitizens;

import org.MTJSP.diceCitizens.commands.DiceBehaviorCommand;
import org.MTJSP.diceCitizens.commands.DiceReloadCommand;
import org.MTJSP.diceCitizens.listeners.DiceRollListener;
import org.MTJSP.diceCore.DicePlugin;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class DiceCitizensPlugin extends JavaPlugin {

    private NPCBehaviorManager behaviorManager;
    private boolean enabledSuccessfully = false;

    @Override
    public void onEnable() {
        // 检查前置插件
        if (!checkDependencies()) {
            getLogger().severe("前置插件未找到，插件将禁用！");
            // Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("正在启用 Citizens 骰子行为控制插件...");

        // 确保数据文件夹存在
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        try {
            //管理器初始化
            behaviorManager = new NPCBehaviorManager(this);
            behaviorManager.loadBehaviors();

            // 注册监听器
            getServer().getPluginManager().registerEvents(
                    new DiceRollListener(behaviorManager), this);

            // 注册命令
            Objects.requireNonNull(getCommand("dicebehavior")).setExecutor(
                    new DiceBehaviorCommand(behaviorManager));
            Objects.requireNonNull(getCommand("dicereload")).setExecutor(new DiceReloadCommand(this));
            enabledSuccessfully = true; // 标记成功启用
            getLogger().info("Citizens骰子行为控制插件已启用！");
        } catch (Exception e) {
            getLogger().severe("启用插件时发生错误: " + e.getMessage());
            e.printStackTrace();
            // 禁用插件，因为初始化失败
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        behaviorManager.saveBehaviors();
        getLogger().info("Citizens骰子行为控制插件已禁用");
    }

    public void reload() {
        behaviorManager.reload();
        getLogger().info("配置已重新加载");
    }

    private boolean checkDependencies() {
        Plugin dicePlugin = Bukkit.getPluginManager().getPlugin("DicePlugin");
        Plugin citizens = Bukkit.getPluginManager().getPlugin("Citizens");

        if (dicePlugin == null || !(dicePlugin instanceof DicePlugin)) {
            getLogger().severe("找不到骰子主插件！");
            return false;
        }

        if (citizens == null) {
            getLogger().severe("找不到Citizens插件！");
            return false;
        }

        if (!CitizensAPI.hasImplementation()) {
            getLogger().severe("Citizens API未正确加载！");
            return false;
        }

        return true;
    }

    public NPCBehaviorManager getBehaviorManager() {
        return behaviorManager;
    }
}