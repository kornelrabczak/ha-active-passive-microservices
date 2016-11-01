package com.thecookiezen.microservices;

import javax.ws.rs.core.Application;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JAXRSConfiguration extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Stream.of(ObjectMapperResolver.class, TestResource.class)
                .collect(Collectors.toSet());
    }

}
