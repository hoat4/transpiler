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

import com.flyordie.code.browserapi.GamePad.Gamepad;
import com.flyordie.code.browserapi.Events.EventInit;
import com.flyordie.code.browserapi.Events.Event;
import com.flyordie.code.browserapi.GamePad.GamepadButton;
import com.flyordie.code.browserapi.GamePad.GamepadEventInit;

public class GamePad {

    private GamePad() {
        throw new Error("should not instantiate");
    }

    // Generated from modules\gamepad\GamepadEvent.idl
    @Name("GamepadEvent")
    public interface GamepadEvent extends Event {
        @Getter Gamepad gamepad();
    }

    @Statics("GamepadEvent")
    public interface GamepadEvents {
        GamepadEvent create(String type, GamepadEventInit eventInitDict);
    }

    // Generated from modules\gamepad\Gamepad.idl
    @Name("Gamepad")
    public interface Gamepad {
        @Getter String id();
        @Getter int index();
        @Getter boolean connected();
        @Getter int timestamp();
        @Getter String mapping();
        @Getter List<Double> axes();
        @Getter List<GamepadButton> buttons();
    }

    // Generated from modules\gamepad\GamepadList.idl
    @Name("GamepadList")
    public interface GamepadList {
        @Getter int length();
        Gamepad item(int index);
    }

    // Generated from modules\gamepad\GamepadEventInit.idl
    @Name("GamepadEventInit")
    public static class GamepadEventInit extends EventInit {
        public Gamepad gamepad;
    }

    // Generated from modules\gamepad\GamepadButton.idl
    @Name("GamepadButton")
    public interface GamepadButton {
        @Getter boolean pressed();
        @Getter double value();
    }

}
