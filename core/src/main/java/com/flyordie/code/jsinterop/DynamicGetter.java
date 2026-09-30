package com.flyordie.code.jsinterop;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

// TODO ki lett felejtve WebIDLBindingGeneratorból hogy ezeket is beírja.
//      ki kell keresni IDL fájlokból, hogy honnan hagyta ki, és pótolni kézzel. pár helyről már pótoltam.
// TODO kéne asszem valami indexelt deleter is
@Target(METHOD)
@Retention(RUNTIME)
public @interface DynamicGetter {
}
