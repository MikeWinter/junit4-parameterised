package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class AbstractRunnerTest<T extends IgnorableRunner> {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private final Class<T> type;

    AbstractRunnerTest(Class<T> type) {
        this.type = type;
    }

    T forValidTest(Class<?> theClass, Class<?>... testParameters) {
        return forTest(theClass, "aValidTest", testParameters);
    }

    T forFailingTest(Class<?> theClass, Class<?>... testParameters) {
        return forTest(theClass, "aFailingTest", testParameters);
    }

    T forTest(Class<?> theClass, String testMethodName, Class<?>... testParameters) {
        TestClass testClass = new TestClass(theClass);
        try {
            FrameworkMethod testCase = new FrameworkMethod(theClass.getDeclaredMethod(testMethodName, testParameters));
            return newInstance(testClass, testCase);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("No such test method: " + testMethodName, e);
        }
    }

    private T newInstance(TestClass testClass, FrameworkMethod testCase) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor(TestClass.class, FrameworkMethod.class);
            return constructor.newInstance(testClass, testCase);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new AssertionError("Could not instantiate runner", e);
        } catch (NoSuchMethodException e) {
            throw new AssertionError("Runner does not declare required constructor", e);
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("Could not instantiate runner", e);
        } catch (IllegalAccessException e) {
            throw new AssertionError("Runner constructor is inaccessible", e);
        }
    }
}
