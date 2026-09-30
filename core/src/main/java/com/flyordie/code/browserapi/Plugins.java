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

import com.flyordie.code.browserapi.Plugins.MimeType;
import com.flyordie.code.browserapi.Plugins.Plugin;

public class Plugins {

    private Plugins() {
        throw new Error("should not instantiate");
    }

    // Generated from modules\plugins\MimeType.idl
    @Name("MimeType")
    public interface MimeType {
        @Getter String type();
        @Getter String suffixes();
        @Getter String description();
        @Getter Plugin enabledPlugin();
    }

    // Generated from modules\plugins\MimeTypeArray.idl
    @Name("MimeTypeArray")
    public interface MimeTypeArray {
        @Getter int length();
        MimeType item(int index);
        MimeType namedItem(String name);
        MimeType get(String name);
    }

    // Generated from modules\plugins\PluginArray.idl
    @Name("PluginArray")
    public interface PluginArray {
        @Getter int length();
        Plugin item(int index);
        Plugin namedItem(String name);
        Plugin get(String name);
        void refresh(boolean reload);
    }

    // Generated from modules\plugins\Plugin.idl
    @Name("Plugin")
    public interface Plugin {
        @Getter String name();
        @Getter String filename();
        @Getter String description();
        @Getter int length();
        MimeType item(int index);
        MimeType namedItem(String name);
        MimeType get(String name);
    }

}
