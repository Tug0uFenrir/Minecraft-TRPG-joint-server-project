package org.MTJSP.dNDRPGCore.skills;
import org.MTJSP.dNDRPGCore.RPGPlugin;
import org.MTJSP.dNDRPGCore.data.SkillData;
import org.MTJSP.dNDRPGCore.utils.ConfigManager;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SkillManager {
    private final RPGPlugin plugin;
    private final Map<String, SkillExecutor> skillExecutors = new HashMap<>();

    public SkillManager(RPGPlugin plugin) {
        this.plugin = plugin;
        registerExecutors();
    }

    private void registerExecutors() {
        // 注册战士技能
        skillExecutors.put("warrior_passive", new WarriorSkills.PassiveActionTide());
        skillExecutors.put("warrior_q", new WarriorSkills.BladeSweep());
        skillExecutors.put("warrior_f", new WarriorSkills.RageRecovery());
        skillExecutors.put("warrior_c", new WarriorSkills.ForwardSlash());

        // 注册法师技能
        skillExecutors.put("mage_passive", new MageSkills.MiracleArcane());
        skillExecutors.put("mage_q", new MageSkills.Fireball());
        skillExecutors.put("mage_f", new MageSkills.IceSpike());
        skillExecutors.put("mage_c", new MageSkills.SwiftFeet());

        // 注册吟游诗人技能
        skillExecutors.put("bard_passive", new BardSkills.HeroicInspiration());
        skillExecutors.put("bard_q", new BardSkills.HarshWords());
        skillExecutors.put("bard_f", new BardSkills.HeroicPoem());
        skillExecutors.put("bard_c", new BardSkills.LegacyOfFire());
    }

    public boolean executeSkill(Player player, String skillId) {
        ConfigManager configManager = plugin.getConfigManager();
        SkillData skillData = configManager.getSkills().get(skillId);

        if (skillData == null) return false;

        SkillExecutor executor = skillExecutors.get(skillId);
        if (executor != null) {
            return executor.execute(player, skillData);
        }

        return false;
    }

    public interface SkillExecutor {
        boolean execute(Player player, SkillData skillData);
    }
}