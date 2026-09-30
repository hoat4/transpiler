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

import com.flyordie.code.browserapi.IndexedDB.IDBOpenDBRequest;
import com.flyordie.code.browserapi.IndexedDB.IDBObjectStore;
import com.flyordie.code.browserapi.IndexedDB.IDBDatabase;
import com.flyordie.code.browserapi.DOM.DOMStringList;
import com.flyordie.code.browserapi.DOM.DOMError;
import com.flyordie.code.browserapi.IndexedDB.IDBIndexParameters;
import com.flyordie.code.browserapi.Events.EventInit;
import com.flyordie.code.browserapi.IndexedDB.IDBObjectStoreParameters;
import com.flyordie.code.browserapi.IndexedDB.IDBKeyRange;
import com.flyordie.code.browserapi.IndexedDB.IDBCursor;
import com.flyordie.code.browserapi.IndexedDB.IDBTransaction;
import com.flyordie.code.browserapi.Events.Event;
import com.flyordie.code.browserapi.IndexedDB.IDBVersionChangeEventInit;
import com.flyordie.code.browserapi.IndexedDB.IDBRequest;
import com.flyordie.code.browserapi.Events.EventTarget;
import com.flyordie.code.browserapi.IndexedDB.IDBIndex;

public class IndexedDB {

    private IndexedDB() {
        throw new Error("should not instantiate");
    }

    // Generated from modules\indexeddb\IDBObjectStoreParameters.idl
    @Name("IDBObjectStoreParameters")
    public static class IDBObjectStoreParameters {
        public @Nullable /* String or List<String> */ Object keyPath = null;
        public boolean autoIncrement = false;
    }

    // Generated from modules\indexeddb\IDBCursor.idl
    @Name("IDBCursorDirection")
    public enum IDBCursorDirection {
        next, nextunique, prev, prevunique
    }

    // Generated from modules\indexeddb\IDBCursorWithValue.idl
    @Name("IDBCursorWithValue")
    public interface IDBCursorWithValue extends IDBCursor {
        @Getter Object value();
    }

    // Generated from modules\indexeddb\IDBDatabase.idl
    @Name("IDBDatabase")
    public interface IDBDatabase extends EventTarget {
        @Getter String name();
        @Getter /* Integer or String */ Object version();
        @Getter DOMStringList objectStoreNames();
        IDBObjectStore createObjectStore(String name, IDBObjectStoreParameters options);
        void deleteObjectStore(String name);
        IDBTransaction transaction(/* String or List<String> or DOMStringList */ Object storeNames, IDBTransactionMode mode);
        void close();
        @Getter EventHandler onabort();
        @Setter void onabort(EventHandler v);
        @Getter EventHandler onclose();
        @Setter void onclose(EventHandler v);
        @Getter EventHandler onerror();
        @Setter void onerror(EventHandler v);
        @Getter EventHandler onversionchange();
        @Setter void onversionchange(EventHandler v);
    }

    // Generated from modules\indexeddb\IDBTransaction.idl
    @Name("IDBTransactionMode")
    public enum IDBTransactionMode {
        readonly, readwrite, versionchange
    }

    // Generated from modules\indexeddb\IDBObjectStore.idl
    @Name("IDBObjectStore")
    public interface IDBObjectStore {
        @Getter String name();
        @Getter Object keyPath();
        @Getter DOMStringList indexNames();
        @Getter IDBTransaction transaction();
        @Getter boolean autoIncrement();
        IDBRequest put(Object value, Object key);
        IDBRequest add(Object value, Object key);
        IDBRequest delete(Object key);
        IDBRequest clear();
        IDBRequest get(Object key);
        IDBRequest getAll(Object range, int maxCount);
        IDBRequest getAllKeys(Object range, int maxCount);
        IDBRequest count(Object key);
        IDBRequest openCursor(Object range, IDBCursorDirection direction);
        IDBRequest openKeyCursor(Object range, IDBCursorDirection direction);
        IDBIndex createIndex(String name, /* String or List<String> */ Object keyPath, IDBIndexParameters options);
        IDBIndex index(String name);
        void deleteIndex(String name);
    }

    // Generated from modules\indexeddb\IDBVersionChangeEventInit.idl
    @Name("IDBVersionChangeEventInit")
    public static class IDBVersionChangeEventInit extends EventInit {
        public int oldVersion = 0;
        public @Nullable Integer newVersion = null;
        public IDBDataLossAmount dataLoss = IDBDataLossAmount.none;
    }

