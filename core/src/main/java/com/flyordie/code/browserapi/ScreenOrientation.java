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

// Generated from modules\screen_orientation\ScreenOrientation.idl
@Name("ScreenOrientation")
public interface ScreenOrientation extends EventTarget {

    @Getter short angle();
    @Getter String type();
    Future<Object> lock(OrientationLockType orientation);
    void unlock();
    @Getter EventHandler onchange();
    @Setter void onchange(EventHandler v);

    // Generated from modules\screen_orientation\ScreenOrientation.idl
    @Name("OrientationLockType")
    enum OrientationLockType {
        any, natural, landscape, portrait, 
        portraitPrimary, portraitSecondary, landscapePrimary, landscapeSecondary
    }

    // Generated from modules\screen_orientation\ScreenOrientation.idl
    @Name("OrientationType")
    enum OrientationType {
        portraitPrimary, portraitSecondary, landscapePrimary, landscapeSecondary
    }

}
