package com.flyordie.code.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.CLASS;

// TODO ez még nincs implementálva
@Target(PARAMETER)
@Retention(CLASS)
public @interface CompileConstant {
}
