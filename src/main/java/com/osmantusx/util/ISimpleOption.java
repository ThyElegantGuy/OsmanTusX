package com.osmantusx.util;

/**
 * Mixin-injected accessor that lets us set a {@link net.minecraft.client.option.SimpleOption}
 * value while bypassing its clamping/validation, so Fullbright can push gamma
 * far past the vanilla 1.0 cap (the same trick Wurst uses).
 */
public interface ISimpleOption<T> {

    void osman$forceSetValue(T value);
}
