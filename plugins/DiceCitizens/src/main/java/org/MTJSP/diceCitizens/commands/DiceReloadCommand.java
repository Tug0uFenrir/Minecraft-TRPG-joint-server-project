package org.MTJSP.diceCitizens.commands;

import org.MTJSP.diceCitizens.DiceCitizensPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DiceReloadCommand implements CommandExecutor {

    private final DiceCitizensPlugin plugin;

    public DiceReloadCommand(DiceCitizensPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        plugin.reload();
        sender.sendMessage("§aCitizens骰子行为配置已重新加载");
        return true;
    }
}
