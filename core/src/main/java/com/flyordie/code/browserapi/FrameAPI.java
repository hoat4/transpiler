package com.flyordie.code.browserapi;

import javax.annotation.Nullable;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.Future;
import java.lang.invoke.MethodHandle;

import com.flyordie.code.jsinterop.*;

import com.flyordie.code.browserapi.DOM.FrameRequestCallback;
import com.flyordie.code.browserapi.DOM.IdleRequestCallback;
import com.flyordie.code.browserapi.CSS.MediaQueryList;
import com.flyordie.code.browserapi.DOM.Document;
import com.flyordie.code.browserapi.FrameAPI.History;
import com.flyordie.code.browserapi.Loader.ApplicationCache;
import com.flyordie.code.browserapi.Speech.SpeechSynthesis;
import com.flyordie.code.browserapi.Plugins.MimeTypeArray;
import com.flyordie.code.browserapi.WebDatabase.Database;
import com.flyordie.code.browserapi.FrameAPI.WindowEventHandlers;
import com.flyordie.code.browserapi.CryptoAPI.Crypto;
import com.flyordie.code.browserapi.FrameAPI.WindowBase64;
import com.flyordie.code.browserapi.Timing.Performance;
import com.flyordie.code.browserapi.FrameAPI.NavigatorCPU;
import com.flyordie.code.browserapi.BluetoothAPI.Bluetooth;
import com.flyordie.code.browserapi.WebDatabase.DatabaseCallback;
import com.flyordie.code.browserapi.EditingAPI.Selection;
import com.flyordie.code.browserapi.CanvasAPI.CanvasRenderingContext2D;
import com.flyordie.code.browserapi.DOM.MessagePort;
import com.flyordie.code.browserapi.FrameAPI.NavigatorStorageUtils;
import com.flyordie.code.browserapi.Quota.StorageQuota;
import com.flyordie.code.browserapi.Media.NavigatorUserMediaErrorCallback;
import com.flyordie.code.browserapi.FileSystemAPI.EntryCallback;
import com.flyordie.code.browserapi.GamePad.GamepadList;
import com.flyordie.code.browserapi.FrameAPI.Window;
import com.flyordie.code.browserapi.WebUSB.USB;
import com.flyordie.code.browserapi.DOM.DOMStringList;
import com.flyordie.code.browserapi.FileSystemAPI.ErrorCallback;
import com.flyordie.code.browserapi.FrameAPI.Navigator;
import com.flyordie.code.browserapi.FrameAPI.NavigatorLanguage;
import com.flyordie.code.browserapi.CSS.CSSStyleDeclaration;
import com.flyordie.code.browserapi.PresentationAPI.Presentation;
import com.flyordie.code.browserapi.Media.MediaStreamConstraints;
import com.flyordie.code.browserapi.Timing.MemoryInfo;
import com.flyordie.code.browserapi.ImageBitmapAPI.ImageBitmapFactories;
import com.flyordie.code.browserapi.Events.Event;
import com.flyordie.code.browserapi.Media.NavigatorUserMediaSuccessCallback;
import com.flyordie.code.browserapi.GeoLocation.Geolocation;
import com.flyordie.code.browserapi.Plugins.PluginArray;
import com.flyordie.code.browserapi.Quota.StorageManager;
import com.flyordie.code.browserapi.FrameAPI.ScrollOptions;
import com.flyordie.code.browserapi.FrameAPI.WindowTimers;
import com.flyordie.code.browserapi.CSS.CSSRuleList;
import com.flyordie.code.browserapi.HTML.HTMLImageElement;
import com.flyordie.code.browserapi.Media.MediaDevices;
import com.flyordie.code.browserapi.PermissionsAPI.Permissions;
import com.flyordie.code.browserapi.Quota.DeprecatedStorageQuota;
import com.flyordie.code.browserapi.HTML.FormData;
import com.flyordie.code.browserapi.FileAPI.Blob;
import com.flyordie.code.browserapi.FrameAPI.ScrollToOptions;
import com.flyordie.code.browserapi.FrameAPI.NavigatorOnLine;
import com.flyordie.code.browserapi.FrameAPI.ConsoleBase;
import com.flyordie.code.browserapi.Media.MediaKeySystemAccess;
import com.flyordie.code.browserapi.DOM.ArrayBufferView;
import com.flyordie.code.browserapi.FrameAPI.NavigatorID;
import com.flyordie.code.browserapi.FrameAPI.BarProp;
import com.flyordie.code.browserapi.HTML.HTMLVideoElement;
import com.flyordie.code.browserapi.Quota.DeprecatedStorageInfo;
import com.flyordie.code.browserapi.FrameAPI.Screen;
import com.flyordie.code.browserapi.DOM.Element;
import com.flyordie.code.browserapi.Media.MediaKeySystemConfiguration;
import com.flyordie.code.browserapi.IndexedDB.IDBFactory;
import com.flyordie.code.browserapi.ServiceWorkers.ServiceWorkerContainer;
import com.flyordie.code.browserapi.DOM.GlobalEventHandlers;
import com.flyordie.code.browserapi.FrameAPI.Console;
import com.flyordie.code.browserapi.Events.EventTarget;
import com.flyordie.code.browserapi.WebMidi.MIDIOptions;
import com.flyordie.code.browserapi.Fetch.Request;
import com.flyordie.code.browserapi.Fetch.Response;
import com.flyordie.code.browserapi.FileSystemAPI.FileSystemCallback;
import com.flyordie.code.browserapi.CSS.StyleMedia;
import com.flyordie.code.browserapi.CredentialManager.CredentialsContainer;
import com.flyordie.code.browserapi.HTML.HTMLCanvasElement;
import com.flyordie.code.browserapi.FrameAPI.Location;

