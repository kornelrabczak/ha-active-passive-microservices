package com.thecookiezen.microservices.infrastructure.status;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
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

    private CuratorFramework client;
    private LeaderLatch leaderLatch;

    private final JsonInstanceSerializer<InstanceDetails> serializer = new JsonInstanceSerializer<>(InstanceDetails.class);

    @PostConstruct
    public void init() {
        client = CuratorFrameworkFactory.newClient(zookeeperConnection, new ExponentialBackoffRetry(1000, 3));
        client.start();

        ServiceInstance<InstanceDetails> serviceInstance;
        try {
            client.blockUntilConnected();

            serviceInstance = ServiceInstance.<InstanceDetails>builder()
                    .uriSpec(new UriSpec("{scheme}://{address}:{port}"))
                    .address("localhost")
                    .port(8080)
                    .name(nodeId)
                    .payload(new InstanceDetails(false))
                    .build();

            ServiceDiscoveryBuilder.builder(InstanceDetails.class)
                    .basePath("load-balancing-example")
                    .client(client)
                    .thisInstance(serviceInstance)
                    .serializer(serializer)
                    .build()
                    .start();

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
    }

    @Override
    public void notLeader() {
        log.info("Node : " + nodeId + " is not a leader");
    }

    public String currentLeaderId() throws Exception {
        return leaderLatch.getLeader().getId();
    }

    @PreDestroy
    public void close() {
        try {
            leaderLatch.close();
        } catch (IOException e) {
            log.error("Error when closing leaderLatch.", e);
        }
        client.close();
    }
}
