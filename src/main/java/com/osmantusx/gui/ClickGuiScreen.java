package com.osmantusx.gui;

import com.osmantusx.OsmanTusX;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.setting.BooleanSetting;
import com.osmantusx.setting.ColorSetting;
import com.osmantusx.setting.DoubleSetting;
import com.osmantusx.setting.EnumSetting;
import com.osmantusx.setting.IntSetting;
import com.osmantusx.setting.KeybindSetting;
import com.osmantusx.setting.Setting;
import com.osmantusx.setting.StringSetting;
import com.osmantusx.theme.Theme;
import com.osmantusx.util.render.Color;
import com.osmantusx.util.render.Render2DUtil;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

/**
 * The main draggable ClickGUI. Each {@link Category} is a movable panel listing
 * its modules; right-clicking a module reveals its settings (toggles, sliders,
 * dropdowns, colour pickers and keybinds). A search bar filters modules and a
 * short fade animates opening.
 */
public final class ClickGuiScreen extends Screen {

    /** GLFW key that opens this screen (Right Shift by default). */
    public static final int DEFAULT_OPEN_KEY = GLFW.GLFW_KEY_RIGHT_SHIFT;

    private static final int PANEL_WIDTH = 124;
    private static final int HEADER_HEIGHT = 16;
    private static final int ROW_HEIGHT = 13;
    /** Sliders need extra height so the value label clears the track/knob. */
    private static final int SLIDER_ROW_HEIGHT = 18;

    private static final int SEARCH_WIDTH = 160;
    private static final int GEAR_SIZE = 14;
    /** Screen-space gap reserved at the top for the logo/search bar. */
    private static final int TOP_MARGIN = 24;
    /** Allowed range for the GUI render scale. */
    private static final double MIN_SCALE = 0.3;
    private static final double MAX_SCALE = 2.0;
    /** Where panels start inside the (scaled, top-margin-shifted) panel space. */
    private static final int PANEL_START_Y = 2;

    /** User-configurable open key and render scale, persisted across openings. */
    private static int openKey = DEFAULT_OPEN_KEY;
    private static double scale = 1.0;
    /** True once the user drags the scale slider; disables auto-fit on open. */
    private static boolean userAdjustedScale;
    /** When true, active module names are listed down the side edge in-game. */
    private static boolean showModulesSide;

    /** How the Array List and side module list order their entries. */
    public enum SortMode {
        LENGTH,
        ALPHABETICAL
    }

    private static SortMode sortMode = SortMode.LENGTH;

    /** Persisted across openings so panels keep their positions and expansion. */
    private static final List<Panel> PANELS = new ArrayList<>();
    private static final Set<Module> EXPANDED = new HashSet<>();

    private Panel draggingPanel;
    private double dragOffsetX;
    private double dragOffsetY;

    private Row activeSlider;
    private StringSetting editingString;
    private Module listeningModule;
    private KeybindSetting listeningKeybind;

    private boolean settingsOpen;
    private boolean listeningOpenKey;
    private boolean draggingScale;

    private String search = "";
    private boolean searchFocused;

    private long openTime;

    /** Description of the row currently under the cursor, drawn as a tooltip. */
    private String tooltip;

    /** @return the currently bound key that opens the ClickGUI. */
    public static int openKey() {
        return openKey;
    }

    /** @return whether the in-game side module list is enabled. */
    public static boolean showModulesSide() {
        return showModulesSide;
    }

    /** @return the active list ordering for the Array List and side list. */
    public static SortMode sortMode() {
        return sortMode;
    }

    /** Sorts modules by the active {@link SortMode}. */
    public static void sortModules(List<Module> modules) {
        if (sortMode == SortMode.ALPHABETICAL) {
            modules.sort(java.util.Comparator.comparing(Module::getDisplayName, String.CASE_INSENSITIVE_ORDER));
        } else {
            modules.sort(java.util.Comparator
                    .comparingInt((Module m) -> Render2DUtil.textWidth(m.getDisplayName())).reversed());
        }
    }

    public ClickGuiScreen() {
        super(Text.literal("Osman Tus X"));
    }

    @Override
    protected void init() {
        openTime = System.currentTimeMillis();
        if (PANELS.isEmpty()) {
            int i = 0;
            for (Category category : Category.values()) {
                PANELS.add(new Panel(category, 8 + i * (PANEL_WIDTH + 6), PANEL_START_Y));
                i++;
            }
        }
        if (!userAdjustedScale) {
            scale = computeFitScale();
        }
    }

