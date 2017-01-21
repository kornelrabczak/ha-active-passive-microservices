package com.thecookiezen.payara;

import fish.payara.micro.BootstrapException;
import fish.payara.micro.PayaraMicro;
import fish.payara.micro.PayaraMicroRuntime;

public class ApplicationRunner {

    public static void main(String args[]) throws BootstrapException {
        PayaraMicroRuntime runtime = PayaraMicro.getInstance()
                .setHttpAutoBind(true)
                .bootstrap();

        runtime.deploy("microservice","/microservice", ApplicationRunner.class.getClassLoader().getResourceAsStream("war/microservice-1.0-SNAPSHOT.war"));
    }

}