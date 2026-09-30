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

import com.flyordie.code.browserapi.PermissionsAPI.PermissionDescriptor;
import com.flyordie.code.browserapi.Events.EventTarget;
import com.flyordie.code.browserapi.PermissionsAPI.PermissionStatus;

public class PermissionsAPI {

    private PermissionsAPI() {
        throw new Error("should not instantiate");
    }

    // Generated from modules\permissions\PushPermissionDescriptor.idl
    @Name("PushPermissionDescriptor")
    public static class PushPermissionDescriptor extends PermissionDescriptor {
        public boolean userVisibleOnly = false;
    }

    // Generated from modules\permissions\Permissions.idl
    @Name("Permissions")
    public interface Permissions {
        Future<PermissionStatus> query(Map<String, Object> permission);
        Future<PermissionStatus> request(Map<String, Object> permissions);
        Future<List<PermissionStatus>> request(List<Map<String, Object>> permissions);
        Future<PermissionStatus> revoke(Map<String, Object> permission);
    }

    // Generated from modules\permissions\PermissionStatus.idl
    @Name("PermissionStatus")
    public interface PermissionStatus extends EventTarget {
        @Getter PermissionState state();
        @Getter EventHandler onchange();
        @Setter void onchange(EventHandler v);
    }

    // Generated from modules\permissions\PermissionStatus.idl
    @Name("PermissionState")
    public enum PermissionState {
        granted, denied, prompt
    }

    // Generated from modules\permissions\PermissionDescriptor.idl
    @Name("PermissionDescriptor")
    public static class PermissionDescriptor {
        public PermissionName name;
    }

    // Generated from modules\permissions\MidiPermissionDescriptor.idl
    @Name("MidiPermissionDescriptor")
    public static class MidiPermissionDescriptor extends PermissionDescriptor {
        public boolean sysex = false;
    }

    // Generated from modules\permissions\PermissionDescriptor.idl
    @Name("PermissionName")
    public enum PermissionName {
        geolocation, midi, notifications, push
    }

}
