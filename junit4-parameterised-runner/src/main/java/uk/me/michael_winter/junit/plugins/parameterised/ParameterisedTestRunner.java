package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.Ignore;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

import static java.util.Objects.nonNull;
import static org.junit.runner.Description.createTestDescription;

class ParameterisedTestRunner extends IgnorableRunner {
    private final FrameworkMethod method;
    private Description description;

    ParameterisedTestRunner(TestClass testClass, FrameworkMethod method) {
        super(testClass);

        if (method.getMethod().getParameterCount() == 0) {
            throw new IllegalArgumentException("Method " + method.getName() + " should have at least one parameter");
        }

        this.method = method;
    }

    @Override
    boolean isIgnored() {
        return nonNull(method.getAnnotation(Ignore.class));
    }

    private Description addedDescription1;
    private Description addedDescription2;

    @Override
    public Description getDescription() {
        if (description == null) {
            description = Description.createSuiteDescription(
//                    String.format(
//                            "%s.%s",
//                            method.getDeclaringClass().getName(),
//                            method.getName()),
                    method.getName() + "foo",
                    method.getName(),
                    method.getAnnotations());

            addedDescription1 = createTestDescription(
                    method.getDeclaringClass().getName(),
                    method.getName() + "(foo1)");
            addedDescription2 = createTestDescription(
                    method.getDeclaringClass().getName(),
                    method.getName() + "(foo2)");
            description.addChild(addedDescription1);
            description.addChild(addedDescription2);

//            description = createTestDescription(
//                    method.getDeclaringClass(),
//                    method.getName(),
//                    method.getAnnotations());
        }
        return description;
    }

    @Override
    public void run(RunNotifier notifier) {
        if (isIgnored()) {
            notifier.fireTestIgnored(description);
        } else {
//            notifier.fireTestStarted(description);
            runTest(methodBlock(), notifier, addedDescription1);
            runTest(methodBlock(), notifier, addedDescription2);
//            notifier.fireTestFinished(description);
        }
    }

    private void runTest(Statement statement, RunNotifier notifier, Description description) {
        notifier.fireTestStarted(description);
        try {
            statement.evaluate();
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
            method.invokeExplosively(instance, 1);
        }
    }
}
