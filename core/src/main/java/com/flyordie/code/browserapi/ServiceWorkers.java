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

import com.flyordie.code.browserapi.BackgroundSync.PeriodicSyncManager;
import com.flyordie.code.browserapi.ServiceWorkers.ClientQueryOptions;
import com.flyordie.code.browserapi.ServiceWorkers.ExtendableEvent;
import com.flyordie.code.browserapi.NotificationsAPI.Notification;
import com.flyordie.code.browserapi.GeoFencing.Geofencing;
import com.flyordie.code.browserapi.NotificationsAPI.NotificationOptions;
import com.flyordie.code.browserapi.PushMessaging.PushManager;
import com.flyordie.code.browserapi.NotificationsAPI.GetNotificationOptions;
import com.flyordie.code.browserapi.DOM.MessagePort;
import com.flyordie.code.browserapi.ServiceWorkers.ServiceWorkerRegistration;
import com.flyordie.code.browserapi.Workers.AbstractWorker;
import com.flyordie.code.browserapi.ServiceWorkers.Client;
import com.flyordie.code.browserapi.Events.EventTarget;
import com.flyordie.code.browserapi.Events.EventInit;
import com.flyordie.code.browserapi.Fetch.Request;
import com.flyordie.code.browserapi.BackgroundSync.SyncManager;
import com.flyordie.code.browserapi.ServiceWorkers.Clients;
import com.flyordie.code.browserapi.ServiceWorkers.WindowClient;
import com.flyordie.code.browserapi.ServiceWorkers.ExtendableEventInit;
import com.flyordie.code.browserapi.Fetch.Response;
import com.flyordie.code.browserapi.Workers.WorkerGlobalScope;
import com.flyordie.code.browserapi.ServiceWorkers.ServiceWorker;
import com.flyordie.code.browserapi.ServiceWorkers.RegistrationOptions;
import com.flyordie.code.browserapi.ServiceWorkers.FetchEventInit;
import com.flyordie.code.browserapi.Events.Event;
import com.flyordie.code.browserapi.ServiceWorkers.ServiceWorkerMessageEventInit;

public class ServiceWorkers {

    private ServiceWorkers() {
        throw new Error("should not instantiate");
    }

    // Generated from modules\serviceworkers\ClientQueryOptions.idl
    @Name("ClientQueryOptions")
    public static class ClientQueryOptions {
        public boolean includeUncontrolled = false;
        public ClientType type = ClientType.window;
    }

    // Generated from modules\serviceworkers\WindowClient.idl
    @Name("WindowClient")
    public interface WindowClient extends Client {
        @Getter VisibilityState visibilityState();
        @Getter boolean focused();
        Future<WindowClient> focus();
        Future<WindowClient> navigate(String url);
    }

    // https://developer.mozilla.org/en-US/docs/Web/API/WindowClient/visibilityState
    @Name("VisibilityState")
    public enum VisibilityState {
        hidden, visible, prerender
    }

    // Generated from modules\serviceworkers\FetchEventInit.idl
    @Name("FetchEventInit")
    public static class FetchEventInit extends ExtendableEventInit {
        public Request request;
        public boolean isReload = false;
    }

    // Generated from modules\notifications\ServiceWorkerRegistrationNotifications.idl, modules\serviceworkers\ServiceWorkerRegistration.idl, modules\geofencing\ServiceWorkerRegistrationGeofencing.idl, modules\push_messaging\ServiceWorkerRegistrationPush.idl, modules\background_sync\ServiceWorkerRegistrationSync.idl
    @Name("ServiceWorkerRegistration")
    public interface ServiceWorkerRegistration extends EventTarget {
        Future<Object> showNotification(String title, NotificationOptions options);
        Future<List<Notification>> getNotifications(GetNotificationOptions filter);
        @Getter Optional<ServiceWorker> installing();
        @Getter Optional<ServiceWorker> waiting();
        @Getter Optional<ServiceWorker> active();
        @Getter String scope();
        Future<Void> update();
        Future<Boolean> unregister();
        @Getter EventHandler onupdatefound();
        @Setter void onupdatefound(EventHandler v);
        @Getter Geofencing geofencing();
        @Getter PushManager pushManager();
        @Getter SyncManager sync();
        @Getter PeriodicSyncManager periodicSync();
    }

    // Generated from modules\serviceworkers\ServiceWorker.idl, modules\serviceworkers\ServiceWorker.idl
    @Name("ServiceWorker")
    public interface ServiceWorker extends EventTarget, AbstractWorker {
        void postMessage(Object message, List<MessagePort> transfer);
        @Getter String scriptURL();
        @Getter ServiceWorkerState state();
        @Getter EventHandler onstatechange();
        @Setter void onstatechange(EventHandler v);
    }

    // Generated from modules\serviceworkers\ServiceWorker.idl
    @Name("ServiceWorkerState")
    public enum ServiceWorkerState {
        installing, installed, activating, activated, 
        redundant
    }

    // Generated from modules\serviceworkers\Clients.idl
    @Name("Clients")
    public interface Clients {
        Future<List<Client>> matchAll(ClientQueryOptions options);
        Future</* optional */ WindowClient> openWindow(String url);
        Future<Void> claim();
    }

