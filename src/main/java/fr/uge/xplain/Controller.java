package fr.uge.xplain;

import fr.uge.database.DBService;
import fr.uge.model.LLMService;
import fr.uge.utilities.SimpleBoxer;
import fr.uge.utilities.ResponseBoxer;
import io.smallrye.mutiny.Multi;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Path("/api")
public final class Controller {

  @Inject
  DBService dbService;

  @Inject
  LLMService llmService;

  @POST
  @Path("/endpoint")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response receiveData(SimpleBoxer data) throws IOException {
    Objects.requireNonNull(data);
    var classText = data.content();
    var compilerResponse = Compiler.compile(classText);

    var success = !compilerResponse.contains("failed");
    // Retourner une réponse indiquant que la génération va commencer
    var responseBoxer = new ResponseBoxer("generationStarted", classText, compilerResponse, "", success);
    // Écriture en base de données asynchrone
    CompletableFuture.runAsync(() -> {
      var id = dbService.createXplainTable(compilerResponse, "", classText, success);
      llmService.newRequest(classText, compilerResponse, id);
    });
    return Response.ok(responseBoxer).build();
  }

  @GET
  @Path("/response")
  @Produces(MediaType.SERVER_SENT_EVENTS)
  @Consumes(MediaType.APPLICATION_JSON)
  public Multi<SimpleBoxer> streamResponse() {
    // Appelle la méthode qui génère une réponse morceau par morceau
    return llmService.generateCompleteResponse();
  }

  @POST
  @Path("/history")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getHistory() {
    return Response.ok(dbService.getAllResponses()).build();
  }

  @POST
  @Path("/model")
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.APPLICATION_JSON)
  public Response receiveModel(String model) {
    Objects.requireNonNull(model);
    llmService.changeModel(model);
    return Response.ok(model).build();
  }
}
