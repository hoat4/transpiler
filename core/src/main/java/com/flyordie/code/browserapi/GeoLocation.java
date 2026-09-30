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

import com.flyordie.code.browserapi.GeoLocation.PositionError;
import com.flyordie.code.browserapi.GeoLocation.PositionCallback;
import com.flyordie.code.browserapi.GeoLocation.PositionErrorCallback;
import com.flyordie.code.browserapi.GeoLocation.PositionOptions;
import com.flyordie.code.browserapi.GeoLocation.Coordinates;
import com.flyordie.code.browserapi.GeoLocation.Geoposition;

public class GeoLocation {

    private GeoLocation() {
        throw new Error("should not instantiate");
    }

    // Generated from modules\geolocation\PositionErrorCallback.idl
    @FunctionalInterface
    @Name("PositionErrorCallback")
    public interface PositionErrorCallback {
        void handleEvent(PositionError error);
    }

    // Generated from modules\geolocation\PositionOptions.idl
    @Name("PositionOptions")
    public static class PositionOptions {
        public boolean enableHighAccuracy = false;
        public int timeout = -1;
        public int maximumAge = 0;
    }

    // Generated from modules\geolocation\Geolocation.idl
    @Name("Geolocation")
    public interface Geolocation {
        void getCurrentPosition(PositionCallback successCallback, PositionErrorCallback errorCallback, PositionOptions options);
        int watchPosition(PositionCallback successCallback, PositionErrorCallback errorCallback, PositionOptions options);
        void clearWatch(int watchID);
    }

    // Generated from modules\geolocation\Coordinates.idl
    @Name("Coordinates")
    public interface Coordinates {
        @Getter double latitude();
        @Getter double longitude();
        @Getter Optional<Double> altitude();
        @Getter double accuracy();
        @Getter Optional<Double> altitudeAccuracy();
        @Getter Optional<Double> heading();
        @Getter Optional<Double> speed();
    }

    // Generated from modules\geolocation\Geoposition.idl
    @Name("Geoposition")
    public interface Geoposition {
        @Getter Coordinates coords();
        @Getter int timestamp();
    }

    // Generated from modules\geolocation\PositionError.idl
    @Name("PositionError")
    public interface PositionError {
        short PERMISSION_DENIED = (short) 1;
        short POSITION_UNAVAILABLE = (short) 2;
        short TIMEOUT = (short) 3;
        @Getter short code();
        @Getter String message();
    }

    // Generated from modules\geolocation\PositionCallback.idl
    @FunctionalInterface
    @Name("PositionCallback")
    public interface PositionCallback {
        void handleEvent(Geoposition position);
    }

}
