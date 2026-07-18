package com.osmantusx.mixin;

import com.osmantusx.module.misc.BetterChat;
import com.osmantusx.module.misc.ChatTweaks;
import com.osmantusx.module.misc.NameProtect;
import com.osmantusx.util.ChatProcessor;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Applies the chat-oriented modules to incoming messages. */
@Mixin(ChatHud.class)
public class ChatHudMixin {

    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"), argsOnly = true)
    private Text osman$process(Text message) {
        if (!NameProtect.isActive() && !BetterChat.isActive() && !ChatTweaks.isActive()) {
            return message;
        }
        return Text.literal(ChatProcessor.process(message.getString()));
    }
}
