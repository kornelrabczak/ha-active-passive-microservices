package com.thecookiezen.microservices.infrastructure.status;

import org.codehaus.jackson.map.annotate.JsonRootName;

@JsonRootName("details")
public class InstanceDetails {
    private boolean isLeader = false;

    public InstanceDetails() {
        this(false);
    }

    public InstanceDetails(boolean isLeader) {
        this.isLeader = isLeader;
    }

    public boolean isLeader() {
        return isLeader;
    }

    public void setLeader(boolean leader) {
        isLeader = leader;
    }
}
