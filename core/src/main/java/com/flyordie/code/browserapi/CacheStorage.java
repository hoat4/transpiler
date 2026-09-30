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

import com.flyordie.code.browserapi.Fetch.Response;
import com.flyordie.code.browserapi.CacheStorage.CacheQueryOptions;
import com.flyordie.code.browserapi.Fetch.Request;
import com.flyordie.code.browserapi.CacheStorage.Cache;

// Generated from modules\cachestorage\CacheStorage.idl
@Name("CacheStorage")
public interface CacheStorage {

    Future<Boolean> has(String cacheName);
    Future<Cache> open(String cacheName);
    Future<Boolean> delete(String cacheName);
    Future<List<String>> keys();
    Future<Response> match(/* Request or String */ Object request, CacheQueryOptions options);

    // Generated from modules\cachestorage\CacheQueryOptions.idl
    @Name("CacheQueryOptions")
    class CacheQueryOptions {
        public boolean ignoreSearch = false;
        public boolean ignoreMethod = false;
        public boolean ignoreVary = false;
        public String cacheName;
    }

    // Generated from modules\cachestorage\Cache.idl
    @Name("Cache")
    interface Cache {
        Future<Response> match(/* Request or String */ Object request, CacheQueryOptions options);
        Future<List<Response>> matchAll(/* Request or String */ Object request, CacheQueryOptions options);
        Future<Void> add(/* Request or String */ Object request);
        Future<Void> addAll(List</* Request or String */ Object> requests);
        Future<Void> put(/* Request or String */ Object request, Response response);
        Future<Boolean> delete(/* Request or String */ Object request, CacheQueryOptions options);
        Future<List<Response>> keys(/* Request or String */ Object request, CacheQueryOptions options);
    }

}
