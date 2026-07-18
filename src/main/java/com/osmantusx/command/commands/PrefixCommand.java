package com.osmantusx.command.commands;

import com.osmantusx.OsmanTusX;
import com.osmantusx.command.Command;
import com.osmantusx.util.ChatUtil;

/** Changes the command prefix. */
public final class PrefixCommand extends Command {

    public PrefixCommand() {
        super("prefix", "Changes the command prefix", "prefix <symbol>");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            ChatUtil.info("Current prefix is '" + OsmanTusX.COMMANDS.getPrefix() + "'.");
            return;
        }
        OsmanTusX.COMMANDS.setPrefix(args[0]);
        ChatUtil.info("Prefix set to '" + args[0] + "'.");
    }
}
