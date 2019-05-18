package uk.me.michael_winter.junit.plugins.parameterised.runners;

import org.junit.runner.Runner;

public abstract class IgnorableRunner extends Runner {
    public abstract boolean isIgnored();
}
