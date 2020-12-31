package uk.me.michael_winter.junit.plugins.parameterised;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Comparator.naturalOrder;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.generate;

class SequentialTestCaseGenerator {
    List<TestCase> createTestCases(List<Supplier<List<?>>> parameterValueSuppliers) {
        List<List<?>> suppliedParameterValues = parameterValuesFrom(parameterValueSuppliers);
        int numberOfTestCases = maximumNumberOf(suppliedParameterValues);

        return assembleParameterListsFrom(suppliedParameterValues)
                .andThen(mapToTestCases())
                .apply(emptyParameterListCreatorFor(numberOfTestCases));
    }

    private List<List<?>> parameterValuesFrom(List<Supplier<List<?>>> suppliers) {
        return suppliers.stream()
                .map(Supplier::get)
                .collect(toList());
    }

    private int maximumNumberOf(List<List<?>> parameterValues) {
        return parameterValues.stream()
                .map(List::size)
                .max(naturalOrder())
                .orElse(0);
    }

    private Function<Supplier<List<List<Object>>>, List<List<Object>>> assembleParameterListsFrom(List<List<?>> parameterValues) {
        return emptyParameterListFactory -> parameterValues.stream()
                .collect(
                        emptyParameterListFactory,
                        this::appendValuesToParameterLists,
                        List::addAll);
    }

    private void appendValuesToParameterLists(List<List<Object>> parameterListsForEachTest, List<?> parameterValues) {
        Iterator<Object> valuesIterator = paddedWithNulls(parameterValues)
                .iterator();
        for (List<Object> parameterList : parameterListsForEachTest) {
            parameterList.add(valuesIterator.next());
        }
    }

    private Stream<Object> paddedWithNulls(List<?> parameterValues) {
        return concat(parameterValues.stream(), generate(() -> null));
    }

    private Function<List<List<Object>>, List<TestCase>> mapToTestCases() {
        return params -> params.stream()
                .map(TestCase::new)
                .collect(toList());
    }

    private Supplier<List<List<Object>>> emptyParameterListCreatorFor(int numberOfTestCases) {
        return () -> newEmptyParameterListsFor(numberOfTestCases);
    }

    private List<List<Object>> newEmptyParameterListsFor(int testCases) {
        List<List<Object>> list = new ArrayList<>();
        for (int i = 0; i < testCases; ++i) {
            list.add(new ArrayList<>());
        }
        return list;
    }
}
