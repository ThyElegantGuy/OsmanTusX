package com.osmantusx.manager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

/**
 * Centralises the small UI sound effects the client plays (module toggles,
 * button clicks). A single toggle lets the user mute all client sounds.
 */
public final class SoundManager {

    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void click() {
        play(1.0f);
    }

    public void enableSound() {
        play(1.2f);
    }

    public void disableSound() {
        play(0.8f);
    }

    private void play(float pitch) {
        if (!enabled) {
            return;
        }
        mc.getSoundManager().play(
                PositionedSoundInstance.ui(SoundEvents.UI_BUTTON_CLICK, pitch));
    }
}
