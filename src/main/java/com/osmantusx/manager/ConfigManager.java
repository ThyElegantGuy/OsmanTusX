package com.osmantusx.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.osmantusx.OsmanTusX;
import com.osmantusx.module.Module;
import com.osmantusx.setting.Setting;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Persists module state (enabled flag, keybind, favourite, per-setting values)
 * to JSON config profiles under {@code config/osmantusx/configs/}.
 *
 * <p>Multiple named profiles are supported; the {@code default} profile is
 * loaded on startup and saved on shutdown.</p>
 */
public final class ConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String DEFAULT = "default";

    private final Path root;
    private final Path configsDir;
    private String current = DEFAULT;

    public ConfigManager() {
        this.root = FabricLoader.getInstance().getConfigDir().resolve(OsmanTusX.MOD_ID);
        this.configsDir = root.resolve("configs");
        createDirectories();
    }

    public String getCurrent() {
        return current;
    }

    public Path getRoot() {
        return root;
    }

    private void createDirectories() {
        try {
            Files.createDirectories(configsDir);
        } catch (IOException e) {
            OsmanTusX.LOGGER.error("Could not create config directory", e);
        }
    }

    private Path configFile(String name) {
        return configsDir.resolve(name + ".json");
    }

    /** @return the names of all saved config profiles. */
    public List<String> listConfigs() {
        List<String> names = new ArrayList<>();
        try (Stream<Path> stream = Files.list(configsDir)) {
            stream.filter(p -> p.toString().endsWith(".json"))
                    .forEach(p -> {
                        String file = p.getFileName().toString();
                        names.add(file.substring(0, file.length() - ".json".length()));
                    });
        } catch (IOException e) {
            OsmanTusX.LOGGER.error("Could not list configs", e);
        }
        return names;
    }

    public void save() {
        save(current);
    }

    /** Serializes all modules to the named profile. */
    public void save(String name) {
        JsonObject root = new JsonObject();
        JsonObject modules = new JsonObject();
        for (Module module : OsmanTusX.MODULES.getModules()) {
            modules.add(module.getName(), serializeModule(module));
        }
        root.add("modules", modules);

        try {
            Files.writeString(configFile(name), GSON.toJson(root));
            OsmanTusX.LOGGER.info("Saved config '{}'", name);
        } catch (IOException e) {
            OsmanTusX.LOGGER.error("Failed to save config '{}'", name, e);
        }
    }

    /** Loads the named profile into all modules, if it exists. */
    public void load(String name) {
        Path file = configFile(name);
        if (!Files.exists(file)) {
            OsmanTusX.LOGGER.info("Config '{}' not found, using defaults", name);
            return;
        }
        try {
            JsonObject root = GSON.fromJson(Files.readString(file), JsonObject.class);
            if (root == null || !root.has("modules")) {
                return;
            }
            JsonObject modules = root.getAsJsonObject("modules");
            for (Module module : OsmanTusX.MODULES.getModules()) {
                if (modules.has(module.getName())) {
                    deserializeModule(module, modules.getAsJsonObject(module.getName()));
                }
            }
            this.current = name;
            OsmanTusX.LOGGER.info("Loaded config '{}'", name);
        } catch (IOException e) {
            OsmanTusX.LOGGER.error("Failed to load config '{}'", name, e);
        }
    }

    public void delete(String name) {
        try {
            Files.deleteIfExists(configFile(name));
        } catch (IOException e) {
            OsmanTusX.LOGGER.error("Failed to delete config '{}'", name, e);
        }
    }

    private JsonObject serializeModule(Module module) {
        JsonObject object = new JsonObject();
        object.addProperty("enabled", module.isEnabled());
        object.addProperty("keybind", module.getKeyBind());
        object.addProperty("favorite", module.isFavorite());
        object.addProperty("visible", module.isVisible());

        JsonObject settings = new JsonObject();
        for (Setting<?> setting : module.getSettings()) {
            settings.add(setting.getName(), setting.toJson());
        }
        object.add("settings", settings);
        return object;
    }

    private void deserializeModule(Module module, JsonObject object) {
        if (object.has("keybind")) {
            module.setKeyBind(object.get("keybind").getAsInt());
        }
        if (object.has("favorite")) {
            module.setFavorite(object.get("favorite").getAsBoolean());
        }
        if (object.has("visible")) {
            module.setVisible(object.get("visible").getAsBoolean());
        }
        if (object.has("settings")) {
            JsonObject settings = object.getAsJsonObject("settings");
            for (Setting<?> setting : module.getSettings()) {
                if (settings.has(setting.getName())) {
                    setting.fromJson(settings.get(setting.getName()));
                }
            }
        }
        // Apply enabled last so onEnable sees the restored settings.
        if (object.has("enabled")) {
            module.setEnabled(object.get("enabled").getAsBoolean());
        }
    }
}
