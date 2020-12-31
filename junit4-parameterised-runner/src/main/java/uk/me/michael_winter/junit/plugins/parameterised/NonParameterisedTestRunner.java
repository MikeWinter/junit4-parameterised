package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.Ignore;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

import static java.util.Objects.nonNull;
import static org.junit.runner.Description.createTestDescription;

class NonParameterisedTestRunner extends IgnorableRunner {
    private final FrameworkMethod method;
    private Description description = null;

    NonParameterisedTestRunner(TestClass testClass, FrameworkMethod method) {
        super(testClass);

        if (method.getMethod().getParameterCount() > 0) {
            throw new IllegalArgumentException("Method " + method.getName() + " should have no parameters");
        }

        this.method = method;
    }

    @Override
    public Description getDescription() {
        if (description == null) {
            description = createTestDescription(
                    method.getDeclaringClass(),
                    method.getName(),
                    method.getAnnotations());
        }
        return description;
    }

    @Override
    boolean isIgnored() {
        return nonNull(method.getAnnotation(Ignore.class));
    }

    @Override
    public void run(RunNotifier notifier) {
        Description description = getDescription();
        if (isIgnored()) {
            notifier.fireTestIgnored(description);
        } else {
            runTest(methodBlock(), description, notifier);
        }
    }

    private void runTest(Statement statement, Description description, RunNotifier notifier) {
        notifier.fireTestStarted(description);
        try {
            statement.evaluate();
        } catch (MultipleFailureException e) {
            e.getFailures()
                    .forEach(throwable -> notifier.fireTestFailure(new Failure(description, throwable)));
        } catch (Throwable throwable) {
            notifier.fireTestFailure(new Failure(description, throwable));
        } finally {
            notifier.fireTestFinished(description);
        }
    }

    private Statement methodBlock() {
        try {
            Object testInstance = new ReflectiveCreator(testClass.getOnlyConstructor()).create();

            Statement statement = new MethodInvoker(method, testInstance);
            statement = executingBeforeMethods(statement, testInstance);
            statement = executingAfterMethods(statement, testInstance);
            statement = executingRules(statement, testInstance);
            return statement;
        } catch (Throwable e) {
            return new DeferredException(e);
        }
    }

    private static class MethodInvoker extends Statement {
        private final FrameworkMethod method;
        private final Object instance;

        MethodInvoker(FrameworkMethod method, Object instance) {
            this.method = method;
            this.instance = instance;
        }

        @Override
        public void evaluate() throws Throwable {
            method.invokeExplosively(instance);
        }
    }
}
