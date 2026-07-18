package com.osmantusx.command;

import com.osmantusx.util.Wrapper;

import java.util.List;

/** Base type for chat commands handled by the {@link CommandManager}. */
public abstract class Command implements Wrapper {

    private final String name;
    private final String description;
    private final String usage;
    private final List<String> aliases;

    protected Command(String name, String description, String usage, String... aliases) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.aliases = List.of(aliases);
    }

    /** Executes the command with the arguments following the command name. */
    public abstract void execute(String[] args);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public boolean matches(String label) {
        if (name.equalsIgnoreCase(label)) {
            return true;
        }
        for (String alias : aliases) {
            if (alias.equalsIgnoreCase(label)) {
                return true;
            }
        }
        return false;
    }
}
