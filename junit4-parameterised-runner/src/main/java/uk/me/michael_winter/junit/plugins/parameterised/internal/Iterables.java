package uk.me.michael_winter.junit.plugins.parameterised.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.Stream;

public class Iterables {
    public static <T, U> Iterable<Tuple<T, U>> zip(Iterable<T> t, Iterable<U> u) {
        Iterator<T> ts = t.iterator();
        Iterator<U> us = u.iterator();

        List<Tuple<T, U>> tuples = new ArrayList<>();
        while (ts.hasNext() && us.hasNext()) {
            tuples.add(Tuple.of(ts.next(), us.next()));
        }

        while (ts.hasNext()) {
            tuples.add(Tuple.of(ts.next(), null));
        }
        while (us.hasNext()) {
            tuples.add(Tuple.of(null, us.next()));
        }

        return tuples;
    }

    private Iterables() {}
}
