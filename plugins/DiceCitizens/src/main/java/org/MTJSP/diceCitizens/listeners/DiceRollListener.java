package org.MTJSP.diceCitizens.listeners;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.MTJSP.diceCitizens.NPCBehaviorManager;
import org.MTJSP.diceCore.DiceRollEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class DiceRollListener implements Listener {
    private final NPCBehaviorManager behaviorManager;
    public DiceRollListener(NPCBehaviorManager behaviorManager) {
        this.behaviorManager = behaviorManager;
    }
    // 注册监听事件
    @EventHandler
    public void onDiceRoll(DiceRollEvent event) {
        if (!(event.getSender() instanceof Player)) return;
        Player player = (Player) event.getSender();
        int diceValue=(int) Math.round(event.getResult().getFinalResult());

        // 检测最近是否存在NPC，如果有则执行所有相匹配的行为
        for (NPC npc : CitizensAPI.getNPCRegistry()) {
            if (!npc.isSpawned()) continue;

            // 检查距离
            if (npc.getEntity().getLocation().distance(player.getLocation()) > 10) continue;

            // 获取NPC的行为
            List<NPCBehaviorManager.DiceBehavior> behaviors =
                    behaviorManager.getBehaviorsForNPC(npc.getId(), diceValue);

            // 执行所有匹配的行为
            for (NPCBehaviorManager.DiceBehavior behavior : behaviors) {
                behavior.execute(player, npc, event.getResult());
            }
        }
    }
}
