package com.thecookiezen.microservices.infrastructure.status;

public interface ClusterSingleton {

    boolean hasLeadership();

}
