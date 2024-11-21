package fr.uge.xplain;

import fr.uge.database.DBService;
import fr.uge.model.LLMService;
import fr.uge.model.ModelDownloader;
import fr.uge.utilities.Boxer;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.IntStream;

@Path("/api")
public class Controller {

  @Inject
  DBService dbService;

  @POST
  @Path("/endpoint")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response receiveData(Boxer data) throws IOException {
    Objects.requireNonNull(data);
    var llmService = new LLMService();
    var classText = data.content();
    var compilerResponse = Compiler.compile(classText);
    var llmResponse = llmService.llmResponse(classText, compilerResponse);
    dbService.createXplainTable(compilerResponse, llmResponse, classText);
    return Response.ok(new Boxer("receiveData", compilerResponse)).build();
  }

  @POST
  @Path("/model")
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.APPLICATION_JSON)
  public Response receiveModel(String model) {
    Objects.requireNonNull(model);
    System.out.println(dbService.getAllResponses());
    //ModelDownloader.download(model);
    return Response.ok(model).build();
  }
}