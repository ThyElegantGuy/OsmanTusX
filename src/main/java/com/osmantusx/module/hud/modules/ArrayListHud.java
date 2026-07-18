package com.osmantusx.module.hud.modules;

import com.osmantusx.OsmanTusX;
import com.osmantusx.module.Module;
import com.osmantusx.module.hud.HudModule;
import com.osmantusx.util.render.Color;
import com.osmantusx.util.render.Render2DUtil;
import net.minecraft.client.gui.DrawContext;

import java.util.Comparator;
import java.util.List;

/** Lists all enabled modules, sorted by width, with a rainbow accent bar. */
public final class ArrayListHud extends HudModule {

    public ArrayListHud() {
        super("Array List", "Lists active modules", 0.99, 0.01);
    }

    private List<Module> activeModules() {
        List<Module> modules = OsmanTusX.MODULES.getEnabled();
        modules.removeIf(module -> !module.isVisible() || module instanceof HudModule);
        modules.sort(Comparator.comparingInt((Module m) -> Render2DUtil.textWidth(m.getDisplayName())).reversed());
        return modules;
    }

    @Override
    public void render(DrawContext context, double x, double y) {
        List<Module> modules = activeModules();
        double screenRight = getPixelX() + getWidth();
        int index = 0;
        double lineY = y;
        for (Module module : modules) {
            String name = module.getDisplayName();
            double textW = Render2DUtil.textWidth(name);
            double left = screenRight - textW - 6;
            Render2DUtil.rect(context, left - 2, lineY, textW + 6, 11, new Color(18, 20, 28, 190));
            Render2DUtil.rect(context, screenRight, lineY, 2, 11, OsmanTusX.THEMES.rainbow(index * 40));
            Render2DUtil.text(context, name, left, lineY + 2, OsmanTusX.THEMES.getActive().text());
            lineY += 11;
            index++;
        }
    }

    @Override
    public double getWidth() {
        double max = 40;
        for (Module module : activeModules()) {
            max = Math.max(max, Render2DUtil.textWidth(module.getDisplayName()) + 8);
        }
        return max;
    }

    @Override
    public double getHeight() {
        return Math.max(11, activeModules().size() * 11);
    }
}
