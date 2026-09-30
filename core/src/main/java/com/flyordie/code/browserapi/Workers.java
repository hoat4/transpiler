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

import com.flyordie.code.browserapi.Quota.StorageManager;
import com.flyordie.code.browserapi.Workers.WorkerLocation;
import com.flyordie.code.browserapi.Workers.WorkerNavigator;
import com.flyordie.code.browserapi.FrameAPI.WindowTimers;
import com.flyordie.code.browserapi.GeoFencing.Geofencing;
import com.flyordie.code.browserapi.FileSystemAPI.EntrySync;
import com.flyordie.code.browserapi.IndexedDB.IDBFactory;
import com.flyordie.code.browserapi.Workers.WorkerConsole;
import com.flyordie.code.browserapi.DOM.MessagePort;
import com.flyordie.code.browserapi.Workers.AbstractWorker;
import com.flyordie.code.browserapi.PermissionsAPI.Permissions;
import com.flyordie.code.browserapi.FileSystemAPI.DOMFileSystemSync;
import com.flyordie.code.browserapi.DOM.URLUtilsReadOnly;
import com.flyordie.code.browserapi.FileSystemAPI.EntryCallback;
import com.flyordie.code.browserapi.Events.EventTarget;
import com.flyordie.code.browserapi.Quota.DeprecatedStorageQuota;
import com.flyordie.code.browserapi.FrameAPI.NavigatorOnLine;
import com.flyordie.code.browserapi.FileSystemAPI.ErrorCallback;
import com.flyordie.code.browserapi.FrameAPI.ConsoleBase;
import com.flyordie.code.browserapi.Fetch.Request;
import com.flyordie.code.browserapi.Timing.WorkerPerformance;
import com.flyordie.code.browserapi.Fetch.Response;
import com.flyordie.code.browserapi.FileSystemAPI.FileSystemCallback;
import com.flyordie.code.browserapi.CryptoAPI.Crypto;
import com.flyordie.code.browserapi.Workers.WorkerGlobalScope;
import com.flyordie.code.browserapi.FrameAPI.WindowBase64;
import com.flyordie.code.browserapi.FrameAPI.NavigatorID;
import com.flyordie.code.browserapi.ImageBitmapAPI.ImageBitmapFactories;
import com.flyordie.code.browserapi.FrameAPI.NavigatorCPU;

public interface Workers {

    Worker create(String scriptUrl);

    // Generated from core\workers\DedicatedWorkerGlobalScope.idl
    @Name("DedicatedWorkerGlobalScope")
    public interface DedicatedWorkerGlobalScope extends WorkerGlobalScope {
        void postMessage(Object message, List<MessagePort> transfer);
        @Getter EventHandler onmessage();
        @Setter void onmessage(EventHandler v);
    }

    // Generated from core\timing\SharedWorkerPerformance.idl, core\workers\SharedWorker.idl, core\workers\SharedWorker.idl
    @Name("SharedWorker")
    public interface SharedWorker extends EventTarget, AbstractWorker {
        @Getter double workerStart();
        @Getter MessagePort port();
    }

    @Statics("SharedWorker")
    public interface SharedWorkers {
        SharedWorker create(String scriptURL, String name);
    }

