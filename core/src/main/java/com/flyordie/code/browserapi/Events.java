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

import com.flyordie.code.browserapi.DOM.TouchList;
import com.flyordie.code.browserapi.Events.PromiseRejectionEventInit;
import com.flyordie.code.browserapi.Events.SecurityPolicyViolationEventInit;
import com.flyordie.code.browserapi.Events.PageTransitionEventInit;
import com.flyordie.code.browserapi.Events.FocusEventInit;
import com.flyordie.code.browserapi.DOM.Node;
import com.flyordie.code.browserapi.Events.RelatedEventInit;
import com.flyordie.code.browserapi.Events.ProgressEvent;
import com.flyordie.code.browserapi.Events.CompositionEventInit;
import com.flyordie.code.browserapi.Clipboard.DataTransfer;
import com.flyordie.code.browserapi.DOM.MessagePort;
import com.flyordie.code.browserapi.Events.AnimationEventInit;
import com.flyordie.code.browserapi.Events.AutocompleteErrorEventInit;
import com.flyordie.code.browserapi.Events.CustomEventInit;
import com.flyordie.code.browserapi.Events.PointerEventInit;
import com.flyordie.code.browserapi.Events.EventListener;
import com.flyordie.code.browserapi.Events.PopStateEventInit;
import com.flyordie.code.browserapi.Events.EventTarget;
import com.flyordie.code.browserapi.Events.UIEvent;
import com.flyordie.code.browserapi.FrameAPI.Window;
import com.flyordie.code.browserapi.Events.ProgressEventInit;
import com.flyordie.code.browserapi.Events.HashChangeEventInit;
import com.flyordie.code.browserapi.Events.MouseEvent;
import com.flyordie.code.browserapi.Events.KeyboardEventInit;
import com.flyordie.code.browserapi.Events.MessageEventInit;
import com.flyordie.code.browserapi.Events.ErrorEventInit;
import com.flyordie.code.browserapi.Events.EventModifierInit;
import com.flyordie.code.browserapi.Events.ApplicationCacheErrorEventInit;
import com.flyordie.code.browserapi.Events.EventInit;
import com.flyordie.code.browserapi.Events.TransitionEventInit;
import com.flyordie.code.browserapi.InputAPI.InputDeviceCapabilities;
import com.flyordie.code.browserapi.Events.Event;
import com.flyordie.code.browserapi.Events.UIEventInit;
import com.flyordie.code.browserapi.Events.WheelEventInit;
import com.flyordie.code.browserapi.Events.DragEventInit;
import com.flyordie.code.browserapi.Events.MouseEventInit;
import com.flyordie.code.browserapi.Events.AnimationPlayerEventInit;

public interface Events {

    Event create(String type, EventInit eventInitDict);

    // Generated from core\events\ProgressEventInit.idl
    @Name("ProgressEventInit")
    public static class ProgressEventInit extends EventInit {
        public boolean lengthComputable = false;
        public int loaded = 0;
        public int total = 0;
    }

    // Generated from core\events\HashChangeEventInit.idl
    @Name("HashChangeEventInit")
    public static class HashChangeEventInit extends EventInit {
        public String oldURL;
        public String newURL;
    }

    // Generated from core\events\MouseEvent.idl, modules\canvas2d\MouseEventHitRegion.idl
    @Name("MouseEvent")
    public interface MouseEvent extends UIEvent {
        @Getter int screenX();
        @Getter int screenY();
        @Getter int clientX();
        @Getter int clientY();
        @Getter boolean ctrlKey();
        @Getter boolean shiftKey();
        @Getter boolean altKey();
        @Getter boolean metaKey();
        @Getter short button();
        @Getter short buttons();
        @Getter Optional<EventTarget> relatedTarget();
        boolean getModifierState(String keyArg);
        void initMouseEvent(String type, boolean bubbles, boolean cancelable, @Nullable Window view, int detail, int screenX, int screenY, int clientX, int clientY, boolean ctrlKey, boolean altKey, boolean shiftKey, boolean metaKey, short button, @Nullable EventTarget relatedTarget);
        @Getter int pageX();
        @Getter int pageY();
        @Getter int x();
        @Getter int y();
        @Getter int offsetX();
        @Getter int offsetY();
        @Getter int movementX();
        @Getter int movementY();
        @Getter Node fromElement();
        @Getter Node toElement();
        @Getter int which();
        @Getter int webkitMovementX();
        @Getter int webkitMovementY();
        @Getter int layerX();
        @Getter int layerY();
        @Getter Optional<String> region();
    }

