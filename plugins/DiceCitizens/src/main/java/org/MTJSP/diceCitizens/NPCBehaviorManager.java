package org.MTJSP.diceCitizens;

import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.MTJSP.diceCore.DiceResult;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class NPCBehaviorManager {

    private final DiceCitizensPlugin plugin;
    private final Map<Integer, List<DiceBehavior>> npcBehaviors = new HashMap<>();
    private final Map<Integer, List<DiceBehavior>> globalBehaviors = new HashMap<>();

    public NPCBehaviorManager(DiceCitizensPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadBehaviors() {
        npcBehaviors.clear();
        globalBehaviors.clear();

        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();

        // 加载全局行为
        loadBehaviorSection(config.getConfigurationSection("global-behaviors"), globalBehaviors);

        // 加载NPC特定行为
        ConfigurationSection npcsSection = config.getConfigurationSection("npcs");
        if (npcsSection != null) {
            for (String npcId : npcsSection.getKeys(false)) {
                try {
                    int id = Integer.parseInt(npcId);
                    ConfigurationSection npcSection = npcsSection.getConfigurationSection(npcId);
                    if (npcSection != null) {
                        loadBehaviorSection(npcSection, npcBehaviors);
                    }
                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("无效的NPC ID: " + npcId);
                }
            }
        }

        plugin.getLogger().info("已加载 " + npcBehaviors.size() + " 个NPC的行为配置");
        plugin.getLogger().info("已加载 " + globalBehaviors.size() + " 个全局行为配置");
    }

    private void loadBehaviorSection(ConfigurationSection section, Map<Integer, List<DiceBehavior>> targetMap) {
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            if (!section.isConfigurationSection(key)) continue;

            ConfigurationSection behaviorSection = section.getConfigurationSection(key);
            try {
                int diceValue = behaviorSection.getInt("dice-value", -1);
                String diceRange = behaviorSection.getString("dice-range", "");
                String action = behaviorSection.getString("action", "");
                String type = behaviorSection.getString("type", "message");
                String target = behaviorSection.getString("target", "player");
                int cooldown = behaviorSection.getInt("cooldown", 0);

                DiceBehavior behavior = new DiceBehavior(
                        diceValue, diceRange, action, type, target, cooldown
                );

                // 添加到目标映射
                if (diceValue != -1) {
                    targetMap.computeIfAbsent(diceValue, k -> new ArrayList<>()).add(behavior);
                } else if (!diceRange.isEmpty()) {
                    // 范围行为会添加到所有相关值
                    int min = Integer.parseInt(diceRange.split("-")[0]);
                    int max = Integer.parseInt(diceRange.split("-")[1]);
                    for (int i = min; i <= max; i++) {
                        targetMap.computeIfAbsent(i, k -> new ArrayList<>()).add(behavior);
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().warning("加载行为配置时出错: " + key + " - " + e.getMessage());
            }
        }
    }

    public void saveBehaviors() {

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        FileConfiguration config = new YamlConfiguration();

        // 保存全局NPC行为，有时候无法正常读取配置文件，很奇怪
        saveBehaviorSection(config.createSection("global-behaviors"), globalBehaviors);

        // 保存NPC的特定行为
        ConfigurationSection npcsSection = config.createSection("npcs");
        for (Map.Entry<Integer, List<DiceBehavior>> entry : npcBehaviors.entrySet()) {
            ConfigurationSection npcSection = npcsSection.createSection(String.valueOf(entry.getKey()));
            saveBehaviorSection(npcSection, Collections.singletonMap(entry.getKey(), entry.getValue()));
        }

        config.set("npcs", npcsSection);

        try {
            config.save(new File(plugin.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            plugin.getLogger().severe("保存配置文件时出错: " + e.getMessage());
        }
    }

    private void saveBehaviorSection(ConfigurationSection section, Map<Integer, List<DiceBehavior>> behaviors) {
        int index = 1;
        for (List<DiceBehavior> behaviorList : behaviors.values()) {
            for (DiceBehavior behavior : behaviorList) {
                ConfigurationSection behaviorSection = section.createSection("behavior" + index++);
                if (behavior.getDiceValue() != -1) {
                    behaviorSection.set("dice-value", behavior.getDiceValue());
                } else {
                    behaviorSection.set("dice-range", behavior.getDiceRange());
                }
                behaviorSection.set("action", behavior.getAction());
                behaviorSection.set("type", behavior.getType());
                behaviorSection.set("target", behavior.getTarget());
                behaviorSection.set("cooldown", behavior.getCooldown());
            }
        }
    }

    public void reload() {
        loadBehaviors();
    }

    public void addBehavior(int npcId, DiceBehavior behavior) {
        List<DiceBehavior> behaviors = npcBehaviors.computeIfAbsent(npcId, k -> new ArrayList<>());
        behaviors.add(behavior);
        saveBehaviors();
    }

    public void addGlobalBehavior(DiceBehavior behavior) {
        if (behavior.getDiceValue() != -1) {
            globalBehaviors.computeIfAbsent(behavior.getDiceValue(), k -> new ArrayList<>()).add(behavior);
        } else if (!behavior.getDiceRange().isEmpty()) {
            int min = Integer.parseInt(behavior.getDiceRange().split("-")[0]);
            int max = Integer.parseInt(behavior.getDiceRange().split("-")[1]);
            for (int i = min; i <= max; i++) {
                globalBehaviors.computeIfAbsent(i, k -> new ArrayList<>()).add(behavior);
            }
        }
        saveBehaviors();
    }

    public List<DiceBehavior> getBehaviorsForNPC(int npcId, int diceValue) {
        List<DiceBehavior> behaviors = new ArrayList<>();

        // 添加全局行为
        if (globalBehaviors.containsKey(diceValue)) {
            behaviors.addAll(globalBehaviors.get(diceValue));
        }

        // 添加NPC特定行为
        if (npcBehaviors.containsKey(npcId) && npcBehaviors.get(npcId) != null) {
            behaviors.addAll(npcBehaviors.get(npcId).stream().filter(b -> b.getDiceValue() == diceValue || b.getDiceRangeMatches(diceValue)).collect(Collectors.toList()));
        }

        return behaviors;
    }

    public static class DiceBehavior {
        private final int diceValue;
        private final String diceRange;
        private final String action;
        private final String type;
        private final String target;
        private final int cooldown;
        private long lastExecution;

        public DiceBehavior(int diceValue, String diceRange, String action,
                            String type, String target, int cooldown) {
            this.diceValue = diceValue;
            this.diceRange = diceRange;
            this.action = action;
            this.type = type;
            this.target = target;
            this.cooldown = cooldown;
        }

        public boolean getDiceRangeMatches(int value) {
            if (diceRange == null || diceRange.isEmpty()) return false;
            try {
                String[] parts = diceRange.split("-");
                int min = Integer.parseInt(parts[0]);
                int max = Integer.parseInt(parts[1]);
                return value >= min && value <= max;
            } catch (Exception e) {
                return false;
            }
        }

        public boolean canExecute() {
            if (cooldown <= 0) return true;
            return System.currentTimeMillis() - lastExecution > cooldown * 1000L;
        }

        public void execute(Player player, NPC npc, DiceResult result) {
            if (!canExecute()) return;

            lastExecution = System.currentTimeMillis();

            // 解析变量
            String parsedAction = action
                    .replace("{player}", player.getName())
                    .replace("{npc}", npc.getName())
                    .replace("{dice}", String.valueOf(result.getFinalResult()))
                    .replace("{dice_expression}", result.getOriginalExpression());

            // 执行行为
            switch (type.toLowerCase()) {
                case "message":
                    sendMessage(player, npc, parsedAction);
                    break;
                case "command":
                    executeCommand(player, npc, parsedAction);
                    break;
                case "animation":
                    playAnimation(npc, parsedAction);
                    break;
                case "sound":
                    playSound(player, npc, parsedAction);
                    break;
                case "effect":
                    playEffect(player, npc, parsedAction);
                    break;
            }
        }

        private void sendMessage(Player player, NPC npc, String message) {
            if ("player".equalsIgnoreCase(target)) {
                player.sendMessage(message);
            } else if ("npc".equalsIgnoreCase(target)) {
                // NPC发送消息给玩家
                player.sendMessage("[" + npc.getName() + "] " + message);
            } else if ("broadcast".equalsIgnoreCase(target)) {
                Bukkit.broadcastMessage(message);
            }
        }

        private void executeCommand(Player player, NPC npc, String command) {
            if ("console".equalsIgnoreCase(target)) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            } else if ("player".equalsIgnoreCase(target)) {
                Bukkit.dispatchCommand(player, command);
            } else if ("npc".equalsIgnoreCase(target)) {
                // 以NPC身份执行命令
                Bukkit.dispatchCommand(new NPCSender(npc), command);
            }
        }

        private void playAnimation(NPC npc, String animation) {
            if (npc.isSpawned()) {
                // Citizens动画API，没有实现，发现在此版本中相关API已经删除
                //npc.getNavigator().getLocalParameters().animation(animation);
                return;
            }
        }

        private void playSound(Player player, NPC npc, String sound) {
            // 格式: sound_name:volume:pitch
            String[] parts = sound.split(":");
            String soundName = parts[0];
            float volume = parts.length > 1 ? Float.parseFloat(parts[1]) : 1.0f;
            float pitch = parts.length > 2 ? Float.parseFloat(parts[2]) : 1.0f;

            if ("player".equalsIgnoreCase(target)) {
                player.playSound(player.getLocation(), soundName, volume, pitch);
            } else if ("npc".equalsIgnoreCase(target) && npc.isSpawned()) {
                npc.getEntity().getWorld().playSound(
                        npc.getEntity().getLocation(), soundName, volume, pitch);
            } else if ("area".equalsIgnoreCase(target) && npc.isSpawned()) {
                npc.getEntity().getWorld().playSound(
                        npc.getEntity().getLocation(), soundName, volume, pitch);
            }
        }

        private void playEffect(Player player, NPC npc, String effect) {
            // 格式: effect_name:data
            String[] parts = effect.split(":");
            String effectName = parts[0];
            int data = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;

            if ("player".equalsIgnoreCase(target) && player.isOnline()) {
                player.playEffect(player.getLocation(),
                        org.bukkit.Effect.valueOf(effectName), data);
            } else if ("npc".equalsIgnoreCase(target) && npc.isSpawned()) {
                npc.getEntity().getWorld().playEffect(
                        npc.getEntity().getLocation(),
                        org.bukkit.Effect.valueOf(effectName), data);
            }
        }

        // Getters
        public int getDiceValue() {
            return diceValue;
        }

        public String getDiceRange() {
            return diceRange;
        }

        public String getAction() {
            return action;
        }

        public String getType() {
            return type;
        }

        public String getTarget() {
            return target;
        }

        public int getCooldown() {
            return cooldown;
        }
    }

    private static class NPCSender implements CommandSender {
        private final NPC npc;

        public NPCSender(NPC npc) {
            this.npc = npc;
        }

        @Override
        public void sendMessage(String message) {
        }

        @Override
        public void sendMessage(String... messages) {
        }

        @Override
        public void sendMessage(UUID uuid, String s) {

        }

        // 修复：添加缺失的 sendMessage(UUID, String...) 方法
        @Override
        public void sendMessage(UUID sender, String... messages) {
        }

        @Override
        public String getName() {
            return npc.getName();
        }


        @Override
        public Server getServer() {
            return Bukkit.getServer();
        }

        @Override
        public boolean isPermissionSet(String permission) {
            return false;
        }

        @Override
        public boolean isPermissionSet(Permission perm) {
            return false;
        }

        @Override
        public boolean hasPermission(String permission) {
            return false;
        }

        @Override
        public boolean hasPermission(Permission perm) {
            return false;
        }

        @Override
        public PermissionAttachment addAttachment(Plugin plugin) {
            return null;
        }

        @Override
        public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
            return null;
        }

        @Override
        public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
            return null;
        }

        @Override
        public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
            return null;
        }

        @Override
        public void removeAttachment(PermissionAttachment attachment) {
        }

        @Override
        public void recalculatePermissions() {
        }

        @Override
        public Set<PermissionAttachmentInfo> getEffectivePermissions() {
            return Collections.emptySet();
        }

        @Override
        public boolean isOp() {
            return false;
        }

        @Override
        public void setOp(boolean value) {
        }

        @Override
        public Spigot spigot() {
            return new Spigot() {
                @Override
                public void sendMessage(BaseComponent component) {
                }

                @Override
                public void sendMessage(BaseComponent... components) {
                }
            };
        }
    }
}