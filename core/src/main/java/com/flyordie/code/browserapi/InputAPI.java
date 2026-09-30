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

import com.flyordie.code.browserapi.InputAPI.InputDeviceCapabilitiesInit;

public class InputAPI {

    private InputAPI() {
        throw new Error("should not instantiate");
    }

    // Generated from core\input\InputDeviceCapabilitiesInit.idl
    @Name("InputDeviceCapabilitiesInit")
    public static class InputDeviceCapabilitiesInit {
        public boolean firesTouchEvents = false;
    }

    // Generated from core\input\InputDeviceCapabilities.idl
    @Name("InputDeviceCapabilities")
    public interface InputDeviceCapabilities {
        @Getter boolean firesTouchEvents();
    }

    @Statics("InputDeviceCapabilities")
    public interface InputDeviceCapabilitiees {
        InputDeviceCapabilities create(InputDeviceCapabilitiesInit deviceInitDict);
    }

}