    @Statics("MouseEvent")
    public interface MouseEvents {
        MouseEvent create(String type, MouseEventInit eventInitDict);
    }

    // Generated from core\events\TouchEvent.idl
    @Name("TouchEvent")
    public interface TouchEvent extends UIEvent {
        @Getter TouchList touches();
        @Getter TouchList targetTouches();
        @Getter TouchList changedTouches();
        @Getter boolean altKey();
        @Getter boolean metaKey();
        @Getter boolean ctrlKey();
        @Getter boolean shiftKey();
        void initTouchEvent(TouchList touches, TouchList targetTouches, TouchList changedTouches, String type, Window view, int unused1, int unused2, int unused3, int unused4, boolean ctrlKey, boolean altKey, boolean shiftKey, boolean metaKey);
    }

    // Generated from core\events\ProgressEvent.idl
    @Name("ProgressEvent")
    public interface ProgressEvent extends Event {
        @Getter boolean lengthComputable();
        @Getter int loaded();
        @Getter int total();
    }

    @Statics("ProgressEvent")
    public interface ProgressEvents {
        ProgressEvent create(String type, ProgressEventInit eventInitDict);
    }

    // Generated from core\events\PromiseRejectionEvent.idl
    @Name("PromiseRejectionEvent")
    public interface PromiseRejectionEvent extends Event {
        @Getter Optional<Future<Object>> promise();
        @Getter Object reason();
    }

    @Statics("PromiseRejectionEvent")
    public interface PromiseRejectionEvents {
        PromiseRejectionEvent create(String type, PromiseRejectionEventInit eventInitDict);
    }

    // Generated from core\events\EventModifierInit.idl
    @Name("EventModifierInit")
    public static class EventModifierInit extends UIEventInit {
        public boolean ctrlKey = false;
        public boolean shiftKey = false;
        public boolean altKey = false;
        public boolean metaKey = false;
    }

    // Generated from core\events\UIEventInit.idl
    @Name("UIEventInit")
    public static class UIEventInit extends EventInit {
        public @Nullable Window view = null;
        public int detail = 0;
        public @Nullable InputDeviceCapabilities sourceCapabilities = null;
    }

    // Generated from core\events\RelatedEventInit.idl
    @Name("RelatedEventInit")
    public static class RelatedEventInit extends EventInit {
        public @Nullable EventTarget relatedTarget;
    }

    // Generated from core\events\AnimationEvent.idl
    @Name("AnimationEvent")
    public interface AnimationEvent extends Event {
        @Getter String animationName();
        @Getter double elapsedTime();
    }

    @Statics("AnimationEvent")
    public interface AnimationEvents {
        AnimationEvent create(String type, AnimationEventInit eventInitDict);
    }

    // Generated from core\events\FocusEventInit.idl
    @Name("FocusEventInit")
    public static class FocusEventInit extends UIEventInit {
        public @Nullable EventTarget relatedTarget = null;
    }

    // Generated from core\events\ClipboardEvent.idl
    @Name("ClipboardEvent")
    public interface ClipboardEvent extends Event {
        @Getter DataTransfer clipboardData();
    }

    // Generated from core\events\MessageEvent.idl
    @Name("MessageEvent")
    public interface MessageEvent extends Event {
        @Getter Object data();
        @Getter String origin();
        @Getter String lastEventId();
        @Getter Optional<EventTarget> source();
        @Getter Optional<List<MessagePort>> ports();
        void initMessageEvent(String typeArg, boolean canBubbleArg, boolean cancelableArg, Object dataArg, String originArg, String lastEventIdArg, Window sourceArg, List<MessagePort> portsArg);
    }

    @Statics("MessageEvent")
    public interface MessageEvents {
        MessageEvent create(String type, MessageEventInit eventInitDict);
    }

    // Generated from core\events\TextEvent.idl
    @Name("TextEvent")
    public interface TextEvent extends UIEvent {
        @Getter String data();
        void initTextEvent(String typeArg, boolean canBubbleArg, boolean cancelableArg, Window viewArg, String dataArg);
    }

