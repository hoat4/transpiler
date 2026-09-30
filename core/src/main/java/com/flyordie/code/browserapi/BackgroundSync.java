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

import com.flyordie.code.browserapi.ServiceWorkers.ExtendableEventInit;
import com.flyordie.code.browserapi.BackgroundSync.PeriodicSyncRegistrationOptions;
import com.flyordie.code.browserapi.BackgroundSync.SyncRegistration;
import com.flyordie.code.browserapi.ServiceWorkers.ExtendableEvent;
import com.flyordie.code.browserapi.BackgroundSync.SyncEventInit;
import com.flyordie.code.browserapi.BackgroundSync.SyncRegistrationOptions;
import com.flyordie.code.browserapi.BackgroundSync.PeriodicSyncRegistration;
import com.flyordie.code.browserapi.BackgroundSync.PeriodicSyncEventInit;

public class BackgroundSync {

    private BackgroundSync() {
        throw new Error("should not instantiate");
    }

    // Generated from modules\background_sync\SyncEventInit.idl
    @Name("SyncEventInit")
    public static class SyncEventInit extends ExtendableEventInit {
        public SyncRegistration registration;
    }

    // Generated from modules\background_sync\PeriodicSyncEventInit.idl
    @Name("PeriodicSyncEventInit")
    public static class PeriodicSyncEventInit extends ExtendableEventInit {
        public PeriodicSyncRegistration registration;
    }

    // Generated from modules\background_sync\SyncRegistrationOptions.idl
    @Name("SyncRegistrationOptions")
    public static class SyncRegistrationOptions {
        public String tag;
    }

    // Generated from modules\background_sync\PeriodicSyncRegistration.idl
    @Name("SyncPowerState")
    public enum SyncPowerState {
        auto, avoidDraining
    }

    // Generated from modules\background_sync\PeriodicSyncRegistration.idl
    @Name("PeriodicSyncRegistration")
    public interface PeriodicSyncRegistration {
        @Getter String tag();
        @Getter int minPeriod();
        @Getter SyncNetworkState networkState();
        @Getter SyncPowerState powerState();
        Future<Boolean> unregister();
    }

    // Generated from modules\background_sync\PeriodicSyncRegistrationOptions.idl
    @Name("PeriodicSyncRegistrationOptions")
    public static class PeriodicSyncRegistrationOptions {
        public String tag;
        public int minPeriod = 0;
        public SyncNetworkState networkState = SyncNetworkState.online;
        public SyncPowerState powerState = SyncPowerState.auto;
    }

    // Generated from modules\background_sync\SyncManager.idl
    @Name("SyncManager")
    public interface SyncManager {
        Future<SyncRegistration> register(SyncRegistrationOptions options);
        Future<SyncRegistration> getRegistration(String tag);
        Future<List<SyncRegistration>> getRegistrations();
        Future<SyncPermissionState> permissionState();
    }

    // Generated from modules\background_sync\SyncEvent.idl
    @Name("SyncEvent")
    public interface SyncEvent extends ExtendableEvent {
        @Getter SyncRegistration registration();
    }

    @Statics("SyncEvent")
    public interface SyncEvents {
        SyncEvent create(String type, SyncEventInit init);
    }

    // Generated from modules\background_sync\SyncManager.idl
    @Name("SyncPermissionState")
    public enum SyncPermissionState {
        default_, denied, granted
    }

    // Generated from modules\background_sync\SyncRegistration.idl
    @Name("SyncRegistration")
    public interface SyncRegistration {
        @Getter String tag();
        @Getter Future<Boolean> done();
        Future<Boolean> unregister();
    }

    // Generated from modules\background_sync\PeriodicSyncEvent.idl
    @Name("PeriodicSyncEvent")
    public interface PeriodicSyncEvent extends ExtendableEvent {
        @Getter PeriodicSyncRegistration registration();
    }

    @Statics("PeriodicSyncEvent")
    public interface PeriodicSyncEvents {
        PeriodicSyncEvent create(String type, PeriodicSyncEventInit init);
    }

    // Generated from modules\background_sync\PeriodicSyncRegistration.idl
    @Name("SyncNetworkState")
    public enum SyncNetworkState {
        any, avoidCellular, online
    }

    // Generated from modules\background_sync\PeriodicSyncManager.idl
    @Name("PeriodicSyncManager")
    public interface PeriodicSyncManager {
        Future<PeriodicSyncRegistration> register(PeriodicSyncRegistrationOptions options);
        Future<PeriodicSyncRegistration> getRegistration(String tag);
        Future<List<PeriodicSyncRegistration>> getRegistrations();
        Future<SyncPermissionState> permissionState();
        @Getter int minPossiblePeriod();
    }

}
