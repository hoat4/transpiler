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

import com.flyordie.code.browserapi.WebUSB.USBConfiguration;
import com.flyordie.code.browserapi.WebUSB.USBDeviceFilter;
import com.flyordie.code.browserapi.Events.EventInit;
import com.flyordie.code.browserapi.WebUSB.USBAlternateInterface;
import com.flyordie.code.browserapi.WebUSB.USBInterface;
import com.flyordie.code.browserapi.WebUSB.USBDeviceRequestOptions;
import com.flyordie.code.browserapi.WebUSB.USBOutTransferResult;
import com.flyordie.code.browserapi.DOM.ArrayBufferView;
import com.flyordie.code.browserapi.WebUSB.USBControlTransferParameters;
import com.flyordie.code.browserapi.Events.Event;
import com.flyordie.code.browserapi.WebUSB.USBConnectionEventInit;
import com.flyordie.code.browserapi.DOM.ArrayBuffer;
import com.flyordie.code.browserapi.WebUSB.USBEndpoint;
import com.flyordie.code.browserapi.Events.EventTarget;
import com.flyordie.code.browserapi.WebUSB.USBInTransferResult;
import com.flyordie.code.browserapi.WebUSB.USBDevice;

public class WebUSB {

    private WebUSB() {
        throw new Error("should not instantiate");
    }

    // Generated from modules\webusb\USBInterface.idl
    @Name("USBInterface")
    public interface USBInterface {
        @Getter byte interfaceNumber();
        @Getter List<USBAlternateInterface> alternates();
    }

    @Statics("USBInterface")
    public interface USBInterfaces {
        USBInterface create(USBConfiguration configuration, byte interfaceNumber);
    }

    // Generated from modules\webusb\USBEndpoint.idl
    @Name("USBEndpointType")
    public enum USBEndpointType {
        bulk, interrupt, isochronous
    }

    // Generated from modules\webusb\USBConfiguration.idl
    @Name("USBConfiguration")
    public interface USBConfiguration {
        @Getter byte configurationValue();
        @Getter Optional<String> configurationName();
        @Getter List<USBInterface> interfaces();
    }

    @Statics("USBConfiguration")
    public interface USBConfigurations {
        USBConfiguration create(USBDevice device, byte configurationValue);
    }

    // Generated from modules\webusb\USBControlTransferParameters.idl
    @Name("USBControlTransferParameters")
    public static class USBControlTransferParameters {
        public USBRequestType requestType;
        public USBRecipient recipient;
        public byte request;
        public short value;
        public short index;
    }

    // Generated from modules\webusb\USBControlTransferParameters.idl
    @Name("USBRequestType")
    public enum USBRequestType {
        standard, class_, vendor
    }

    // Generated from modules\webusb\USBDeviceRequestOptions.idl
    @Name("USBDeviceRequestOptions")
    public static class USBDeviceRequestOptions {
        public List<USBDeviceFilter> filters;
    }

    // Generated from modules\webusb\USB.idl
    @Name("USB")
    public interface USB extends EventTarget {
        @Getter EventHandler onconnect();
        @Setter void onconnect(EventHandler v);
        @Getter EventHandler ondisconnect();
        @Setter void ondisconnect(EventHandler v);
        Future<List<USBDevice>> getDevices();
        Future<List<USBDevice>> requestDevice(USBDeviceRequestOptions options);
    }

    // Generated from modules\webusb\USBConnectionEvent.idl
    @Name("USBConnectionEvent")
    public interface USBConnectionEvent extends Event {
        @Getter USBDevice device();
    }

    @Statics("USBConnectionEvent")
    public interface USBConnectionEvents {
        USBConnectionEvent create(String type, USBConnectionEventInit eventInitDict);
    }

    // Generated from modules\webusb\USBOutTransferResult.idl
    @Name("USBOutTransferResult")
    public interface USBOutTransferResult {
        @Getter int bytesWritten();
        @Getter USBTransferStatus status();
    }

