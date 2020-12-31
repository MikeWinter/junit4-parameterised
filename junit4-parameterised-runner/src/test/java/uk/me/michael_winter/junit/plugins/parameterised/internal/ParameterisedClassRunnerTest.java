package uk.me.michael_winter.junit.plugins.parameterised.internal;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runners.model.InitializationError;
import uk.me.michael_winter.junit.plugins.parameterised.ParameterisedRunner;
import uk.me.michael_winter.junit.plugins.parameterised.TestExample;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore
public class ParameterisedClassRunnerTest {
    @Test
    public void shouldDescribeASuiteOfTests() throws Exception {
        Runner runner = new ParameterisedClassRunner(TestExample.class);

        Description description = runner.getDescription();

        assertThat(description.isSuite()).isTrue();
    }

    @Test
    public void shouldUseFullyQualifiedNameOfClassUnderTestWhenDescribingClassName() throws Exception {
        Runner runner = new ParameterisedClassRunner(TestExample.class);

        Description description = runner.getDescription();

        assertThat(description.getClassName()).isEqualTo(TestExample.class.getTypeName());
    }

    @Test
    public void shouldUseFullyQualifiedNameOfClassUnderTestWhenDescribingDisplayName() throws Exception {
        Runner runner = new ParameterisedClassRunner(TestExample.class);

        Description description = runner.getDescription();

        assertThat(description.getDisplayName()).isEqualTo(TestExample.class.getTypeName());
    }

    @Test
    public void shouldDescribeTestMethodsAsTests() throws Exception {
        Runner runner = new ParameterisedClassRunner(TestExample.class);

        List<Description> methodDescriptions = runner.getDescription().getChildren();

        assertThat(methodDescriptions).allMatch(Description::isTest);
    }

    @Test
    public void shouldNotDescribeNonTestMethods() throws Exception {
        Runner runner = new ParameterisedClassRunner(TestExample.class);

        List<Description> methodDescriptions = runner.getDescription().getChildren();

        assertThat(methodDescriptions).hasSize(2);
    }

    @Test
    public void shouldNotDescribeNonParameterisedTestMethods() throws Exception {
        Runner runner = new ParameterisedClassRunner(TestExample.class);

        List<Description> methodDescriptions = runner.getDescription().getChildren();

        assertThat(methodDescriptions).hasSize(0);
    }

    @Ignore
    @Test
    public void shouldDescribeNonParameterisedTestsWithoutNesting() throws InitializationError {
        ParameterisedRunner runner = new ParameterisedRunner(NonParameterisedTestClassExample.class);

        Description description = runner.getDescription();

        assertThat(description.isTest()).isFalse();
        assertThat(description.isSuite()).isTrue();
        assertThat(description.getMethodName()).isNull();
        assertThat(description.getTestClass()).isEqualTo(NonParameterisedTestClassExample.class);
        assertThat(description.getAnnotations()).isEmpty();
        assertThat(description)
                .extracting("className", "displayName")
                .containsOnly(NonParameterisedTestClassExample.class.getTypeName());
        assertThat(description.getChildren())
                .hasSize(1)
                .allSatisfy(child -> {
                    assertThat(child.isTest()).isFalse();
                    assertThat(child.isSuite()).isTrue();
                    assertThat(child.getChildren())
                            .hasSize(2)
                            .extracting("methodName")
                            .containsExactly("shouldTestSomething", "shouldTestSomethingElse");
                });
    }

    @Ignore
    @Test
    public void shouldDescribeParameterisedTestsWithNesting() throws InitializationError {
        ParameterisedRunner runner = new ParameterisedRunner(ParameterisedTestClassExample.class);

        Description description = runner.getDescription();

        assertThat(description.isTest()).isFalse();
        assertThat(description.isSuite()).isTrue();
        assertThat(description.getMethodName()).isNull();
        assertThat(description.getTestClass()).isEqualTo(ParameterisedTestClassExample.class);
        assertThat(description.getAnnotations()).isEmpty();
        assertThat(description)
                .extracting("className", "displayName")
                .containsOnly(ParameterisedTestClassExample.class.getTypeName());
        assertThat(description.getChildren())
                .hasSize(2)
                .allSatisfy(child -> assertThat(child.getChildren()).isNotEmpty());
    }

    @Ignore("Used only for obtaining test methods by reflection")
    public static class NonParameterisedTestClassExample {
        @Test
        public void shouldTestSomething() {
        }

        @Test
        public void shouldTestSomethingElse() {
        }
    }

    @Ignore("Used only for obtaining test methods by reflection")
    public static class ParameterisedTestClassExample {
        @Test
        public void shouldTestSomething() {
        }

        @Test
        public void shouldTestSomethingElse() {
        }
    }
}

/*
jUnit4ClassRunnerDescription = {Description@836} "uk.me.michael_winter.junit.plugins.parameterised.ParameterisedRunnerTest$NonParameterisedTestClass"
    fChildren = {ConcurrentLinkedQueue@841}  size = 1
    0 = {Description@845} "shouldTestSomething(uk.me.michael_winter.junit.plugins.parameterised.ParameterisedRunnerTest$NonParameterisedTestClass)"
        fChildren = {ConcurrentLinkedQueue@847}  size = 0
        fDisplayName = "shouldTestSomething(uk.me.michael_winter.junit.plugins.parameterised.ParameterisedRunnerTest$NonParameterisedTestClass)"
        fUniqueId = "shouldTestSomething(uk.me.michael_winter.junit.plugins.parameterised.ParameterisedRunnerTest$NonParameterisedTestClass)"
        fAnnotations = {Annotation[1]@848}
        fTestClass = {Class@822} "class uk.me.michael_winter.junit.plugins.parameterised.ParameterisedRunnerTest$NonParameterisedTestClass"
    fDisplayName = "uk.me.michael_winter.junit.plugins.parameterised.ParameterisedRunnerTest$NonParameterisedTestClass"
    fUniqueId = "uk.me.michael_winter.junit.plugins.parameterised.ParameterisedRunnerTest$NonParameterisedTestClass"
    fAnnotations = {Annotation[0]@842}
    fTestClass = null
*/
