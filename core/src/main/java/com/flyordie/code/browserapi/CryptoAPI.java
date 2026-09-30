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
import com.flyordie.code.browserapi.CryptoAPI.SubtleCrypto;
import com.flyordie.code.browserapi.CryptoAPI.CryptoKey;
import com.flyordie.code.browserapi.DOM.ArrayBuffer;

public class CryptoAPI {

    private CryptoAPI() {
        throw new Error("should not instantiate");
    }

    // Generated from modules\crypto\Crypto.idl
    @Name("Crypto")
    public interface Crypto {
        ArrayBufferView getRandomValues(ArrayBufferView array);
        @Getter SubtleCrypto subtle();
    }

    // Generated from modules\crypto\SubtleCrypto.idl
    @Name("SubtleCrypto")
    public interface SubtleCrypto {
        Future<Object> encrypt(/* Map<String, Object> or String */ Object algorithm, CryptoKey key, /* ArrayBuffer or ArrayBufferView */ Object data);
        Future<Object> decrypt(/* Map<String, Object> or String */ Object algorithm, CryptoKey key, /* ArrayBuffer or ArrayBufferView */ Object data);
        Future<Object> sign(/* Map<String, Object> or String */ Object algorithm, CryptoKey key, /* ArrayBuffer or ArrayBufferView */ Object data);
        Future<Object> verify(/* Map<String, Object> or String */ Object algorithm, CryptoKey key, /* ArrayBuffer or ArrayBufferView */ Object signature, /* ArrayBuffer or ArrayBufferView */ Object data);
        Future<Object> digest(/* Map<String, Object> or String */ Object algorithm, /* ArrayBuffer or ArrayBufferView */ Object data);
        Future<Object> generateKey(/* Map<String, Object> or String */ Object algorithm, boolean extractable, List<String> keyUsages);
        Future<Object> importKey(String format, /* ArrayBuffer or ArrayBufferView or Map<String, Object> */ Object keyData, /* Map<String, Object> or String */ Object algorithm, boolean extractable, List<String> keyUsages);
        Future<Object> exportKey(String format, CryptoKey key);
        Future<Object> deriveBits(/* Map<String, Object> or String */ Object algorithm, CryptoKey baseKey, int length);
        Future<Object> deriveKey(/* Map<String, Object> or String */ Object algorithm, CryptoKey baseKey, /* Map<String, Object> or String */ Object derivedKeyType, boolean extractable, List<String> keyUsages);
        Future<Object> wrapKey(String format, CryptoKey key, CryptoKey wrappingKey, /* Map<String, Object> or String */ Object wrapAlgorithm);
        Future<Object> unwrapKey(String format, /* ArrayBuffer or ArrayBufferView */ Object wrappedKey, CryptoKey unwrappingKey, /* Map<String, Object> or String */ Object unwrapAlgorithm, /* Map<String, Object> or String */ Object unwrappedKeyAlgorithm, boolean extractable, List<String> keyUsages);
    }

    // Generated from modules\crypto\CryptoKey.idl
    @Name("CryptoKey")
    public interface CryptoKey {
        @Getter String type();
        @Getter boolean extractable();
        @Getter Object algorithm();
        @Getter List<String> usages();
    }

}
