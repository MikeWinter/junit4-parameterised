package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

abstract class IgnorableRunner extends Runner {
    final TestClass testClass;

    IgnorableRunner(TestClass testClass) {
        this.testClass = testClass;
    }

    abstract boolean isIgnored();

    Statement executingBeforeMethods(Statement next, Object testInstance) {
        List<FrameworkMethod> beforeMethods = testClass.getAnnotatedMethods(Before.class);
        return beforeMethods.isEmpty() ? next : new BeforeInvoker(next, beforeMethods, testInstance);
    }

    Statement executingAfterMethods(Statement next, Object testInstance) {
        List<FrameworkMethod> afterMethods = testClass.getAnnotatedMethods(After.class);
        return afterMethods.isEmpty() ? next : new AfterInvoker(next, afterMethods, testInstance);
    }

    Statement executingRules(Statement next, Object testInstance) {
        List<TestRule> rules = new ArrayList<>();
        rules.addAll(testClass.getAnnotatedFieldValues(testInstance, Rule.class, TestRule.class));
        rules.addAll(testClass.getAnnotatedMethodValues(testInstance, Rule.class, TestRule.class));
        return rules.isEmpty() ? next : new RuleInvoker(next, rules, getDescription());
    }

    static class ReflectiveCreator {
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

    static class BeforeInvoker extends Statement {
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

    static class AfterInvoker extends Statement {
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

    static class RuleInvoker extends Statement {
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

    static class DeferredException extends Statement {
        private final Throwable e;

        DeferredException(Throwable e) {
            this.e = e;
        }

        public void evaluate() throws Throwable {
            throw e;
        }
    }
}
