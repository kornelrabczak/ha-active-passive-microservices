package com.thecookiezen.microservices.infrastructure.status;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.log4j.Logger;

import java.io.IOException;

public class ClusterStatus implements LeaderLatchListener {

    private static final Logger log = Logger.getLogger(ClusterStatus.class);

    private CuratorFramework client;
    private String latchPath;
    private String id;
    private LeaderLatch leaderLatch;

    public ClusterStatus(CuratorFramework client, String latchPath, String nodeId) {
        this.client = client;
        this.latchPath = latchPath;
        this.id = nodeId;

        log.info(latchPath);
        log.info(id);
    }

    public void init() throws Exception {
        client.start();
        client.blockUntilConnected();

        leaderLatch = new LeaderLatch(client, latchPath, id);
        leaderLatch.addListener(this);
        leaderLatch.start();
    }

    public boolean hasLeadership() {
        return leaderLatch.hasLeadership();
    }

    @Override
    public void isLeader() {
        log.info("Node : " + id + " is a leader");
    }

    @Override
    public void notLeader() {
        log.info("Node : " + id + " is not a leader");
    }

    public String currentLeaderId() throws Exception {
        return leaderLatch.getLeader().getId();
    }

    public void close() throws IOException {
        leaderLatch.close();
        client.close();
    }
}