    // Generated from core\events\HashChangeEvent.idl
    @Name("HashChangeEvent")
    public interface HashChangeEvent extends Event {
        @Getter String oldURL();
        @Getter String newURL();
    }

    @Statics("HashChangeEvent")
    public interface HashChangeEvents {
        HashChangeEvent create(String type, HashChangeEventInit eventInitDict);
    }

    // Generated from core\events\PointerEventInit.idl
    @Name("PointerEventInit")
    public static class PointerEventInit extends MouseEventInit {
        public int pointerId = 0;
        public double width = 0;
        public double height = 0;
        public double pressure = 0;
        public int tiltX = 0;
        public int tiltY = 0;
        public String pointerType = "";
        public boolean isPrimary = false;
    }

    // Generated from core\events\ApplicationCacheErrorEventInit.idl
    @Name("ApplicationCacheErrorEventInit")
    public static class ApplicationCacheErrorEventInit extends EventInit {
        public String reason;
        public String url;
        public short status;
        public String message;
    }

    // Generated from core\events\AutocompleteErrorEvent.idl
    @Name("AutocompleteErrorEvent")
    public interface AutocompleteErrorEvent extends Event {
        @Getter AutocompleteErrorReason reason();
    }

    @Statics("AutocompleteErrorEvent")
    public interface AutocompleteErrorEvents {
        AutocompleteErrorEvent create(String type, AutocompleteErrorEventInit eventInitDict);
    }

    // Generated from core\events\CustomEventInit.idl
    @Name("CustomEventInit")
    public static class CustomEventInit extends EventInit {
        public Object detail = null;
    }

    // Generated from core\events\AnimationEventInit.idl
    @Name("AnimationEventInit")
    public static class AnimationEventInit extends EventInit {
        public String animationName = "";
        public double elapsedTime = 0.0;
    }

    // Generated from core\events\ResourceProgressEvent.idl
    @Name("ResourceProgressEvent")
    public interface ResourceProgressEvent extends ProgressEvent {
        @Getter String url();
    }

    // Generated from core\events\WheelEventInit.idl
    @Name("WheelEventInit")
    public static class WheelEventInit extends MouseEventInit {
        public double deltaX = 0.0;
        public double deltaY = 0.0;
        public double deltaZ = 0.0;
        public int deltaMode = 0;
        public int wheelDeltaX = 0;
        public int wheelDeltaY = 0;
    }

    // Generated from core\events\BeforeUnloadEvent.idl
    @Name("BeforeUnloadEvent")
    public interface BeforeUnloadEvent extends Event {
        @Getter String returnValue();
        @Setter void returnValue(String v);
    }

    // Generated from core\events\AutocompleteErrorEventInit.idl
    @Name("AutocompleteErrorEventInit")
    public static class AutocompleteErrorEventInit extends EventInit {
        public AutocompleteErrorReason reason;
    }

    // Generated from core\events\WheelEvent.idl
    @Name("WheelEvent")
    public interface WheelEvent extends MouseEvent {
        int DOM_DELTA_PIXEL = 0;
        int DOM_DELTA_LINE = 1;
        int DOM_DELTA_PAGE = 2;
        @Getter double deltaX();
        @Getter double deltaY();
        @Getter double deltaZ();
        @Getter int deltaMode();
        @Getter int wheelDeltaX();
        @Getter int wheelDeltaY();
        @Getter int wheelDelta();
    }

    @Statics("WheelEvent")
    public interface WheelEvents {
        WheelEvent create(String type, WheelEventInit eventInitDict);
    }

    // Generated from core\events\MutationEvent.idl
    @Name("MutationEvent")
    public interface MutationEvent extends Event {
        short MODIFICATION = (short) 1;
        short ADDITION = (short) 2;
        short REMOVAL = (short) 3;
        @Getter Optional<Node> relatedNode();
        @Getter String prevValue();
        @Getter String newValue();
        @Getter String attrName();
        @Getter short attrChange();
        void initMutationEvent(String type, boolean bubbles, boolean cancelable, Node relatedNode, String prevValue, String newValue, String attrName, short attrChange);
    }

    // Generated from core\events\CompositionEventInit.idl
    @Name("CompositionEventInit")
    public static class CompositionEventInit extends UIEventInit {
        public String data = "";
    }

