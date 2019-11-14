package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.fail;

public class FailingTeardownWithAfterExample {
    @After
    public void teardown() {
        fail("Teardown is expected to fail");
    }

    @Test
    public void aValidTest() {
    }
}
