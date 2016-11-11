package com.thecookiezen.microservices.bussiness.boundary;

import com.thecookiezen.microservices.infrastructure.status.ClusterStatus;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/test")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class TestResource {

    @Inject
    private ClusterStatus clusterStatus;

    @GET
    public boolean hasLeader() {
        return clusterStatus.hasLeadership();
    }
}