    // Generated from core\events\PointerEvent.idl
    @Name("PointerEvent")
    public interface PointerEvent extends MouseEvent {
        @Getter int pointerId();
        @Getter double width();
        @Getter double height();
        @Getter double pressure();
        @Getter int tiltX();
        @Getter int tiltY();
        @Getter String pointerType();
        @Getter boolean isPrimary();
    }

    @Statics("PointerEvent")
    public interface PointerEvents {
        PointerEvent create(String type, PointerEventInit eventInitDict);
    }

    // Generated from core\events\PopStateEventInit.idl
    @Name("PopStateEventInit")
    public static class PopStateEventInit extends EventInit {
        public Object state;
    }

    // Generated from core\events\MouseEventInit.idl
    @Name("MouseEventInit")
    public static class MouseEventInit extends EventModifierInit {
        public int screenX = 0;
        public int screenY = 0;
        public int clientX = 0;
        public int clientY = 0;
        public short button = (short) 0;
        public short buttons = (short) 0;
        public @Nullable EventTarget relatedTarget = null;
        public int movementX = 0;
        public int movementY = 0;
    }

    // Generated from core\events\ErrorEventInit.idl
    @Name("ErrorEventInit")
    public static class ErrorEventInit extends EventInit {
        public String message;
        public String filename;
        public int lineno;
        public int colno;
        public Object error;
    }

    // Generated from core\events\AnimationPlayerEventInit.idl
    @Name("AnimationPlayerEventInit")
    public static class AnimationPlayerEventInit extends EventInit {
        public @Nullable Double currentTime = null;
        public @Nullable Double timelineTime = null;
    }

    // Generated from core\events\SecurityPolicyViolationEvent.idl
    @Name("SecurityPolicyViolationEvent")
    public interface SecurityPolicyViolationEvent extends Event {
        @Getter String documentURI();
        @Getter String referrer();
        @Getter String blockedURI();
        @Getter String violatedDirective();
        @Getter String effectiveDirective();
        @Getter String originalPolicy();
        @Getter String sourceFile();
        @Getter int statusCode();
        @Getter int lineNumber();
        @Getter int columnNumber();
    }

    @Statics("SecurityPolicyViolationEvent")
    public interface SecurityPolicyViolationEvents {
        SecurityPolicyViolationEvent create(String type, SecurityPolicyViolationEventInit eventInitDict);
    }

    // Generated from core\events\RelatedEvent.idl
    @Name("RelatedEvent")
    public interface RelatedEvent extends Event {
        @Getter Optional<EventTarget> relatedTarget();
    }

    @Statics("RelatedEvent")
    public interface RelatedEvents {
        RelatedEvent create(String type, RelatedEventInit eventInitDict);
    }

    // Generated from core\events\SecurityPolicyViolationEventInit.idl
    @Name("SecurityPolicyViolationEventInit")
    public static class SecurityPolicyViolationEventInit extends EventInit {
        public String documentURI;
        public String referrer;
        public String blockedURI;
        public String violatedDirective;
        public String effectiveDirective;
        public String originalPolicy;
        public String sourceFile;
        public int statusCode;
        public int lineNumber;
        public int columnNumber;
    }

    // Generated from core\events\AnimationPlayerEvent.idl
    @Name("AnimationPlayerEvent")
    public interface AnimationPlayerEvent extends Event {
        @Getter double currentTime();
        @Getter double timelineTime();
    }

    @Statics("AnimationPlayerEvent")
    public interface AnimationPlayerEvents {
        AnimationPlayerEvent create(String type, AnimationPlayerEventInit eventInitDict);
    }

    // Generated from core\events\ApplicationCacheErrorEvent.idl
    @Name("ApplicationCacheErrorEvent")
    public interface ApplicationCacheErrorEvent extends Event {
        @Getter String reason();
        @Getter String url();
        @Getter short status();
        @Getter String message();
    }

    @Statics("ApplicationCacheErrorEvent")
    public interface ApplicationCacheErrorEvents {
        ApplicationCacheErrorEvent create(String type, ApplicationCacheErrorEventInit eventInitDict);
    }

    // Generated from core\events\UIEvent.idl
    @Name("UIEvent")
    public interface UIEvent extends Event {
        @Getter Optional<Window> view();
        @Getter int detail();
        @Getter Optional<InputDeviceCapabilities> sourceCapabilities();
        void initUIEvent(String type, boolean bubbles, boolean cancelable, @Nullable Window view, int detail);
        @Getter int which();
    }

