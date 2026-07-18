package com.osmantusx.module.hud.modules;

import com.osmantusx.module.hud.TextHudModule;

/** Displays the biome the player is currently standing in. */
public final class BiomeHud extends TextHudModule {

    public BiomeHud() {
        super("Biome", "Shows the current biome", 0.01, 0.35);
    }

    @Override
    protected String getText() {
        if (mc.player == null || mc.world == null) {
            return "Biome: -";
        }
        String id = mc.world.getBiome(mc.player.getBlockPos()).getIdAsString();
        int colon = id.indexOf(':');
        String name = colon >= 0 ? id.substring(colon + 1) : id;
        return "Biome: " + name.replace('_', ' ');
    }
}
