package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.junit.runner.Description.createTestDescription;

class NonParameterisedTestRunner extends IgnorableRunner {
    private final TestClass testClass;
    private final FrameworkMethod method;
    private Description description = null;

    NonParameterisedTestRunner(TestClass testClass, FrameworkMethod method) {
        if (method.getMethod().getParameterCount() > 0) {
            throw new IllegalArgumentException("Method " + method.getName() + " should have no parameters");
        }

        this.testClass = testClass;
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

    private Statement executingBeforeMethods(Statement next, Object testInstance) {
        List<FrameworkMethod> beforeMethods = testClass.getAnnotatedMethods(Before.class);
        return beforeMethods.isEmpty() ? next : new BeforeInvoker(next, beforeMethods, testInstance);
    }

    private Statement executingAfterMethods(Statement next, Object testInstance) {
        List<FrameworkMethod> afterMethods = testClass.getAnnotatedMethods(After.class);
        return afterMethods.isEmpty() ? next : new AfterInvoker(next, afterMethods, testInstance);
    }

    private Statement executingRules(Statement next, Object testInstance) {
        List<TestRule> rules = testClass.getAnnotatedFieldValues(testInstance, Rule.class, TestRule.class);
        rules.addAll(testClass.getAnnotatedMethodValues(testInstance, Rule.class, TestRule.class));
        return rules.isEmpty() ? next : new RuleInvoker(next, rules, getDescription());
    }

    public static class ReflectiveCreator {
        private final Constructor<?> constructor;

        ReflectiveCreator(Constructor<?> constructor) {
            this.constructor = constructor;
        }

        Object create() throws Throwable {
            try {
                return constructor.newInstance();
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
    }

    public static class MethodInvoker extends Statement {
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

    public static class BeforeInvoker extends Statement {
        private final Statement next;
        private final List<FrameworkMethod> beforeMethods;
        private final Object test;

        BeforeInvoker(Statement next, List<FrameworkMethod> beforeMethods, Object test) {
            this.next = next;
            this.beforeMethods = beforeMethods;
            this.test = test;
        }

        @Override
        public void evaluate() throws Throwable {
            for (FrameworkMethod before : beforeMethods) {
                before.invokeExplosively(test);
            }
            next.evaluate();
        }
    }

    public static class AfterInvoker extends Statement {
        private final Statement next;
        private final List<FrameworkMethod> afterMethods;
        private final Object test;

        AfterInvoker(Statement next, List<FrameworkMethod> afterMethods, Object test) {
            this.next = next;
            this.afterMethods = afterMethods;
            this.test = test;
        }

        @Override
        public void evaluate() throws Throwable {
            List<Throwable> errors = new LinkedList<>();
            try {
                next.evaluate();
            } catch (Throwable e) {
                errors.add(e);
            } finally {
                for (FrameworkMethod after : afterMethods) {
                    try {
                        after.invokeExplosively(test);
                    } catch (Throwable e) {
                        errors.add(e);
                    }
                }
            }
            MultipleFailureException.assertEmpty(errors);
        }
    }

    public static class RuleInvoker extends Statement {
        private final Statement statement;

        RuleInvoker(Statement next, Iterable<TestRule> rules, Description description) {
            statement = applyAll(next, rules, description);
        }

        private Statement applyAll(Statement next, Iterable<TestRule> rules, Description description) {
            for (TestRule rule : rules) {
                next = rule.apply(next, description);
            }
            return next;
        }

        @Override
        public void evaluate() throws Throwable {
            statement.evaluate();
        }
    }

    public static class DeferredException extends Statement {
        private final Throwable e;

        DeferredException(Throwable e) {
            this.e = e;
        }

        public void evaluate() throws Throwable {
            throw e;
        }
    }
}
