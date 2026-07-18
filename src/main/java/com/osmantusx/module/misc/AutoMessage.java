package com.osmantusx.module.misc;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.setting.IntSetting;
import com.osmantusx.setting.StringSetting;
import com.osmantusx.util.ChatUtil;

/** Repeats a configurable chat message on a fixed interval. */
public final class AutoMessage extends Module {

    private final StringSetting message = add(new StringSetting("Message", "Text to send", "Powered by Osman Tus X"));
    private final IntSetting interval = add(new IntSetting("Interval", "Seconds between sends", 30, 5, 600));

    private long lastSend;

    public AutoMessage() {
        super("Auto Message", "Sends a message periodically", Category.MISC);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame()) {
            return;
        }
        if (System.currentTimeMillis() - lastSend >= interval.get() * 1000L) {
            ChatUtil.sendChat(message.get());
            lastSend = System.currentTimeMillis();
        }
    }
}
