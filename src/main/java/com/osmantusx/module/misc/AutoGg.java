package com.osmantusx.module.misc;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.setting.StringSetting;
import com.osmantusx.util.ChatUtil;

/** Sends a friendly "gg" message once when the local player dies. */
public final class AutoGg extends Module {

    private final StringSetting message = add(new StringSetting("Message", "Text to send", "gg"));

    private boolean sent;

    public AutoGg() {
        super("Auto GG", "Says gg on death", Category.MISC);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || player() == null) {
            return;
        }
        if (player().isDead() || player().getHealth() <= 0) {
            if (!sent) {
                ChatUtil.sendChat(message.get());
                sent = true;
            }
        } else {
            sent = false;
        }
    }
}
