package com.erastus.orientate.student.chat.conversations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Indicates all getter methods whose data is fetched lazily*/

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface FetchedLazily {
}