import static java.lang.invoke.MethodHandles.lookup;

public class FrameAPI {

    private FrameAPI() {
        throw new Error("should not instantiate");
    }

    // Generated from core\frame\NavigatorOnLine.idl
    @Name("NavigatorOnLine")
    public interface NavigatorOnLine {
        @Getter boolean onLine();
    }

    // Generated from core\frame\ImageBitmap.idl
    @Name("ImageBitmap")
    public interface ImageBitmap {
        @Getter int width();
        @Getter int height();
    }

    // Generated from core\frame\NavigatorStorageUtils.idl
    @Name("NavigatorStorageUtils")
    public interface NavigatorStorageUtils {
        @Getter boolean cookieEnabled();
        void getStorageUpdates();
    }

    // Generated from core\frame\WindowTimers.idl
    @Name("WindowTimers")
    public interface WindowTimers {
        int setTimeout(MethodHandle handler, int timeout, Object... arguments);
        int setTimeout(String handler, int timeout, Object... arguments);
        void clearTimeout(int handle);
        int setInterval(MethodHandle handler, int timeout, Object... arguments);
        int setInterval(String handler, int timeout, Object... arguments);
        void clearInterval(int handle);

        // kézzel írva az ez alattiak
        int setTimeout(TimeoutHandler runnable, int timeout);
        int setInterval(TimeoutHandler runnable, int timeout);


        @FunctionalInterface
        @NoInterfaceObject
        interface TimeoutHandler {

            void run();
        }
    }

    // Generated from core\frame\ScrollOptions.idl
    @Name("ScrollBehavior")
    public enum ScrollBehavior {
        auto, instant, smooth
    }

