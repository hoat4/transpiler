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

import com.flyordie.code.browserapi.Quota.StorageUsageCallback;
import com.flyordie.code.browserapi.DOM.DOMError;
import com.flyordie.code.browserapi.Quota.StorageQuotaCallback;
import com.flyordie.code.browserapi.Quota.StorageErrorCallback;

public class Quota {

    private Quota() {
        throw new Error("should not instantiate");
    }

    // Generated from modules\quota\DeprecatedStorageInfo.idl
    @Name("DeprecatedStorageInfo")
    public interface DeprecatedStorageInfo {
        short TEMPORARY = (short) 0;
        short PERSISTENT = (short) 1;
        void queryUsageAndQuota(short storageType, StorageUsageCallback usageCallback, StorageErrorCallback errorCallback);
        void requestQuota(short storageType, int newQuotaInBytes, StorageQuotaCallback quotaCallback, StorageErrorCallback errorCallback);
    }

    // Generated from modules\quota\StorageInfo.idl
    @Name("StorageInfo")
    public interface StorageInfo {
        @Getter int usage();
        @Getter int quota();
    }

    // Generated from modules\quota\StorageErrorCallback.idl
    @FunctionalInterface
    @Name("StorageErrorCallback")
    public interface StorageErrorCallback {
        void handleEvent(DOMError error);
    }

    // Generated from modules\quota\StorageManager.idl
    @Name("PersistentStoragePermission")
    public enum PersistentStoragePermission {
        default_, denied, granted
    }

    // Generated from modules\quota\StorageQuota.idl
    @Name("StorageQuota")
    public interface StorageQuota {
        @Getter List<StorageType> supportedTypes();
        Future<Object> queryInfo(StorageType type);
        Future<Object> requestPersistentQuota(int newQuota);
    }

    // Generated from modules\quota\StorageManager.idl
    @Name("StorageManager")
    public interface StorageManager {
        Future<Boolean> requestPersistent();
        Future<PersistentStoragePermission> persistentPermission();
    }

    // Generated from modules\quota\StorageUsageCallback.idl
    @FunctionalInterface
    @Name("StorageUsageCallback")
    public interface StorageUsageCallback {
        void handleEvent(int currentUsageInBytes, int currentQuotaInBytes);
    }

    // Generated from modules\quota\StorageQuota.idl
    @Name("StorageType")
    public enum StorageType {
        temporary, persistent
    }

    // Generated from modules\quota\StorageQuotaCallback.idl
    @FunctionalInterface
    @Name("StorageQuotaCallback")
    public interface StorageQuotaCallback {
        void handleEvent(int grantedQuotaInBytes);
    }

    // Generated from modules\quota\DeprecatedStorageQuota.idl
    @Name("DeprecatedStorageQuota")
    public interface DeprecatedStorageQuota {
        void queryUsageAndQuota(StorageUsageCallback usageCallback, StorageErrorCallback errorCallback);
        void requestQuota(int newQuotaInBytes, StorageQuotaCallback quotaCallback, StorageErrorCallback errorCallback);
    }

}