    @Statics("UIEvent")
    public interface UIEvents {
        UIEvent create(String type, UIEventInit eventInitDict);
    }

    // Generated from core\events\CustomEvent.idl
    @Name("CustomEvent")
    public interface CustomEvent extends Event {
        @Getter Object detail();
        void initCustomEvent(String type, boolean bubbles, boolean cancelable, Object detail);
    }

    @Statics("CustomEvent")
    public interface CustomEvents {
        CustomEvent create(String type, CustomEventInit eventInitDict);
    }

    // Generated from core\events\KeyboardEvent.idl
    @Name("KeyboardEvent")
    public interface KeyboardEvent extends UIEvent {
        int DOM_KEY_LOCATION_STANDARD = 0;
        int DOM_KEY_LOCATION_LEFT = 1;
        int DOM_KEY_LOCATION_RIGHT = 2;
        int DOM_KEY_LOCATION_NUMPAD = 3;
        @Getter String key();
        @Getter String code();
        @Getter int location();
        @Getter boolean ctrlKey();
        @Getter boolean shiftKey();
        @Getter boolean altKey();
        @Getter boolean metaKey();
        @Getter boolean repeat();
        boolean getModifierState(String keyArg);
        void initKeyboardEvent(String type, boolean bubbles, boolean cancelable, Window view, String keyIdentifier, int location, boolean ctrlKey, boolean altKey, boolean shiftKey, boolean metaKey);
        @Getter int charCode();
        @Getter int keyCode();
        @Getter int which();
        @Getter String keyIdentifier();
        @Getter int keyLocation();
    }

    @Statics("KeyboardEvent")
    public interface KeyboardEvents {
        KeyboardEvent create(String type, KeyboardEventInit eventInitDict);
    }

    // Generated from core\events\MessageEventInit.idl
    @Name("MessageEventInit")
    public static class MessageEventInit extends EventInit {
        public Object data;
        public String origin;
        public String lastEventId;
        public EventTarget source;
        public @Nullable List<MessagePort> ports;
    }

    // Generated from core\events\KeyboardEventInit.idl
    @Name("KeyboardEventInit")
    public static class KeyboardEventInit extends EventModifierInit {
        public int location = 0;
        public boolean repeat = false;
        public String keyIdentifier = "";
        public int keyLocation = 0;
    }

    // Generated from core\events\TransitionEventInit.idl
    @Name("TransitionEventInit")
    public static class TransitionEventInit extends EventInit {
        public String propertyName = "";
        public double elapsedTime = 0.0;
        public String pseudoElement = "";
    }

    // Generated from core\events\Event.idl
    @Name("Event")
    public interface Event {
        short NONE = (short) 0;
        short CAPTURING_PHASE = (short) 1;
        short AT_TARGET = (short) 2;
        short BUBBLING_PHASE = (short) 3;
        short MOUSEDOWN = (short) 1;
        short MOUSEUP = (short) 2;
        short MOUSEOVER = (short) 4;
        short MOUSEOUT = (short) 8;
        short MOUSEMOVE = (short) 16;
        short MOUSEDRAG = (short) 32;
        short CLICK = (short) 64;
        short DBLCLICK = (short) 128;
        short KEYDOWN = (short) 256;
        short KEYUP = (short) 512;
        short KEYPRESS = (short) 1024;
        short DRAGDROP = (short) 2048;
        short FOCUS = (short) 4096;
        short BLUR = (short) 8192;
        short SELECT = (short) 16384;
        short CHANGE = (short) 32768;
        @Getter String type();
        @Getter Optional<EventTarget> target();
        @Getter Optional<EventTarget> currentTarget();
        @Getter short eventPhase();
        void stopPropagation();
        void stopImmediatePropagation();
        @Getter boolean bubbles();
        @Getter boolean cancelable();
        void preventDefault();
        @Getter boolean defaultPrevented();
        @Getter boolean isTrusted();
        @Getter int timeStamp();
        void initEvent(String type, boolean bubbles, boolean cancelable);
        @Getter List<EventTarget> path();
        @Getter EventTarget srcElement();