    // Generated from modules\storage\WindowStorage.idl, modules\filesystem\WindowFileSystem.idl, modules\fetch\WindowFetch.idl, modules\device_light\WindowDeviceLight.idl, modules\crypto\WindowCrypto.idl, modules\cachestorage\WindowCacheStorage.idl, modules\speech\WindowSpeechSynthesis.idl, modules\webdatabase\WindowWebDatabase.idl, modules\device_orientation\WindowDeviceOrientation.idl, modules\imagebitmap\WindowImageBitmapFactories.idl, modules\mediastream\WindowMediaStream.idl, modules\webaudio\WindowWebAudio.idl, core\imagebitmap\ImageBitmapFactories.idl, modules\speech\WindowSpeech.idl, modules\device_orientation\WindowDeviceMotion.idl, core\frame\Window.idl, core\frame\Window.idl, core\frame\Window.idl, core\frame\Window.idl, core\frame\Window.idl, modules\quota\WindowQuota.idl, modules\indexeddb\WindowIndexedDatabase.idl, core\timing\WindowPerformance.idl
    @Name("Window")
    public interface Window extends EventTarget, ImageBitmapFactories, GlobalEventHandlers, WindowBase64, WindowEventHandlers, WindowTimers,
            // innentől kézzel beírt superinterface deklarációk
            WebSockets {

        short TEMPORARY = (short) 0;
        short PERSISTENT = (short) 1;
        @Getter Storage sessionStorage();
        @Getter Storage localStorage();
        void webkitRequestFileSystem(short type, int size, FileSystemCallback successCallback, ErrorCallback errorCallback);
        void webkitResolveLocalFileSystemURL(String url, EntryCallback successCallback, ErrorCallback errorCallback);
        Future<Response> fetch(/* Request or String */ Object input, Map<String, Object> init);
        @Getter EventHandler ondevicelight();
        @Setter void ondevicelight(EventHandler v);
        @Getter Crypto crypto();
        @Getter CacheStorage caches();
        @Getter SpeechSynthesis speechSynthesis();
        Database openDatabase(String name, String version, String displayName, int estimatedSize, DatabaseCallback creationCallback);
        @Getter EventHandler ondeviceorientation();
        @Setter void ondeviceorientation(EventHandler v);
        Future<Object> createImageBitmap(HTMLImageElement image);
        Future<Object> createImageBitmap(HTMLImageElement image, int sx, int sy, int sw, int sh);
        Future<Object> createImageBitmap(HTMLVideoElement video);
        Future<Object> createImageBitmap(HTMLVideoElement video, int sx, int sy, int sw, int sh);
        Future<Object> createImageBitmap(CanvasRenderingContext2D context);
        Future<Object> createImageBitmap(CanvasRenderingContext2D context, int sx, int sy, int sw, int sh);
        Future<Object> createImageBitmap(HTMLCanvasElement canvas);
        Future<Object> createImageBitmap(HTMLCanvasElement canvas, int sx, int sy, int sw, int sh);
        /*
        @Getter MediaStreamConstructor webkitMediaStream();
        @Setter void webkitMediaStream(MediaStreamConstructor v);
        @Getter RTCPeerConnectionConstructor webkitRTCPeerConnection();
        @Setter void webkitRTCPeerConnection(RTCPeerConnectionConstructor v);
        @Getter AudioContextConstructor AudioContext();
        @Setter void AudioContext(AudioContextConstructor v);
        @Getter OfflineAudioContextConstructor OfflineAudioContext();
        @Setter void OfflineAudioContext(OfflineAudioContextConstructor v);
        @Getter AudioContextConstructor webkitAudioContext();
        @Setter void webkitAudioContext(AudioContextConstructor v);
        @Getter OfflineAudioContextConstructor webkitOfflineAudioContext();
        @Setter void webkitOfflineAudioContext(OfflineAudioContextConstructor v);
        @Getter SpeechGrammarConstructor webkitSpeechGrammar();
        @Setter void webkitSpeechGrammar(SpeechGrammarConstructor v);
        @Getter SpeechGrammarListConstructor webkitSpeechGrammarList();
        @Setter void webkitSpeechGrammarList(SpeechGrammarListConstructor v);
        @Getter SpeechRecognitionConstructor webkitSpeechRecognition();
        @Setter void webkitSpeechRecognition(SpeechRecognitionConstructor v);
        @Getter SpeechRecognitionErrorConstructor webkitSpeechRecognitionError();
        @Setter void webkitSpeechRecognitionError(SpeechRecognitionErrorConstructor v);
        @Getter SpeechRecognitionEventConstructor webkitSpeechRecognitionEvent();
        @Setter void webkitSpeechRecognitionEvent(SpeechRecognitionEventConstructor v);
         */
        @Getter EventHandler ondevicemotion();
        @Setter void ondevicemotion(EventHandler v);
        @Getter Window window();
        @Getter Window self();
        @Getter Document document();
        @Getter String name();
        @Setter void name(String v);
        @Getter Location location();
        @Getter History history();
        @Getter BarProp locationbar();
        @Getter BarProp menubar();
        @Getter BarProp personalbar();
        @Getter BarProp scrollbars();
        @Getter BarProp statusbar();
        @Getter BarProp toolbar();
        @Getter String status();
        @Setter void status(String v);
        void close();
        @Getter boolean closed();
        void stop();
        void focus();
        void blur();
        @Getter Window frames();
        @Getter int length();
        @Getter Window top();
        @Getter Window opener();
        @Setter void opener(Window v);
        @Getter Window parent();
        @Getter Optional<Element> frameElement();
        Window open(String url, String target, String features);
        @DynamicGetter Window get(int index);
        @DynamicGetter Object get(String name);
        @Getter Navigator navigator();
        @Getter ApplicationCache applicationCache();
        void alert();
        void alert(String message);
        boolean confirm(String message);
        @Nullable String prompt(String message, String defaultValue);
        void print();
        int requestAnimationFrame(FrameRequestCallback callback);
        void cancelAnimationFrame(int handle);
        int requestIdleCallback(IdleRequestCallback callback, double timeout);
        void cancelIdleCallback(int handle);
        void postMessage(Object message, String targetOrigin, List<MessagePort> transfer);
        void captureEvents();
        void releaseEvents();
        CSSStyleDeclaration getComputedStyle(Element elt, @Nullable String pseudoElt);
        MediaQueryList matchMedia(String query);
        @Getter Screen screen();
        void moveTo(int x, int y);
        void moveBy(int x, int y);
        void resizeTo(int x, int y);
        void resizeBy(int x, int y);
        @Getter int innerWidth();
        @Getter int innerHeight();
        @Getter double scrollX();
        @Getter double pageXOffset();
        @Getter double scrollY();
        @Getter double pageYOffset();
        void scroll(ScrollToOptions options);
        void scroll(double x, double y);
        void scrollTo(ScrollToOptions options);
        void scrollTo(double x, double y);
        void scrollBy(ScrollToOptions options);
        void scrollBy(double x, double y);
        @Getter int screenX();
        @Getter int screenY();
        @Getter int outerWidth();
        @Getter int outerHeight();
        @Getter double devicePixelRatio();
        @Nullable Selection getSelection();
        @Getter Console console();
        @Getter Navigator clientInformation();
        @Getter Event event();
        @Setter void event(Event v);
        boolean find(String string, boolean caseSensitive, boolean backwards, boolean wrap, boolean wholeWord, boolean searchInFrames, boolean showDialog);
        @Getter boolean offscreenBuffering();
        @Getter int screenLeft();
        @Getter int screenTop();
        @Getter String defaultStatus();
        @Setter void defaultStatus(String v);
        @Getter String defaultstatus();
        @Setter void defaultstatus(String v);
        @Getter StyleMedia styleMedia();
        CSSRuleList getMatchedCSSRules(Element element, @Nullable String pseudoElement);
        @Getter int orientation();
        int webkitRequestAnimationFrame(FrameRequestCallback callback);
        void webkitCancelAnimationFrame(int id);
        void webkitCancelRequestAnimationFrame(int id);
        /*
        @Getter TransitionEventConstructor WebKitTransitionEvent();
        @Setter void WebKitTransitionEvent(TransitionEventConstructor v);
        @Getter AnimationEventConstructor WebKitAnimationEvent();
        @Setter void WebKitAnimationEvent(AnimationEventConstructor v);
        @Getter URLConstructor webkitURL();
        @Setter void webkitURL(URLConstructor v);
        @Getter MutationObserverConstructor WebKitMutationObserver();
        @Setter void WebKitMutationObserver(MutationObserverConstructor v);
         */
        @Getter EventHandler onanimationend();
        @Setter void onanimationend(EventHandler v);
        @Getter EventHandler onanimationiteration();
        @Setter void onanimationiteration(EventHandler v);
        @Getter EventHandler onanimationstart();
        @Setter void onanimationstart(EventHandler v);
        @Getter EventHandler onorientationchange();
        @Setter void onorientationchange(EventHandler v);
        @Getter EventHandler onsearch();
        @Setter void onsearch(EventHandler v);
        @Getter EventHandler ontouchcancel();
        @Setter void ontouchcancel(EventHandler v);
        @Getter EventHandler ontouchend();
        @Setter void ontouchend(EventHandler v);
        @Getter EventHandler ontouchmove();
        @Setter void ontouchmove(EventHandler v);
        @Getter EventHandler ontouchstart();
        @Setter void ontouchstart(EventHandler v);
        @Getter EventHandler ontransitionend();
        @Setter void ontransitionend(EventHandler v);
        @Getter EventHandler onwebkitanimationend();
        @Setter void onwebkitanimationend(EventHandler v);
        @Getter EventHandler onwebkitanimationiteration();
        @Setter void onwebkitanimationiteration(EventHandler v);
        @Getter EventHandler onwebkitanimationstart();
        @Setter void onwebkitanimationstart(EventHandler v);
        @Getter EventHandler onwebkittransitionend();
        @Setter void onwebkittransitionend(EventHandler v);
        @Getter EventHandler onwheel();
        @Setter void onwheel(EventHandler v);
        @Getter DeprecatedStorageInfo webkitStorageInfo();
        @Getter IDBFactory webkitIndexedDB();
        @Getter IDBFactory indexedDB();
        /*
        @Getter IDBCursorConstructor webkitIDBCursor();
        @Setter void webkitIDBCursor(IDBCursorConstructor v);
        @Getter IDBDatabaseConstructor webkitIDBDatabase();
        @Setter void webkitIDBDatabase(IDBDatabaseConstructor v);
        @Getter IDBFactoryConstructor webkitIDBFactory();
        @Setter void webkitIDBFactory(IDBFactoryConstructor v);
        @Getter IDBIndexConstructor webkitIDBIndex();
        @Setter void webkitIDBIndex(IDBIndexConstructor v);
        @Getter IDBKeyRangeConstructor webkitIDBKeyRange();
        @Setter void webkitIDBKeyRange(IDBKeyRangeConstructor v);
        @Getter IDBObjectStoreConstructor webkitIDBObjectStore();
        @Setter void webkitIDBObjectStore(IDBObjectStoreConstructor v);
        @Getter IDBRequestConstructor webkitIDBRequest();
        @Setter void webkitIDBRequest(IDBRequestConstructor v);
        @Getter IDBTransactionConstructor webkitIDBTransaction();
        @Setter void webkitIDBTransaction(IDBTransactionConstructor v);
         */
        @Getter Performance performance();
    }

