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

import com.flyordie.code.browserapi.GeoFencing.GeofencingRegion;
import com.flyordie.code.browserapi.GeoFencing.CircularGeofencingRegionInit;
import com.flyordie.code.browserapi.Events.Event;

public class GeoFencing {

    private GeoFencing() {
        throw new Error("should not instantiate");
    }

    // Generated from modules\geofencing\GeofencingEvent.idl
    @Name("GeofencingEvent")
    public interface GeofencingEvent extends Event {
        @Getter String id();
        @Getter GeofencingRegion region();
    }

    // Generated from modules\geofencing\Geofencing.idl
    @Name("Geofencing")
    public interface Geofencing {
        Future<Object> registerRegion(GeofencingRegion region);
        Future<Object> unregisterRegion(String regionId);
        Future<Object> getRegisteredRegions();
    }

    // Generated from modules\geofencing\CircularGeofencingRegionInit.idl
    @Name("CircularGeofencingRegionInit")
    public static class CircularGeofencingRegionInit {
        public @Nullable String id = null;
        public double latitude;
        public double longitude;
        public double radius;
    }

    // Generated from modules\geofencing\CircularGeofencingRegion.idl
    @Name("CircularGeofencingRegion")
    public interface CircularGeofencingRegion extends GeofencingRegion {
        double MIN_RADIUS = 1.0;
        double MAX_RADIUS = 100.0;
        @Getter double latitude();
        @Getter double longitude();
        @Getter double radius();
    }

    @Statics("CircularGeofencingRegion")
    public interface CircularGeofencingRegions {
        CircularGeofencingRegion create(CircularGeofencingRegionInit init);
    }

    // Generated from modules\geofencing\GeofencingRegion.idl
    @Name("GeofencingRegion")
    public interface GeofencingRegion {
        @Getter String id();
    }

}
