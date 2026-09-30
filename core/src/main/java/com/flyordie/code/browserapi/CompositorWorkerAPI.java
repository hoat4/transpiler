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

import com.flyordie.code.browserapi.DOM.FrameRequestCallback;
import com.flyordie.code.browserapi.Workers.WorkerGlobalScope;
import com.flyordie.code.browserapi.DOM.MessagePort;
import com.flyordie.code.browserapi.Workers.AbstractWorker;
import com.flyordie.code.browserapi.Events.EventTarget;

public class CompositorWorkerAPI {

    private CompositorWorkerAPI() {
        throw new Error("should not instantiate");
    }

    // Generated from modules\compositorworker\CompositorWorkerGlobalScope.idl
    @Name("CompositorWorkerGlobalScope")
    public interface CompositorWorkerGlobalScope extends WorkerGlobalScope {
        void postMessage(Object message, List<MessagePort> transfer);
        @Getter EventHandler onmessage();
        @Setter void onmessage(EventHandler v);
        int requestAnimationFrame(FrameRequestCallback callback);
        void cancelAnimationFrame(int handle);
    }

    // Generated from modules\compositorworker\CompositorWorker.idl, modules\compositorworker\CompositorWorker.idl
    @Name("CompositorWorker")
    public interface CompositorWorker extends EventTarget, AbstractWorker {
        @Getter EventHandler onmessage();
        @Setter void onmessage(EventHandler v);
        void postMessage(Object message, List<MessagePort> transfer);
        void terminate();
    }

    @Statics("CompositorWorker")
    public interface CompositorWorkers {
        CompositorWorker create(String scriptUrl);
    }

}
