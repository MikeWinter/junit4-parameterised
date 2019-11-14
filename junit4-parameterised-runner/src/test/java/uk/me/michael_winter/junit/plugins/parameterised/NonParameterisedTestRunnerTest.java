package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;

public class NonParameterisedTestRunnerTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private static NonParameterisedTestRunner forValidTest(Class<?> theClass) {
        return forTest(theClass, "aValidTest");
    }

    private static NonParameterisedTestRunner forFailingTest(Class<?> theClass) {
        return forTest(theClass, "aFailingTest");
    }

    private static NonParameterisedTestRunner forTest(Class<?> theClass, String testMethodName) {
        TestClass testClass = new TestClass(theClass);
        try {
            FrameworkMethod testCase = new FrameworkMethod(theClass.getDeclaredMethod(testMethodName));
            return new NonParameterisedTestRunner(testClass, testCase);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("No such test method, " + testMethodName, e);
        }
    }

    @Test
    public void shouldDescribeTestMethodsAsTests() {
        Runner runner = forValidTest(TestExample.class);

        Description description = runner.getDescription();

        assertThat(description.isTest())
                .isTrue();
    }

    @Test
    public void shouldUseMethodNameWhenDescribingTests() {
        Runner runner = forValidTest(TestExample.class);

        Description description = runner.getDescription();

        assertThat(description.getMethodName())
                .isEqualTo("aValidTest");
    }

    @Test
    public void shouldUseFullyQualifiedClassNameWhenDescribingClassOfTestMethods() {
        Runner runner = forValidTest(TestExample.class);

        Description description = runner.getDescription();

        assertThat(description.getClassName())
                .isEqualTo("uk.me.michael_winter.junit.plugins.parameterised.TestExample");
    }

    @Test
    public void shouldProvideAnnotationsWhenDescribingTestMethods() {
        Runner runner = forValidTest(TestExample.class);

        Description description = runner.getDescription();

        assertThat(description.getAnnotations())
                .hasOnlyElementsOfType(Test.class);
    }

    @Test
    public void shouldFailOnConstructionIfTestRequiresParameters() throws NoSuchMethodException {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("no parameters");
        TestClass testClass = new TestClass(ParameterisedTestExample.class);
        FrameworkMethod testCase = new FrameworkMethod(
                ParameterisedTestExample.class.getDeclaredMethod(
                        "aParameterisedTest",
                        int.class));

        new NonParameterisedTestRunner(testClass, testCase);
    }

    @Test
    public void shouldNotSendFailureLifecycleEventForSuccessfulTests() {
        Runner runner = forValidTest(TestExample.class);
        RunNotifier runNotifier = Mockito.mock(RunNotifier.class);

        runner.run(runNotifier);

        verify(runNotifier, never()).fireTestFailure(any());
    }

    @Test
    public void shouldSendSuccessfulLifecycleEventNotificationsInOrder() {
        Runner runner = forValidTest(TestExample.class);
        RunNotifier runNotifier = Mockito.mock(RunNotifier.class);

        runner.run(runNotifier);

        InOrder inOrder = Mockito.inOrder(runNotifier);
        inOrder.verify(runNotifier).fireTestStarted(notNull());
        inOrder.verify(runNotifier).fireTestFinished(notNull());
    }

    @Test
    public void shouldSendFailureLifecycleEventForFailingTests() {
        Runner runner = forFailingTest(FailingTestExample.class);
        RunNotifier runNotifier = Mockito.mock(RunNotifier.class);

        runner.run(runNotifier);

        verify(runNotifier).fireTestFailure(notNull());
    }

    @Test
    public void shouldIncludeTheImmediateCauseOfFailure() {
        Runner runner = forFailingTest(FailingTestExample.class);
        RunNotifier runNotifier = Mockito.mock(RunNotifier.class);

        runner.run(runNotifier);

        ArgumentCaptor<Failure> failureArgumentCaptor = ArgumentCaptor.forClass(Failure.class);
        verify(runNotifier).fireTestFailure(failureArgumentCaptor.capture());
        Throwable cause = failureArgumentCaptor.getValue().getException();

        assertThat(cause)
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("expected to fail");
    }

    @Test
    public void shouldSendFailureLifecycleEventNotificationsInOrder() {
        Runner runner = forFailingTest(FailingTestExample.class);
        RunNotifier runNotifier = Mockito.mock(RunNotifier.class);
        InOrder inOrder = Mockito.inOrder(runNotifier);

        runner.run(runNotifier);

        inOrder.verify(runNotifier).fireTestStarted(notNull());
        inOrder.verify(runNotifier).fireTestFailure(notNull());
        inOrder.verify(runNotifier).fireTestFinished(notNull());
    }

    @Test
    public void shouldRunMethodsWithBeforeAnnotationsOnceBeforeTest() {
        Runner runner = forValidTest(BeforeExample.class);
        RunNotifier runNotifier = Mockito.mock(RunNotifier.class);

        runner.run(runNotifier);

        verify(runNotifier, never()).fireTestFailure(any());
        assertThat(BeforeExample.hasBeenSetupForTest()).isTrue();
    }

    @Test
    public void shouldRunMethodsWithAfterAnnotationsOnceAfterTest() {
        Runner runner = forValidTest(AfterExample.class);
        RunNotifier runNotifier = Mockito.mock(RunNotifier.class);

        runner.run(runNotifier);

        verify(runNotifier, never()).fireTestFailure(any());
        assertThat(AfterExample.hasBeenTornDownForTest()).isTrue();
    }

    @Test
    public void shouldRunAfterMethodsEvenIfPrecededByException() {
        Runner runner = forFailingTest(FailingWithAfterExample.class);
        RunNotifier runNotifier = Mockito.mock(RunNotifier.class);

        runner.run(runNotifier);

        verify(runNotifier).fireTestFailure(notNull());
        assertThat(FailingWithAfterExample.hasBeenTornDownForTest()).isTrue();
    }

    @Test
    public void shouldReportAllExceptionsInAfterMethods() {
        Runner runner = forValidTest(FailingTeardownWithAfterExample.class);
        RunNotifier runNotifier = Mockito.mock(RunNotifier.class);

        runner.run(runNotifier);

        final ArgumentCaptor<Failure> failureArgumentCaptor = ArgumentCaptor.forClass(Failure.class);
        verify(runNotifier).fireTestFailure(failureArgumentCaptor.capture());
        Throwable cause = failureArgumentCaptor.getValue().getException();

        assertThat(cause)
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("expected to fail");
    }

    @Test
    public void shouldEvaluateRuleFieldsForEachTest() {
        Runner runner = forValidTest(RuleFieldExample.class);
        RunNotifier runNotifier = Mockito.mock(RunNotifier.class);

        runner.run(runNotifier);

        verify(runNotifier, never()).fireTestFailure(any());
        assertThat(RuleFieldExample.rulesHaveBeenProcessedForTest()).isTrue();
    }

    @Test
    public void shouldEvaluateRuleMethodsForEachTest() {
        Runner runner = forValidTest(RuleMethodExample.class);
        RunNotifier runNotifier = Mockito.mock(RunNotifier.class);

        runner.run(runNotifier);

        verify(runNotifier, never()).fireTestFailure(any());
        assertThat(RuleMethodExample.rulesHaveBeenProcessedForTest()).isTrue();
    }

    @Test
    public void shouldEvaluateRuleFieldsBeforeRuleMethods() {
        Runner runner = forValidTest(RuleOrderExample.class);
        RunNotifier runNotifier = Mockito.mock(RunNotifier.class);

        runner.run(runNotifier);

        verify(runNotifier, never()).fireTestFailure(any());
        assertThat(RuleOrderExample.ruleFieldWasInvokedFirst()).isTrue();
    }

    @Test
    public void shouldConsiderTestIgnoredIfAnnotatedWithIgnore() {
        IgnorableRunner runner = forTest(IgnoredTestMethodExample.class, "testIsIgnored");

        assertThat(runner.isIgnored()).isTrue();
    }

    @Test
    public void shouldOnlyNotifyTestAsIgnoredIfAnnotatedWithIgnore() {
        IgnorableRunner runner = forTest(IgnoredTestMethodExample.class, "testIsIgnored");
        RunNotifier runNotifier = Mockito.mock(RunNotifier.class);

        runner.run(runNotifier);

        verify(runNotifier).fireTestIgnored(notNull());
        verifyNoMoreInteractions(runNotifier);
    }
}
