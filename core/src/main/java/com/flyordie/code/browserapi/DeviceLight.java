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

import com.flyordie.code.browserapi.Events.EventInit;
import com.flyordie.code.browserapi.Events.Event;
import com.flyordie.code.browserapi.DeviceLight.DeviceLightEventInit;

public class DeviceLight {

    private DeviceLight() {
        throw new Error("should not instantiate");
    }

    // Generated from modules\device_light\DeviceLightEvent.idl
    @Name("DeviceLightEvent")
    public interface DeviceLightEvent extends Event {
        @Getter double value();
    }

    @Statics("DeviceLightEvent")
    public interface DeviceLightEvents {
        DeviceLightEvent create(String type, DeviceLightEventInit eventInitDict);
    }

    // Generated from modules\device_light\DeviceLightEventInit.idl
    @Name("DeviceLightEventInit")
    public static class DeviceLightEventInit extends EventInit {
        public double value;
    }

}
