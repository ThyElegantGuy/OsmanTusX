package com.osmantusx.command.commands;

import com.osmantusx.OsmanTusX;
import com.osmantusx.command.Command;
import com.osmantusx.module.Module;
import com.osmantusx.util.ChatUtil;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/** Binds or clears a keybind for a module. */
public final class BindCommand extends Command {

    public BindCommand() {
        super("bind", "Binds a module to a key", "bind <module> <key|none>", "keybind");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            ChatUtil.info("Usage: " + OsmanTusX.COMMANDS.getPrefix() + getUsage());
            return;
        }
        String key = args[args.length - 1];
        String name = String.join(" ", java.util.Arrays.copyOf(args, args.length - 1));

        Module module = OsmanTusX.MODULES.getByName(name);
        if (module == null) {
            ChatUtil.info("No module named '" + name + "'.");
            return;
        }

        if (key.equalsIgnoreCase("none")) {
            module.setKeyBind(GLFW.GLFW_KEY_UNKNOWN);
            ChatUtil.info("Cleared bind for " + module.getName() + ".");
            return;
        }

        try {
            int code = InputUtil.fromTranslationKey("key.keyboard." + key.toLowerCase()).getCode();
            module.setKeyBind(code);
            ChatUtil.info("Bound " + module.getName() + " to " + key.toUpperCase() + ".");
        } catch (RuntimeException e) {
            ChatUtil.info("Unknown key '" + key + "'.");
        }
    }
}
