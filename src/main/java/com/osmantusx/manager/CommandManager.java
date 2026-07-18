package com.osmantusx.manager;

import com.osmantusx.command.Command;
import com.osmantusx.command.commands.BindCommand;
import com.osmantusx.command.commands.ConfigCommand;
import com.osmantusx.command.commands.FriendCommand;
import com.osmantusx.command.commands.HelpCommand;
import com.osmantusx.command.commands.PrefixCommand;
import com.osmantusx.command.commands.ToggleCommand;
import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.ChatSendEvent;
import com.osmantusx.util.ChatUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses chat input beginning with the command prefix and dispatches it to the
 * matching {@link Command}. Registered on the event bus to intercept outgoing
 * chat messages.
 */
public final class CommandManager {

    private final List<Command> commands = new ArrayList<>();
    private String prefix = ".";

    public void init() {
        register(new HelpCommand());
        register(new ToggleCommand());
        register(new BindCommand());
        register(new FriendCommand());
        register(new ConfigCommand());
        register(new PrefixCommand());
    }

    private void register(Command command) {
        commands.add(command);
    }

    public List<Command> getCommands() {
        return commands;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        if (prefix != null && !prefix.isEmpty()) {
            this.prefix = prefix;
        }
    }

    public Command find(String label) {
        for (Command command : commands) {
            if (command.matches(label)) {
                return command;
            }
        }
        return null;
    }

    @EventHandler
    public void onChatSend(ChatSendEvent event) {
        String message = event.message();
        if (!message.startsWith(prefix)) {
            return;
        }
        event.cancel();

        String withoutPrefix = message.substring(prefix.length()).trim();
        if (withoutPrefix.isEmpty()) {
            ChatUtil.info("Type " + prefix + "help for a list of commands.");
            return;
        }

        String[] parts = withoutPrefix.split("\\s+");
        String label = parts[0];
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);

        Command command = find(label);
        if (command == null) {
            ChatUtil.info("Unknown command '" + label + "'. Try " + prefix + "help.");
            return;
        }
        try {
            command.execute(args);
        } catch (RuntimeException e) {
            ChatUtil.info("Error: " + e.getMessage());
        }
    }
}
