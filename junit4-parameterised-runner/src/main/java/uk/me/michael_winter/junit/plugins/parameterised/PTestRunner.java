package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

import java.util.List;

public class PTestRunner extends ParentRunner<TestCase> {
    protected PTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected List<TestCase> getChildren() {
        return null;
    }

    @Override
    protected Description describeChild(TestCase child) {
        return null;
    }

    @Override
    protected void runChild(TestCase child, RunNotifier notifier) {

    }
}
