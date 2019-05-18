package uk.me.michael_winter.junit.plugins.parameterised;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.runners.model.InitializationError;

public class InitializationErrorMatcher extends TypeSafeDiagnosingMatcher<InitializationError> {
    private final String causeSubstring;

    private InitializationErrorMatcher(String causeSubstring) {
        this.causeSubstring = causeSubstring;
    }

    public static InitializationErrorMatcher anInitialisationErrorMessageContaining(String substring) {
        return new InitializationErrorMatcher(substring);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("an initialisation error message containing ")
                .appendValue(causeSubstring);
    }

    @Override
    protected boolean matchesSafely(InitializationError error, Description mismatchDescription) {
        boolean matchFound = error.getCauses()
                .stream()
                .anyMatch(this::containsSubstring);

        if (!matchFound) {
            mismatchDescription.appendText("was not found.");
        }
        return matchFound;
    }

    private boolean containsSubstring(Throwable c) {
        return c.getMessage().contains(causeSubstring);
    }
}
