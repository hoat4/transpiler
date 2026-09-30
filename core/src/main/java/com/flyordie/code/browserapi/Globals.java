package com.flyordie.code.browserapi;

import com.flyordie.code.browserapi.FrameAPI.Window;

import java.util.ServiceLoader;

// ez az egyetlen osztály a packageben ami nem generálva lett, hanem kézzel írva
public class Globals {

    public static Window window() {
        return ServiceLoader.load(Window.class).findFirst().get();
    }

    public static WebSockets webSockets() {
        return ServiceLoader.load(WebSockets.class).findFirst().get();
    }

    // egyelőre csak ez a 2 van, mert ezeket használjuk
}
