package com.flyordie.code.browserapi;

import com.flyordie.code.browserapi.DOM.ArrayBuffer;
import com.flyordie.code.browserapi.DOM.ArrayBufferView;
import com.flyordie.code.browserapi.Events.Event;
import com.flyordie.code.browserapi.Events.EventInit;
import com.flyordie.code.browserapi.Events.EventTarget;
import com.flyordie.code.browserapi.FileAPI.Blob;
import com.flyordie.code.jsinterop.*;

@Statics("WebSocket")
public interface WebSockets {

    @Constructor WebSocket create(String url, /* String or List<String> */ Object protocols);

    // Generated from modules\websockets\WebSocket.idl
    @Name("BinaryType")
    public enum BinaryType {
        blob, arraybuffer
    }

    // Generated from modules\websockets\CloseEvent.idl
    @Name("CloseEvent")
    public interface CloseEvent extends Event {
        @Getter boolean wasClean();
        @Getter short code();
        @Getter String reason();
    }

    @Statics("CloseEvent")
    public interface CloseEvents {
        CloseEvent create(String type, CloseEventInit eventInitDict);
    }

    // Generated from modules\websockets\CloseEventInit.idl
    @Name("CloseEventInit")
    public static class CloseEventInit extends EventInit {
        public boolean wasClean;
        public short code;
        public String reason;
    }

    // Generated from modules\websockets\WebSocket.idl
    @Name("WebSocket")
    public interface WebSocket extends EventTarget {
        short CONNECTING = (short) 0;
        short OPEN = (short) 1;
        short CLOSING = (short) 2;
        short CLOSED = (short) 3;
        @Getter String url();
        @Getter short readyState();
        @Getter int bufferedAmount();
        @Getter EventHandler onopen();
        @Setter void onopen(EventHandler v);
        @Getter EventHandler onerror();
        @Setter void onerror(EventHandler v);
        @Getter EventHandler onclose();
        @Setter void onclose(EventHandler v);
        @Getter String extensions();
        @Getter String protocol();
        void close(short code, String reason);
        @Getter EventHandler onmessage();
        @Setter void onmessage(EventHandler v);
        @Getter BinaryType binaryType();
        @Setter void binaryType(BinaryType v);
        void send(String data);
        void send(Blob data);
        void send(ArrayBuffer data);
        void send(ArrayBufferView data);
    }

}
