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

import com.flyordie.code.browserapi.Events.EventInit;
import com.flyordie.code.browserapi.Events.Event;
import com.flyordie.code.browserapi.Storage.StorageEventInit;

// Generated from modules\storage\Storage.idl
@Name("Storage")
public interface Storage {

    String get(int index);
    String set(int index, String value);
    boolean delete(int index);
    String get(String name);
    String set(String name, String value);
    boolean delete(String name);
    @Getter int length();
    @Nullable String key(int index);
    @Nullable String getItem(String key);
    void setItem(String key, String data);
    void removeItem(String key);
    void clear();

    // Generated from modules\storage\StorageEventInit.idl
    @Name("StorageEventInit")
    class StorageEventInit extends EventInit {
        public @Nullable String key;
        public @Nullable String oldValue;
        public @Nullable String newValue;
        public String url;
        public @Nullable Storage storageArea;
    }

    // Generated from modules\storage\StorageEvent.idl
    @Name("StorageEvent")
    interface StorageEvent extends Event {
        @Getter Optional<String> key();
        @Getter Optional<String> oldValue();
        @Getter Optional<String> newValue();
        @Getter String url();
        @Getter Optional<Storage> storageArea();
        void initStorageEvent(String typeArg, boolean canBubbleArg, boolean cancelableArg, String keyArg, @Nullable String oldValueArg, @Nullable String newValueArg, String urlArg, Storage storageAreaArg);
    }

    @Statics("StorageEvent")
    public interface StorageEvents {
        StorageEvent create(String type, StorageEventInit eventInitDict);
    }

}
