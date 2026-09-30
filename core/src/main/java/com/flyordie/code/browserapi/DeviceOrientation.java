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

import com.flyordie.code.browserapi.DeviceOrientation.DeviceAcceleration;
import com.flyordie.code.browserapi.DeviceOrientation.DeviceRotationRate;
import com.flyordie.code.browserapi.Events.Event;

public class DeviceOrientation {

    private DeviceOrientation() {
        throw new Error("should not instantiate");
    }

    // Generated from modules\device_orientation\DeviceAcceleration.idl
    @Name("DeviceAcceleration")
    public interface DeviceAcceleration {
        @Getter Optional<Double> x();
        @Getter Optional<Double> y();
        @Getter Optional<Double> z();
    }

    // Generated from modules\device_orientation\DeviceRotationRate.idl
    @Name("DeviceRotationRate")
    public interface DeviceRotationRate {
        @Getter Optional<Double> alpha();
        @Getter Optional<Double> beta();
        @Getter Optional<Double> gamma();
    }

    // Generated from modules\device_orientation\DeviceMotionEvent.idl
    @Name("DeviceMotionEvent")
    public interface DeviceMotionEvent extends Event {
        @Getter DeviceAcceleration acceleration();
        @Getter DeviceAcceleration accelerationIncludingGravity();
        @Getter DeviceRotationRate rotationRate();
        @Getter Optional<Double> interval();
        void initDeviceMotionEvent(String type, boolean bubbles, boolean cancelable, DeviceAcceleration acceleration, DeviceAcceleration accelerationIncludingGravity, DeviceRotationRate rotationRate, double interval);
    }

    // Generated from modules\device_orientation\DeviceOrientationEvent.idl
    @Name("DeviceOrientationEvent")
    public interface DeviceOrientationEvent extends Event {
        @Getter Optional<Double> alpha();
        @Getter Optional<Double> beta();
        @Getter Optional<Double> gamma();
        @Getter Optional<Boolean> absolute();
        void initDeviceOrientationEvent(String type, boolean bubbles, boolean cancelable, @Nullable Double alpha, @Nullable Double beta, @Nullable Double gamma, @Nullable Boolean absolute);
    }

}