    /** Picks a default scale so every category panel fits on screen. */
    private double computeFitScale() {
        double neededWidth = 8 + Category.values().length * (PANEL_WIDTH + 6) + 8;
        int maxModules = 1;
        for (Category category : Category.values()) {
            maxModules = Math.max(maxModules, OsmanTusX.MODULES.getByCategory(category).size());
        }
        double neededHeight = PANEL_START_Y + HEADER_HEIGHT + maxModules * ROW_HEIGHT + 8;
        double fit = Math.min(this.width / neededWidth, (this.height - TOP_MARGIN) / neededHeight);
        return Math.max(MIN_SCALE, Math.min(1.0, fit));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    // --- Rendering -----------------------------------------------------------

    private int searchX() {
        return this.width / 2 - (SEARCH_WIDTH + 4 + GEAR_SIZE) / 2;
    }

    private int gearX() {
        return searchX() + SEARCH_WIDTH + 4;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        float progress = Math.min(1f, (System.currentTimeMillis() - openTime) / 180f);
        Theme theme = OsmanTusX.THEMES.getActive();

        Render2DUtil.blurBackdrop(context, this.width, this.height, (int) (140 * progress));

        // Panels are drawn under a scale transform and shifted below the top bar;
        // the logo and search bar stay fixed size.
        tooltip = null;
        float s = (float) scale;
        context.getMatrices().pushMatrix();
        context.getMatrices().translate(0f, TOP_MARGIN);
        context.getMatrices().scale(s, s);
        double pmx = mouseX / scale;
        double pmy = (mouseY - TOP_MARGIN) / scale;
        for (Panel panel : PANELS) {
            renderPanel(context, panel, theme, pmx, pmy);
        }
        context.getMatrices().popMatrix();

        renderTitle(context, theme);
        renderSearchBar(context, theme);
        renderSettings(context, theme);
        renderTooltip(context, theme, mouseX, mouseY);
    }

    /** Draws the hovered element's description in a small box beside the cursor. */
    private void renderTooltip(DrawContext context, Theme theme, int mouseX, int mouseY) {
        if (tooltip == null || tooltip.isEmpty()) {
            return;
        }
        int textW = Render2DUtil.textWidth(tooltip);
        int boxW = textW + 8;
        int boxH = Render2DUtil.textHeight() + 6;
        int x = mouseX + 10;
        int y = mouseY + 10;
        if (x + boxW > this.width) {
            x = this.width - boxW - 2;
        }
        if (y + boxH > this.height) {
            y = this.height - boxH - 2;
        }
        Render2DUtil.roundedRect(context, x, y, boxW, boxH, 2, theme.background().withAlpha(240));
        Render2DUtil.outline(context, x, y, boxW, boxH, theme.accent());
        Render2DUtil.regularText(context, tooltip, x + 4, y + 4, theme.text());
    }

    /** Purple -> cyan -> pink gradient used for the client logo. */
    private static final Color[] LOGO_GRADIENT = {
            new Color(170, 90, 255, 255),
            new Color(0, 220, 255, 255),
            new Color(255, 95, 205, 255),
    };

    /**
     * Client logo: "Osman Tus X" drawn with a purple/cyan/pink gradient inside a
     * rounded panel, with the "X" enlarged to ~120% of the rest of the name.
     */
    private void renderTitle(DrawContext context, Theme theme) {
        String base = "Osman Tus ";
        float xScale = 1.2f;

        int baseH = Render2DUtil.textHeight();
        int xH = (int) Math.ceil(baseH * xScale);
        int baseW = Render2DUtil.textWidth(base);
        int xW = (int) Math.ceil(Render2DUtil.textWidth("X") * xScale);

        int padX = 6;
        int padY = 3;
        int panelX = 6;
        int panelY = 4;
        int panelW = baseW + xW + padX * 2;
        int panelH = xH + padY * 2;

        // Rounded panel with a thin accent border.
        Render2DUtil.roundedRect(context, panelX - 1, panelY - 1, panelW + 2, panelH + 2, 4,
                theme.accent().withAlpha(200));
        Render2DUtil.roundedRect(context, panelX, panelY, panelW, panelH, 4,
                theme.background().withAlpha(235));

        int total = base.length() + 1;
        double tx = panelX + padX;
        double baseY = panelY + padY + (xH - baseH) / 2.0;
        for (int i = 0; i < base.length(); i++) {
            String ch = String.valueOf(base.charAt(i));
            Render2DUtil.text(context, ch, tx, baseY, Render2DUtil.gradient((double) i / (total - 1), LOGO_GRADIENT));
            tx += Render2DUtil.textWidth(ch);
        }

        // Enlarged "X" at the end of the gradient (pink).
        context.getMatrices().pushMatrix();
        context.getMatrices().translate((float) tx, (float) (panelY + padY));
        context.getMatrices().scale(xScale, xScale);
        Render2DUtil.text(context, "X", 0, 0, Render2DUtil.gradient(1.0, LOGO_GRADIENT));
        context.getMatrices().popMatrix();
    }

    private void renderSearchBar(DrawContext context, Theme theme) {
        int x = searchX();
        int y = 4;
        Render2DUtil.roundedRect(context, x, y, SEARCH_WIDTH, 14, 2, theme.panel().withAlpha(230));
        if (searchFocused) {
            Render2DUtil.outline(context, x, y, SEARCH_WIDTH, 14, theme.accent());
        }
        String shown = search.isEmpty() && !searchFocused ? "Search modules..." : search + (searchFocused ? "_" : "");
        Color textColor = search.isEmpty() && !searchFocused ? theme.textDim() : theme.text();
        Render2DUtil.text(context, shown, x + 5, y + 3, textColor);

        // Gear button.
        int gx = gearX();
        Render2DUtil.roundedRect(context, gx, y, GEAR_SIZE, 14, 2,
                settingsOpen ? theme.accent() : theme.panel().withAlpha(230));
        Render2DUtil.centeredText(context, "\u2699", gx + GEAR_SIZE / 2.0, y + 3, theme.text());
    }

    private void renderSettings(DrawContext context, Theme theme) {
        if (!settingsOpen) {
            return;
        }
        int w = 150;
        int x = gearX() + GEAR_SIZE - w;
        int y = 20;
        int h = 90;
        Render2DUtil.roundedRect(context, x, y, w, h, 2, theme.background().withAlpha(240));
        Render2DUtil.outline(context, x, y, w, h, theme.accent());

        // Hotkey row.
        String keyName = listeningOpenKey ? "..."
                : (openKey == GLFW.GLFW_KEY_UNKNOWN ? "None" : keyLabel(openKey));
        Render2DUtil.text(context, "GUI Hotkey", x + 6, y + 5, theme.text());
        Render2DUtil.text(context, keyName, x + w - 6 - Render2DUtil.textWidth(keyName), y + 5, theme.accent());

        // Scale slider.
        Render2DUtil.text(context, "Scale: " + String.format("%.2f", scale), x + 6, y + 20, theme.text());
        double barX = x + 6;
        double barW = w - 12;
        double frac = (scale - MIN_SCALE) / (MAX_SCALE - MIN_SCALE);
        Render2DUtil.rect(context, barX, y + 34, barW, 2, theme.textDim().withAlpha(120));
        Render2DUtil.rect(context, barX, y + 34, barW * frac, 2, theme.accent());
        Render2DUtil.rect(context, barX + barW * frac - 1, y + 32, 2, 6, theme.text());

        // Side module list toggle.
        Render2DUtil.text(context, "Modules on side", x + 6, y + 48, theme.text());
        Render2DUtil.rect(context, x + w - 15, y + 48, 8, 8, showModulesSide ? theme.accent() : theme.textDim());

        // Sort mode cycle.
        String sortLabel = sortMode == SortMode.ALPHABETICAL ? "A-Z" : "Length";
        Render2DUtil.text(context, "Sort modules", x + 6, y + 62, theme.text());
        Render2DUtil.text(context, sortLabel, x + w - 6 - Render2DUtil.textWidth(sortLabel), y + 62, theme.accent());

        // HUD editor button.
        Render2DUtil.roundedRect(context, x + 6, y + 75, w - 12, 11, 2, theme.panel());
        Render2DUtil.centeredText(context, "Edit HUD", x + w / 2.0, y + 77, theme.text());
    }

    private static String keyLabel(int key) {
        String name = GLFW.glfwGetKeyName(key, 0);
        return name != null ? name.toUpperCase() : "Key " + key;
    }

    private void renderPanel(DrawContext context, Panel panel, Theme theme, double mouseX, double mouseY) {
        // Header.
        Render2DUtil.roundedRect(context, panel.x, panel.y, PANEL_WIDTH, HEADER_HEIGHT, 3, theme.accent());
        Render2DUtil.text(context, panel.category.getIcon() + "  " + panel.category.getDisplayName(),
                panel.x + 6, panel.y + 4, Color.WHITE);

        if (panel.collapsed) {
            return;
        }

        List<Row> rows = buildRows(panel);
        Row lastRow = rows.isEmpty() ? null : rows.get(rows.size() - 1);
        double bottom = lastRow == null ? panel.y + HEADER_HEIGHT + 2 : lastRow.y + lastRow.height;
        Render2DUtil.rect(context, panel.x, panel.y + HEADER_HEIGHT, PANEL_WIDTH,
                bottom - (panel.y + HEADER_HEIGHT), theme.background().withAlpha(235));

        for (Row row : rows) {
            renderRow(context, row, theme, mouseX, mouseY);
        }
    }

    private void renderRow(DrawContext context, Row row, Theme theme, double mouseX, double mouseY) {
        boolean hovered = mouseX >= row.x && mouseX <= row.x + PANEL_WIDTH
                && mouseY >= row.y && mouseY <= row.y + row.height;
        if (hovered) {
            Render2DUtil.rect(context, row.x, row.y, PANEL_WIDTH, row.height, theme.panel().withAlpha(120));
            tooltip = descriptionFor(row);
        }
        double tx = row.x + (row.kind == Row.Kind.MODULE ? 6 : 12);
        double ty = row.y + 3;

        switch (row.kind) {
            case MODULE -> {
                Color c = row.module.isEnabled() ? theme.accent() : theme.textDim();
                Render2DUtil.regularText(context, row.module.getName(), tx, ty, c);
                if (!row.module.getSettings().isEmpty()) {
                    Render2DUtil.regularText(context, EXPANDED.contains(row.module) ? "-" : "+",
                            row.x + PANEL_WIDTH - 10, ty, theme.text());
                }
            }
            case BOOL -> {
                Render2DUtil.regularText(context, row.label, tx, ty, theme.text());
                boolean on = ((BooleanSetting) row.setting).get();
                Color box = on ? theme.accent() : theme.textDim();
                Render2DUtil.rect(context, row.x + PANEL_WIDTH - 16, row.y + 3, 7, 7, box);
            }
            case ENUM -> {
                Render2DUtil.regularText(context, row.label, tx, ty, theme.text());
                Render2DUtil.regularText(context, String.valueOf(row.setting.get()),
                        row.x + PANEL_WIDTH - 12 - Render2DUtil.textWidth(String.valueOf(row.setting.get())), ty,
                        theme.accent());
            }
            case STRING -> {
                Render2DUtil.regularText(context, row.label, tx, ty, theme.text());
                String value = editingString == row.setting ? row.setting.get() + "_" : String.valueOf(row.setting.get());
                Render2DUtil.regularText(context, value,
                        row.x + PANEL_WIDTH - 12 - Render2DUtil.textWidth(value), ty, theme.accent());
            }
            case KEYBIND -> {
                Render2DUtil.regularText(context, row.label, tx, ty, theme.text());
                String key = listeningKeybind == row.setting || (row.module != null && listeningModule == row.module)
                        ? "..." : row.keyText;
                Render2DUtil.regularText(context, key,
                        row.x + PANEL_WIDTH - 12 - Render2DUtil.textWidth(key), ty, theme.accent());
            }
            case RAINBOW -> {
                Render2DUtil.regularText(context, row.label, tx, ty, theme.text());
                boolean on = ((ColorSetting) row.setting).isRainbow();
                Render2DUtil.rect(context, row.x + PANEL_WIDTH - 16, row.y + 3, 7, 7,
                        on ? theme.accent() : theme.textDim());
            }
            case SLIDER -> {
                double frac = (row.get.getAsDouble() - row.min) / (row.max - row.min);
                frac = Math.max(0, Math.min(1, frac));
                double barX = row.x + 8;
                double barW = PANEL_WIDTH - 16;
                double barY = row.y + 13;
                Render2DUtil.regularText(context, row.label + ": " + row.valueText(), tx, row.y + 2, theme.text());
                Render2DUtil.rect(context, barX, barY, barW, 2, theme.textDim().withAlpha(120));
                Render2DUtil.rect(context, barX, barY, barW * frac, 2, theme.accent());
                Render2DUtil.rect(context, barX + barW * frac - 1, barY - 2, 2, 6, theme.text());
            }
        }
    }

    /** @return the description shown when hovering the given row, or null. */
    private String descriptionFor(Row row) {
        if (row.kind == Row.Kind.MODULE && row.module != null) {
            return row.module.getDescription();
        }
        if (row.kind == Row.Kind.KEYBIND && row.setting == null && row.module != null) {
            return "Key that toggles " + row.module.getName();
        }
        if (row.setting != null) {
            return row.setting.getDescription();
        }
        return null;
    }

    // --- Layout --------------------------------------------------------------

    private List<Row> buildRows(Panel panel) {
        List<Row> rows = new ArrayList<>();
        double y = panel.y + HEADER_HEIGHT;
        for (Module module : OsmanTusX.MODULES.getByCategory(panel.category)) {
            if (!search.isEmpty() && !module.getName().toLowerCase().contains(search.toLowerCase())) {
                continue;
            }
            Row moduleRow = new Row(Row.Kind.MODULE, panel.x, y);
            moduleRow.module = module;
            rows.add(moduleRow);
            y += ROW_HEIGHT;

            if (!EXPANDED.contains(module)) {
                continue;
            }
            // Optional per-module keybind row.
            Row bind = new Row(Row.Kind.KEYBIND, panel.x, y);
            bind.module = module;
            bind.label = "Bind";
            bind.keyText = module.getKeyBind() == GLFW.GLFW_KEY_UNKNOWN
                    ? "None" : GLFW.glfwGetKeyName(module.getKeyBind(), 0);
            if (bind.keyText == null) {
                bind.keyText = "Key " + module.getKeyBind();
            }
            rows.add(bind);
            y += ROW_HEIGHT;

            for (Setting<?> setting : module.getSettings()) {
                if (!setting.isVisible()) {
                    continue;
                }
                y = addSettingRows(rows, panel, setting, y);
            }
        }
        return rows;
    }

    private double addSettingRows(List<Row> rows, Panel panel, Setting<?> setting, double y) {
        if (setting instanceof BooleanSetting) {
            rows.add(labelled(Row.Kind.BOOL, panel.x, y, setting));
            return y + ROW_HEIGHT;
        }
        if (setting instanceof IntSetting is) {
            Row row = labelled(Row.Kind.SLIDER, panel.x, y, setting);
            row.height = SLIDER_ROW_HEIGHT;
            row.min = is.getMin();
            row.max = is.getMax();
            row.get = () -> is.get();
            row.set = v -> is.set((int) Math.round(v));
            row.integer = true;
            rows.add(row);
            return y + row.height;
        }
        if (setting instanceof DoubleSetting ds) {
            Row row = labelled(Row.Kind.SLIDER, panel.x, y, setting);
            row.height = SLIDER_ROW_HEIGHT;
            row.min = ds.getMin();
            row.max = ds.getMax();
            row.get = () -> ds.get();
            row.set = ds::set;
            rows.add(row);
            return y + row.height;
        }
        if (setting instanceof EnumSetting<?>) {
            rows.add(labelled(Row.Kind.ENUM, panel.x, y, setting));
            return y + ROW_HEIGHT;
        }
        if (setting instanceof StringSetting) {
            rows.add(labelled(Row.Kind.STRING, panel.x, y, setting));
            return y + ROW_HEIGHT;
        }
        if (setting instanceof KeybindSetting ks) {
            Row row = labelled(Row.Kind.KEYBIND, panel.x, y, setting);
            row.keyText = ks.getKeyName();
            rows.add(row);
            return y + ROW_HEIGHT;
        }
        if (setting instanceof ColorSetting cs) {
            Row rainbow = labelled(Row.Kind.RAINBOW, panel.x, y, setting);
            rainbow.label = setting.getName() + " Rainbow";
            rows.add(rainbow);
            y += ROW_HEIGHT;
            if (!cs.isRainbow()) {
                y = addColorChannel(rows, panel, cs, y, "R", 0);
                y = addColorChannel(rows, panel, cs, y, "G", 1);
                y = addColorChannel(rows, panel, cs, y, "B", 2);
            }
            return y;
        }
        return y;
    }

    private double addColorChannel(List<Row> rows, Panel panel, ColorSetting cs, double y, String label, int channel) {
        Row row = new Row(Row.Kind.SLIDER, panel.x, y);
        row.height = SLIDER_ROW_HEIGHT;
        row.setting = cs;
        row.label = label;
        row.min = 0;
        row.max = 255;
        row.integer = true;
        row.get = () -> switch (channel) {
            case 0 -> cs.get().r();
            case 1 -> cs.get().g();
            default -> cs.get().b();
        };
        row.set = v -> {
            Color c = cs.get();
            int iv = (int) Math.round(v);
            cs.set(switch (channel) {
                case 0 -> new Color(iv, c.g(), c.b(), c.a());
                case 1 -> new Color(c.r(), iv, c.b(), c.a());
                default -> new Color(c.r(), c.g(), iv, c.a());
            });
        };
        rows.add(row);
        return y + row.height;
    }

    private Row labelled(Row.Kind kind, double x, double y, Setting<?> setting) {
        Row row = new Row(kind, x, y);
        row.setting = setting;
        row.label = setting.getName();
        return row;
    }

    // --- Input ---------------------------------------------------------------

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double rawX = click.x();
        double rawY = click.y();
        int button = click.button();

        // Top bar (drawn unscaled) uses raw coordinates.
        int sx = searchX();
        if (rawX >= sx && rawX <= sx + SEARCH_WIDTH && rawY >= 4 && rawY <= 18) {
            searchFocused = true;
            return true;
        }
        searchFocused = false;

        int gx = gearX();
        if (rawX >= gx && rawX <= gx + GEAR_SIZE && rawY >= 4 && rawY <= 18) {
            settingsOpen = !settingsOpen;
            return true;
        }
        if (settingsOpen && handleSettingsClick(rawX, rawY)) {
            return true;
        }

        // Panels are scaled and shifted, so convert the cursor into panel space.
        double mx = rawX / scale;
        double my = (rawY - TOP_MARGIN) / scale;

        for (Panel panel : PANELS) {
            // Header: drag with left, collapse with right.
            if (mx >= panel.x && mx <= panel.x + PANEL_WIDTH && my >= panel.y && my <= panel.y + HEADER_HEIGHT) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                    panel.collapsed = !panel.collapsed;
                } else {
                    draggingPanel = panel;
                    dragOffsetX = mx - panel.x;
                    dragOffsetY = my - panel.y;
                }
                return true;
            }
            if (panel.collapsed) {
                continue;
            }
            for (Row row : buildRows(panel)) {
                if (my < row.y || my > row.y + row.height || mx < row.x || mx > row.x + PANEL_WIDTH) {
                    continue;
                }
                handleRowClick(row, button, mx);
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    /** Handles clicks inside the settings popup. Returns true when consumed. */
    private boolean handleSettingsClick(double rawX, double rawY) {
        int w = 150;
        int x = gearX() + GEAR_SIZE - w;
        int y = 20;
        if (rawX < x || rawX > x + w || rawY < y || rawY > y + 90) {
            return false;
        }
        if (rawY >= y + 4 && rawY <= y + 16) {
            listeningOpenKey = true;
        } else if (rawY >= y + 30 && rawY <= y + 40) {
            draggingScale = true;
            updateScale(rawX);
        } else if (rawY >= y + 46 && rawY <= y + 58) {
            showModulesSide = !showModulesSide;
        } else if (rawY >= y + 60 && rawY <= y + 72) {
            sortMode = sortMode == SortMode.LENGTH ? SortMode.ALPHABETICAL : SortMode.LENGTH;
        } else if (rawY >= y + 75 && rawY <= y + 86) {
            net.minecraft.client.MinecraftClient.getInstance().setScreen(new HudEditorScreen());
        }
        return true;
    }

    private void updateScale(double rawX) {
        int w = 150;
        int x = gearX() + GEAR_SIZE - w;
        double barX = x + 6;
        double barW = w - 12;
        double frac = Math.max(0, Math.min(1, (rawX - barX) / barW));
        scale = MIN_SCALE + frac * (MAX_SCALE - MIN_SCALE);
        userAdjustedScale = true;
    }

    private void handleRowClick(Row row, int button, double mx) {
        switch (row.kind) {
            case MODULE -> {
                if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                    if (EXPANDED.contains(row.module)) {
                        EXPANDED.remove(row.module);
                    } else {
                        EXPANDED.add(row.module);
                    }
                } else {
                    row.module.toggle();
                }
            }
            case BOOL -> ((BooleanSetting) row.setting).toggle();
            case ENUM -> ((EnumSetting<?>) row.setting).cycle();
            case STRING -> editingString = (StringSetting) row.setting;
            case RAINBOW -> {
                ColorSetting cs = (ColorSetting) row.setting;
                cs.setRainbow(!cs.isRainbow());
            }
            case KEYBIND -> {
                if (row.module != null) {
                    listeningModule = row.module;
                    listeningKeybind = null;
                } else {
                    listeningKeybind = (KeybindSetting) row.setting;
                    listeningModule = null;
                }
            }
            case SLIDER -> {
                activeSlider = row;
                updateSlider(row, mx);
            }
        }
    }

