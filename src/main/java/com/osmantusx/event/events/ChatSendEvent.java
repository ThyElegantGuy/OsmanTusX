package com.osmantusx.event.events;

import com.osmantusx.event.Event;

/**
 * Fired when the local player sends a chat message. The message is mutable so
 * modules (e.g. the command system, Better Chat) can rewrite or cancel it.
 */
public final class ChatSendEvent extends Event {

    private String message;

    public ChatSendEvent(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean isCancellable() {
        return true;
    }
}
