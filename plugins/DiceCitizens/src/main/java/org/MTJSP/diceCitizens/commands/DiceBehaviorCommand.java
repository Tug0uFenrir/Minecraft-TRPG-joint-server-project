package org.MTJSP.diceCitizens.commands;



import org.MTJSP.diceCitizens.DiceCitizensPlugin;
import org.MTJSP.diceCitizens.NPCBehaviorManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class DiceBehaviorCommand implements CommandExecutor {

    private final NPCBehaviorManager behaviorManager;

    public DiceBehaviorCommand(NPCBehaviorManager behaviorManager) {
        this.behaviorManager = behaviorManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("只有玩家可以使用此命令");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 4) {
            sendUsage(player);
            return true;
        }

        // 获取目标NPC
        NPC npc = CitizensAPI.getDefaultNPCSelector().getSelected(player);
        if (npc == null) {
            player.sendMessage("§c请先选择一个NPC");
            return true;
        }

        try {
            String diceSpec = args[0];
            String type = args[1];
            String target = args[2];
            int cooldown = args.length > 4 ? Integer.parseInt(args[3]) : 0;

            // 合并行为参数
            StringBuilder actionBuilder = new StringBuilder();
            for (int i = (cooldown > 0 ? 4 : 3); i < args.length; i++) {
                actionBuilder.append(args[i]).append(" ");
            }
            String action = actionBuilder.toString().trim();

            int diceValue = -1;
            String diceRange = "";

            if (diceSpec.contains("-")) {
                diceRange = diceSpec;
            } else {
                diceValue = Integer.parseInt(diceSpec);
            }

            // 创建行为
            NPCBehaviorManager.DiceBehavior behavior =
                    new NPCBehaviorManager.DiceBehavior(
                            diceValue, diceRange, action, type, target, cooldown
                    );

            // 添加行为
            if ("global".equalsIgnoreCase(args[0])) {
                behaviorManager.addGlobalBehavior(behavior);
                player.sendMessage("§a已添加全局骰子行为");
            } else {
                behaviorManager.addBehavior(npc.getId(), behavior);
                player.sendMessage("§a已为NPC §e" + npc.getName() + " §a添加骰子行为");
            }

            return true;
        } catch (NumberFormatException e) {
            player.sendMessage("§c无效的数字格式");
            sendUsage(player);
            return true;
        }
    }

    private void sendUsage(Player player) {
        player.sendMessage("§c用法: /dicebehavior <骰值|骰值范围|global> <类型> <目标> [冷却] <行为>");
        player.sendMessage("§e示例:");
        player.sendMessage("§7/dicebehavior 1 message player 你掷出了1点！");
        player.sendMessage("§7/dicebehavior 1-3 animation npc angry");
        player.sendMessage("§7/dicebehavior global command console say {npc}: 玩家 {player} 掷出了 {dice} 点！");
        player.sendMessage("§6可用类型: §emessage, command, animation, sound, effect");
        player.sendMessage("§6可用目标: §eplayer, npc, console, broadcast, area");
    }
}