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

import java.util.Comparator;
import java.util.List;

/**
 * Optional in-game list of active modules pinned to the left edge of the
 * screen. Unlike the Array List HUD element this has no background panel: it is
 * just the "Osman Tus X" sign followed by the plain module names. Toggled from
 * the ClickGUI settings popup ("Modules on side").
 */
public final class SideModuleList implements Wrapper {

    private static final String SIGN = "Osman Tus X";
    private static final double MARGIN = 4;
    private static final double LINE_HEIGHT = 11;

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        if (!ClickGuiScreen.showModulesSide() || mc.player == null) {
            return;
        }
        DrawContext context = event.context();

        double y = MARGIN;
        Render2DUtil.text(context, SIGN, MARGIN, y, OsmanTusX.THEMES.accent());
        y += Render2DUtil.textHeight() + 3;

        List<Module> modules = activeModules();
        int index = 0;
        for (Module module : modules) {
            Color color = OsmanTusX.THEMES.rainbow(index * 40);
            Render2DUtil.text(context, module.getDisplayName(), MARGIN, y, color);
            y += LINE_HEIGHT;
            index++;
        }
    }

    private List<Module> activeModules() {
        List<Module> modules = OsmanTusX.MODULES.getEnabled();
        modules.removeIf(module -> !module.isVisible() || module instanceof HudModule);
        modules.sort(Comparator.comparing(Module::getDisplayName, String.CASE_INSENSITIVE_ORDER));
        return modules;
    }
}
