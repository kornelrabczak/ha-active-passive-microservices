package com.thecookiezen.microservices.bussiness.boundary;

import com.thecookiezen.microservices.infrastructure.replication.LambdaReplicator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/zxc")
@ApplicationScoped
public class KafkaResource {

    @Inject
    private LambdaReplicator lambdaReplicator;

    @GET
    public String working() {
        return "yes, its working";
    }

    @POST
    public void putToKafka(String dupa) {
        lambdaReplicator.replicate(dupa);
    }

}
