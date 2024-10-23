package fr.uge.xplain;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;

import java.io.IOException;
import java.util.Objects;

@Path("/api/endpoint")
public class TextController {

  @POST
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.APPLICATION_JSON)
  public Response receiveData(String data) throws IOException {
    //TO DO: ITS NOT WORKING FOR NOW, NEED TO FIX IT
    Objects.requireNonNull(data);
    var output = Compiler.compile(data);
    return Response.ok(output).build();
  }

}
