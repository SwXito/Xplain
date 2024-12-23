package fr.uge.xplain;

import fr.uge.database.DBService;
import fr.uge.model.LLMService;
import fr.uge.model.ModelDownloader;
import fr.uge.utilities.HistoryDTO;
import fr.uge.utilities.SimpleBoxer;
import fr.uge.utilities.ResponseBoxer;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Path("/api")
public final class Controller {

  @Inject
  DBService dbService;

  @POST
  @Path("/endpoint")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response receiveData(SimpleBoxer data) throws IOException {
    Objects.requireNonNull(data);
    var llmService = new LLMService();
    var classText = data.content();
    var compilerResponse = Compiler.compile(classText);
    //llmService.llmResponse(classText, compilerResponse);
    var llmResponse = "";
    var success = !compilerResponse.contains("failed");
    dbService.createXplainTable(compilerResponse, llmResponse, classText, success);
    var responseBoxer = new ResponseBoxer("receiveData", classText, compilerResponse, llmResponse, success);
    return Response.ok(responseBoxer).build();
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

  @POST
  @Path("/history")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getHistory() {
    return Response.ok(dbService.getAllResponses()).build();
  }

}