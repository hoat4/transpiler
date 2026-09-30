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

import com.flyordie.code.browserapi.HTML.FormData;
import com.flyordie.code.browserapi.DOM.ArrayBufferView;
import com.flyordie.code.browserapi.FileAPI.Blob;
import com.flyordie.code.browserapi.XMLHttpRequestAPI.XMLHttpRequestEventTarget;
import com.flyordie.code.browserapi.XMLHttpRequestAPI.XMLHttpRequestUpload;
import com.flyordie.code.browserapi.DOM.ArrayBuffer;
import com.flyordie.code.browserapi.Events.ProgressEvent;
import com.flyordie.code.browserapi.DOM.Document;
import com.flyordie.code.browserapi.Events.EventTarget;

public class XMLHttpRequestAPI {

    private XMLHttpRequestAPI() {
        throw new Error("should not instantiate");
    }

    // Generated from core\xmlhttprequest\XMLHttpRequestUpload.idl
    @Name("XMLHttpRequestUpload")
    public interface XMLHttpRequestUpload extends XMLHttpRequestEventTarget {
    }

    // Generated from core\xmlhttprequest\XMLHttpRequestEventTarget.idl
    @Name("XMLHttpRequestEventTarget")
    public interface XMLHttpRequestEventTarget extends EventTarget {
        @Getter EventHandler onloadstart();
        @Setter void onloadstart(EventHandler v);
        @Getter EventHandler onprogress();
        @Setter void onprogress(EventHandler v);
        @Getter EventHandler onabort();
        @Setter void onabort(EventHandler v);
        @Getter EventHandler onerror();
        @Setter void onerror(EventHandler v);
        @Getter EventHandler onload();
        @Setter void onload(EventHandler v);
        @Getter EventHandler ontimeout();
        @Setter void ontimeout(EventHandler v);
        @Getter EventHandler onloadend();
        @Setter void onloadend(EventHandler v);
    }

    // Generated from core\xmlhttprequest\XMLHttpRequestProgressEvent.idl
    @Name("XMLHttpRequestProgressEvent")
    public interface XMLHttpRequestProgressEvent extends ProgressEvent {
        @Getter int position();
        @Getter int totalSize();
    }

    // Generated from core\xmlhttprequest\XMLHttpRequest.idl
    @Name("XMLHttpRequestResponseType")
    public enum XMLHttpRequestResponseType {
        _EMPTY, arraybuffer, blob, document, 
        json, text, legacystream
    }

    // Generated from core\xmlhttprequest\XMLHttpRequest.idl
    @Name("XMLHttpRequest")
    public interface XMLHttpRequest extends XMLHttpRequestEventTarget {
        short UNSENT = (short) 0;
        short OPENED = (short) 1;
        short HEADERS_RECEIVED = (short) 2;
        short LOADING = (short) 3;
        short DONE = (short) 4;
        @Getter EventHandler onreadystatechange();
        @Setter void onreadystatechange(EventHandler v);
        @Getter short readyState();
        void open(String method, String url);
        void open(String method, String url, boolean async, @Nullable String username, @Nullable String password);
        void setRequestHeader(String name, String value);
        @Getter int timeout();
        @Setter void timeout(int v);
        @Getter boolean withCredentials();
        @Setter void withCredentials(boolean v);
        @Getter XMLHttpRequestUpload upload();
        void send(@Nullable /* ArrayBuffer or ArrayBufferView or Blob or Document or String or FormData */ Object body);
        void abort();
        @Getter String responseURL();
        @Getter short status();
        @Getter String statusText();
        @Nullable String getResponseHeader(String name);
        String getAllResponseHeaders();
        void overrideMimeType(String mime);
        @Getter XMLHttpRequestResponseType responseType();
        @Setter void responseType(XMLHttpRequestResponseType v);
        @Getter Object response();
        @Getter String responseText();
        @Getter Optional<Document> responseXML();
    }

}
