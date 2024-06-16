package me.autobot.playerdoll.wrapper;

import java.util.Objects;

public abstract class Wrapper<T> {

    protected final T source;

    @SuppressWarnings("unchecked")
    public Wrapper(Object o) {
        Objects.requireNonNull(o, "object");
        source = (T) o;
    }

    public T getSource() {
        return source;
    }
    public Object toObj() {
        return source;
    }
}
