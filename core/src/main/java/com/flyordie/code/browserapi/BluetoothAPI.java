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
import com.flyordie.code.browserapi.BluetoothAPI.BluetoothDevice;
import com.flyordie.code.browserapi.BluetoothAPI.RequestDeviceOptions;
import com.flyordie.code.browserapi.BluetoothAPI.BluetoothGATTRemoteServer;
import com.flyordie.code.browserapi.DOM.ArrayBuffer;
import com.flyordie.code.browserapi.BluetoothAPI.BluetoothScanFilter;
import com.flyordie.code.browserapi.BluetoothAPI.BluetoothGATTCharacteristic;
import com.flyordie.code.browserapi.BluetoothAPI.BluetoothGATTService;

public class BluetoothAPI {

    private BluetoothAPI() {
        throw new Error("should not instantiate");
    }

    // Generated from modules\bluetooth\BluetoothUUID.idl
    @Statics("BluetoothUUID")
    @Name("BluetoothUUID")
    public interface BluetoothUUID {
        String getService(/* String or Integer */ Object name);
        String getCharacteristic(/* String or Integer */ Object name);
        String getDescriptor(/* String or Integer */ Object name);
        String canonicalUUID(int alias);
    }

    // Generated from modules\bluetooth\BluetoothScanFilter.idl
    @Name("BluetoothScanFilter")
    public static class BluetoothScanFilter {
        public List</* String or Integer */ Object> services;
    }

    // Generated from modules\bluetooth\BluetoothGATTRemoteServer.idl
    @Name("BluetoothGATTRemoteServer")
    public interface BluetoothGATTRemoteServer {
        @Getter boolean connected();
        Future<BluetoothGATTService> getPrimaryService(/* String or Integer */ Object service);
    }

    // Generated from modules\bluetooth\BluetoothGATTCharacteristic.idl
    @Name("BluetoothGATTCharacteristic")
    public interface BluetoothGATTCharacteristic {
        @Getter String uuid();
        Future<ArrayBuffer> readValue();
        Future<Void> writeValue(/* ArrayBuffer or ArrayBufferView */ Object value);
    }

    // Generated from modules\bluetooth\RequestDeviceOptions.idl
    @Name("RequestDeviceOptions")
    public static class RequestDeviceOptions {
        public List<BluetoothScanFilter> filters;
        public List</* String or Integer */ Object> optionalServices = Collections.emptyList();
    }

    // Generated from modules\bluetooth\BluetoothDevice.idl
    @Name("VendorIDSource")
    public enum VendorIDSource {
        bluetooth, usb
    }

    // Generated from modules\bluetooth\BluetoothGATTService.idl
    @Name("BluetoothGATTService")
    public interface BluetoothGATTService {
        @Getter String uuid();
        @Getter boolean isPrimary();
        Future<BluetoothGATTCharacteristic> getCharacteristic(/* String or Integer */ Object characteristic);
    }

    // Generated from modules\bluetooth\Bluetooth.idl
    @Name("Bluetooth")
    public interface Bluetooth {
        Future<BluetoothDevice> requestDevice(RequestDeviceOptions options);
    }

    // Generated from modules\bluetooth\BluetoothDevice.idl
    @Name("BluetoothDevice")
    public interface BluetoothDevice {
        @Getter String instanceID();
        @Getter Optional<String> name();
        @Getter Optional<Integer> deviceClass();
        @Getter Optional<VendorIDSource> vendorIDSource();
        @Getter Optional<Integer> vendorID();
        @Getter Optional<Integer> productID();
        @Getter Optional<Integer> productVersion();
        @Getter boolean paired();
        @Getter List<String> uuids();
        Future<BluetoothGATTRemoteServer> connectGATT();
    }

}
