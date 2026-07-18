package com.osmantusx.module;

import com.osmantusx.module.combat.AutoClicker;
import com.osmantusx.module.combat.Criticals;
import com.osmantusx.module.combat.KillAura;
import com.osmantusx.module.combat.Reach;
import com.osmantusx.module.combat.TriggerBot;
import com.osmantusx.module.combat.Velocity;
import com.osmantusx.module.hud.modules.ArmorHud;
import com.osmantusx.module.hud.modules.ArrayListHud;
import com.osmantusx.module.hud.modules.BiomeHud;
import com.osmantusx.module.hud.modules.ClockHud;
import com.osmantusx.module.hud.modules.CompassHud;
import com.osmantusx.module.hud.modules.CoordinatesHud;
import com.osmantusx.module.hud.modules.CpsHud;
import com.osmantusx.module.hud.modules.DirectionHud;
import com.osmantusx.module.hud.modules.DurabilityHud;
import com.osmantusx.module.hud.modules.EntityCounterHud;
import com.osmantusx.module.hud.modules.FpsHud;
import com.osmantusx.module.hud.modules.HotbarOverlayHud;
import com.osmantusx.module.hud.modules.InventoryHud;
import com.osmantusx.module.hud.modules.KeystrokesHud;
import com.osmantusx.module.hud.modules.MemoryHud;
import com.osmantusx.module.hud.modules.NotificationsHud;
import com.osmantusx.module.hud.modules.PingHud;
import com.osmantusx.module.hud.modules.PotionEffectsHud;
import com.osmantusx.module.hud.modules.SessionTimeHud;
import com.osmantusx.module.hud.modules.SpeedHud;
import com.osmantusx.module.hud.modules.TargetHud;
import com.osmantusx.module.hud.modules.TotemCounterHud;
import com.osmantusx.module.hud.modules.TpsHud;
import com.osmantusx.module.hud.modules.WatermarkHud;
import com.osmantusx.module.misc.AntiAfk;
import com.osmantusx.module.misc.AutoEat;
import com.osmantusx.module.misc.AutoFish;
import com.osmantusx.module.misc.AutoGg;
import com.osmantusx.module.misc.AutoMessage;
import com.osmantusx.module.misc.AutoReconnect;
import com.osmantusx.module.misc.AutoRespawn;
import com.osmantusx.module.misc.AutoSprint;
import com.osmantusx.module.misc.AutoTool;
import com.osmantusx.module.misc.AutoWalk;
import com.osmantusx.module.misc.BetterChat;
import com.osmantusx.module.misc.ChatTweaks;
import com.osmantusx.module.misc.FakePlayer;
import com.osmantusx.module.misc.FastBreak;
import com.osmantusx.module.misc.FastPlace;
import com.osmantusx.module.misc.Freecam;
import com.osmantusx.module.misc.InventoryMove;
import com.osmantusx.module.misc.MiddleClickFriend;
import com.osmantusx.module.misc.NameProtect;
import com.osmantusx.module.misc.NoRotate;
import com.osmantusx.module.misc.NoteBot;
import com.osmantusx.module.misc.ScreenshotViewer;
import com.osmantusx.module.misc.Spammer;
import com.osmantusx.module.misc.Timer;
import com.osmantusx.module.misc.Zoom;
import com.osmantusx.module.movement.AirJump;
import com.osmantusx.module.movement.ElytraHelper;
import com.osmantusx.module.movement.Flight;
import com.osmantusx.module.movement.HighJump;
import com.osmantusx.module.movement.LongJump;
import com.osmantusx.module.movement.NoSlow;
import com.osmantusx.module.movement.SafeWalk;
import com.osmantusx.module.movement.Speed;
import com.osmantusx.module.movement.Sprint;
import com.osmantusx.module.movement.Step;
import com.osmantusx.module.player.AutoArmor;
import com.osmantusx.module.player.AutoCraft;
import com.osmantusx.module.player.AutoTotem;
import com.osmantusx.module.player.ChestStealer;
import com.osmantusx.module.player.FastUse;
import com.osmantusx.module.player.InventoryCleaner;
import com.osmantusx.module.player.Refill;
import com.osmantusx.module.render.BlockEsp;
import com.osmantusx.module.render.Chams;
import com.osmantusx.module.render.Esp;
import com.osmantusx.module.render.Fullbright;
import com.osmantusx.module.render.ItemEsp;
import com.osmantusx.module.render.NameTags;
import com.osmantusx.module.render.NoFog;
import com.osmantusx.module.render.NoHurtCam;
import com.osmantusx.module.render.ShulkerPreview;
import com.osmantusx.module.render.StorageEsp;
import com.osmantusx.module.render.Tracers;
import com.osmantusx.module.render.Waypoints;
import com.osmantusx.module.render.XRay;
import com.osmantusx.module.world.BlockFinder;
import com.osmantusx.module.world.FastMine;
import com.osmantusx.module.world.Nuker;
import com.osmantusx.module.world.Scaffold;
import com.osmantusx.module.world.WaypointManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Central list of every module instance in the client. Kept separate from
 * {@link com.osmantusx.manager.ModuleManager} so the manager stays focused on
 * lookups while this file simply enumerates the available features.
 */
