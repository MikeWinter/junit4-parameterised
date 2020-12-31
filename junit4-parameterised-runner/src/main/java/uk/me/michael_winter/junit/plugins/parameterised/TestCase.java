package uk.me.michael_winter.junit.plugins.parameterised;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

class TestCase {
    private final List<?> parameters;

    TestCase(List<?> parameters) {
        this.parameters = parameters;
    }

    void execute(Object test, Method method) throws Throwable {
        try {
            method.invoke(test, parameters.toArray());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }
}
