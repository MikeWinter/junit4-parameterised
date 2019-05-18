package uk.me.michael_winter.junit.plugins.parameterised.runners;

import org.junit.After;
import org.junit.Test;

public class AfterExample {
    private static int teardownCallCount = 0;
    private static int anotherTeardownCallCount = 0;

    static boolean hasBeenTornDownForTest() {
        return teardownCallCount == 1 && anotherTeardownCallCount == 1;
    }

    @After
    public void teardown() {
        ++teardownCallCount;
    }

    @After
    public void anotherTeardown() {
        ++anotherTeardownCallCount;
    }

    @Test
    public void aValidTest() {
        assert teardownCallCount == 0 && anotherTeardownCallCount == 0;
    }
}
