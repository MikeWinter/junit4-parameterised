package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;

public class ParameterisedTestRunnerTest extends AbstractRunnerTest<ParameterisedTestRunner> {
    public ParameterisedTestRunnerTest() {
        super(ParameterisedTestRunner.class);
    }

    @Test
    public void shouldDescribeTestMethodsAsSuites() {
        Runner runner = forValidTest(TestExample.class, int.class);

        Description description = runner.getDescription();

        assertThat(description.isSuite())
                .isTrue();
    }

    @Test
    public void shouldUseMethodNameWhenDescribingTests() {
        Runner runner = forValidTest(TestExample.class, int.class);

        Description description = runner.getDescription();

        assertThat(description.getMethodName())
                .isEqualTo("aValidTest");
    }

    @Test
    public void shouldUseFullyQualifiedClassNameWhenDescribingClassOfTestMethods() {
        Runner runner = forValidTest(TestExample.class, int.class);

        Description description = runner.getDescription();

        assertThat(description.getClassName())
                .isEqualTo("uk.me.michael_winter.junit.plugins.parameterised.TestExample");
    }

    @Test
    public void shouldProvideAnnotationsWhenDescribingTestMethods() {
        Runner runner = forValidTest(TestExample.class, int.class);

        Description description = runner.getDescription();

        assertThat(description.getAnnotations())
                .hasAtLeastOneElementOfType(Test.class);
    }

    @Test
    public void shouldFailOnConstructionIfTestHasNoParameters() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("at least one parameter");

        forValidTest(TestExample.class);
    }

    @Test
    public void shouldSendSuccessfulLifecycleEventNotificationsInOrder() {
        Runner runner = forValidTest(TestExample.class, int.class);
        RunNotifier runNotifier = mock(RunNotifier.class);

        runner.run(runNotifier);

        InOrder inOrder = inOrder(runNotifier);
        inOrder.verify(runNotifier).fireTestStarted(notNull());
        inOrder.verify(runNotifier).fireTestFinished(notNull());
    }

    @Test
    public void shouldSendFailureLifecycleEventForFailingTests() {
        Runner runner = forFailingTest(FailingTestExample.class, int.class);
        RunNotifier runNotifier = mock(RunNotifier.class);

        runner.run(runNotifier);

        verify(runNotifier).fireTestFailure(notNull());
    }

    @Test
    public void shouldNotSendFailureLifecycleEventForSuccessfulTests() {
        Runner runner = forValidTest(TestExample.class, int.class);
        RunNotifier runNotifier = mock(RunNotifier.class);

        runner.run(runNotifier);

        verify(runNotifier, never()).fireTestFailure(any());
    }

    @Test
    public void shouldSendFailureLifecycleEventNotificationsInOrder() {
        Runner runner = forFailingTest(FailingTestExample.class, int.class);
        RunNotifier runNotifier = mock(RunNotifier.class);
        InOrder inOrder = inOrder(runNotifier);

        runner.run(runNotifier);

        inOrder.verify(runNotifier).fireTestStarted(notNull());
        inOrder.verify(runNotifier).fireTestFailure(notNull());
        inOrder.verify(runNotifier).fireTestFinished(notNull());
    }

    @Test
    public void shouldIncludeTheImmediateCauseOfFailure() {
        Runner runner = forFailingTest(FailingTestExample.class, int.class);
        RunNotifier runNotifier = mock(RunNotifier.class);

        runner.run(runNotifier);

        ArgumentCaptor<Failure> failureArgumentCaptor = forClass(Failure.class);
        verify(runNotifier).fireTestFailure(failureArgumentCaptor.capture());
        Throwable cause = failureArgumentCaptor.getValue().getException();

        assertThat(cause)
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("expected to fail");
    }

    @Test
    public void shouldRunMethodsWithBeforeAnnotationsOnceBeforeTest() {
        Runner runner = forValidTest(BeforeExample.class, int.class);
        RunNotifier runNotifier = mock(RunNotifier.class);

        runner.run(runNotifier);

        verify(runNotifier, never()).fireTestFailure(any());
        assertThat(BeforeExample.hasBeenSetupForTest()).isTrue();
    }

    @Test
    public void shouldRunMethodsWithAfterAnnotationsOnceAfterTest() {
        Runner runner = forValidTest(AfterExample.class, int.class);
        RunNotifier runNotifier = mock(RunNotifier.class);

        runner.run(runNotifier);

        verify(runNotifier, never()).fireTestFailure(any());
        assertThat(AfterExample.hasBeenTornDownForTest()).isTrue();
    }

    @Test
    public void shouldRunAfterMethodsEvenIfPrecededByException() {
        Runner runner = forFailingTest(FailingWithAfterExample.class, int.class);
        RunNotifier runNotifier = mock(RunNotifier.class);

        runner.run(runNotifier);

        verify(runNotifier).fireTestFailure(notNull());
        assertThat(FailingWithAfterExample.hasBeenTornDownForTest()).isTrue();
    }

    @Test
    public void shouldReportAllExceptionsInAfterMethods() {
        Runner runner = forValidTest(FailingTeardownWithAfterExample.class, int.class);
        RunNotifier runNotifier = mock(RunNotifier.class);

        runner.run(runNotifier);

        ArgumentCaptor<Failure> failureArgumentCaptor = forClass(Failure.class);
        verify(runNotifier).fireTestFailure(failureArgumentCaptor.capture());
        Throwable cause = failureArgumentCaptor.getValue().getException();

        assertThat(cause)
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("expected to fail");
    }

    @Test
    public void shouldEvaluateRuleFieldsForEachTest() {
        Runner runner = forValidTest(RuleFieldExample.class, int.class);
        RunNotifier runNotifier = mock(RunNotifier.class);

        runner.run(runNotifier);

        verify(runNotifier, never()).fireTestFailure(any());
        assertThat(RuleFieldExample.rulesHaveBeenProcessedForTest()).isTrue();
    }

    @Test
    public void shouldEvaluateRuleMethodsForEachTest() {
        Runner runner = forValidTest(RuleMethodExample.class, int.class);
        RunNotifier runNotifier = mock(RunNotifier.class);

        runner.run(runNotifier);

        verify(runNotifier, never()).fireTestFailure(any());
        assertThat(RuleMethodExample.rulesHaveBeenProcessedForTest()).isTrue();
    }

    @Test
    public void shouldEvaluateRuleFieldsBeforeRuleMethods() {
        Runner runner = forValidTest(RuleOrderExample.class, int.class);
        RunNotifier runNotifier = mock(RunNotifier.class);

        runner.run(runNotifier);

        verify(runNotifier, never()).fireTestFailure(any());
        assertThat(RuleOrderExample.ruleFieldWasInvokedFirst()).isTrue();
    }

    @Test
    public void shouldConsiderTestIgnoredIfAnnotatedWithIgnore() {
        IgnorableRunner runner = forTest(IgnoredTestMethodExample.class, "testIsIgnored", int.class);

        assertThat(runner.isIgnored()).isTrue();
    }

    @Test
    public void shouldOnlyNotifyTestAsIgnoredWhenAnnotatedWithIgnore() {
        IgnorableRunner runner = forTest(IgnoredTestMethodExample.class, "testIsIgnored", int.class);
        RunNotifier runNotifier = mock(RunNotifier.class);

        runner.run(runNotifier);

        verify(runNotifier).fireTestIgnored(notNull());
        verifyNoMoreInteractions(runNotifier);
    }

    // TODO: correct test count based on parameters
}
