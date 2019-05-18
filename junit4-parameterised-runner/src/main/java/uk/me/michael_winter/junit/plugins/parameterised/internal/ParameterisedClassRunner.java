package uk.me.michael_winter.junit.plugins.parameterised.internal;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ParameterisedClassRunner extends ParentRunner<FrameworkMethod> {
    public ParameterisedClassRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    private static boolean isParameterised(FrameworkMethod method) {
        return method.getMethod().getParameterCount() > 0;
    }

    @Override
    protected List<FrameworkMethod> getChildren() {
        return testMethods().collect(toList());
    }

    private Stream<FrameworkMethod> testMethods() {
        return getTestClass().getAnnotatedMethods(Test.class)
                .stream()
                .filter(ParameterisedClassRunner::isParameterised);
    }

    @Override
    protected Description describeChild(FrameworkMethod method) {
        return Description.createTestDescription(getTestClass().getJavaClass(), method.getName(), method.getAnnotations());
    }

    @Override
    protected void runChild(FrameworkMethod child, RunNotifier notifier) {

    }
}
