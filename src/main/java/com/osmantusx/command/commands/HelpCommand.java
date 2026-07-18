package com.osmantusx.command.commands;

import com.osmantusx.OsmanTusX;
import com.osmantusx.command.Command;
import com.osmantusx.util.ChatUtil;

/** Lists all available commands. */
public final class HelpCommand extends Command {

    public HelpCommand() {
        super("help", "Lists available commands", "help", "?", "commands");
    }

    @Override
    public void execute(String[] args) {
        String prefix = OsmanTusX.COMMANDS.getPrefix();
        ChatUtil.info("Available commands:");
        for (Command command : OsmanTusX.COMMANDS.getCommands()) {
            ChatUtil.info("\u00A7b" + prefix + command.getUsage() + " \u00A77- " + command.getDescription());
        }
    }
}
