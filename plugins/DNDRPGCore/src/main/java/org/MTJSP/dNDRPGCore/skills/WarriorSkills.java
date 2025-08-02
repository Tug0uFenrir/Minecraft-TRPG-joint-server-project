package org.MTJSP.dNDRPGCore.skills;

import org.MTJSP.dNDRPGCore.RPGPlugin;
import org.MTJSP.dNDRPGCore.data.PlayerData;
import org.MTJSP.dNDRPGCore.data.PlayerDataManager;
import org.MTJSP.dNDRPGCore.data.SkillData;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Collection;

public class WarriorSkills {

    public static class PassiveActionTide implements SkillManager.SkillExecutor {
        @Override
        public boolean execute(Player player, SkillData skillData) {
            // 被动技能，在玩家加入时应用
            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.FAST_DIGGING,
                    Integer.MAX_VALUE,
                    0, // 等级0对应20%攻击速度
                    false,
                    false
            ));
            return true;
        }
    }

    public static class BladeSweep implements SkillManager.SkillExecutor {
        @Override
        public boolean execute(Player player, SkillData skillData) {
            PlayerDataManager dataManager = RPGPlugin.getInstance().getPlayerDataManager();
            PlayerData data = dataManager.getPlayerData(player.getUniqueId());

            // 计算伤害
            double damage = 10 * skillData.getEffectValue("damage");

            // 获取前方扇形范围内的实体
            Collection<Entity> nearbyEntities = player.getWorld().getNearbyEntities(
                    player.getLocation(), 5, 2, 5
            );

            for (Entity entity : nearbyEntities) {
                if (entity instanceof Player && entity != player) {
                    // 对玩家造成伤害
                    ((Player) entity).damage(damage, player);
                }
            }

            // 视觉效果
            player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, player.getLocation(), 10);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.0f);

            return true;
        }
    }

    public static class RageRecovery implements SkillManager.SkillExecutor {
        @Override
        public boolean execute(Player player, SkillData skillData) {
            // 应用恢复效果
            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.REGENERATION,
                    60, // 3秒 * 20 ticks
                    0,
                    false,
                    false
            ));

            // 视觉效果
            player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 2, 0), 5);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

            return true;
        }
    }

    public static class ForwardSlash implements SkillManager.SkillExecutor {
        @Override
        public boolean execute(Player player, SkillData skillData) {
            // 向前冲刺
            Vector direction = player.getLocation().getDirection().normalize().multiply(1.5);
            player.setVelocity(direction);

            // 对路径上的敌人造成伤害
            PlayerDataManager dataManager = RPGPlugin.getInstance().getPlayerDataManager();
            PlayerData data = dataManager.getPlayerData(player.getUniqueId());
            double damage = 10 * skillData.getEffectValue("damage");

            // 视觉效果
            player.getWorld().spawnParticle(Particle.CRIT, player.getLocation(), 20);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.0f, 1.0f);

            return true;
        }
    }
}