        // ez inkompatiblis BeforeUnloadEventben lévőkkel, mert ott String a típusa
        // @Getter boolean returnValue();
        // @Setter void returnValue(boolean v);

        @Getter boolean cancelBubble();
        @Setter void cancelBubble(boolean v);
    }

    // Generated from core\events\PageTransitionEventInit.idl
    @Name("PageTransitionEventInit")
    public static class PageTransitionEventInit extends EventInit {
        public boolean persisted;
    }

    // Generated from core\events\DragEventInit.idl
    @Name("DragEventInit")
    public static class DragEventInit extends MouseEventInit {
        public @Nullable DataTransfer dataTransfer;
    }

    // Generated from core\events\DragEvent.idl
    @Name("DragEvent")
    public interface DragEvent extends MouseEvent {
        @Getter DataTransfer dataTransfer();
    }

    @Statics("DragEvent")
    public interface DragEvents {
        DragEvent create(String type, DragEventInit eventInitDict);
    }

    // Generated from core\events\EventTarget.idl
    @Name("EventTarget")
    public interface EventTarget {
        void addEventListener(@Nullable String type, @Nullable EventListener listener, boolean capture);
        void removeEventListener(@Nullable String type, @Nullable EventListener listener, boolean capture);
        boolean dispatchEvent(Event event);
    }

    // Generated from core\events\AutocompleteErrorEvent.idl
    @Name("AutocompleteErrorReason")
    public enum AutocompleteErrorReason {
        _EMPTY, cancel, disabled, invalid
    }

    // Generated from core\events\PopStateEvent.idl
    @Name("PopStateEvent")
    public interface PopStateEvent extends Event {
        @Getter Object state();
    }

    @Statics("PopStateEvent")
    public interface PopStateEvents {
        PopStateEvent create(String type, PopStateEventInit eventInitDict);
    }

    // Generated from core\events\EventListener.idl
    @FunctionalInterface
    @Name("EventListener")
    public interface EventListener {
        void handleEvent(Event event);
    }

    // Generated from core\events\ErrorEvent.idl
    @Name("ErrorEvent")
    public interface ErrorEvent extends Event {
        @Getter String message();
        @Getter String filename();
        @Getter int lineno();
        @Getter int colno();
        @Getter Object error();
    }

    @Statics("ErrorEvent")
    public interface ErrorEvents {
        ErrorEvent create(String type, ErrorEventInit eventInitDict);
    }

    // Generated from core\events\PageTransitionEvent.idl
    @Name("PageTransitionEvent")
    public interface PageTransitionEvent extends Event {
        @Getter boolean persisted();
    }

    @Statics("PageTransitionEvent")
    public interface PageTransitionEvents {
        PageTransitionEvent create(String type, PageTransitionEventInit eventInitDict);
    }

    // Generated from core\events\PromiseRejectionEventInit.idl
    @Name("PromiseRejectionEventInit")
    public static class PromiseRejectionEventInit extends EventInit {
        public @Nullable Future<Object> promise;
        public Object reason = null;
    }

    // Generated from core\events\FocusEvent.idl
    @Name("FocusEvent")
    public interface FocusEvent extends UIEvent {
        @Getter Optional<EventTarget> relatedTarget();
    }

    @Statics("FocusEvent")
    public interface FocusEvents {
        FocusEvent create(String type, FocusEventInit eventInitDict);
    }

    // Generated from core\events\TransitionEvent.idl
    @Name("TransitionEvent")
    public interface TransitionEvent extends Event {
        @Getter String propertyName();
        @Getter double elapsedTime();
        @Getter String pseudoElement();
    }

    @Statics("TransitionEvent")
    public interface TransitionEvents {
        TransitionEvent create(String type, TransitionEventInit eventInitDict);
    }

    // Generated from core\events\EventInit.idl
    @Name("EventInit")
    public static class EventInit {
        public boolean bubbles = false;
        public boolean cancelable = false;
    }

    // Generated from core\events\CompositionEvent.idl
    @Name("CompositionEvent")
    public interface CompositionEvent extends UIEvent {
        @Getter String data();
        void initCompositionEvent(String type, boolean bubbles, boolean cancelable, @Nullable Window view, String data);
    }

    @Statics("CompositionEvent")
    public interface CompositionEvents {
        CompositionEvent create(String type, CompositionEventInit eventInitDict);
    }
}
