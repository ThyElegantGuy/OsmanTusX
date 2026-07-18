package com.osmantusx.theme;

import com.osmantusx.util.render.Color;

/**
 * A named colour palette used by the GUI and HUD. Themes are intentionally
 * simple value objects so custom themes can be added by supplying a palette.
 */
public final class Theme {

    private final String name;
    private final Color accent;
    private final Color background;
    private final Color panel;
    private final Color text;
    private final Color textDim;

    public Theme(String name, Color accent, Color background, Color panel, Color text, Color textDim) {
        this.name = name;
        this.accent = accent;
        this.background = background;
        this.panel = panel;
        this.text = text;
        this.textDim = textDim;
    }

    public String getName() {
        return name;
    }

    public Color accent() {
        return accent;
    }

    public Color background() {
        return background;
    }

    public Color panel() {
        return panel;
    }

    public Color text() {
        return text;
    }

    public Color textDim() {
        return textDim;
    }
}
