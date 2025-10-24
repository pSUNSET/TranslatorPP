package net.psunset.translatorpp.event;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.Function;

public class Event<T extends Event.Callback> {
    private T[] callbacks;
    private final Function<T[], T> merger;
    private volatile T invoker;

    Event(Class<? extends T> type, Function<T[], T> merger) {
        this.merger = merger;
        this.callbacks = (T[]) Array.newInstance(type, 0);
    }

    public void register(T callback) {
        int oldLength = callbacks.length;
        callbacks = Arrays.copyOf(callbacks, oldLength + 1);
        callbacks[oldLength] = callback;
    }

    public T merge() {
        invoker = merger.apply(callbacks);
        return invoker;
    }

    public T getInvoker() {
        return invoker;
    }

    interface Callback {
    }
}
