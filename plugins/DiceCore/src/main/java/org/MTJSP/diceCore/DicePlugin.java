package org.MTJSP.diceCore;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class DicePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("插件已启用！");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("r")) {
            if (args.length == 0) {
                sender.sendMessage("§c用法: /r <骰子表达式>");
                sender.sendMessage("§e示例: /r 1d6, /r 2d20+5, /r (1d4+2)*3");
                return true;
            }

            try {
                String expression = String.join(" ", args);
                DiceExpressionParser parser = new DiceExpressionParser();
                DiceResult result = parser.parseAndEvaluate(expression);

                // 触发事件
                DiceRollEvent event = new DiceRollEvent(sender, expression, result);
                Bukkit.getPluginManager().callEvent(event);

                // 显示结果
                sendResultMessage(sender, result);
            } catch (IllegalArgumentException | ArithmeticException e) {
                sender.sendMessage("§c错误: " + e.getMessage());
            }
            return true;
        }
        return false;
    }

    private void sendResultMessage(CommandSender sender, DiceResult result) {
        StringBuilder message = new StringBuilder();
        message.append("§a骰子投掷结果: §e").append(result.getOriginalExpression()).append("\n");

        // 显示每个骰子的点数
        if (!result.getDiceRolls().isEmpty()) {
            message.append("§6骰子详情:\n");
            for (DiceRoll roll : result.getDiceRolls()) {
                message.append("§7- ")
                        .append(roll.getCount()).append("d").append(roll.getFaces())
                        .append(": [");

                int[] rolls = roll.getRolls();
                for (int i = 0; i < rolls.length; i++) {
                    if (i > 0) message.append(", ");
                    message.append(rolls[i]);
                }

                message.append("] = §b").append(roll.getTotal()).append("\n");
            }
        }

        // 显示最终结果
        message.append("§a最终结果: §e").append(result.getFinalResult());

        sender.sendMessage(message.toString());
    }
}