    // Generated from modules\serviceworkers\FetchEvent.idl
    @Name("FetchEvent")
    public interface FetchEvent extends ExtendableEvent {
        @Getter Request request();
        @Getter boolean isReload();
        void respondWith(Future<Response> r);
    }

    @Statics("FetchEvent")
    public interface FetchEvents {
        FetchEvent create(String type, FetchEventInit eventInitDict);
    }

    // Generated from modules\serviceworkers\ExtendableEventInit.idl
    @Name("ExtendableEventInit")
    public static class ExtendableEventInit extends EventInit {
    }

    // Generated from modules\serviceworkers\WindowClient.idl
    @Name("ContextFrameType")
    public enum ContextFrameType {
        topLevel, nested, auxiliary, none
    }

    // Generated from modules\serviceworkers\ExtendableEvent.idl
    @Name("ExtendableEvent")
    public interface ExtendableEvent extends Event {
        void waitUntil(Future<Object> f);
    }

    @Statics("ExtendableEvent")
    public interface ExtendableEvents {
        ExtendableEvent create(String type, ExtendableEventInit eventInitDict);
    }

    // Generated from modules\serviceworkers\ClientQueryOptions.idl
    @Name("ClientType")
    public enum ClientType {
        window, worker, sharedworker, all
    }

    // Generated from modules\serviceworkers\ServiceWorkerGlobalScope.idl, modules\push_messaging\ServiceWorkerGlobalScopePush.idl, modules\geofencing\ServiceWorkerGlobalScopeGeofencing.idl, modules\notifications\ServiceWorkerGlobalScopeNotifications.idl, modules\background_sync\ServiceWorkerGlobalScopeSync.idl
    @Name("ServiceWorkerGlobalScope")
    public interface ServiceWorkerGlobalScope extends WorkerGlobalScope {
        @Getter Clients clients();
        @Getter ServiceWorkerRegistration registration();
        Future<Response> fetch(/* Request or String */ Object input, Map<String, Object> init);
        void close();
        Future<Void> skipWaiting();
        @Getter EventHandler onactivate();
        @Setter void onactivate(EventHandler v);
        @Getter EventHandler onfetch();
        @Setter void onfetch(EventHandler v);
        @Getter EventHandler oninstall();
        @Setter void oninstall(EventHandler v);
        @Getter EventHandler onmessage();
        @Setter void onmessage(EventHandler v);
        @Getter EventHandler onpush();
        @Setter void onpush(EventHandler v);
        @Getter EventHandler ongeofenceenter();
        @Setter void ongeofenceenter(EventHandler v);
        @Getter EventHandler ongeofenceleave();
        @Setter void ongeofenceleave(EventHandler v);
        @Getter EventHandler onnotificationclick();
        @Setter void onnotificationclick(EventHandler v);
        @Getter EventHandler onsync();
        @Setter void onsync(EventHandler v);
        @Getter EventHandler onperiodicsync();
        @Setter void onperiodicsync(EventHandler v);
    }

    // Generated from modules\serviceworkers\ServiceWorkerMessageEventInit.idl
    @Name("ServiceWorkerMessageEventInit")
    public static class ServiceWorkerMessageEventInit extends EventInit {
        public Object data;
        public String origin;
        public String lastEventId;
        public @Nullable /* ServiceWorker or MessagePort */ Object source;
        public List<MessagePort> ports;
    }

    // Generated from modules\serviceworkers\ServiceWorkerMessageEvent.idl
    @Name("ServiceWorkerMessageEvent")
    public interface ServiceWorkerMessageEvent extends Event {
        @Getter Object data();
        @Getter String origin();
        @Getter String lastEventId();
        @Getter Optional</* ServiceWorker or MessagePort */ Object> source();
        @Getter Optional<List<MessagePort>> ports();
    }

    @Statics("ServiceWorkerMessageEvent")
    public interface ServiceWorkerMessageEvents {
        ServiceWorkerMessageEvent create(String type, ServiceWorkerMessageEventInit eventInitDict);
    }

    // Generated from modules\serviceworkers\RegistrationOptions.idl
    @Name("RegistrationOptions")
    public static class RegistrationOptions {
        public String scope;
    }

    // Generated from modules\serviceworkers\Client.idl
    @Name("Client")
    public interface Client {
        @Getter String url();
        @Getter ContextFrameType frameType();
        @Getter String id();
        void postMessage(Object message, List<MessagePort> transfer);
    }

    // Generated from modules\serviceworkers\ServiceWorkerContainer.idl
    @Name("ServiceWorkerContainer")
    public interface ServiceWorkerContainer extends EventTarget {
        @Getter Optional<ServiceWorker> controller();
        @Getter Future<ServiceWorkerRegistration> ready();
        Future<ServiceWorkerRegistration> register(String url, RegistrationOptions options);
        Future<ServiceWorkerRegistration> getRegistration(String documentURL);
        Future<List<ServiceWorkerRegistration>> getRegistrations();
        @Getter EventHandler oncontrollerchange();
        @Setter void oncontrollerchange(EventHandler v);
        @Getter EventHandler onmessage();
        @Setter void onmessage(EventHandler v);
    }

}
