package fr.uge.xplain;

import fr.uge.model.Boxer;
import fr.uge.model.LLMService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;

import java.io.IOException;
import java.util.Objects;


@Path("/api/endpoint")
public class TextController {

  @POST
  @Path("/endpoint")
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.APPLICATION_JSON)
  public Response receiveData(String data) throws IOException {
    Objects.requireNonNull(data);
    return Response.ok(new Boxer("receiveData", "ok")).build();
  }



}
