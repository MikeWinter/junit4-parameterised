package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;
import static uk.me.michael_winter.junit.plugins.parameterised.InitializationErrorMatcher.anInitialisationErrorMessageContaining;

public class ParameterisedRunnerTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldCountEveryNonParameterisedTestOnce() throws Exception {
        Runner runner = new ParameterisedRunner(TestExample.class);

        int testCount = runner.testCount();

        assertThat(testCount).isEqualTo(2);
    }

    @Test
    public void shouldUseFullyQualifiedNameOfClassUnderTestWhenDescribingClassName() throws Exception {
        Runner runner = new ParameterisedRunner(TestExample.class);

        String className = runner.getDescription().getClassName();

        assertThat(className).isEqualTo("uk.me.michael_winter.junit.plugins.parameterised.TestExample");
    }

    @Test
    public void shouldUseFullyQualifiedNameOfClassUnderTestWhenDescribingDisplayName() throws Exception {
        Runner runner = new ParameterisedRunner(TestExample.class);

        String displayName = runner.getDescription().getDisplayName();

        assertThat(displayName).isEqualTo("uk.me.michael_winter.junit.plugins.parameterised.TestExample");
    }

    @Test
    public void shouldDescribeTheClassUnderTestAsASuite() throws Exception {
        Runner runner = new ParameterisedRunner(TestExample.class);

        boolean suite = runner.getDescription().isSuite();

        assertThat(suite).isTrue();
    }

    @Test
    public void shouldNotDescribeAMethodForTheClassUnderTest() throws Exception {
        Runner runner = new ParameterisedRunner(TestExample.class);

        String methodName = runner.getDescription().getMethodName();

        assertThat(methodName).isNull();
    }

    @Test
    public void shouldFailOnConstructionIfTestMethodIsNotPublic() throws Exception {
        expectedException.expect(InitializationError.class);
        expectedException.expect(anInitialisationErrorMessageContaining("public"));

        new ParameterisedRunner(InvalidTestMethodVisibilityExample.class);
    }

    @Test
    public void shouldFailOnConstructionIfTestMethodIsStatic() throws Exception {
        expectedException.expect(InitializationError.class);
        expectedException.expect(anInitialisationErrorMessageContaining("static"));

        new ParameterisedRunner(InvalidStaticTestMethodExample.class);
    }

    @Test
    public void shouldFailOnConstructionIfTestMethodReturnsNonVoidType() throws Exception {
        expectedException.expect(InitializationError.class);
        expectedException.expect(anInitialisationErrorMessageContaining("void"));

        new ParameterisedRunner(InvalidTestReturnTypeExample.class);
    }

    @Test
    public void shouldFailOnConstructionIfTestUsesTimeoutAnnotationParameter() throws Exception {
        expectedException.expect(InitializationError.class);
        expectedException.expect(anInitialisationErrorMessageContaining("timeout"));

        new ParameterisedRunner(FailingTestUsingTimeoutParameterExample.class);
    }

    @Test
    public void shouldFailOnConstructionIfTestUsesExpectedAnnotationParameter() throws Exception {
        expectedException.expect(InitializationError.class);
        expectedException.expect(anInitialisationErrorMessageContaining("expected"));

        new ParameterisedRunner(FailingTestUsingExpectedParameterExample.class);
    }

    @Test
    public void shouldRunStaticMethodsWithBeforeClassAnnotationsOnceBeforeTheFirstTest() throws Exception {
        Runner runner = new ParameterisedRunner(BeforeClassExample.class);
        RunNotifier runNotifier = mock(RunNotifier.class);

        runner.run(runNotifier);

        verify(runNotifier, never()).fireTestFailure(any());
        assertThat(BeforeClassExample.hasBeenSetupOnce()).isTrue();
    }

    @Test
    public void shouldRunStaticMethodsWithAfterClassAnnotationsOnceAfterTheLastTest() throws Exception {
        Runner runner = new ParameterisedRunner(AfterClassExample.class);
        RunNotifier runNotifier = mock(RunNotifier.class);

        runner.run(runNotifier);

        verify(runNotifier, never()).fireTestFailure(any());
        assertThat(AfterClassExample.hasBeenTornDownOnce()).isTrue();
    }

    @Test
    public void shouldRunAfterClassMethodsEvenIfPrecededByException() throws Exception {
        Runner runner = new ParameterisedRunner(FailingWithAfterClassExample.class);
        RunNotifier runNotifier = mock(RunNotifier.class);

        runner.run(runNotifier);

        verify(runNotifier).fireTestFailure(notNull());
        assertThat(FailingWithAfterClassExample.hasBeenTornDownOnce()).isTrue();
    }

    @Test
    public void shouldRunClassRulesOncePerClass() throws Exception {
        Runner runner = new ParameterisedRunner(ClassRuleFieldExample.class);
        RunNotifier runNotifier = mock(RunNotifier.class);

        runner.run(runNotifier);

        verify(runNotifier, never()).fireTestFailure(any());
        assertThat(ClassRuleFieldExample.rulesHaveBeenProcessedOnce()).isTrue();
    }

    @Test
    public void shouldFailOnConstructionIfClassRuleFieldIsNotPublic() throws Exception {
        expectedException.expect(InitializationError.class);
        expectedException.expect(anInitialisationErrorMessageContaining("public"));

        new ParameterisedRunner(InvalidClassRuleFieldVisibilityExample.class);
    }

    @Test
    public void shouldFailOnConstructionIfClassRuleFieldIsNotStatic() throws Exception {
        expectedException.expect(InitializationError.class);
        expectedException.expect(anInitialisationErrorMessageContaining("static"));

        new ParameterisedRunner(InvalidNonStaticClassRuleFieldExample.class);
    }

    @Test
    public void shouldFailOnConstructionIfClassRuleFieldIsNotTestRule() throws Exception {
        expectedException.expect(InitializationError.class);
        expectedException.expect(anInitialisationErrorMessageContaining("implement"));

        new ParameterisedRunner(InvalidClassRuleFieldTypeExample.class);
    }

    @Test
    public void shouldFailOnConstructionIfRuleFieldIsNotPublic() throws Exception {
        expectedException.expect(InitializationError.class);
        expectedException.expect(anInitialisationErrorMessageContaining("public"));

        new ParameterisedRunner(InvalidRuleFieldVisibilityExample.class);
    }

    @Test
    public void shouldFailOnConstructionIfRuleFieldIsStatic() throws Exception {
        expectedException.expect(InitializationError.class);
        expectedException.expect(anInitialisationErrorMessageContaining("static"));

        new ParameterisedRunner(InvalidStaticRuleFieldExample.class);
    }

    @Test
    public void shouldFailOnConstructionIfRuleFieldIsNotTestRule() throws Exception {
        expectedException.expect(InitializationError.class);
        expectedException.expect(anInitialisationErrorMessageContaining("implement"));

        new ParameterisedRunner(InvalidRuleFieldTypeExample.class);
    }

    @Test
    public void shouldEvaluateClassRuleMethodsOncePerClass() throws Exception {
        Runner runner = new ParameterisedRunner(ClassRuleMethodExample.class);
        RunNotifier runNotifier = mock(RunNotifier.class);

        runner.run(runNotifier);

        verify(runNotifier, never()).fireTestFailure(any());
        assertThat(ClassRuleMethodExample.rulesHaveBeenProcessedOnce()).isTrue();
    }

    @Test
    public void shouldFailOnConstructionIfClassRuleMethodIsNotPublic() throws Exception {
        expectedException.expect(InitializationError.class);
        expectedException.expect(anInitialisationErrorMessageContaining("public"));

        new ParameterisedRunner(InvalidClassRuleMethodVisibilityExample.class);
    }

    @Test
    public void shouldFailOnConstructionIfClassRuleMethodIsNotStatic() throws Exception {
        expectedException.expect(InitializationError.class);
        expectedException.expect(anInitialisationErrorMessageContaining("static"));

        new ParameterisedRunner(InvalidNonStaticClassRuleMethodExample.class);
    }

    @Test
    public void shouldFailOnConstructionIfClassRuleMethodReturnTypeIsNotTestRule() throws Exception {
        expectedException.expect(InitializationError.class);
        expectedException.expect(anInitialisationErrorMessageContaining("implement"));

        new ParameterisedRunner(InvalidClassRuleMethodReturnTypeExample.class);
    }

    @Test
    public void shouldFailOnConstructionIfRuleMethodIsNotPublic() throws Exception {
        expectedException.expect(InitializationError.class);
        expectedException.expect(anInitialisationErrorMessageContaining("public"));

        new ParameterisedRunner(InvalidRuleMethodVisibilityExample.class);
    }

    @Test
    public void shouldFailOnConstructionIfRuleMethodIsStatic() throws Exception {
        expectedException.expect(InitializationError.class);
        expectedException.expect(anInitialisationErrorMessageContaining("static"));

        new ParameterisedRunner(InvalidStaticRuleMethodExample.class);
    }

    @Test
    public void shouldFailOnConstructionIfRuleMethodReturnTypeIsNotTestRule() throws Exception {
        expectedException.expect(InitializationError.class);
        expectedException.expect(anInitialisationErrorMessageContaining("implement"));

        new ParameterisedRunner(InvalidRuleMethodTypeExample.class);
    }

    @Test
    public void shouldFailOnConstructionIfInnerClassAndNonStatic() throws Exception {
        expectedException.expect(InitializationError.class);
        expectedException.expect(anInitialisationErrorMessageContaining("inner class"));

        new ParameterisedRunner(InvalidNonStaticInnerClassExample.TestClass.class);
    }

    @Test
    public void shouldFailOnConstructionIfMoreThanOneConstructorIsDefined() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("one constructor");

        new ParameterisedRunner(InvalidMultipleConstructorsExample.class);
    }

    @Test
    public void shouldFailOnConstructionIfConstructorHasArguments() throws Exception {
        expectedException.expect(InitializationError.class);
        expectedException.expect(anInitialisationErrorMessageContaining("arguments"));

        new ParameterisedRunner(InvalidArgumentReceivingConstructorExample.class);
    }

    @Test
    public void shouldSkipIgnoredTests() throws Exception {
        Runner runner = new ParameterisedRunner(IgnoredTestMethodExample.class);
        RunNotifier runNotifier = mock(RunNotifier.class);

        runner.run(runNotifier);

        verify(runNotifier).fireTestIgnored(notNull());
    }
}
