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

import com.flyordie.code.browserapi.PresentationAPI.PresentationSessionConnectEventInit;
import com.flyordie.code.browserapi.FileAPI.Blob;
import com.flyordie.code.browserapi.DOM.ArrayBufferView;
import com.flyordie.code.browserapi.Events.EventInit;
import com.flyordie.code.browserapi.PresentationAPI.PresentationRequest;
import com.flyordie.code.browserapi.Events.Event;
import com.flyordie.code.browserapi.PresentationAPI.PresentationAvailability;
import com.flyordie.code.browserapi.DOM.ArrayBuffer;
import com.flyordie.code.browserapi.Events.EventTarget;
import com.flyordie.code.browserapi.PresentationAPI.PresentationSession;

public class PresentationAPI {

    private PresentationAPI() {
        throw new Error("should not instantiate");
    }

    // Generated from modules\presentation\Presentation.idl
    @Name("Presentation")
    public interface Presentation extends EventTarget {
        @Getter @Nullable PresentationRequest defaultRequest();
        @Setter void defaultRequest(@Nullable PresentationRequest v);
        @Getter Optional<PresentationSession> session();
    }

    // Generated from modules\presentation\PresentationSession.idl
    @Name("PresentationSessionState")
    public enum PresentationSessionState {
        connected, disconnected
    }

    // Generated from modules\presentation\PresentationAvailability.idl
    @Name("PresentationAvailability")
    public interface PresentationAvailability extends EventTarget {
        @Getter boolean value();
        @Getter EventHandler onchange();
        @Setter void onchange(EventHandler v);
    }

    // Generated from modules\presentation\PresentationSession.idl
    @Name("PresentationSession")
    public interface PresentationSession extends EventTarget {
        @Getter Optional<String> id();
        @Getter PresentationSessionState state();
        void close();
        @Getter EventHandler onstatechange();
        @Setter void onstatechange(EventHandler v);
        @Getter BinaryType binaryType();
        @Setter void binaryType(BinaryType v);
        @Getter EventHandler onmessage();
        @Setter void onmessage(EventHandler v);
        void send(String message);
        void send(Blob data);
        void send(ArrayBuffer data);
        void send(ArrayBufferView data);
    }

    // Generated from modules\presentation\PresentationSessionConnectEvent.idl
    @Name("PresentationSessionConnectEvent")
    public interface PresentationSessionConnectEvent extends Event {
        @Getter PresentationSession session();
    }

    @Statics("PresentationSessionConnectEvent")
    public interface PresentationSessionConnectEvents {
        PresentationSessionConnectEvent create(String type, PresentationSessionConnectEventInit eventInitDict);
    }

    // Generated from modules\presentation\PresentationRequest.idl
    @Name("PresentationRequest")
    public interface PresentationRequest extends EventTarget {
        Future<PresentationSession> start();
        Future<PresentationSession> reconnect(String id);
        Future<PresentationAvailability> getAvailability();
        @Getter EventHandler onsessionconnect();
        @Setter void onsessionconnect(EventHandler v);
    }

    @Statics("PresentationRequest")
    public interface PresentationRequests {
        PresentationRequest create(String url);
    }

    // Generated from modules\presentation\PresentationSessionConnectEventInit.idl
    @Name("PresentationSessionConnectEventInit")
    public static class PresentationSessionConnectEventInit extends EventInit {
        public PresentationSession session;
    }

}
