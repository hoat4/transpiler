package com.flyordie.code.jsinterop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// TODO a webidl-es generátorba be kéne ezt rakni


// ez implikálja azt hogy natívinterfaceről van szó
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface NoInterfaceObject {
}
