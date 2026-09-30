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

import com.flyordie.code.browserapi.DOM.DOMPoint;
import com.flyordie.code.browserapi.WebVR.VREyeParameters;
import com.flyordie.code.browserapi.WebVR.VRDevice;
import com.flyordie.code.browserapi.WebVR.VRFieldOfView;
import com.flyordie.code.browserapi.DOM.DOMRect;
import com.flyordie.code.browserapi.WebVR.VRFieldOfViewInit;
import com.flyordie.code.browserapi.WebVR.VRPositionState;

public class WebVR {

    private WebVR() {
        throw new Error("should not instantiate");
    }

    // Generated from modules\vr\VREyeParameters.idl
    @Name("VREyeParameters")
    public interface VREyeParameters {
        @Getter VRFieldOfView minimumFieldOfView();
        @Getter VRFieldOfView maximumFieldOfView();
        @Getter VRFieldOfView recommendedFieldOfView();
        @Getter DOMPoint eyeTranslation();
        @Getter VRFieldOfView currentFieldOfView();
        @Getter DOMRect renderRect();
    }

    // Generated from modules\vr\VRDevice.idl
    @Name("VRDevice")
    public interface VRDevice {
        @Getter String hardwareUnitId();
        @Getter String deviceId();
        @Getter String deviceName();
    }

    // Generated from modules\vr\HMDVRDevice.idl
    @Name("VREye")
    public enum VREye {
        left, right
    }

    // Generated from modules\vr\VRFieldOfView.idl
    @Name("VRFieldOfView")
    public interface VRFieldOfView {
        @Getter double upDegrees();
        @Setter void upDegrees(double v);
        @Getter double downDegrees();
        @Setter void downDegrees(double v);
        @Getter double leftDegrees();
        @Setter void leftDegrees(double v);
        @Getter double rightDegrees();
        @Setter void rightDegrees(double v);
    }

    @Statics("VRFieldOfView")
    public interface VRFieldOfViews {
        VRFieldOfView create(VRFieldOfViewInit fov);
    }

    // Generated from modules\vr\PositionSensorVRDevice.idl
    @Name("PositionSensorVRDevice")
    public interface PositionSensorVRDevice extends VRDevice {
        VRPositionState getState();
        VRPositionState getImmediateState();
        void resetSensor();
    }

    // Generated from modules\vr\VRFieldOfViewInit.idl
    @Name("VRFieldOfViewInit")
    public static class VRFieldOfViewInit {
        public double upDegrees = 0.0;
        public double rightDegrees = 0.0;
        public double downDegrees = 0.0;
        public double leftDegrees = 0.0;
    }

    // Generated from modules\vr\VRPositionState.idl
    @Name("VRPositionState")
    public interface VRPositionState {
        @Getter double timeStamp();
        @Getter Optional<DOMPoint> position();
        @Getter Optional<DOMPoint> linearVelocity();
        @Getter Optional<DOMPoint> linearAcceleration();
        @Getter Optional<DOMPoint> orientation();
        @Getter Optional<DOMPoint> angularVelocity();
        @Getter Optional<DOMPoint> angularAcceleration();
    }

    // Generated from modules\vr\HMDVRDevice.idl
    @Name("HMDVRDevice")
    public interface HMDVRDevice extends VRDevice {
        VREyeParameters getEyeParameters(VREye whichEye);
        void setFieldOfView(VRFieldOfView leftFov, VRFieldOfView rightFov);
    }

}
