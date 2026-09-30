package com.flyordie.code.browserapi.impl;

import com.flyordie.code.browserapi.DOM.ArrayBuffer;
import com.flyordie.code.browserapi.DOM.ArrayBufferView;
import com.flyordie.code.browserapi.DOM.MessagePort;
import com.flyordie.code.browserapi.EventHandler;
import com.flyordie.code.browserapi.Events.Event;
import com.flyordie.code.browserapi.Events.EventListener;
import com.flyordie.code.browserapi.Events.EventTarget;
import com.flyordie.code.browserapi.Events.MessageEvent;
import com.flyordie.code.browserapi.FileAPI.Blob;
import com.flyordie.code.browserapi.FrameAPI.Window;
import com.flyordie.code.browserapi.WebSockets;
import com.flyordie.code.browserapi.WebSockets.BinaryType;
import com.flyordie.code.browserapi.WebSockets.CloseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class WebSocketImpl implements WebSockets.WebSocket {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketImpl.class);

    public final WebSocket.Builder wsBuilder = HttpClient.newHttpClient().newWebSocketBuilder().
            connectTimeout(Duration.ofSeconds(2));
    private EventHandler onopen, onmessage, onclose;
    private java.net.http.WebSocket jWebSocket;
    private String url;

    public static class WebSocketsImpl implements WebSockets {

        @Override
        public WebSocket create(String url, Object protocols) {
            WebSocketImpl w = new WebSocketImpl();
            w.url = url;
            return w;
        }
    }

    @Override
    public void addEventListener(@Nullable String type, @Nullable EventListener listener, boolean capture) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeEventListener(@Nullable String type, @Nullable EventListener listener, boolean capture) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean dispatchEvent(Event event) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String url() {
        throw new UnsupportedOperationException();
    }

    @Override
    public short readyState() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int bufferedAmount() {
        throw new UnsupportedOperationException();
    }

    @Override
    public EventHandler onopen() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onopen(EventHandler v) {
        onopen = v;
    }

    @Override
    public EventHandler onerror() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onerror(EventHandler v) {
        // TODO
    }

    @Override
    public EventHandler onclose() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onclose(EventHandler v) {
        this.onclose = v;
    }

    @Override
    public String extensions() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String protocol() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close(short code, String reason) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EventHandler onmessage() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onmessage(EventHandler v) {
        onmessage = v;
        // System.out.println("OM");
        wsBuilder.buildAsync(URI.create(url), new Listener() {

            private StringBuilder sb = new StringBuilder();

            @Override
            public void onOpen(WebSocket webSocket) {
                jWebSocket = webSocket;
                if (onopen != null)
                    onopen.handleEvent(null);
                Listener.super.onOpen(webSocket);
            }

            @Override
            public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                sb.append(data);
                if (last) {
                    if (onmessage != null) {
                        final String s = sb.toString();
                        EventQueue.invokeLater(() -> { // TODO
                            onmessage.handleEvent(new MessageEventImpl(s));
                        });
                    }
                    sb.setLength(0);
                }
                return Listener.super.onText(webSocket, data, last);
            }

            @Override
            public void onError(WebSocket webSocket, Throwable error) {
                error.printStackTrace();
            }

            @Override
            public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
                System.out.println("WS CLOSED " + statusCode + " " + reason);
                return Listener.super.onClose(webSocket, statusCode, reason);
            }
        }).whenComplete((ws, e) -> {
            if (e != null) {
                logger.error("Couldn't open connection: " + e);
                // e.printStackTrace(); sokat szemetelt, elég csak a tostringet kiírni
                onclose.handleEvent(new CloseEvent() {
                    @Override
                    public boolean wasClean() {
                        return false;
                    }

                    @Override
                    public short code() {
                        return 0;
                    }

                    @Override
                    public String reason() {
                        return null;
                    }

                    @Override
                    public String type() {
                        return null;
                    }

                    @Override
                    public Optional<EventTarget> target() {
                        return Optional.empty();
                    }

                    @Override
                    public Optional<EventTarget> currentTarget() {
                        return Optional.empty();
                    }

                    @Override
                    public short eventPhase() {
                        return 0;
                    }

                    @Override
                    public void stopPropagation() {

                    }

                    @Override
                    public void stopImmediatePropagation() {

                    }

                    @Override
                    public boolean bubbles() {
                        return false;
                    }

                    @Override
                    public boolean cancelable() {
                        return false;
                    }

                    @Override
                    public void preventDefault() {

                    }

                    @Override
                    public boolean defaultPrevented() {
                        return false;
                    }

                    @Override
                    public boolean isTrusted() {
                        return false;
                    }

                    @Override
                    public int timeStamp() {
                        return 0;
                    }

                    @Override
                    public void initEvent(String type, boolean bubbles, boolean cancelable) {

                    }

                    @Override
                    public List<EventTarget> path() {
                        return null;
                    }

                    @Override
                    public EventTarget srcElement() {
                        return null;
                    }

                    @Override
                    public boolean cancelBubble() {
                        return false;
                    }

                    @Override
                    public void cancelBubble(boolean v) {

                    }
                });
            }
        });
    }

    @Override
    public BinaryType binaryType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void binaryType(BinaryType v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void send(String data) {
        jWebSocket.sendText(data, true);
    }

    @Override
    public void send(Blob data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void send(ArrayBuffer data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void send(ArrayBufferView data) {
        throw new UnsupportedOperationException();
    }

    private static class MessageEventImpl implements MessageEvent {

        private final String fullMsg;

        public MessageEventImpl(String fullMsg) {
            this.fullMsg = fullMsg;
        }

        @Override
        public Object data() {
            return fullMsg;
        }

        @Override
        public String origin() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String lastEventId() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<EventTarget> source() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<List<MessagePort>> ports() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void initMessageEvent(String typeArg, boolean canBubbleArg, boolean cancelableArg, Object dataArg, String originArg, String lastEventIdArg, Window sourceArg, List<MessagePort> portsArg) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String type() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<EventTarget> target() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<EventTarget> currentTarget() {
            throw new UnsupportedOperationException();
        }

        @Override
        public short eventPhase() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void stopPropagation() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void stopImmediatePropagation() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean bubbles() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean cancelable() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void preventDefault() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean defaultPrevented() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isTrusted() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int timeStamp() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void initEvent(String type, boolean bubbles, boolean cancelable) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<EventTarget> path() {
            throw new UnsupportedOperationException();
        }

        @Override
        public EventTarget srcElement() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean cancelBubble() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void cancelBubble(boolean v) {
            throw new UnsupportedOperationException();
        }
    }
}
