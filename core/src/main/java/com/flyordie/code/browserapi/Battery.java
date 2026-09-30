package com.flyordie.code.browserapi;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.Future;
import java.lang.invoke.MethodHandle;

import com.flyordie.code.jsinterop.Getter;
import com.flyordie.code.jsinterop.Setter;
import com.flyordie.code.jsinterop.Name;
import com.flyordie.code.jsinterop.Statics;

import com.flyordie.code.browserapi.Events.EventTarget;

public class Battery {

    private Battery() {
        throw new Error("should not instantiate");
    }

    // Generated from modules\battery\BatteryManager.idl
    @Name("BatteryManager")
    public interface BatteryManager extends EventTarget {
        @Getter boolean charging();
        @Getter double chargingTime();
        @Getter double dischargingTime();
        @Getter double level();
        @Getter EventHandler onchargingchange();
        @Setter void onchargingchange(EventHandler v);
        @Getter EventHandler onchargingtimechange();
        @Setter void onchargingtimechange(EventHandler v);
        @Getter EventHandler ondischargingtimechange();
        @Setter void ondischargingtimechange(EventHandler v);
        @Getter EventHandler onlevelchange();
        @Setter void onlevelchange(EventHandler v);
    }

}