    // Generated from core\frame\NavigatorLanguage.idl
    @Name("NavigatorLanguage")
    public interface NavigatorLanguage {
        @Getter Optional<String> language();
        @Getter List<String> languages();
    }

    // Generated from core\frame\WindowEventHandlers.idl
    @Name("WindowEventHandlers")
    public interface WindowEventHandlers {
        @Getter EventHandler onbeforeunload();
        @Setter void onbeforeunload(EventHandler v);
        @Getter EventHandler onhashchange();
        @Setter void onhashchange(EventHandler v);
        @Getter EventHandler onlanguagechange();
        @Setter void onlanguagechange(EventHandler v);
        @Getter EventHandler onmessage();
        @Setter void onmessage(EventHandler v);
        @Getter EventHandler onoffline();
        @Setter void onoffline(EventHandler v);
        @Getter EventHandler ononline();
        @Setter void ononline(EventHandler v);
        @Getter EventHandler onpagehide();
        @Setter void onpagehide(EventHandler v);
        @Getter EventHandler onpageshow();
        @Setter void onpageshow(EventHandler v);
        @Getter EventHandler onpopstate();
        @Setter void onpopstate(EventHandler v);
        @Getter EventHandler onstorage();
        @Setter void onstorage(EventHandler v);
        @Getter EventHandler onunload();
        @Setter void onunload(EventHandler v);
        @Getter EventHandler onrejectionhandled();
        @Setter void onrejectionhandled(EventHandler v);
        @Getter EventHandler onunhandledrejection();
        @Setter void onunhandledrejection(EventHandler v);
    }

