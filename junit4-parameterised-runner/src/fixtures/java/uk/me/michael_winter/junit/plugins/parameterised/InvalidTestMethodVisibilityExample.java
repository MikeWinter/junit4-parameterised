package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.Test;

public class InvalidTestMethodVisibilityExample {
    @Test
    @SuppressWarnings("TestMethodWithIncorrectSignature")
    void testIsNotPublic() {
    }
}
