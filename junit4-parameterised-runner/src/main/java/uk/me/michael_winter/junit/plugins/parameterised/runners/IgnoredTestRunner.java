package uk.me.michael_winter.junit.plugins.parameterised.runners;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

public class IgnoredTestRunner extends IgnorableRunner {
    public IgnoredTestRunner(TestClass testClass, FrameworkMethod method) {
    }

    @Override
    public Description getDescription() {
        return Description.EMPTY;
    }

    @Override
    public void run(RunNotifier notifier) {

    }

    @Override
    public boolean isIgnored() {
        return true;
    }
}