    // Generated from core\frame\WindowBase64.idl
    @Name("WindowBase64")
    public interface WindowBase64 {
        String btoa(String btoa);
        String atob(String atob);
    }

    // Generated from core\frame\NavigatorCPU.idl
    @Name("NavigatorCPU")
    public interface NavigatorCPU {
        @Getter int hardwareConcurrency();
    }

    // Generated from core\frame\NavigatorID.idl
    @Name("NavigatorID")
    public interface NavigatorID {
        @Getter String appCodeName();
        @Getter String appName();
        @Getter String appVersion();
        @Getter String platform();
        @Getter String product();
        @Getter String userAgent();
    }

    // Generated from core\frame\History.idl
    @Name("ScrollRestoration")
    public enum ScrollRestoration {
        auto, manual
    }

    // Generated from core\frame\ConsoleBase.idl
    @Name("ConsoleBase")
    @NoInterfaceObject
    public interface ConsoleBase {
        void trace(Object... args);
        void trace(String formatString, Object... args);
        void debug(Object... args);
        void debug(String formatString, Object... args);
        void info(Object... args);
        void info(String formatString, Object... args);
        void warn(Object... args);
        void warn(String formatString, Object... args);
        void error(Object... args);
        void error(String formatString, Object... args);
        void log(Object... args);
        void log(String formatString, Object... args);
        @Name("assert") void assert_(boolean condition, Object... args);
        @Name("assert") void assert_(boolean condition, String formatString, Object... args);
        void dir(Object args);
        void dirxml(Object args);
        void table(Object... args);
        void count();
        void count(String label);
        void markTimeline(String title);
        void profile(String title);
        void profileEnd(String title);
        void time(String title);
        void timeEnd(String title);
        void timeStamp(String title);
        void timeline(String title);
        void timelineEnd(String title);

