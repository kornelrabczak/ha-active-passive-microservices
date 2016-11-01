package com.thecookiezen.microservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/test")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TestResource {

    @GET
    public Book isWorking() {
        Book book = new Book();
        book.setAuthor("blee");
        book.setIsbn("qew");
        book.setTitle("qweert");
        book.setId(12345L);
        return book;
    }

}
