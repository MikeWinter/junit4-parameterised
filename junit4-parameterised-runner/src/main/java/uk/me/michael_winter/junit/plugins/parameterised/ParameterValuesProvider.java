package uk.me.michael_winter.junit.plugins.parameterised;

import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;

@Target(ANNOTATION_TYPE)
@interface ParameterValuesProvider {
}
