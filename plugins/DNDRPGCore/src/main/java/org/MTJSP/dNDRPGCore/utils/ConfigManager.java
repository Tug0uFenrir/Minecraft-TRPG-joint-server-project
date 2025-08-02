package org.MTJSP.dNDRPGCore.utils;

import org.MTJSP.dNDRPGCore.RPGPlugin;
import org.MTJSP.dNDRPGCore.data.ClassData;
import org.MTJSP.dNDRPGCore.data.OriginData;
import org.MTJSP.dNDRPGCore.data.SkillData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class ConfigManager {
    private final RPGPlugin plugin;
    private YamlConfiguration classesConfig;
    private YamlConfiguration originsConfig;
    private YamlConfiguration skillsConfig;

    private final Map<String, ClassData> classes = new HashMap<>();
    private final Map<String, OriginData> origins = new HashMap<>();
    private final Map<String, SkillData> skills = new HashMap<>();

    public ConfigManager(RPGPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfigs() {
        // 加载职业配置
        classesConfig = loadConfig("classes.yml");
        loadClassesConfig();

        // 加载出身配置
        originsConfig = loadConfig("origins.yml");
        loadOriginsConfig();

        // 加载技能配置
        skillsConfig = loadConfig("skills.yml");
        loadSkillsConfig();
    }

    private YamlConfiguration loadConfig(String fileName) {
        File configFile = new File(plugin.getDataFolder(), fileName);
        if (!configFile.exists()) {
            plugin.saveResource(fileName, false);
        }
        return YamlConfiguration.loadConfiguration(configFile);
    }

    private void loadClassesConfig() {
        classes.clear();
        for (String key : classesConfig.getKeys(false)) {
            ConfigurationSection section = classesConfig.getConfigurationSection(key);
            if (section != null) {
                ClassData classData = new ClassData(
                        key,
                        section.getString("name"),
                        section.getString("description"),
                        section.getString("primary-attribute"),
                        section.getStringList("starting-skills"),
                        section.getStringList("starting-items")
                );
                classes.put(key, classData);
            }
        }
    }

    private void loadOriginsConfig() {
        origins.clear();
        for (String key : originsConfig.getKeys(false)) {
            ConfigurationSection section = originsConfig.getConfigurationSection(key);
            if (section != null) {
                Map<String, Integer> bonuses = new HashMap<>();
                ConfigurationSection bonusSection = section.getConfigurationSection("attribute-bonuses");
                if (bonusSection != null) {
                    for (String attr : bonusSection.getKeys(false)) {
                        bonuses.put(attr, bonusSection.getInt(attr));
                    }
                }

                OriginData originData = new OriginData(
                        key,
                        section.getString("name"),
                        section.getString("description"),
                        section.getString("class"),
                        bonuses,
                        section.getStringList("starting-items")
                );
                origins.put(key, originData);
            }
        }
    }

    private void loadSkillsConfig() {
        skills.clear();
        for (String key : skillsConfig.getKeys(false)) {
            ConfigurationSection section = skillsConfig.getConfigurationSection(key);
            if (section != null) {
                SkillData skillData = new SkillData(
                        key,
                        section.getString("name"),
                        section.getString("description"),
                        section.getString("class"),
                        section.getString("type"),
                        section.getInt("level", 1),
                        section.getDouble("cooldown", 10.0),
                        section.getDouble("mana-cost", 0.0),
                        section.getDouble("cast-time", 0.0),
                        section.getStringList("effects")
                );
                skills.put(key, skillData);
            }
        }
    }

    public Map<String, OriginData> getOriginsByClass(String className) {
        Map<String, OriginData> result = new HashMap<>();
        for (OriginData origin : origins.values()) {
            if (origin.getClassName().equalsIgnoreCase(className)) {
                result.put(origin.getId(), origin);
            }
        }
        return null;
    }

    // Getters
    public Map<String, ClassData> getClasses() { return classes; }
    public Map<String, OriginData> getOrigins() { return origins; }
    public Map<String, SkillData> getSkills() { return skills; }

    public ConfigManager getOriginsConfig() {
        return null;
    }

    public OriginData getOrigin(String originName) {
    }
}