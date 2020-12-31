package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

/*
 * Tests are annotated with @RunWith with ParameterisedRunner as the value.
 * Test methods are annotated with @Test and may, or may not, have parameters.
 * Parameters in a parameterised test have a source annotation that is meta-annotated with @ValueSource, which has a
   ValueFactory implementation as its value.
 * ValueFactory instances return a list of test values.
 * The set of test cases that can be derived from the combination of each @ValueSource is generated by an instance of
   TestCaseFactory.
 * If different value factories within the same test return a different number of arguments, it is up to the
   TestCaseFactory instance to provide an alternative value according to its rules.
 */

public class SequentialTestCaseGeneratorTest {
    private SequentialTestCaseGenerator generator;

    @Before
    public void setUp() {
        generator = new SequentialTestCaseGenerator();
    }

    @Test
    public void createsATestCaseForEachValueWhenThereIsOnlyOneParameter() {
        List<Integer> values = asList(5, 6, 7);
        List<Supplier<List<?>>> suppliers = singletonList(() -> values);

        List<TestCase> testCases = generator.createTestCases(suppliers);

        assertThat(testCases)
                .extracting("parameters")
                .containsExactly(
                        singletonList(5),
                        singletonList(6),
                        singletonList(7));
    }

    @Test
    public void createsATestCaseSelectingSingleValuesFromEachParameter() {
        List<Integer> numbers = asList(1, 2, 3);
        List<String> strings = asList("a", "b", "c");
        List<Supplier<List<?>>> suppliers = asList(() -> numbers, () -> strings);

        List<TestCase> testCases = generator.createTestCases(suppliers);

        assertThat(testCases)
                .extracting("parameters")
                .containsExactly(
                        asList(1, "a"),
                        asList(2, "b"),
                        asList(3, "c"));
    }

    @Test
    public void fillsMissingParameterValuesWithNulls() {
        List<Integer> numbers = asList(8, 9);
        List<String> strings = asList("x", "y", "z");
        List<Boolean> booleans = asList(false, true, false);
        List<Supplier<List<?>>> suppliers = asList(() -> numbers, () -> strings, () -> booleans);

        List<TestCase> testCases = generator.createTestCases(suppliers);

        assertThat(testCases)
                .extracting("parameters")
                .containsExactly(
                        asList(8, "x", false),
                        asList(9, "y", true),
                        asList(null, "z", false));
    }
}