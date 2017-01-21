package com.thecookiezen.microservices.bussiness.boundary;

import com.thecookiezen.microservices.infrastructure.status.ClusterStatus;
import com.thecookiezen.microservices.infrastructure.status.SystemProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

    @Inject
    @SystemProperty("nodeId")
    private String nodeId;

    @GET
    @Path("/hello")
    public String hello() {
        return nodeId;
    }

    @DELETE
    @Path("/hello")
    public String hello_delete() {
        return nodeId;
    }

    @POST
    @Path("/hello")
    public String hello_post() {
        return nodeId;
    }

    @GET
    public boolean hasLeader() {
        return clusterStatus.hasLeadership();
    }
}
