package com.flaregames.imports.integration;

import com.google.inject.Guice;
import com.google.inject.Injector;

import com.flaregames.imports.config.ConfigLogger;

import org.junit.Before;

public class AbstractIntegrationTest {
    protected Injector injector = Guice.createInjector(new IntegrationTestModule());

    @Before
    public void setup() {
        injector.getInstance(ConfigLogger.class).logConfig();
        injector.injectMembers(this);
    }
}
