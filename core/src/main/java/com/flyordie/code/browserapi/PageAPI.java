package com.flyordie.code.browserapi;

import com.flyordie.code.jsinterop.Getter;
import com.flyordie.code.jsinterop.Setter;
import com.flyordie.code.jsinterop.Name;

import com.flyordie.code.browserapi.DOM.Document;
import com.flyordie.code.browserapi.Events.EventTarget;
import com.flyordie.code.jsinterop.Statics;

public class PageAPI {

    private PageAPI() {
        throw new Error("should not instantiate");
    }

    // Generated from core\page\scrolling\ScrollState.idl
    @Name("ScrollState")
    public interface ScrollState {
        @Getter double deltaX();
        @Getter double deltaY();
        @Getter double deltaGranularity();
        @Getter double velocityX();
        @Getter double velocityY();
        @Getter boolean inInertialPhase();
        @Getter boolean isEnding();
        @Getter boolean isBeginning();
        @Getter boolean fromUserInput();
        @Getter boolean shouldPropagate();
        void consumeDelta(double x, double y);
        void distributeToScrollChainDescendant();
    }

    @Statics("ScrollState")
    public interface ScrollStates {
        ScrollState create(double deltaX, double deltaY, double deltaGranularity, double velocityX, double velocityY, boolean inInertialPhase, boolean isBeginning, boolean isEnding);
    }

    // Generated from core\page\EventSourceInit.idl
    @Name("EventSourceInit")
    public static class EventSourceInit {
        public boolean withCredentials = false;
    }

    // Generated from core\page\scrolling\ScrollStateCallback.idl
    @FunctionalInterface
    @Name("ScrollStateCallback")
    public interface ScrollStateCallback {
        void handleEvent(ScrollState scrollState);
    }

    // Generated from core\page\EventSource.idl
    @Name("EventSource")
    public interface EventSource extends EventTarget {
        short CONNECTING = (short) 0;
        short OPEN = (short) 1;
        short CLOSED = (short) 2;
        @Getter String url();
        @Getter boolean withCredentials();
        @Getter short readyState();
        @Getter EventHandler onopen();
        @Setter void onopen(EventHandler v);
        @Getter EventHandler onmessage();
        @Setter void onmessage(EventHandler v);
        @Getter EventHandler onerror();
        @Setter void onerror(EventHandler v);
        void close();
    }

    @Statics("EventSource")
    public interface EventSources {
        EventSource create(String url, EventSourceInit eventSourceInitDict);
    }

    // Generated from core\page\PagePopupController.idl
    @Name("PagePopupController")
    public interface PagePopupController {
        void setValueAndClosePopup(int numberValue, String stringValue);
        void setValue(String value);
        void closePopup();
        void selectFontsFromOwnerDocument(Document targetDocument);
        String localizeNumberString(String numberString);
        String formatMonth(int year, int zeroBaseMonth);
        String formatShortMonth(int year, int zeroBaseMonth);
        String formatWeek(int year, int weekNumber, String localizedStartDate);
        void histogramEnumeration(String name, int sample, int boundaryValue);
        void setWindowRect(int x, int y, int width, int height);
    }

}
