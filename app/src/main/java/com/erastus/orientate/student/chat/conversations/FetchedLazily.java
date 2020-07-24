package com.erastus.orientate.student.chat.conversations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Indicates a*/

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RunWithInclude {
}
