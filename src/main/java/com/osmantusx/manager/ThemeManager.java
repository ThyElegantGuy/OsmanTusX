package com.osmantusx.manager;

import com.osmantusx.theme.Theme;
import com.osmantusx.util.render.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the available themes and the currently selected one. Also provides a
 * global animated "rainbow" accent that colour settings can opt into.
 */
public final class ThemeManager {

    private final List<Theme> themes = new ArrayList<>();
    private Theme active;

    public ThemeManager() {
        themes.add(new Theme("Midnight",
                new Color(120, 90, 255),
                new Color(14, 15, 22, 235),
                new Color(24, 26, 36, 240),
                new Color(235, 236, 245),
                new Color(150, 155, 170)));
        themes.add(new Theme("Ocean",
                new Color(60, 190, 255),
                new Color(10, 18, 26, 235),
                new Color(18, 30, 42, 240),
                new Color(230, 240, 245),
                new Color(140, 165, 180)));
        themes.add(new Theme("Ember",
                new Color(255, 120, 60),
                new Color(22, 14, 12, 235),
                new Color(34, 22, 20, 240),
                new Color(245, 238, 235),
                new Color(180, 150, 140)));
        themes.add(new Theme("Matrix",
                new Color(60, 230, 120),
                new Color(8, 14, 10, 235),
                new Color(14, 24, 18, 240),
                new Color(230, 245, 235),
                new Color(130, 175, 150)));
        this.active = themes.get(0);
    }

    public List<Theme> getThemes() {
        return themes;
    }

    public Theme getActive() {
        return active;
    }

    public void setActive(Theme theme) {
        this.active = theme;
    }

    public void setActive(String name) {
        for (Theme theme : themes) {
            if (theme.getName().equalsIgnoreCase(name)) {
                this.active = theme;
                return;
            }
        }
    }

    /** @return the theme accent, or a time-based rainbow colour when requested. */
    public Color accent() {
        return active.accent();
    }

    /** Smooth rainbow colour cycling roughly every 5 seconds, shared globally. */
    public Color rainbow(int offset) {
        float hue = ((System.currentTimeMillis() + offset * 4L) % 5000L) / 5000.0f;
        return Color.fromHsb(hue, 0.7f, 1.0f, 255);
    }
}
