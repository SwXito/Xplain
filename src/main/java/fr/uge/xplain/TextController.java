package fr.uge.xplain;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;
import java.util.Objects;

@Path("/api/endpoint")
public class TextController {

  @POST
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.APPLICATION_JSON)
  public Response receiveData(String data) {
    Objects.requireNonNull(data);
    return Response.ok(data).build();
  }

}
