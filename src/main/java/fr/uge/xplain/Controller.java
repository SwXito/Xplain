package fr.uge.xplain;

import fr.uge.model.LLMService;
import fr.uge.model.ModelDownloader;
import fr.uge.utilities.Boxer;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;

import java.io.IOException;
import java.util.Objects;

@Path("/api")
public class Controller {

  @POST
  @Path("/endpoint")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response receiveData(Boxer data) throws IOException {
    Objects.requireNonNull(data);
    var output = Compiler.compile(data.content());
    var llmService = new LLMService();
    output += llmService.llmResponse(data.content(), output);
    return Response.ok(new Boxer("receiveData", output)).build();
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