package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.fail;

public class IgnoredTestMethodExample {
    @Ignore
    @Test
    public void testIsIgnored() {
        fail("This test should fail if evaluated");
    }
}
