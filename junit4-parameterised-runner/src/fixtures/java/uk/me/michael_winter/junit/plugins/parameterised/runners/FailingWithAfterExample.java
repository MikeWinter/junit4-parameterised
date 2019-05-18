package uk.me.michael_winter.junit.plugins.parameterised.runners;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.fail;

public class FailingWithAfterExample {
    private static int teardownCallCount = 0;

    @BeforeClass
    public static void resetCount() {
        teardownCallCount = 0;
    }

    static boolean hasBeenTornDownForTest() {
        return teardownCallCount == 1;
    }

    @After
    public void teardown() {
        ++teardownCallCount;
    }

    @Test
    public void aFailingTest() {
        fail("This test is expected to fail");
    }
}
