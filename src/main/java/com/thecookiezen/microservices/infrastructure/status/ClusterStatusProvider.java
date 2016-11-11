package com.thecookiezen.microservices.infrastructure.status;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

public class ClusterStatusProvider {

    @Produces
    @Singleton
    public ClusterStatus createInstance() throws Exception {
        String zookeeperConnection = findProperty("zookeeperConnection");
        String latchPath = findProperty("latchPath");
        String nodeId = findProperty("nodeId");
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(zookeeperConnection, new ExponentialBackoffRetry(1000, 3));
        ClusterStatus clusterStatus = new ClusterStatus(curatorFramework, latchPath, nodeId);
        clusterStatus.init();
        return clusterStatus;
    }

    private String findProperty(String propertyName) {
        String found = System.getProperty(propertyName);
        if (found == null) {
            throw new IllegalStateException("System property '" + propertyName + "' is not defined!");
        }
        return found;
    }

}
