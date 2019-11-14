package uk.me.michael_winter.junit.plugins.parameterised;

import org.junit.runner.Runner;

abstract class IgnorableRunner extends Runner {
    abstract boolean isIgnored();
}