    // Generated from modules\webusb\USBInTransferResult.idl
    @Name("USBInTransferResult")
    public interface USBInTransferResult {
        @Getter ArrayBuffer data();
        @Getter USBTransferStatus status();
    }

    // Generated from modules\webusb\USBEndpoint.idl
    @Name("USBDirection")
    public enum USBDirection {
        in, out
    }

    // Generated from modules\webusb\USBAlternateInterface.idl
    @Name("USBAlternateInterface")
    public interface USBAlternateInterface {
        @Getter byte alternateSetting();
        @Getter byte interfaceClass();
        @Getter byte interfaceSubclass();
        @Getter byte interfaceProtocol();
        @Getter Optional<String> interfaceName();
        @Getter List<USBEndpoint> endpoints();
    }

    @Statics("USBAlternateInterface")
    public interface USBAlternateInterfaces {
        USBAlternateInterface create(USBInterface deviceInterface, byte alternateSetting);
    }

    // Generated from modules\webusb\USBDevice.idl
    @Name("USBDevice")
    public interface USBDevice {
        @Getter String guid();
        @Getter byte usbVersionMajor();
        @Getter byte usbVersionMinor();
        @Getter byte usbVersionSubminor();
        @Getter byte deviceClass();
        @Getter byte deviceSubclass();
        @Getter byte deviceProtocol();
        @Getter short vendorId();
        @Getter short productId();
        @Getter byte deviceVersionMajor();
        @Getter byte deviceVersionMinor();
        @Getter byte deviceVersionSubminor();
        @Getter Optional<String> manufacturerName();
        @Getter Optional<String> productName();
        @Getter Optional<String> serialNumber();
        @Getter List<USBConfiguration> configurations();
        Future<Void> open();
        Future<Void> close();
        Future<Void> getConfiguration();
        Future<Void> setConfiguration(byte configurationValue);
        Future<Void> claimInterface(byte interfaceNumber);
        Future<Void> releaseInterface(byte interfaceNumber);
        Future<Void> setInterface(byte interfaceNumber, byte alternateSetting);
        Future<USBInTransferResult> controlTransferIn(USBControlTransferParameters setup, short length);
        Future<USBOutTransferResult> controlTransferOut(USBControlTransferParameters setup, /* ArrayBuffer or ArrayBufferView */ Object data);
        Future<Void> clearHalt(byte endpointNumber);
        Future<USBInTransferResult> transferIn(byte endpointNumber, int length);
        Future<USBOutTransferResult> transferOut(byte endpointNumber, /* ArrayBuffer or ArrayBufferView */ Object data);
        Future<Void> reset();
    }

    // Generated from modules\webusb\USBDevice.idl
    @Name("USBTransferStatus")
    public enum USBTransferStatus {
        ok, stall, babble
    }

    // Generated from modules\webusb\USBEndpoint.idl
    @Name("USBEndpoint")
    public interface USBEndpoint {
        @Getter byte endpointNumber();
        @Getter USBDirection direction();
        @Getter USBEndpointType type();
        @Getter int packetSize();
    }

    @Statics("USBEndpoint")
    public interface USBEndpoints {
        USBEndpoint create(USBAlternateInterface alternate, byte endpointNumber, USBDirection direction);
    }

    // Generated from modules\webusb\USBConnectionEventInit.idl
    @Name("USBConnectionEventInit")
    public static class USBConnectionEventInit extends EventInit {
        public USBDevice device;
    }

    // Generated from modules\webusb\USBControlTransferParameters.idl
    @Name("USBRecipient")
    public enum USBRecipient {
        device, interface_, endpoint, other
    }

    // Generated from modules\webusb\USBDeviceFilter.idl
    @Name("USBDeviceFilter")
    public static class USBDeviceFilter {
        public short vendorId;
        public short productId;
        public byte classCode;
        public byte subclassCode;
        public byte protocolCode;
    }

}
