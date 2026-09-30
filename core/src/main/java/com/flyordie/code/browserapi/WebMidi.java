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

import com.flyordie.code.browserapi.WebMidi.MIDIConnectionEventInit;
import com.flyordie.code.browserapi.Events.EventInit;
import com.flyordie.code.browserapi.WebMidi.MIDIInputMap;
import com.flyordie.code.browserapi.WebMidi.MIDIPort;
import com.flyordie.code.browserapi.Events.Event;
import com.flyordie.code.browserapi.DOM.Uint8Array;
import com.flyordie.code.browserapi.WebMidi.MIDIOutputMap;
import com.flyordie.code.browserapi.Events.EventTarget;
import com.flyordie.code.browserapi.WebMidi.MIDIMessageEventInit;

public class WebMidi {

    private WebMidi() {
        throw new Error("should not instantiate");
    }

    // Generated from modules\webmidi\MIDIPort.idl
    @Name("MIDIPortConnectionState")
    public enum MIDIPortConnectionState {
        open, closed, pending
    }

    // Generated from modules\webmidi\MIDIPort.idl
    @Name("MIDIPortDeviceState")
    public enum MIDIPortDeviceState {
        disconnected, connected
    }

    // Generated from modules\webmidi\MIDIMessageEvent.idl
    @Name("MIDIMessageEvent")
    public interface MIDIMessageEvent extends Event {
        @Getter double receivedTime();
        @Getter Uint8Array data();
    }

    @Statics("MIDIMessageEvent")
    public interface MIDIMessageEvents {
        MIDIMessageEvent create(String type, MIDIMessageEventInit eventInitDict);
    }

    // Generated from modules\webmidi\MIDIPort.idl
    @Name("MIDIPort")
    public interface MIDIPort extends EventTarget {
        @Getter MIDIPortConnectionState connection();
        @Getter String id();
        @Getter String manufacturer();
        @Getter String name();
        @Getter MIDIPortDeviceState state();
        @Getter MIDIPortType type();
        @Getter String version();
        @Getter EventHandler onstatechange();
        @Setter void onstatechange(EventHandler v);
        Future<Object> open();
        Future<Object> close();
    }

    // Generated from modules\webmidi\MIDIAccess.idl
    @Name("MIDIAccess")
    public interface MIDIAccess extends EventTarget {
        @Getter MIDIInputMap inputs();
        @Getter MIDIOutputMap outputs();
        @Getter boolean sysexEnabled();
        @Getter EventHandler onstatechange();
        @Setter void onstatechange(EventHandler v);
    }

    // Generated from modules\webmidi\MIDIInput.idl
    @Name("MIDIInput")
    public interface MIDIInput extends MIDIPort {
        @Getter EventHandler onmidimessage();
        @Setter void onmidimessage(EventHandler v);
    }

    // Generated from modules\webmidi\MIDIConnectionEvent.idl
    @Name("MIDIConnectionEvent")
    public interface MIDIConnectionEvent extends Event {
        @Getter MIDIPort port();
    }

    @Statics("MIDIConnectionEvent")
    public interface MIDIConnectionEvents {
        MIDIConnectionEvent create(String type, MIDIConnectionEventInit eventInitDict);
    }

    // Generated from modules\webmidi\MIDIConnectionEventInit.idl
    @Name("MIDIConnectionEventInit")
    public static class MIDIConnectionEventInit extends EventInit {
        public MIDIPort port;
    }

    // Generated from modules\webmidi\MIDIPort.idl
    @Name("MIDIPortType")
    public enum MIDIPortType {
        input, output
    }

    // Generated from modules\webmidi\MIDIOutput.idl
    @Name("MIDIOutput")
    public interface MIDIOutput extends MIDIPort {
        void send(Uint8Array data, double timestamp);
        void send(List<Integer> data, double timestamp);
    }

    // Generated from modules\webmidi\MIDIMessageEventInit.idl
    @Name("MIDIMessageEventInit")
    public static class MIDIMessageEventInit extends EventInit {
        public double receivedTime;
        public Uint8Array data;
    }

    // Generated from modules\webmidi\MIDIOutputMap.idl
    @Name("MIDIOutputMap")
    public interface MIDIOutputMap {
        @Getter int size();
    }

    // Generated from modules\webmidi\MIDIOptions.idl
    @Name("MIDIOptions")
    public static class MIDIOptions {
        public boolean sysex;
    }

    // Generated from modules\webmidi\MIDIInputMap.idl
    @Name("MIDIInputMap")
    public interface MIDIInputMap {
        @Getter int size();
    }

}
