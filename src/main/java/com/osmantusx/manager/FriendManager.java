package com.osmantusx.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.osmantusx.OsmanTusX;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Tracks friend usernames (case-insensitive) so combat/render modules can avoid
 * targeting or highlighting trusted players. Persisted as a small JSON list.
 */
public final class FriendManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type SET_TYPE = new TypeToken<LinkedHashSet<String>>() {
    }.getType();

    private final Path file;
    private final Set<String> friends = new LinkedHashSet<>();

    public FriendManager() {
        this.file = FabricLoader.getInstance().getConfigDir().resolve(OsmanTusX.MOD_ID).resolve("friends.json");
        load();
    }

    public boolean isFriend(String name) {
        return name != null && friends.contains(name.toLowerCase());
    }

    public boolean isFriend(PlayerEntity player) {
        return player != null && isFriend(player.getGameProfile().name());
    }

    public boolean add(String name) {
        boolean added = friends.add(name.toLowerCase());
        if (added) {
            save();
        }
        return added;
    }

    public boolean remove(String name) {
        boolean removed = friends.remove(name.toLowerCase());
        if (removed) {
            save();
        }
        return removed;
    }

    public Set<String> getFriends() {
        return friends;
    }

    private void load() {
        if (!Files.exists(file)) {
            return;
        }
        try {
            Set<String> loaded = GSON.fromJson(Files.readString(file), SET_TYPE);
            if (loaded != null) {
                friends.addAll(loaded);
            }
        } catch (IOException e) {
            OsmanTusX.LOGGER.error("Failed to load friends", e);
        }
    }

    private void save() {
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, GSON.toJson(friends));
        } catch (IOException e) {
            OsmanTusX.LOGGER.error("Failed to save friends", e);
        }
    }
}
