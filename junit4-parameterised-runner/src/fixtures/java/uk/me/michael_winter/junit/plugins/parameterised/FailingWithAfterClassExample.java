package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.fail;

public class FailingWithAfterClassExample {
    private static int teardownClassCallCount = 0;

    @BeforeClass
    public static void resetCount() {
        teardownClassCallCount = 0;
    }

    @AfterClass
    public static void teardownClass() {
        ++teardownClassCallCount;
    }

    static boolean hasBeenTornDownOnce() {
        return teardownClassCallCount == 1;
    }

    @Test
    public void failingTest() {
        fail("This test is expected to fail");
    }
}