    private void updateSlider(Row row, double mx) {
        double barX = row.x + 8;
        double barW = PANEL_WIDTH - 16;
        double frac = Math.max(0, Math.min(1, (mx - barX) / barW));
        row.set.accept(row.min + frac * (row.max - row.min));
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        if (draggingScale) {
            updateScale(click.x());
            return true;
        }
        if (draggingPanel != null) {
            draggingPanel.x = click.x() / scale - dragOffsetX;
            draggingPanel.y = (click.y() - TOP_MARGIN) / scale - dragOffsetY;
            return true;
        }
        if (activeSlider != null) {
            updateSlider(activeSlider, click.x() / scale);
            return true;
        }
        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean mouseReleased(Click click) {
        draggingPanel = null;
        activeSlider = null;
        draggingScale = false;
        return super.mouseReleased(click);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        int key = input.key();
        if (listeningOpenKey) {
            openKey = key == GLFW.GLFW_KEY_ESCAPE ? GLFW.GLFW_KEY_UNKNOWN : key;
            listeningOpenKey = false;
            return true;
        }
        if (listeningKeybind != null) {
            listeningKeybind.set(key == GLFW.GLFW_KEY_ESCAPE ? GLFW.GLFW_KEY_UNKNOWN : key);
            listeningKeybind = null;
            return true;
        }
        if (listeningModule != null) {
            listeningModule.setKeyBind(key == GLFW.GLFW_KEY_ESCAPE ? GLFW.GLFW_KEY_UNKNOWN : key);
            listeningModule = null;
            return true;
        }
        if (editingString != null) {
            if (key == GLFW.GLFW_KEY_BACKSPACE) {
                String current = editingString.get();
                if (!current.isEmpty()) {
                    editingString.set(current.substring(0, current.length() - 1));
                }
                return true;
            }
            if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_ESCAPE) {
                editingString = null;
                return true;
            }
            return true;
        }
        if (searchFocused) {
            if (key == GLFW.GLFW_KEY_BACKSPACE && !search.isEmpty()) {
                search = search.substring(0, search.length() - 1);
                return true;
            }
            if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_ESCAPE) {
                searchFocused = false;
                return true;
            }
        }
        if (key == GLFW.GLFW_KEY_ESCAPE || key == openKey) {
            close();
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean charTyped(CharInput input) {
        String typed = input.asString();
        if (editingString != null) {
            editingString.set(editingString.get() + typed);
            return true;
        }
        if (searchFocused) {
            search += typed;
            return true;
        }
        return super.charTyped(input);
    }

    // --- Data holders --------------------------------------------------------

    private static final class Panel {
        final Category category;
        double x;
        double y;
        boolean collapsed;

        Panel(Category category, double x, double y) {
            this.category = category;
            this.x = x;
            this.y = y;
        }
    }

    private static final class Row {
        enum Kind { MODULE, BOOL, ENUM, STRING, KEYBIND, RAINBOW, SLIDER }

        final Kind kind;
        final double x;
        final double y;

        Module module;
        Setting<?> setting;
        String label = "";
        String keyText = "None";

        double height = ROW_HEIGHT;
        DoubleSupplier get;
        DoubleConsumer set;
        double min;
        double max;
        boolean integer;

        Row(Kind kind, double x, double y) {
            this.kind = kind;
            this.x = x;
            this.y = y;
        }

        String valueText() {
            double v = get.getAsDouble();
            return integer ? String.valueOf((int) Math.round(v)) : String.format("%.2f", v);
        }
    }
}
