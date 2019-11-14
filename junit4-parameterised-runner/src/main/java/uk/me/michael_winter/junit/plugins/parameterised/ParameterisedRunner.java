package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.FrameworkMember;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

public class ParameterisedRunner extends ParentRunner<IgnorableRunner> {
    private static final Predicate<FrameworkMember> IS_PUBLIC_MEMBER = FrameworkMember::isPublic;
    private static final Predicate<FrameworkMember> IS_STATIC_MEMBER = FrameworkMember::isStatic;
    private static final Predicate<FrameworkMember> IS_NOT_PUBLIC_MEMBER = IS_PUBLIC_MEMBER.negate();
    private static final Predicate<FrameworkMember> HAS_TIMEOUT_PARAMETER
            = member -> member.getAnnotation(Test.class).timeout() != 0;
    private static final Predicate<FrameworkMember> HAS_EXPECTED_PARAMETER
            = member -> member.getAnnotation(Test.class).expected() != Test.None.class;
    private static final Predicate<FrameworkField> IS_TEST_RULE_FIELD
            = field -> !TestRule.class.isAssignableFrom(field.getType());
    private static final Predicate<FrameworkMethod> IS_TEST_RULE_METHOD
            = method -> !TestRule.class.isAssignableFrom(method.getReturnType());

    private final List<IgnorableRunner> runners;

    /**
     * Constructs a new {@code ParameterisedRunner} that will run {@code testClass}
     *
     * @throws InitializationError if the test class is malformed.
     */
    public ParameterisedRunner(Class<?> testClass) throws InitializationError {
        super(testClass);

        runners = testMethods()
                .map(this::toRunner)
                .collect(toList());
    }

    @Override
    protected List<IgnorableRunner> getChildren() {
        return runners;
    }

    @Override
    protected Description describeChild(IgnorableRunner runner) {
        return runner.getDescription();
    }

    @Override
    protected void runChild(IgnorableRunner runner, RunNotifier notifier) {
        runner.run(notifier);
    }

    @Override
    protected boolean isIgnored(IgnorableRunner child) {
        return child.isIgnored();
    }

    @Override
    protected void collectInitializationErrors(List<Throwable> errors) {
        super.collectInitializationErrors(errors);

        checkTestsAreNotInNonStaticInnerClass(errors);
        checkConstructorsAreValid(errors);
        checkMethodsAreValid(errors);
        checkRuleFieldsAreValid(errors);
        checkRuleMethodsAreValid(errors);
    }

    private void checkTestsAreNotInNonStaticInnerClass(List<Throwable> errors) {
        if (getTestClass().isANonStaticInnerClass()) {
            errors.add(new Exception(getTestClass().getName() + " should not be an inner class"));
        }
    }

    private void checkConstructorsAreValid(List<Throwable> errors) {
        if (getTestClass().getOnlyConstructor().getParameterCount() != 0) {
            errors.add(new Exception(getTestClass().getName() + " constructor should not require arguments"));
        }
    }

    private Stream<FrameworkMethod> testMethods() {
        return getTestClass()
                .getAnnotatedMethods(Test.class)
                .stream();
    }

    private void checkMethodsAreValid(List<Throwable> errors) {
        checkMethodsArePublicVoid(errors);
        checkMethodsDoNotUseTimeoutParameter(errors);
        checkMethodsDoNotUseExpectedExceptionParameter(errors);
    }

    private void checkMethodsArePublicVoid(List<Throwable> errors) {
        testMethods().forEachOrdered(method -> method.validatePublicVoid(false, errors));
    }

    private void checkMethodsDoNotUseTimeoutParameter(List<Throwable> errors) {
        testMethods().filter(HAS_TIMEOUT_PARAMETER)
                .map(f -> new Exception("Method " + f.getName()
                        + "() uses timeout parameter. Use Timeout rule instead."))
                .forEachOrdered(errors::add);
    }

    private void checkMethodsDoNotUseExpectedExceptionParameter(List<Throwable> errors) {
        testMethods().filter(HAS_EXPECTED_PARAMETER)
                .map(f -> new Exception("Method " + f.getName()
                        + "() uses expected parameter. Use ExpectedException rule instead."))
                .forEachOrdered(errors::add);
    }

    private void checkRuleFieldsAreValid(List<Throwable> errors) {
        final List<FrameworkField> ruleFields = getTestClass().getAnnotatedFields(Rule.class);
        checkRuleFieldsArePublic(ruleFields, errors);
        checkRuleFieldsAreNotStatic(ruleFields, errors);
        checkRuleFieldsImplementTestRule(ruleFields, errors);
    }

    private void checkRuleFieldsArePublic(List<FrameworkField> ruleFields, List<Throwable> errors) {
        ruleFields.stream()
                .filter(IS_NOT_PUBLIC_MEMBER)
                .map(field -> new Exception("Field " + field.getName() + " should be public"))
                .forEachOrdered(errors::add);
    }

    private void checkRuleFieldsAreNotStatic(List<FrameworkField> ruleFields, List<Throwable> errors) {
        ruleFields.stream()
                .filter(IS_STATIC_MEMBER)
                .map(field -> new Exception("Field " + field.getName() + " should not be static"))
                .forEachOrdered(errors::add);
    }

    private void checkRuleFieldsImplementTestRule(List<FrameworkField> ruleFields, List<Throwable> errors) {
        ruleFields.stream()
                .filter(IS_TEST_RULE_FIELD)
                .map(field -> new Exception("Field " + field.getName()
                        + " should implement " + TestRule.class.getCanonicalName()))
                .forEachOrdered(errors::add);
    }

    private void checkRuleMethodsAreValid(List<Throwable> errors) {
        final List<FrameworkMethod> ruleMethods = getTestClass().getAnnotatedMethods(Rule.class);
        checkRuleMethodsArePublic(ruleMethods, errors);
        checkRuleMethodsAreNotStatic(ruleMethods, errors);
        checkRuleMethodsImplementTestRule(ruleMethods, errors);
    }

    private void checkRuleMethodsArePublic(List<FrameworkMethod> ruleMethods, List<Throwable> errors) {
        ruleMethods.stream()
                .filter(IS_NOT_PUBLIC_MEMBER)
                .map(method -> new Exception("Method " + method.getName() + "() should be public"))
                .forEachOrdered(errors::add);
    }

    private void checkRuleMethodsAreNotStatic(List<FrameworkMethod> ruleMethods, List<Throwable> errors) {
        ruleMethods.stream()
                .filter(IS_STATIC_MEMBER)
                .map(method -> new Exception("Method " + method.getName() + "() should not be static"))
                .forEachOrdered(errors::add);
    }

    private void checkRuleMethodsImplementTestRule(List<FrameworkMethod> ruleMethods, List<Throwable> errors) {
        ruleMethods.stream()
                .filter(IS_TEST_RULE_METHOD)
                .map(method -> new Exception("Method " + method.getName()
                        + "() should implement " + TestRule.class.getCanonicalName()))
                .forEachOrdered(errors::add);
    }

    private IgnorableRunner toRunner(FrameworkMethod method) {
        if (method.getMethod().getParameterCount() == 0) {
            return new NonParameterisedTestRunner(getTestClass(), method);
        }

        throw new UnsupportedOperationException(format(
                "No suitable test runner for method %s(%s)",
                method.getName(),
                Arrays.stream(method.getMethod().getParameterTypes())
                        .map(Class::getSimpleName)
                        .collect(Collectors.joining(", "))));
    }
}
