package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.fail;

@SuppressWarnings({"unused", "TestMethodWithIncorrectSignature"})
public class IgnoredTestMethodExample {
    @Ignore
    @Test
    public void testIsIgnored() {
        fail("This test should fail if evaluated");
    }

    @Ignore
    @Test
    public void testIsIgnored(int value) {
        fail("This test should fail if evaluated");
    }
}
