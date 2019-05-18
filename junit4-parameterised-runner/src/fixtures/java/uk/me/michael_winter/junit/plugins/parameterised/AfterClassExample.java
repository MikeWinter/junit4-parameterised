package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class AfterClassExample {
    private static int teardownClassCallCount = 0;
    private static int teardownClassAgainCallCount = 0;

    static boolean hasBeenTornDownOnce() {
        return teardownClassCallCount == 1 && teardownClassAgainCallCount == 1;
    }

    @BeforeClass
    public static void resetCount() {
        teardownClassCallCount = 0;
        teardownClassAgainCallCount = 0;
    }

    @AfterClass
    public static void teardownClass() {
        ++teardownClassCallCount;
    }

    @AfterClass
    public static void teardownClassAgain() {
        ++teardownClassAgainCallCount;
    }

    @Test
    public void aValidTest() {
        assert teardownClassCallCount == 0;
    }

    @Test
    public void anotherValidTest() {
        assert teardownClassCallCount == 0;
    }
}
