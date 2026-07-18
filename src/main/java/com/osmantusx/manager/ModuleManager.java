package com.osmantusx.manager;

import com.osmantusx.OsmanTusX;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.module.ModuleRegistry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Owns the single instance of every module and provides lookups by name,
 * category and type. Population is delegated to {@link ModuleRegistry} to keep
 * the (large) instantiation list separate from the manager logic.
 */
public final class ModuleManager {

    private final List<Module> modules = new ArrayList<>();
    private final Map<String, Module> byName = new LinkedHashMap<>();
    private final Map<Class<? extends Module>, Module> byType = new LinkedHashMap<>();

    /** Instantiates and registers all modules. Called once during startup. */
    public void init() {
        for (Module module : ModuleRegistry.createAll()) {
            register(module);
        }
        OsmanTusX.LOGGER.info("Registered {} modules", modules.size());
    }

    private void register(Module module) {
        modules.add(module);
        byName.put(module.getName().toLowerCase(), module);
        byType.put(module.getClass(), module);
    }

    public List<Module> getModules() {
        return modules;
    }

    public List<Module> getByCategory(Category category) {
        List<Module> result = new ArrayList<>();
        for (Module module : modules) {
            if (module.getCategory() == category) {
                result.add(module);
            }
        }
        return result;
    }

    /** Toggles any module bound to the given GLFW key code. */
    public void onKeyPress(int key) {
        for (Module module : modules) {
            if (module.getKeyBind() == key) {
                module.toggle();
            }
        }
    }

    public List<Module> getEnabled() {
        List<Module> result = new ArrayList<>();
        for (Module module : modules) {
            if (module.isEnabled()) {
                result.add(module);
            }
        }
        return result;
    }

    public Module getByName(String name) {
        return byName.get(name.toLowerCase());
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T get(Class<T> type) {
        return (T) byType.get(type);
    }
}