    // Generated from modules\filesystem\WorkerGlobalScopeFileSystem.idl, modules\indexeddb\WorkerGlobalScopeIndexedDatabase.idl, modules\fetch\WorkerFetch.idl, modules\crypto\WorkerGlobalScopeCrypto.idl, core\workers\WorkerGlobalScope.idl, core\workers\WorkerGlobalScope.idl, core\workers\WorkerGlobalScope.idl, core\imagebitmap\ImageBitmapFactories.idl, modules\cachestorage\WorkerCacheStorage.idl, core\timing\WorkerGlobalScopePerformance.idl
    @Name("WorkerGlobalScope")
    public interface WorkerGlobalScope extends EventTarget, WindowBase64, WindowTimers, ImageBitmapFactories {
        short TEMPORARY = (short) 0;
        short PERSISTENT = (short) 1;
        void webkitRequestFileSystem(short type, int size, FileSystemCallback successCallback, ErrorCallback errorCallback);
        DOMFileSystemSync webkitRequestFileSystemSync(short type, int size);
        void webkitResolveLocalFileSystemURL(String url, EntryCallback successCallback, ErrorCallback errorCallback);
        EntrySync webkitResolveLocalFileSystemSyncURL(String url);
        /*
        @Getter FileErrorConstructor FileError();
        @Setter void FileError(FileErrorConstructor v);
        @Getter IDBFactory webkitIndexedDB();
        @Getter IDBCursorConstructor webkitIDBCursor();
        @Setter void webkitIDBCursor(IDBCursorConstructor v);
        @Getter IDBDatabaseConstructor webkitIDBDatabase();
        @Setter void webkitIDBDatabase(IDBDatabaseConstructor v);
        @Getter IDBFactoryConstructor webkitIDBFactory();
        @Setter void webkitIDBFactory(IDBFactoryConstructor v);
        @Getter IDBIndexConstructor webkitIDBIndex();
        @Setter void webkitIDBIndex(IDBIndexConstructor v);
        @Getter IDBKeyRangeConstructor webkitIDBKeyRange();
        @Setter void webkitIDBKeyRange(IDBKeyRangeConstructor v);
        @Getter IDBObjectStoreConstructor webkitIDBObjectStore();
        @Setter void webkitIDBObjectStore(IDBObjectStoreConstructor v);
        @Getter IDBRequestConstructor webkitIDBRequest();
        @Setter void webkitIDBRequest(IDBRequestConstructor v);
        @Getter IDBTransactionConstructor webkitIDBTransaction();
        @Setter void webkitIDBTransaction(IDBTransactionConstructor v);
         */
        @Getter IDBFactory indexedDB();
        Future<Response> fetch(/* Request or String */ Object input, Map<String, Object> init);
        @Getter Crypto crypto();
        @Getter WorkerGlobalScope self();
        @Getter WorkerLocation location();
        void close();
        @Getter EventHandler onerror();
        @Setter void onerror(EventHandler v);
        void importScripts(String... urls);
        @Getter WorkerNavigator navigator();
        @Getter WorkerConsole console();
        @Getter EventHandler onrejectionhandled();
        @Setter void onrejectionhandled(EventHandler v);
        @Getter EventHandler onunhandledrejection();
        @Setter void onunhandledrejection(EventHandler v);
        @Getter CacheStorage caches();
        @Getter WorkerPerformance performance();
    }

    // Generated from core\workers\AbstractWorker.idl
    @Name("AbstractWorker")
    public interface AbstractWorker {
        @Getter EventHandler onerror();
        @Setter void onerror(EventHandler v);
    }

    // Generated from core\workers\SharedWorkerGlobalScope.idl
    @Name("SharedWorkerGlobalScope")
    public interface SharedWorkerGlobalScope extends WorkerGlobalScope {
        @Getter String name();
        @Getter EventHandler onconnect();
        @Setter void onconnect(EventHandler v);
    }

    // Generated from core\workers\Worker.idl, core\workers\Worker.idl
    @Name("Worker")
    public interface Worker extends EventTarget, AbstractWorker {
        void terminate();
        void postMessage(Object message, List<MessagePort> transfer);
        @Getter EventHandler onmessage();
        @Setter void onmessage(EventHandler v);
    }

    // Generated from core\workers\WorkerConsole.idl
    @Name("WorkerConsole")
    public interface WorkerConsole extends ConsoleBase {
    }

    // Generated from modules\quota\WorkerNavigatorStorageQuota.idl, modules\permissions\WorkerNavigatorPermissions.idl, modules\geofencing\WorkerNavigatorGeofencing.idl, core\workers\WorkerNavigator.idl, core\workers\WorkerNavigator.idl, core\workers\WorkerNavigator.idl, core\workers\WorkerNavigator.idl, modules\netinfo\WorkerNavigatorNetworkInformation.idl
    @Name("WorkerNavigator")
    public interface WorkerNavigator extends NavigatorCPU, NavigatorID, NavigatorOnLine {
        @Getter DeprecatedStorageQuota webkitTemporaryStorage();
        @Getter DeprecatedStorageQuota webkitPersistentStorage();
        @Getter StorageManager storage();
        @Getter Permissions permissions();
        @Getter Geofencing geofencing();
        @Getter NetworkInformation connection();
    }

    // Generated from core\workers\WorkerLocation.idl, core\workers\WorkerLocation.idl
    @Name("WorkerLocation")
    public interface WorkerLocation extends URLUtilsReadOnly {
    }

}
