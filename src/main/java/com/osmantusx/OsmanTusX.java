package com.osmantusx;

import com.osmantusx.event.EventBus;
import com.osmantusx.event.events.Render2DEvent;
import com.osmantusx.event.events.Render3DEvent;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.gui.SideModuleList;
import com.osmantusx.manager.CommandManager;
import com.osmantusx.manager.ConfigManager;
import com.osmantusx.manager.FriendManager;
import com.osmantusx.manager.ModuleManager;
import com.osmantusx.manager.NotificationManager;
import com.osmantusx.manager.RotationManager;
import com.osmantusx.manager.SoundManager;
import com.osmantusx.manager.ThemeManager;
import com.osmantusx.util.TickRateTracker;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point and service locator for the Osman Tus X client.
 *
 * <p>All long-lived managers are exposed as {@code static final} singletons so
 * modules can reach shared services tersely. {@link #onInitializeClient()} wires
 * the managers, bridges Fabric API callbacks into the internal
 * {@link EventBus}, and loads the default config.</p>
 */
public final class OsmanTusX implements ClientModInitializer {

    public static final String MOD_ID = "osmantusx";
    public static final String NAME = "Osman Tus X";
    public static final String VERSION = "1.0.0";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    // --- Core services (order-independent construction) ----------------------
    public static final EventBus EVENT_BUS = new EventBus();
    public static final SoundManager SOUNDS = new SoundManager();
    public static final ThemeManager THEMES = new ThemeManager();
    public static final NotificationManager NOTIFICATIONS = new NotificationManager();
    public static final RotationManager ROTATIONS = new RotationManager();
    public static final FriendManager FRIENDS = new FriendManager();
    public static final ModuleManager MODULES = new ModuleManager();
    public static final ConfigManager CONFIG = new ConfigManager();
    public static final CommandManager COMMANDS = new CommandManager();

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing {} v{}", NAME, VERSION);

        // Build modules and commands.
        MODULES.init();
        COMMANDS.init();

        // Managers that react to events register themselves.
        EVENT_BUS.register(NOTIFICATIONS);
        EVENT_BUS.register(ROTATIONS);
        EVENT_BUS.register(COMMANDS);
        EVENT_BUS.register(new TickRateTracker());
        EVENT_BUS.register(new SideModuleList());

        registerFabricBridges();

        // Restore the last-used configuration.
        CONFIG.load(CONFIG.getCurrent());

        LOGGER.info("{} ready.", NAME);
    }

    /** Bridges Fabric API callbacks into the internal event bus. */
    private void registerFabricBridges() {
        ClientTickEvents.START_CLIENT_TICK.register(client ->
                EVENT_BUS.post(new TickEvent(TickEvent.Phase.PRE)));
        ClientTickEvents.END_CLIENT_TICK.register(client ->
                EVENT_BUS.post(new TickEvent(TickEvent.Phase.POST)));

        HudRenderCallback.EVENT.register((context, tickCounter) ->
                EVENT_BUS.post(new Render2DEvent(context, tickCounter.getTickProgress(false))));

        WorldRenderEvents.AFTER_ENTITIES.register(context ->
                EVENT_BUS.post(new Render3DEvent(context)));
    }
}