    // Generated from modules\indexeddb\IDBCursor.idl
    @Name("IDBCursor")
    public interface IDBCursor {
        @Getter Object source();
        @Getter IDBCursorDirection direction();
        @Getter Object key();
        @Getter Object primaryKey();
        IDBRequest update(Object value);
        void advance(int count);
        @Name("continue") void continue_(Object key);
        void continuePrimaryKey(Object key, Object primaryKey);
        IDBRequest delete();
    }

    // Generated from modules\indexeddb\IDBIndex.idl
    @Name("IDBIndex")
    public interface IDBIndex {
        @Getter String name();
        @Getter IDBObjectStore objectStore();
        @Getter Object keyPath();
        @Getter boolean multiEntry();
        @Getter boolean unique();
        IDBRequest get(Object key);
        IDBRequest getKey(Object key);
        IDBRequest getAll(Object range, int maxCount);
        IDBRequest getAllKeys(Object range, int maxCount);
        IDBRequest count(Object key);
        IDBRequest openCursor(Object range, IDBCursorDirection direction);
        IDBRequest openKeyCursor(Object range, IDBCursorDirection direction);
    }

    // Generated from modules\indexeddb\IDBTransaction.idl
    @Name("IDBTransaction")
    public interface IDBTransaction extends EventTarget {
        @Getter DOMStringList objectStoreNames();
        @Getter IDBTransactionMode mode();
        @Getter IDBDatabase db();
        @Getter DOMError error();
        IDBObjectStore objectStore(String name);
        void abort();
        @Getter EventHandler onabort();
        @Setter void onabort(EventHandler v);
        @Getter EventHandler oncomplete();
        @Setter void oncomplete(EventHandler v);
        @Getter EventHandler onerror();
        @Setter void onerror(EventHandler v);
    }

    // Generated from modules\indexeddb\IDBOpenDBRequest.idl
    @Name("IDBOpenDBRequest")
    public interface IDBOpenDBRequest extends IDBRequest {
        @Getter EventHandler onblocked();
        @Setter void onblocked(EventHandler v);
        @Getter EventHandler onupgradeneeded();
        @Setter void onupgradeneeded(EventHandler v);
    }

    // Generated from modules\indexeddb\IDBRequest.idl
    @Name("IDBRequest")
    public interface IDBRequest extends EventTarget {
        @Getter Object result();
        @Getter DOMError error();
        @Getter Object source();
        @Getter IDBTransaction transaction();
        @Getter IDBRequestReadyState readyState();
        @Getter EventHandler onsuccess();
        @Setter void onsuccess(EventHandler v);
        @Getter EventHandler onerror();
        @Setter void onerror(EventHandler v);
    }

    // Generated from modules\indexeddb\IDBFactory.idl
    @Name("IDBFactory")
    public interface IDBFactory {
        IDBRequest webkitGetDatabaseNames();
        IDBOpenDBRequest open(String name, int version);
        IDBOpenDBRequest deleteDatabase(String name);
        short cmp(Object first, Object second);
    }

    // Generated from modules\indexeddb\IDBIndexParameters.idl
    @Name("IDBIndexParameters")
    public static class IDBIndexParameters {
        public boolean unique = false;
        public boolean multiEntry = false;
    }

    // Generated from modules\indexeddb\IDBVersionChangeEvent.idl
    @Name("IDBVersionChangeEvent")
    public interface IDBVersionChangeEvent extends Event {
        @Getter int oldVersion();
        @Getter Optional<Integer> newVersion();
        @Getter IDBDataLossAmount dataLoss();
        @Getter String dataLossMessage();
    }

    @Statics("IDBVersionChangeEvent")
    public interface IDBVersionChangeEvents {
        IDBVersionChangeEvent create(String type, IDBVersionChangeEventInit eventInitDict);
    }

    // Generated from modules\indexeddb\IDBKeyRange.idl
    @Name("IDBKeyRange")
    public interface IDBKeyRange {
        @Getter Object lower();
        @Getter Object upper();
        @Getter boolean lowerOpen();
        @Getter boolean upperOpen();
    }

    @Statics("IDBKeyRange")
    @Name("IDBKeyRanges")
    public interface IDBKeyRanges {
        IDBKeyRange only(Object value);
        IDBKeyRange lowerBound(Object bound, boolean open);
        IDBKeyRange upperBound(Object bound, boolean open);
        IDBKeyRange bound(Object lower, Object upper, boolean lowerOpen, boolean upperOpen);
    }

    // Generated from modules\indexeddb\IDBVersionChangeEvent.idl
    @Name("IDBDataLossAmount")
    public enum IDBDataLossAmount {
        none, total
    }

    // Generated from modules\indexeddb\IDBRequest.idl
    @Name("IDBRequestReadyState")
    public enum IDBRequestReadyState {
        pending, done
    }

}
