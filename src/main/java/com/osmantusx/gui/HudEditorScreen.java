package com.osmantusx.gui;

import com.osmantusx.OsmanTusX;
import com.osmantusx.module.Module;
import com.osmantusx.module.hud.HudModule;
import com.osmantusx.util.render.Color;
import com.osmantusx.util.render.Render2DUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.gui.Click;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * Drag-and-drop editor for HUD elements. Every enabled HUD module is rendered
 * with a selection box; the user drags them to reposition and presses
 * Enter/Escape to finish. Positions persist through each element's hidden
 * X/Y settings.
 */
public final class HudEditorScreen extends Screen {

    private HudModule dragging;
    private double offsetX;
    private double offsetY;

    public HudEditorScreen() {
        super(Text.literal("HUD Editor"));
    }

    private List<HudModule> huds() {
        List<HudModule> list = new ArrayList<>();
        for (Module module : OsmanTusX.MODULES.getEnabled()) {
            if (module instanceof HudModule hud && module.isVisible()) {
                list.add(hud);
            }
        }
        return list;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Render2DUtil.blurBackdrop(context, this.width, this.height, 120);
        var theme = OsmanTusX.THEMES.getActive();

        for (HudModule hud : huds()) {
            hud.render(context, hud.getPixelX(), hud.getPixelY());
            Color box = hud == dragging ? theme.accent() : theme.accent().withAlpha(90);
            Render2DUtil.outline(context, hud.getPixelX() - 1, hud.getPixelY() - 1,
                    hud.getWidth() + 2, hud.getHeight() + 2, box);
        }

        Render2DUtil.centeredText(context, "Drag HUD elements  -  Enter / Esc to finish",
                this.width / 2.0, 6, theme.text());
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        List<HudModule> huds = huds();
        // Iterate in reverse so the topmost-drawn element wins overlaps.
        for (int i = huds.size() - 1; i >= 0; i--) {
            HudModule hud = huds.get(i);
            double x = hud.getPixelX();
            double y = hud.getPixelY();
            if (click.x() >= x && click.x() <= x + hud.getWidth()
                    && click.y() >= y && click.y() <= y + hud.getHeight()) {
                dragging = hud;
                offsetX = click.x() - x;
                offsetY = click.y() - y;
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        if (dragging != null) {
            dragging.setPixelPosition(click.x() - offsetX, click.y() - offsetY);
            return true;
        }
        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(Click click) {
        dragging = null;
        return super.mouseReleased(click);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.key() == GLFW.GLFW_KEY_ENTER || input.key() == GLFW.GLFW_KEY_ESCAPE) {
            this.close();
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
