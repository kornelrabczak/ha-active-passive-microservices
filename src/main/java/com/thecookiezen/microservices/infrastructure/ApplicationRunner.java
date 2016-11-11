package com.thecookiezen.microservices.infrastructure;

import io.undertow.Undertow;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;

public class ApplicationRunner {

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(args[0]);

        Undertow.Builder serverBuilder = Undertow.builder().addHttpListener(port, "0.0.0.0");
        UndertowJaxrsServer undertowJaxrsServer = new UndertowJaxrsServer();
        undertowJaxrsServer.start(serverBuilder);

        ResteasyDeployment deployment = new ResteasyDeployment();
        deployment.setInjectorFactoryClass("org.jboss.resteasy.cdi.CdiInjectorFactory");
        deployment.setApplicationClass(JAXRSConfiguration.class.getName());

        DeploymentInfo di = undertowJaxrsServer.undertowDeployment(deployment)
                .setClassLoader(ApplicationRunner.class.getClassLoader())
                .setContextPath("/microservice")
                .setDeploymentName("Undertow microservice")
                .addListeners(Servlets.listener(org.jboss.weld.environment.servlet.Listener.class));

        undertowJaxrsServer.deploy(di);

        Runtime.getRuntime().addShutdownHook(new Thread(undertowJaxrsServer::stop));
    }

}
