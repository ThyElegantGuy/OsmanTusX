package com.osmantusx.module;

/**
 * Top-level grouping for modules, shown as draggable panels in the ClickGUI.
 * The {@code icon} is a single glyph used for the category header.
 */
public enum Category {
    COMBAT("Combat", "\u2694"),
    MOVEMENT("Movement", "\u21A8"),
    RENDER("Render", "\u25C9"),
    PLAYER("Player", "\u263A"),
    WORLD("World", "\u26F0"),
    MISC("Misc", "\u2699"),
    HUD("HUD", "\u25A4");

    private final String displayName;
    private final String icon;

    Category(String displayName, String icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIcon() {
        return icon;
    }
}
