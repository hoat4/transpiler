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

import com.flyordie.code.browserapi.Timing.MemoryInfo;
import com.flyordie.code.browserapi.Timing.PerformanceEntry;
import com.flyordie.code.browserapi.Timing.PerformanceTiming;
import com.flyordie.code.browserapi.Timing.PerformanceObserverEntryList;
import com.flyordie.code.browserapi.Timing.PerformanceObserver;
import com.flyordie.code.browserapi.Timing.PerformanceObserverInit;
import com.flyordie.code.browserapi.Timing.PerformanceNavigation;
import com.flyordie.code.browserapi.Events.EventTarget;

public class Timing {

    private Timing() {
        throw new Error("should not instantiate");
    }

    // Generated from core\timing\PerformanceTiming.idl
    @Name("PerformanceTiming")
    public interface PerformanceTiming {
        @Getter int navigationStart();
        @Getter int unloadEventStart();
        @Getter int unloadEventEnd();
        @Getter int redirectStart();
        @Getter int redirectEnd();
        @Getter int fetchStart();
        @Getter int domainLookupStart();
        @Getter int domainLookupEnd();
        @Getter int connectStart();
        @Getter int connectEnd();
        @Getter int secureConnectionStart();
        @Getter int requestStart();
        @Getter int responseStart();
        @Getter int responseEnd();
        @Getter int domLoading();
        @Getter int domInteractive();
        @Getter int domContentLoadedEventStart();
        @Getter int domContentLoadedEventEnd();
        @Getter int domComplete();
        @Getter int loadEventStart();
        @Getter int loadEventEnd();
    }

    // Generated from core\timing\PerformanceEntry.idl
    @Name("PerformanceEntry")
    public interface PerformanceEntry {
        @Getter String name();
        @Getter String entryType();
        @Getter double startTime();
        @Getter double duration();
    }

    // Generated from core\timing\PerformanceObserverEntryList.idl
    @Name("PerformanceObserverEntryList")
    public interface PerformanceObserverEntryList {
        List<PerformanceEntry> getEntries();
        List<PerformanceEntry> getEntriesByType(String entryType);
        List<PerformanceEntry> getEntriesByName(String name, String entryType);
    }

    // Generated from core\timing\PerformanceObserver.idl
    @FunctionalInterface
    @Name("PerformanceObserverCallback")
    public interface PerformanceObserverCallback {
        void apply(PerformanceObserverEntryList entries, PerformanceObserver observer);
    }

    // Generated from core\timing\PerformanceNavigation.idl
    @Name("PerformanceNavigation")
    public interface PerformanceNavigation {
        short TYPE_NAVIGATE = (short) 0;
        short TYPE_RELOAD = (short) 1;
        short TYPE_BACK_FORWARD = (short) 2;
        short TYPE_RESERVED = (short) 255;
        @Getter short type();
        @Getter short redirectCount();
    }

    // Generated from core\timing\PerformanceCompositeTiming.idl
    @Name("PerformanceCompositeTiming")
    public interface PerformanceCompositeTiming extends PerformanceEntry {
        @Getter int sourceFrame();
    }

    // Generated from core\timing\Performance.idl
    @Name("Performance")
    public interface Performance extends EventTarget {
        double now();
        List<PerformanceEntry> getEntries();
        List<PerformanceEntry> getEntriesByType(String entryType);
        List<PerformanceEntry> getEntriesByName(String name, String entryType);
        void clearResourceTimings();
        void setResourceTimingBufferSize(int maxSize);
        @Getter EventHandler onresourcetimingbufferfull();
        @Setter void onresourcetimingbufferfull(EventHandler v);
        void webkitClearResourceTimings();
        void webkitSetResourceTimingBufferSize(int maxSize);
        @Getter EventHandler onwebkitresourcetimingbufferfull();
        @Setter void onwebkitresourcetimingbufferfull(EventHandler v);
        @Getter PerformanceTiming timing();
        @Getter PerformanceNavigation navigation();
        void mark(String markName);
        void clearMarks(String markName);
        void measure(String measureName, String startMark, String endMark);
        void clearMeasures(String measureName);
        void clearFrameTimings();
        void setFrameTimingBufferSize(int maxSize);
        @Getter EventHandler onframetimingbufferfull();
        @Setter void onframetimingbufferfull(EventHandler v);
        @Getter MemoryInfo memory();
    }

    // Generated from core\timing\MemoryInfo.idl
    @Name("MemoryInfo")
    public interface MemoryInfo {
        @Getter int totalJSHeapSize();
        @Getter int usedJSHeapSize();
        @Getter int jsHeapSizeLimit();
    }

    // Generated from core\timing\PerformanceRenderTiming.idl
    @Name("PerformanceRenderTiming")
    public interface PerformanceRenderTiming extends PerformanceEntry {
        @Getter int sourceFrame();
    }

    // Generated from core\timing\PerformanceObserver.idl
    @Name("PerformanceObserver")
    public interface PerformanceObserver {
        void observe(PerformanceObserverInit options);
        void disconnect();
    }

    // Generated from core\timing\WorkerPerformance.idl
    @Name("WorkerPerformance")
    public interface WorkerPerformance extends EventTarget {
        double now();
        List<PerformanceEntry> getEntries();
        List<PerformanceEntry> getEntriesByType(String entryType);
        List<PerformanceEntry> getEntriesByName(String name, String entryType);
        void clearResourceTimings();
        void setResourceTimingBufferSize(int maxSize);
        @Getter EventHandler onresourcetimingbufferfull();
        @Setter void onresourcetimingbufferfull(EventHandler v);
        void mark(String markName);
        void clearMarks(String markName);
        void measure(String measureName, String startMark, String endMark);
        void clearMeasures(String measureName);
        @Getter MemoryInfo memory();
    }

    // Generated from core\timing\PerformanceResourceTiming.idl
    @Name("PerformanceResourceTiming")
    public interface PerformanceResourceTiming extends PerformanceEntry {
        @Getter String initiatorType();
        @Getter double workerStart();
        @Getter double redirectStart();
        @Getter double redirectEnd();
        @Getter double fetchStart();
        @Getter double domainLookupStart();
        @Getter double domainLookupEnd();
        @Getter double connectStart();
        @Getter double connectEnd();
        @Getter double secureConnectionStart();
        @Getter double requestStart();
        @Getter double responseStart();
        @Getter double responseEnd();
    }

    // Generated from core\timing\PerformanceObserverInit.idl
    @Name("PerformanceObserverInit")
    public static class PerformanceObserverInit {
        public List<String> entryTypes;
    }

    // Generated from core\timing\PerformanceMark.idl
    @Name("PerformanceMark")
    public interface PerformanceMark extends PerformanceEntry {
    }

    // Generated from core\timing\PerformanceMeasure.idl
    @Name("PerformanceMeasure")
    public interface PerformanceMeasure extends PerformanceEntry {
    }

}
