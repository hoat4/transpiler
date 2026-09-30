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
import com.flyordie.code.browserapi.Fetch.Response;
import com.flyordie.code.browserapi.FileAPI.Blob;
import com.flyordie.code.browserapi.DOM.ArrayBufferView;
import com.flyordie.code.browserapi.Fetch.Headers;
import com.flyordie.code.browserapi.Fetch.Request;
import com.flyordie.code.browserapi.DOM.ArrayBuffer;
import com.flyordie.code.browserapi.Fetch.Body;
import com.flyordie.code.browserapi.Streams.ReadableByteStream;

public class Fetch {

    private Fetch() {
        throw new Error("should not instantiate");
    }

    // Generated from modules\fetch\Request.idl
    @Name("RequestCredentials")
    public enum RequestCredentials {
        omit, sameOrigin, include
    }

    // Generated from modules\fetch\Response.idl
    @Name("ResponseType")
    public enum ResponseType {
        basic, cors, default_, error, 
        opaque, opaqueredirect
    }

    // Generated from modules\fetch\Headers.idl
    @Name("Headers")
    public interface Headers {
        void append(String name, String value);
        void delete(String key);
        @Nullable String get(String key);
        List<String> getAll(String name);
        boolean has(String key);
        void set(String key, String value);
    }

    @Statics("Headers")
    public interface Headeres {
        Headers create(Headers input);
        Headers create(Map<String, Object> input);
        Headers create(List<List<String>> input);
    }

    // Generated from modules\fetch\Body.idl
    @Name("Body")
    public interface Body {
        @Getter boolean bodyUsed();
        Future<ArrayBuffer> arrayBuffer();
        Future<Blob> blob();
        Future<Object> json();
        Future<String> text();
    }

    // Generated from modules\fetch\Request.idl
    @Name("RequestContext")
    public enum RequestContext {
        _EMPTY, audio, beacon, cspreport, 
        download, embed, eventsource, favicon, 
        fetch, font, form, frame, 
        hyperlink, iframe, image, imageset, 
        import_, internal, location, manifest, 
        metarefresh, object, ping, plugin, 
        prefetch, script, serviceworker, sharedworker, 
        subresource, style, track, video, 
        worker, xmlhttprequest, xslt
    }

    // Generated from modules\fetch\Request.idl, modules\fetch\Request.idl
    @Name("Request")
    public interface Request extends Body {
        @Getter String method();
        @Getter String url();
        @Getter Headers headers();
        @Getter String referrer();
        @Getter RequestMode mode();
        @Getter RequestCredentials credentials();
        @Getter RequestRedirect redirect();
        @Getter String integrity();
        Request clone();
    }

    @Statics("Request")
    public interface Requests {
        Request create(/* Request or String */ Object input, Map<String, Object> requestInitDict);
    }

    // Generated from modules\fetch\Request.idl
    @Name("RequestMode")
    public enum RequestMode {
        sameOrigin, noCors, cors
    }

    // Generated from modules\fetch\Request.idl
    @Name("RequestRedirect")
    public enum RequestRedirect {
        follow, error, manual
    }

    // Generated from modules\fetch\Response.idl, modules\fetch\Response.idl
    @Name("Response")
    public interface Response extends Body {
        @Getter ResponseType type();
        @Getter String url();
        @Getter short status();
        @Getter boolean ok();
        @Getter String statusText();
        @Getter Headers headers();
        Response clone();
        @Getter ReadableByteStream body();
    }

    @Statics("Response")
    @Name("Responses")
    public interface Responses {
        Response create(/* Blob or ArrayBuffer or ArrayBufferView or FormData or String */ Object body, Map<String, Object> responseInitDict);
        Response error();
        Response redirect(String url, short status);
    }

}