public final class ModuleRegistry {

    private ModuleRegistry() {
    }

    public static List<Module> createAll() {
        List<Module> m = new ArrayList<>();

        // --- Combat ----------------------------------------------------------
        m.add(new KillAura());
        m.add(new AutoClicker());
        m.add(new TriggerBot());
        m.add(new Criticals());
        m.add(new Reach());
        m.add(new Velocity());

        // --- Movement --------------------------------------------------------
        m.add(new Sprint());
        m.add(new Step());
        m.add(new SafeWalk());
        m.add(new NoSlow());
        m.add(new Speed());
        m.add(new LongJump());
        m.add(new HighJump());
        m.add(new Flight());
        m.add(new ElytraHelper());
        m.add(new AirJump());

        // --- Render ----------------------------------------------------------
        m.add(new Fullbright());
        m.add(new Esp());
        m.add(new Tracers());
        m.add(new StorageEsp());
        m.add(new ItemEsp());
        m.add(new BlockEsp());
        m.add(new NameTags());
        m.add(new NoHurtCam());
        m.add(new NoFog());
        m.add(new Chams());
        m.add(new ShulkerPreview());
        m.add(new Waypoints());
        m.add(new XRay());

        // --- Player ----------------------------------------------------------
        m.add(new AutoArmor());
        m.add(new AutoTotem());
        m.add(new ChestStealer());
        m.add(new InventoryCleaner());
        m.add(new FastUse());
        m.add(new Refill());
        m.add(new AutoCraft());

        // --- World -----------------------------------------------------------
        m.add(new Nuker());
        m.add(new Scaffold());
        m.add(new FastMine());
        m.add(new BlockFinder());
        m.add(new WaypointManager());

        // --- Misc ------------------------------------------------------------
        m.add(new AutoFish());
        m.add(new AutoEat());
        m.add(new AutoRespawn());
        m.add(new AutoReconnect());
        m.add(new AutoGg());
        m.add(new ChatTweaks());
        m.add(new BetterChat());
        m.add(new NameProtect());
        m.add(new AntiAfk());
        m.add(new FastPlace());
        m.add(new FastBreak());
        m.add(new NoRotate());
        m.add(new AutoTool());
        m.add(new AutoSprint());
        m.add(new AutoWalk());
        m.add(new InventoryMove());
        m.add(new MiddleClickFriend());
        m.add(new FakePlayer());
        m.add(new Timer());
        m.add(new Freecam());
        m.add(new Zoom());
        m.add(new AutoMessage());
        m.add(new Spammer());
        m.add(new NoteBot());
        m.add(new ScreenshotViewer());

        // --- HUD -------------------------------------------------------------
        m.add(new FpsHud());
        m.add(new CpsHud());
        m.add(new PingHud());
        m.add(new CoordinatesHud());
        m.add(new DirectionHud());
        m.add(new ArmorHud());
        m.add(new InventoryHud());
        m.add(new PotionEffectsHud());
        m.add(new KeystrokesHud());
        m.add(new WatermarkHud());
        m.add(new SpeedHud());
        m.add(new ClockHud());
        m.add(new SessionTimeHud());
        m.add(new TpsHud());
        m.add(new EntityCounterHud());
        m.add(new MemoryHud());
        m.add(new BiomeHud());
        m.add(new CompassHud());
        m.add(new TargetHud());
        m.add(new NotificationsHud());
        m.add(new ArrayListHud());
        m.add(new HotbarOverlayHud());
        m.add(new TotemCounterHud());
        m.add(new DurabilityHud());

        return m;
    }
}
