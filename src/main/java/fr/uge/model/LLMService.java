package fr.uge.model;

import de.kherud.llama.ModelParameters;

import de.kherud.llama.InferenceParameters;
import de.kherud.llama.LlamaModel;
import de.kherud.llama.args.MiroStat;
import fr.uge.database.DBService;
import fr.uge.database.XplainTable;
import fr.uge.utilities.SimpleBoxer;
import io.smallrye.mutiny.Multi;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestStreamElementType;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

@Path("/api/generate")
public class LLMService {
  private final ReentrantLock lock = new ReentrantLock();
  private String modelPath = "mistral-7b-instruct-v0.2.Q5_K_S.gguf";
  private final HashMap<String, ModelParameters> modelsParams = new HashMap<>();
  private final String prompt = "You are Llama an assistant that provide advice for java programmer\n" +
    "Llama is helpful, and suggest only 5 things upgradable, with short sentences" +
    "Once the User submit a java class with a compiler message.\n\n";
  //private InferenceParameters inferParams = null;
  private final ConcurrentHashMap<String, LlamaModel> models = new ConcurrentHashMap<>();
  private final LinkedBlockingQueue<RequestData> queue = new LinkedBlockingQueue<>();
  //private long current;

  /***
   * This record is used to manage the request data
   * @param id the id of the in the database
   * @param parameters paramaters required to generate the response
   */
  private record RequestData(long id, InferenceParameters parameters, String model){}
  @Inject
  DBService dbService;

  public LLMService() {
    ModelDownloader.map.forEach((k, v) -> modelsParams.put(v.modelName(),
      new ModelParameters()
        .setModelFilePath("models/" + v.modelName())
        .setNGpuLayers(10)
              .setNSequences(5)
              .setNParallel(5)));
    Thread.ofPlatform().start(() -> {
      lock.lock();
        //ModelDownloader.map.forEach((k, v) -> { models.put(v.modelName(), null /*new LlamaModel(modelsParams.get(v.modelName()))*/);});
        models.put("mistral-7b-instruct-v0.2.Q5_K_S.gguf", new LlamaModel(modelsParams.get("mistral-7b-instruct-v0.2.Q5_K_S.gguf")));
      lock.unlock();
    });
  }


  public void newRequest(String classPrompt, String compilerOutputPrompt, long id) {
    Objects.requireNonNull(classPrompt);
    Objects.requireNonNull(compilerOutputPrompt);
    var prompt = this.prompt + "\nUser: " + classPrompt + "\n\n" + "And this is the answer of the compiler :" +
      compilerOutputPrompt + "\n\nCan you give me some advices to improve my class ?";
    prompt += "\nLama: ";
      queue.offer(new RequestData(id,new InferenceParameters(prompt)
        .setTemperature(0.5f)
        .setPenalizeNl(true)
        .setMiroStat(MiroStat.V2)
        .setStopStrings("User:"), modelPath));
  }

  @Path("response")
  @GET
  @RestStreamElementType(MediaType.APPLICATION_JSON)
  public Multi<SimpleBoxer> generateCompleteResponse() {
    return Multi.createFrom().emitter(emitter -> {
      Thread.ofPlatform().start(() -> {
          var data = queue.poll();
          if (data == null || models.get(data.model) == null) {
          emitter.complete(); // nien a envoyer
        } else {
          var model = models.get(data.model);
          emitter.emit(new SimpleBoxer("start", "")); // debut de la generation de reponse
          var builder = new StringBuilder();

          for (var output : model.generate(data.parameters)) {
            emitter.emit(new SimpleBoxer("token", output.toString()));
            builder.append(output.toString());
          }
          dbService.updateLlmResponse(data.id, builder.toString());
          emitter.emit(new SimpleBoxer("end", "")); // fin de la genereation
          emitter.complete();
          //inferParams = null; // nettoie les parametres
          //current = null;  // nettoie la reference de la bdd
        }
      });
    });
  }

  @POST
  @Path("/model")
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.APPLICATION_JSON)
  public Response receiveModel(String model) {
    Objects.requireNonNull(model);
    lock.lock();
    try {
      System.out.println(model);
      //models.get(modelPath).close(); // ferme le vieux model
      models.put(model, new LlamaModel(modelsParams.get(model))); // lance le nouveau model
      modelPath = model; // nouveau model path
      return Response.ok(model).build();
    } finally {
      lock.unlock();
    }
  }

}
