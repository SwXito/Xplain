package fr.uge.xplain;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;

import java.util.Objects;

@Path("/api")
public class Controller {

  @POST
  @Path("/endpoint")
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.APPLICATION_JSON)
  public Response receiveData(String data) {
    Objects.requireNonNull(data);
    return Response.ok(data).build();
  }

  @POST
  @Path("/model")
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.APPLICATION_JSON)
  public Response receiveModel(String model) {
    Objects.requireNonNull(model);
    System.out.println("Downloading " + model + "...");
    return Response.ok(model).build();
  }
}
