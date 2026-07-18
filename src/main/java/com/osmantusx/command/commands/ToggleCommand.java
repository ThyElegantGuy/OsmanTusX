package com.osmantusx.command.commands;

import com.osmantusx.OsmanTusX;
import com.osmantusx.command.Command;
import com.osmantusx.module.Module;
import com.osmantusx.util.ChatUtil;

/** Toggles a module on or off by name. */
public final class ToggleCommand extends Command {

    public ToggleCommand() {
        super("toggle", "Toggles a module by name", "toggle <module>", "t");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            ChatUtil.info("Usage: " + OsmanTusX.COMMANDS.getPrefix() + getUsage());
            return;
        }
        String name = String.join(" ", args);
        Module module = OsmanTusX.MODULES.getByName(name);
        if (module == null) {
            ChatUtil.info("No module named '" + name + "'.");
            return;
        }
        module.toggle();
        ChatUtil.info(module.getName() + " is now " + (module.isEnabled() ? "\u00A7aON" : "\u00A7cOFF"));
    }
}
