package com.flyordie.code.browserapi;

import com.flyordie.code.browserapi.Events.Event;
import com.flyordie.code.jsinterop.Name;

@FunctionalInterface
public interface EventHandler {

    Object handleEvent(Event event);
}
