package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.BeforeClass;
import org.junit.Test;

public class BeforeClassExample {
    private static int setupClassCallCount = 0;
    private static int setupClassAgainCallCount = 0;

    static boolean hasBeenSetupOnce() {
        return setupClassCallCount == 1 && setupClassAgainCallCount == 1;
    }

    @BeforeClass
    public static void setupClass() {
        ++setupClassCallCount;
    }

    @BeforeClass
    public static void setupClassAgain() {
        ++setupClassAgainCallCount;
    }

    @Test
    public void aValidTest() {
        assert setupClassCallCount > 0;
    }

    @Test
    public void anotherValidTest() {
        assert setupClassCallCount > 0;
    }
}
