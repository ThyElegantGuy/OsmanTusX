package com.osmantusx.command.commands;

import com.osmantusx.OsmanTusX;
import com.osmantusx.command.Command;
import com.osmantusx.util.ChatUtil;

/** Manages the friend list. */
public final class FriendCommand extends Command {

    public FriendCommand() {
        super("friend", "Adds, removes or lists friends", "friend <add|remove|list> [name]", "f");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            ChatUtil.info("Usage: " + OsmanTusX.COMMANDS.getPrefix() + getUsage());
            return;
        }
        switch (args[0].toLowerCase()) {
            case "add" -> {
                if (args.length < 2) {
                    ChatUtil.info("Specify a name to add.");
                    return;
                }
                ChatUtil.info(OsmanTusX.FRIENDS.add(args[1])
                        ? "Added " + args[1] + " as a friend."
                        : args[1] + " is already a friend.");
            }
            case "remove", "del" -> {
                if (args.length < 2) {
                    ChatUtil.info("Specify a name to remove.");
                    return;
                }
                ChatUtil.info(OsmanTusX.FRIENDS.remove(args[1])
                        ? "Removed " + args[1] + " from friends."
                        : args[1] + " is not a friend.");
            }
            case "list" -> {
                if (OsmanTusX.FRIENDS.getFriends().isEmpty()) {
                    ChatUtil.info("You have no friends added.");
                } else {
                    ChatUtil.info("Friends: " + String.join(", ", OsmanTusX.FRIENDS.getFriends()));
                }
            }
            default -> ChatUtil.info("Usage: " + OsmanTusX.COMMANDS.getPrefix() + getUsage());
        }
    }
}
