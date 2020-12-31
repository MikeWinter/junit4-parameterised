package uk.me.michael_winter.junit.plugins.parameterised;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.lang.reflect.Method;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class TestCaseTest {
    private final TestExample validTest = mock(TestExample.class);
    private final FailingTestExample failingTest = mock(FailingTestExample.class);

    @Test
    public void executesMethodWithSingleParameter() throws Throwable {
        TestCase testCase = new TestCase(singletonList(5));
        Method method = aValidTestMethod(int.class);

        testCase.execute(validTest, method);

        verify(validTest).aValidTest(5);
    }

    @Test
    public void executesMethodWithMultipleParameters() throws Throwable {
        TestCase testCase = new TestCase(asList(-3, "an object"));
        Method method = aValidTestMethod(int.class, Object.class);

        testCase.execute(validTest, method);

        verify(validTest).aValidTest(-3, "an object");
    }

    @Test
    public void logsParametersOnAssertionFailure() {
        TestCase testCase = new TestCase(singletonList(1));
        doThrow(AssertionError.class)
                .when(failingTest).aFailingTest(anyInt());
        Method method = aFailingTestMethod(int.class);

        assertThatThrownBy(() -> testCase.execute(failingTest, method))
                .isInstanceOf(AssertionError.class);
    }

    // TODO: Determine what exceptions we want to report when a test fails or cannot be invoked.
    //   Do we use the standard reflection exceptions or do we hide these by throwing another kind?

    private Method aValidTestMethod(Class<?>... testParameters) {
        return aTest(TestExample.class, "aValidTest", testParameters);
    }

    private Method aFailingTestMethod(Class<?>... testParameters) {
        return aTest(FailingTestExample.class, "aFailingTest", testParameters);
    }

    private Method aTest(Class<?> testClass, String methodName, Class<?>[] testParameters) {
        try {
            return testClass.getDeclaredMethod(methodName, testParameters);
        } catch (NoSuchMethodException e) {
            throw new AssertionError("unable to find method", e);
        }
    }
}
