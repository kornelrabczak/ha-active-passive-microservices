package com.thecookiezen.microservices.infrastructure.status;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

public class SystemPropertiesProvider {

    @Produces
    @SystemProperty
    private String findProperty(InjectionPoint injectionPoint) {
        String name = injectionPoint.getAnnotated().getAnnotation(SystemProperty.class).value();
        String found = System.getProperty(name);
        if (found == null) {
            throw new IllegalStateException("System property '" + name + "' is not defined!");
        }
        return found;
    }

}
