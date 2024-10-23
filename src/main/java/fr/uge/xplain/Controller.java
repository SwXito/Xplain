package fr.uge.xplain;

import fr.uge.model.ModelDownloader;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;

import java.io.IOException;
import java.util.Objects;

@Path("/api")
public class Controller {

  @POST
  @Path("/endpoint")
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.APPLICATION_JSON)
  public Response receiveData(String data) throws IOException {
    Objects.requireNonNull(data);
    var output = Compiler.compile(data);
    return Response.ok(output).build();
  }

  @POST
  @Path("/model")
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.APPLICATION_JSON)
  public Response receiveModel(String model) {
    Objects.requireNonNull(model);
    ModelDownloader.download(model);
    return Response.ok(model).build();
  }
}