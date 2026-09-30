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

import com.flyordie.code.browserapi.DOM.ArrayBufferView;
import com.flyordie.code.browserapi.Encoding.TextDecodeOptions;
import com.flyordie.code.browserapi.DOM.Uint8Array;
import com.flyordie.code.browserapi.DOM.ArrayBuffer;
import com.flyordie.code.browserapi.Encoding.TextDecoderOptions;

public class Encoding {

    private Encoding() {
        throw new Error("should not instantiate");
    }

    // Generated from modules\encoding\TextDecodeOptions.idl
    @Name("TextDecodeOptions")
    public static class TextDecodeOptions {
        public boolean stream = false;
    }

    // Generated from modules\encoding\TextEncoder.idl
    @Name("TextEncoder")
    public interface TextEncoder {
        @Getter String encoding();
        Uint8Array encode(String input);
    }

    @Statics("TextEncoder")
    public interface TextEncoders {
        TextEncoder create(String utfLabel);
    }

    // Generated from modules\encoding\TextDecoderOptions.idl
    @Name("TextDecoderOptions")
    public static class TextDecoderOptions {
        public boolean fatal = false;
        public boolean ignoreBOM = false;
    }

    // Generated from modules\encoding\TextDecoder.idl
    @Name("TextDecoder")
    public interface TextDecoder {
        @Getter String encoding();
        @Getter boolean fatal();
        @Getter boolean ignoreBOM();
        String decode(/* ArrayBuffer or ArrayBufferView */ Object input, TextDecodeOptions options);
    }

    @Statics("TextDecoder")
    public interface TextDecoders {
        TextDecoder create(String label, TextDecoderOptions options);
    }

}
