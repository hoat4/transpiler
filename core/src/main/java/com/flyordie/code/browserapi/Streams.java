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

import com.flyordie.code.browserapi.Streams.ReadableByteStreamReader;
import com.flyordie.code.browserapi.Streams.ReadableStreamReader;

public class Streams {

    private Streams() {
        throw new Error("should not instantiate");
    }

    // Generated from core\streams\ReadableStream.idl
    @Name("ReadableStream")
    public interface ReadableStream {
        ReadableStreamReader getReader();
        Future<Void> cancel(Object reason);
    }

    // Generated from core\streams\ReadableByteStream.idl
    @Name("ReadableByteStream")
    public interface ReadableByteStream {
        ReadableByteStreamReader getReader();
        Future<Void> cancel(Object reason);
    }

    // Generated from core\streams\ReadableByteStreamReader.idl
    @Name("ReadableByteStreamReader")
    public interface ReadableByteStreamReader {
        @Getter Future<Void> closed();
        Future<Object> read();
        Future<Void> cancel(Object reason);
        void releaseLock();
    }

    // Generated from core\streams\Stream.idl
    @Name("Stream")
    public interface Stream {
        @Getter String type();
    }

    // Generated from core\streams\ReadableStreamReader.idl
    @Name("ReadableStreamReader")
    public interface ReadableStreamReader {
        @Getter Future<Void> closed();
        Future<Object> read();
        Future<Void> cancel(Object reason);
        void releaseLock();
    }

}
