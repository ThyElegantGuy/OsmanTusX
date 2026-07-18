package com.osmantusx.module.misc;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.setting.IntSetting;
import com.osmantusx.setting.StringSetting;
import com.osmantusx.util.ChatUtil;

/**
 * Repeatedly sends a message for private/single-player testing. A counter is
 * appended so servers do not silently drop identical consecutive messages.
 */
public final class Spammer extends Module {

    private final StringSetting message = add(new StringSetting("Message", "Text to spam", "Osman Tus X"));
    private final IntSetting delay = add(new IntSetting("Delay", "Seconds between sends", 3, 1, 60));

    private long lastSend;
    private int counter;

    public Spammer() {
        super("Spammer", "Chat spammer for private testing", Category.MISC);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame()) {
            return;
        }
        if (System.currentTimeMillis() - lastSend >= delay.get() * 1000L) {
            ChatUtil.sendChat(message.get() + " [" + (++counter) + "]");
            lastSend = System.currentTimeMillis();
        }
    }
}