        // groupEnd kivételével lenne varargs itt Chrome IDL szerint, de MDN doc szerint nem
        void group();
        void group(String label);
        void groupCollapsed();
        void groupCollapsed(String label);
        void groupEnd();
        void clear();
    }

    // Generated from core\frame\History.idl
    @Name("History")
    public interface History {
        @Getter int length();
        @Getter Object state();
        void go(int delta);
        void back();
        void forward();
        void pushState(Object data, @Nullable String title, @Nullable String url);
        void replaceState(Object data, @Nullable String title, @Nullable String url);
        @Getter ScrollRestoration scrollRestoration();
        @Setter void scrollRestoration(ScrollRestoration v);
    }

    // Generated from modules\webusb\NavigatorUSB.idl, modules\plugins\NavigatorPlugins.idl, modules\beacon\NavigatorBeacon.idl, modules\serviceworkers\NavigatorServiceWorker.idl, modules\donottrack\NavigatorDoNotTrack.idl, modules\mediastream\NavigatorUserMedia.idl, modules\geolocation\NavigatorGeolocation.idl, modules\bluetooth\NavigatorBluetooth.idl, modules\vibration\NavigatorVibration.idl, modules\vr\NavigatorVRDevice.idl, modules\navigatorcontentutils\NavigatorContentUtils.idl, modules\battery\NavigatorBattery.idl, modules\credentialmanager\NavigatorCredentials.idl, core\events\NavigatorEvents.idl, core\frame\Navigator.idl, core\frame\Navigator.idl, core\frame\Navigator.idl, core\frame\Navigator.idl, core\frame\Navigator.idl, core\frame\Navigator.idl, modules\nfc\NavigatorNFC.idl, modules\mediastream\NavigatorMediaStream.idl, modules\encryptedmedia\NavigatorRequestMediaKeySystemAccess.idl, modules\netinfo\NavigatorNetworkInformation.idl, modules\permissions\NavigatorPermissions.idl, modules\quota\NavigatorStorageQuota.idl, modules\webmidi\NavigatorWebMIDI.idl, modules\gamepad\NavigatorGamepad.idl, modules\presentation\NavigatorPresentation.idl
    @Name("Navigator")
    public interface Navigator extends NavigatorCPU, NavigatorID, NavigatorLanguage, NavigatorOnLine, NavigatorStorageUtils {
        @Getter USB usb();
        @Getter PluginArray plugins();
        @Getter MimeTypeArray mimeTypes();
        boolean javaEnabled();
        boolean sendBeacon(String url, @Nullable /* ArrayBufferView or Blob or String or FormData */ Object data);
        @Getter ServiceWorkerContainer serviceWorker();
        @Getter Optional<String> doNotTrack();
        @Getter MediaDevices mediaDevices();
        @Getter Geolocation geolocation();
        @Getter Bluetooth bluetooth();
        boolean vibrate(int pattern);
        boolean vibrate(List<Integer> pattern);
        Future<Object> getVRDevices();
        void registerProtocolHandler(String scheme, String url, String title);
        String isProtocolHandlerRegistered(String scheme, String url);
        void unregisterProtocolHandler(String scheme, String url);
        Future<Object> getBattery();
        @Getter CredentialsContainer credentials();
        @Getter int maxTouchPoints();
        @Getter String vendorSub();
        @Getter String productSub();
        @Getter String vendor();
        // NFC-t kiszedtük, lásd megjegyzés NFC.java-ban
        // @Getter NFC nfc();
        void webkitGetUserMedia(MediaStreamConstraints options, NavigatorUserMediaSuccessCallback successCallback, NavigatorUserMediaErrorCallback errorCallback);
        Future<MediaKeySystemAccess> requestMediaKeySystemAccess(String keySystem, List<MediaKeySystemConfiguration> supportedConfigurations);
        @Getter NetworkInformation connection();
        @Getter Permissions permissions();
        @Getter DeprecatedStorageQuota webkitTemporaryStorage();
        @Getter DeprecatedStorageQuota webkitPersistentStorage();
        @Getter StorageQuota storageQuota();
        @Getter StorageManager storage();
        Future<Object> requestMIDIAccess(MIDIOptions options);
        GamepadList getGamepads();
        @Getter Presentation presentation();
    }

