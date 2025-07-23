package net.psunset.translatorpp.event;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.function.Function;

public class TPPEvent<T extends TPPEvent.Callback> {
    private final List<T> callbacks = Lists.newArrayList();
    private final Function<Iterable<T>, T> merger;

    TPPEvent(Function<Iterable<T>, T> merger) {
        this.merger = merger;
    }

    public void register(T callback) {
        callbacks.add(callback);
    }

    public T merge() {
        return merger.apply(callbacks);
    }

    interface Callback {
    }
}
