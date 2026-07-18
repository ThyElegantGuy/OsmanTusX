package com.osmantusx.mixin;

import com.osmantusx.util.ISimpleOption;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Consumer;

/** Bypasses {@link SimpleOption}'s value clamping (used by Fullbright's gamma). */
@Mixin(SimpleOption.class)
public class SimpleOptionMixin<T> implements ISimpleOption<T> {

    @Shadow
    private T value;

    @Shadow
    @Final
    private Consumer<T> changeCallback;

    @Override
    public void osman$forceSetValue(T value) {
        this.value = value;
        this.changeCallback.accept(value);
    }
}
