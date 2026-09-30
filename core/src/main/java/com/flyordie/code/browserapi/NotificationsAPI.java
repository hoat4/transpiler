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

import com.flyordie.code.browserapi.NotificationsAPI.NotificationOptions;
import com.flyordie.code.browserapi.ServiceWorkers.ExtendableEventInit;
import com.flyordie.code.browserapi.NotificationsAPI.NotificationPermissionCallback;
import com.flyordie.code.browserapi.NotificationsAPI.NotificationAction;
import com.flyordie.code.browserapi.ServiceWorkers.ExtendableEvent;
import com.flyordie.code.browserapi.NotificationsAPI.NotificationEventInit;
import com.flyordie.code.browserapi.Events.EventTarget;
import com.flyordie.code.browserapi.NotificationsAPI.Notification;

public class NotificationsAPI {

    private NotificationsAPI() {
        throw new Error("should not instantiate");
    }

    // Generated from modules\notifications\Notification.idl
    @Name("Notification")
    public interface Notification extends EventTarget {
        @Getter EventHandler onclick();
        @Setter void onclick(EventHandler v);
        @Getter EventHandler onshow();
        @Setter void onshow(EventHandler v);
        @Getter EventHandler onerror();
        @Setter void onerror(EventHandler v);
        @Getter EventHandler onclose();
        @Setter void onclose(EventHandler v);
        @Getter String title();
        @Getter String dir();
        @Getter String lang();
        @Getter String body();
        @Getter String tag();
        @Getter String icon();
        @Getter Optional<List<Integer>> vibrate();
        @Getter boolean silent();
        @Getter boolean requireInteraction();
        @Getter Object data();
        @Getter List<NotificationAction> actions();
        void close();
    }

    @Statics("Notification")
    @Name("Notifications")
    public interface Notifications {
        Notification create(String title, NotificationOptions options);
        @Getter NotificationPermission permission();
        Future<NotificationPermission> requestPermission(NotificationPermissionCallback deprecatedCallback);
        @Getter int maxActions();
    }

    // Generated from modules\notifications\NotificationOptions.idl
    @Name("NotificationOptions")
    public static class NotificationOptions {
        public NotificationDirection dir = NotificationDirection.auto;
        public String lang = "";
        public String body = "";
        public String tag = "";
        public String icon;
        public /* Integer or List<Integer> */ Object vibrate;
        public boolean silent = false;
        public boolean requireInteraction = false;
        public Object data = null;
        public List<NotificationAction> actions = Collections.emptyList();
    }

    // Generated from modules\notifications\NotificationAction.idl
    @Name("NotificationAction")
    public static class NotificationAction {
        public String action;
        public String title;
    }

    // Generated from modules\notifications\Notification.idl
    @Name("NotificationPermission")
    public enum NotificationPermission {
        default_, denied, granted
    }

    // Generated from modules\notifications\NotificationEventInit.idl
    @Name("NotificationEventInit")
    public static class NotificationEventInit extends ExtendableEventInit {
        public Notification notification;
        public String action = "";
    }

    // Generated from modules\notifications\NotificationOptions.idl
    @Name("NotificationDirection")
    public enum NotificationDirection {
        auto, ltr, rtl
    }

    // Generated from modules\notifications\GetNotificationOptions.idl
    @Name("GetNotificationOptions")
    public static class GetNotificationOptions {
        public String tag = "";
    }

    // Generated from modules\notifications\NotificationPermissionCallback.idl
    @FunctionalInterface
    @Name("NotificationPermissionCallback")
    public interface NotificationPermissionCallback {
        void handleEvent(String permission);
    }

    // Generated from modules\notifications\NotificationEvent.idl
    @Name("NotificationEvent")
    public interface NotificationEvent extends ExtendableEvent {
        @Getter Notification notification();
        @Getter String action();
    }

    @Statics("NotificationEvent")
    public interface NotificationEvents {
        NotificationEvent create(String type, NotificationEventInit eventInitDict);
    }

}
