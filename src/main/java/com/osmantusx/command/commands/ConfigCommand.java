package com.osmantusx.command.commands;

import com.osmantusx.OsmanTusX;
import com.osmantusx.command.Command;
import com.osmantusx.util.ChatUtil;

/** Saves, loads and lists config profiles. */
public final class ConfigCommand extends Command {

    public ConfigCommand() {
        super("config", "Manages config profiles", "config <save|load|list|delete> [name]", "cfg");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            ChatUtil.info("Usage: " + OsmanTusX.COMMANDS.getPrefix() + getUsage());
            return;
        }
        switch (args[0].toLowerCase()) {
            case "save" -> {
                String name = args.length > 1 ? args[1] : OsmanTusX.CONFIG.getCurrent();
                OsmanTusX.CONFIG.save(name);
                ChatUtil.info("Saved config '" + name + "'.");
            }
            case "load" -> {
                if (args.length < 2) {
                    ChatUtil.info("Specify a config name to load.");
                    return;
                }
                OsmanTusX.CONFIG.load(args[1]);
                ChatUtil.info("Loaded config '" + args[1] + "'.");
            }
            case "list" -> ChatUtil.info("Configs: " + String.join(", ", OsmanTusX.CONFIG.listConfigs()));
            case "delete" -> {
                if (args.length < 2) {
                    ChatUtil.info("Specify a config name to delete.");
                    return;
                }
                OsmanTusX.CONFIG.delete(args[1]);
                ChatUtil.info("Deleted config '" + args[1] + "'.");
            }
            default -> ChatUtil.info("Usage: " + OsmanTusX.COMMANDS.getPrefix() + getUsage());
        }
    }
}
