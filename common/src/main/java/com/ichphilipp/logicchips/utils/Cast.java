package com.ichphilipp.logicchips.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @author ZZZank
 */
public interface Cast {

    @SuppressWarnings("unchecked")
    static <T> T to(Object o) {
        return (T) o;
    }

    /**
     * @return cast object if {@code o} in an instance of {@code type}, null otherwise
     */
    @Nullable
    static <T> T inst(Class<T> type, Object o) {
        if (type.isInstance(o)) {
            return to(o);
        }
        return null;
    }

    /**
     * @return an optional containing cast object if {@code o} in an instance of {@code type}, empty optional otherwise
     */
    @NotNull
    static <T> Optional<T> instOptional(Class<T> type, Object o) {
        return Optional.ofNullable(inst(type, o));
    }
}
