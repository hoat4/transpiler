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

public class Loader {

    private Loader() {
        throw new Error("should not instantiate");
    }

    // Generated from core\loader\appcache\ApplicationCache.idl
    @Name("ApplicationCache")
    public interface ApplicationCache extends EventTarget {
        short UNCACHED = (short) 0;
        short IDLE = (short) 1;
        short CHECKING = (short) 2;
        short DOWNLOADING = (short) 3;
        short UPDATEREADY = (short) 4;
        short OBSOLETE = (short) 5;
        @Getter short status();
        void update();
        void abort();
        void swapCache();
        @Getter EventHandler onchecking();
        @Setter void onchecking(EventHandler v);
        @Getter EventHandler onerror();
        @Setter void onerror(EventHandler v);
        @Getter EventHandler onnoupdate();
        @Setter void onnoupdate(EventHandler v);
        @Getter EventHandler ondownloading();
        @Setter void ondownloading(EventHandler v);
        @Getter EventHandler onprogress();
        @Setter void onprogress(EventHandler v);
        @Getter EventHandler onupdateready();
        @Setter void onupdateready(EventHandler v);
        @Getter EventHandler oncached();
        @Setter void oncached(EventHandler v);
        @Getter EventHandler onobsolete();
        @Setter void onobsolete(EventHandler v);
    }

}
