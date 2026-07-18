package com.osmantusx.gui;

import com.osmantusx.OsmanTusX;
import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.Render2DEvent;
import com.osmantusx.module.Module;
import com.osmantusx.module.hud.HudModule;
import com.osmantusx.util.Wrapper;
import com.osmantusx.util.render.Color;
import com.osmantusx.util.render.Render2DUtil;
import net.minecraft.client.gui.DrawContext;

import java.util.List;

/**
 * Optional in-game list of active modules pinned to the right edge of the
 * screen (so it doesn't overlap the corner HUD elements). Unlike the Array
 * List HUD element this has no background panel: it is just the "Osman Tus X"
 * sign followed by the plain module names. Toggled from the ClickGUI settings
 * popup ("Modules on side").
 */
public final class SideModuleList implements Wrapper {

    private static final String LOGO = "OsmanTus";
    private static final String LOGO_X = "X";
    private static final double MARGIN = 4;
    private static final double LINE_HEIGHT = 11;

    /** Purple -> cyan gradient stops used for the logo, matching the ClickGUI. */
    private static final Color LOGO_A = new Color(170, 90, 255);
    private static final Color LOGO_B = new Color(80, 200, 255);

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        if (!ClickGuiScreen.showModulesSide() || mc.player == null) {
            return;
        }
        DrawContext context = event.context();
        double screenRight = mc.getWindow().getScaledWidth() - MARGIN;

        double y = MARGIN;
        drawLogo(context, screenRight, y);
        y += Render2DUtil.textHeight() + 4;

        List<Module> modules = activeModules();
        int index = 0;
        for (Module module : modules) {
            String name = module.getDisplayName();
            Color color = OsmanTusX.THEMES.rainbow(index * 40);
            Render2DUtil.text(context, name, screenRight - Render2DUtil.textWidth(name), y, color);
            y += LINE_HEIGHT;
            index++;
        }
    }

    /** Draws a right-aligned, per-character gradient "OsmanTus X" logo with a bold X. */
    private void drawLogo(DrawContext context, double right, double y) {
        String full = LOGO + " " + LOGO_X;
        double totalWidth = Render2DUtil.textWidth(full);
        double x = right - totalWidth;
        for (int i = 0; i < LOGO.length(); i++) {
            String ch = String.valueOf(LOGO.charAt(i));
            Color c = LOGO_A.lerp(LOGO_B, i / (float) Math.max(1, LOGO.length() - 1));
            Render2DUtil.text(context, ch, x, y, c);
            x += Render2DUtil.textWidth(ch);
        }
        x += Render2DUtil.textWidth(" ");
        // Standout X: bright accent + a soft glow underlay.
        Render2DUtil.text(context, LOGO_X, x + 0.5, y, LOGO_B.withAlpha(150));
        Render2DUtil.text(context, LOGO_X, x, y, new Color(255, 255, 255));
    }

    private List<Module> activeModules() {
        List<Module> modules = OsmanTusX.MODULES.getEnabled();
        modules.removeIf(module -> !module.isVisible() || module instanceof HudModule);
        ClickGuiScreen.sortModules(modules);
        return modules;
    }
}
