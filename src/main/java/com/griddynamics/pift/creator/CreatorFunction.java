package com.griddynamics.pift.creator;

import java.lang.reflect.Field;
import java.util.function.Function;

@FunctionalInterface
public interface CreatorFunction extends Function<Field, Object> {
}
