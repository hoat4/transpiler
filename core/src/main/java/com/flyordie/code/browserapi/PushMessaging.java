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
import com.flyordie.code.browserapi.PushMessaging.PushEventInit;
import com.flyordie.code.browserapi.DOM.ArrayBufferView;
import com.flyordie.code.browserapi.FileAPI.Blob;
import com.flyordie.code.browserapi.ServiceWorkers.ExtendableEvent;
import com.flyordie.code.browserapi.DOM.ArrayBuffer;
import com.flyordie.code.browserapi.PushMessaging.PushMessageData;
import com.flyordie.code.browserapi.PushMessaging.PushSubscriptionOptions;
import com.flyordie.code.browserapi.PushMessaging.PushSubscription;

public class PushMessaging {

    private PushMessaging() {
        throw new Error("should not instantiate");
    }

    // Generated from modules\push_messaging\PushSubscription.idl
    @Name("PushEncryptionKeyName")
    public enum PushEncryptionKeyName {
        curve25519dh
    }

    // Generated from modules\push_messaging\PushSubscription.idl
    @Name("PushSubscription")
    public interface PushSubscription {
        @Getter String endpoint();
        @Nullable ArrayBuffer getKey(PushEncryptionKeyName name);
        Future<Boolean> unsubscribe();
    }

    // Generated from modules\push_messaging\PushEventInit.idl
    @Name("PushEventInit")
    public static class PushEventInit extends ExtendableEventInit {
        public /* ArrayBuffer or ArrayBufferView or String */ Object data;
    }

    // Generated from modules\push_messaging\PushEvent.idl
    @Name("PushEvent")
    public interface PushEvent extends ExtendableEvent {
        @Getter PushMessageData data();
    }

    @Statics("PushEvent")
    public interface PushEvents {
        PushEvent create(String type, PushEventInit eventInitDict);
    }

    // Generated from modules\push_messaging\PushSubscriptionOptions.idl
    @Name("PushSubscriptionOptions")
    public static class PushSubscriptionOptions {
        public boolean userVisibleOnly = false;
    }

    // Generated from modules\push_messaging\PushManager.idl
    @Name("PushManager")
    public interface PushManager {
        Future<PushSubscription> subscribe(PushSubscriptionOptions options);
        Future</* optional */ PushSubscription> getSubscription();
        Future<Object> permissionState(PushSubscriptionOptions options);
    }

    // Generated from modules\push_messaging\PushMessageData.idl
    @Name("PushMessageData")
    public interface PushMessageData {
        ArrayBuffer arrayBuffer();
        Blob blob();
        Object json();
        String text();
    }

}
