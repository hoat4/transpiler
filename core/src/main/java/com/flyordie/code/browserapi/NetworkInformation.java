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

import com.flyordie.code.browserapi.Events.EventTarget;

// Generated from modules\netinfo\NetworkInformation.idl
@Name("NetworkInformation")
public interface NetworkInformation extends EventTarget {

    @Getter ConnectionType type();
    @Getter double downlinkMax();
    @Getter EventHandler onchange();
    @Setter void onchange(EventHandler v);
    @Getter EventHandler ontypechange();
    @Setter void ontypechange(EventHandler v);

    // Generated from modules\netinfo\NetworkInformation.idl
    @Name("ConnectionType")
    enum ConnectionType {
        cellular, bluetooth, ethernet, wifi, 
        wimax, other, none, unknown
    }
}
