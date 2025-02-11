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
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * REST API Controller that handles requests related to the processing and generation of suggestions
 * for Java code. It also manages model configurations and retrieves history data.
 */
@Path("/api")
public final class Controller {

  @Inject
  DBService dbService; // Service for database interactions

  @Inject
  LLMService llmService; // Service for interacting with the language model (LLM)

  /**
   * Endpoint to receive Java code and its compilation result, which starts the suggestion generation process.
   * It processes the data and stores the request in the database.
   *
   * @param data The Java code and relevant information provided by the user.
   * @return A response with the status of the suggestion generation process.
   * @throws IOException If an error occurs during the compilation of the Java code.
   */
  @POST
  @Path("/endpoint")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(
    summary = "Process Java code and start generating suggestions",
    description = "Accepts Java code and its compilation result, initiates the suggestion generation process, and stores the request in the database."
  )
  @APIResponse(
    responseCode = "200",
    description = "Generation process started successfully",
    content = @Content(schema = @Schema(implementation = ResponseBoxer.class))
  )
  public Response receiveData(
    @Parameter(description = "The Java code provided by the user", required = true) SimpleBoxer data
  ) throws IOException {
    Objects.requireNonNull(data);
    var classText = data.content();

    // Compile the Java code and get the response
    var compilerResponse = Compiler.compile(classText);

    // Determine whether the compilation was successful
    var success = !compilerResponse.contains("failed");
    var responseBoxer = new ResponseBoxer("generationStarted", classText, compilerResponse, "", success);

    // Asynchronous database operation for creating a new entry and starting the suggestion process
    CompletableFuture.runAsync(() -> {
      var id = dbService.createXplainTable(compilerResponse, "", classText, success);
      llmService.newRequest(classText, compilerResponse, id);
    });

    // Return the response indicating the generation has started
    return Response.ok(responseBoxer).build();
  }

  /**
   * Endpoint to stream the generated suggestions from the LLM.
   * It returns a stream of suggestions as they are being processed.
   *
   * @return A stream of suggestions generated by the LLM.
   */
  @GET
  @Path("/response")
  @Produces(MediaType.SERVER_SENT_EVENTS)
  @Operation(
    summary = "Stream LLM response",
    description = "Streams the generated suggestions from the LLM as they are being processed."
  )
  @APIResponse(
    responseCode = "200",
    description = "Stream of suggestions started",
    content = @Content(schema = @Schema(implementation = SimpleBoxer.class))
  )
  public Multi<SimpleBoxer> streamResponse() {
    return llmService.generateCompleteResponse();
  }

  /**
   * Endpoint to fetch all previously processed requests and their suggestions stored in the database.
   *
   * @return A response containing the generation history from the database.
   */
  @POST
  @Path("/history")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(
    summary = "Fetch generation history",
    description = "Returns all previously processed requests along with their suggestions stored in the database."
  )
  @APIResponse(
    responseCode = "200",
    description = "History retrieved successfully",
    content = @Content(schema = @Schema(type = SchemaType.ARRAY, implementation = SimpleBoxer.class))
  )
  public Response getHistory() {
    return Response.ok(dbService.getAllResponses().reversed()).build();
  }

  /**
   * Endpoint to update the currently active LLM model used for generating suggestions.
   *
   * @param model The model name to set as active.
   * @return A response confirming the model change with the model name.
   */
  @POST
  @Path("/model")
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(
    summary = "Set active LLM model",
    description = "Updates the currently active LLM model used for generating suggestions."
  )
  @APIResponse(
    responseCode = "200",
    description = "Model successfully updated",
    content = @Content(schema = @Schema(implementation = SimpleBoxer.class))
  )
  public Response receiveModel(
    @Parameter(description = "The model name to set as active", required = true) String model
  ) {
    Objects.requireNonNull(model);
    llmService.changeModel(model);

    // Return a response with a SimpleBoxer object that contains the model name
    return Response.ok(new SimpleBoxer("model", model)).build();
  }
}
