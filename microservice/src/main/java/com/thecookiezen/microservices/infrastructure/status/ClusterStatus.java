package com.thecookiezen.microservices.infrastructure.status;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

@Singleton
public class ClusterStatus implements LeaderLatchListener {

    private static final Logger log = Logger.getLogger(ClusterStatus.class);

    @Inject
    @SystemProperty("zookeeperConnection")
    private String zookeeperConnection;

    @Inject
    @SystemProperty("latchPath")
    private String latchPath;

    @Inject
    @SystemProperty("nodeId")
    private String nodeId;

    @Inject
    @SystemProperty("port")
    private String port;

    private CuratorFramework client;
    private LeaderLatch leaderLatch;
    private ServiceDiscovery<InstanceDetails> discovery;

    private final JsonInstanceSerializer<InstanceDetails> serializer = new JsonInstanceSerializer<>(InstanceDetails.class);

    private final InstanceDetails payload = new InstanceDetails(false);
    private ServiceInstance<InstanceDetails> serviceInstance;

    @PostConstruct
    public void init() {
        client = CuratorFrameworkFactory.newClient(zookeeperConnection, new ExponentialBackoffRetry(1000, 3));
        client.start();

        try {
            client.blockUntilConnected();

            serviceInstance = ServiceInstance.<InstanceDetails>builder()
                    .uriSpec(new UriSpec("{scheme}://{address}:{port}"))
                    .address("localhost")
                    .port(Integer.parseInt(port))
                    .name("bookService")
                    .payload(payload)
                    .build();

            discovery = ServiceDiscoveryBuilder.builder(InstanceDetails.class)
                    .basePath("service-discovery")
                    .client(client)
                    .thisInstance(serviceInstance)
                    .watchInstances(true)
                    .serializer(serializer)
                    .build();

            discovery.start();

            leaderLatch = new LeaderLatch(client, latchPath, nodeId);
            leaderLatch.addListener(this);
            leaderLatch.start();
        } catch (Exception e) {
            log.error("Error when starting leaderLatch", e);
        }
    }

    public boolean hasLeadership() {
        return leaderLatch.hasLeadership();
    }

    @Override
    public void isLeader() {
        log.info("Node : " + nodeId + " is a leader");
        payload.setLeader(true);
        try {
            discovery.updateService(serviceInstance);
        } catch (Exception e) {
            log.error("Error when updating service discovery", e);
        }
    }

    @Override
    public void notLeader() {
        log.info("Node : " + nodeId + " is not a leader");
        payload.setLeader(false);
        try {
            discovery.updateService(serviceInstance);
        } catch (Exception e) {
            log.error("Error when updating service discovery", e);
        }
    }

    @PreDestroy
    public void close() {
        try {
            leaderLatch.close();
            discovery.close();
        } catch (IOException e) {
            log.error("Error when closing leaderLatch.", e);
        }
        client.close();
    }
}
