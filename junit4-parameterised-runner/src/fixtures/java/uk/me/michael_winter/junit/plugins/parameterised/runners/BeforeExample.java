package uk.me.michael_winter.junit.plugins.parameterised.runners;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BeforeExample {
    private static int setupCallCount = 0;
    private static int anotherSetupCallCount = 0;

    static boolean hasBeenSetupForTest() {
        return setupCallCount == 1 && anotherSetupCallCount == 1;
    }

    @BeforeClass
    public static void resetCount() {
        setupCallCount = 0;
        anotherSetupCallCount = 0;
    }

    @Before
    public void setup() {
        ++setupCallCount;
    }

    @Before
    public void anotherSetup() {
        ++anotherSetupCallCount;
    }

    @Test
    public void aValidTest() {
        assert setupCallCount > 0;
    }
}