    // Generated from core\frame\ScrollOptions.idl
    @Name("ScrollOptions")
    public static class ScrollOptions {
        public ScrollBehavior behavior = ScrollBehavior.auto;
    }

    // Generated from core\frame\BarProp.idl
    @Name("BarProp")
    public interface BarProp {
        @Getter boolean visible();
    }

    // Generated from core\frame\Console.idl, core\timing\ConsoleMemory.idl
    @Name("Console")
    @NoInterfaceObject
    public interface Console extends ConsoleBase {
        @Getter MemoryInfo memory();
        @Setter void memory(MemoryInfo v);
    }

    // Generated from core\frame\Location.idl
    @Name("Location")
    public interface Location {
        void assign(String url);
        void replace(String url);
        void reload();
        @Getter DOMStringList ancestorOrigins();
        @Getter String href();
        @Setter void href(String v);
        String toString();
        @Getter String origin();
        @Getter String protocol();
        @Setter void protocol(String v);
        @Getter String host();
        @Setter void host(String v);
        @Getter String hostname();
        @Setter void hostname(String v);
        @Getter String port();
        @Setter void port(String v);
        @Getter String pathname();
        @Setter void pathname(String v);
        @Getter String search();
        @Setter void search(String v);
        @Getter String hash();
        @Setter void hash(String v);
        Object valueOf();
    }

    // Generated from core\frame\ScrollToOptions.idl
    @Name("ScrollToOptions")
    public static class ScrollToOptions extends ScrollOptions {
        public double left;
        public double top;
    }

    // Generated from core\frame\Screen.idl, modules\wake_lock\ScreenWakeLock.idl, modules\screen_orientation\ScreenScreenOrientation.idl
    @Name("Screen")
    public interface Screen {
        @Getter int availWidth();
        @Getter int availHeight();
        @Getter int width();
        @Getter int height();
        @Getter int colorDepth();
        @Getter int pixelDepth();
        @Getter int availLeft();
        @Getter int availTop();
        @Getter boolean keepAwake();
        @Setter void keepAwake(boolean v);
        @Getter ScreenOrientation orientation();
    }

}
