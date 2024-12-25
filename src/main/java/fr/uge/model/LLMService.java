package fr.uge.model;

import de.kherud.llama.ModelParameters;

import de.kherud.llama.InferenceParameters;
import de.kherud.llama.LlamaModel;
import de.kherud.llama.args.MiroStat;
import fr.uge.utilities.SimpleBoxer;
import io.smallrye.mutiny.Multi;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestStreamElementType;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

@Path("/api/generate")
public class LLMService {
  private final ReentrantLock lock = new ReentrantLock();
  private String modelPath = "mistral-7b-instruct-v0.2.Q2_K.gguf";
  private final HashMap<String, ModelParameters> modelsParams = new HashMap<>();
  private String prompt = "You are Llama an assistant that provide advice for java programmer\n" +
    "Llama is helpful, and suggest only 5 things upgradable, with short sentences" +
    "Once the User submit a java class with a compiler message.\n\n";
  private InferenceParameters inferParams = null;
  private final HashMap<String, LlamaModel> models = new HashMap<>();


  public LLMService() {
    ModelDownloader.map.forEach((k, v) -> modelsParams.put(v.modelName(),
      new ModelParameters()
        .setModelFilePath("models/" + v.modelName())
        .setNGpuLayers(10)));
    Thread.ofPlatform().start(() -> {
      lock.lock();
      modelsParams.forEach((k, v) -> models.put(k, new LlamaModel(v)));
      lock.unlock();
    });
  }


  public void newRequest(String classPrompt, String compilerOutputPrompt) {
    Objects.requireNonNull(classPrompt);
    Objects.requireNonNull(compilerOutputPrompt);
    this.prompt += "\nUser: " + classPrompt + "\n\n" + "And this is the answer of the compiler :" +
      compilerOutputPrompt + "\n\nCan you give me some advices to improve my class ?";
    this.prompt += "\nLama: ";
    lock.lock();
    try {
      this.inferParams = new InferenceParameters(this.prompt)
        .setTemperature(0.5f)
        .setPenalizeNl(true)
        .setMiroStat(MiroStat.V2)
        .setStopStrings("User:");
    } finally {
      lock.unlock();
    }
  }

  @Path("response")
  @GET
  @RestStreamElementType(MediaType.APPLICATION_JSON)
  public Multi<SimpleBoxer> generateCompleteResponse() {
    return Multi.createFrom().emitter(emitter -> {
      Thread.ofPlatform().start(() -> {
        lock.lock();
        try {
          var model = models.get(modelPath);
          if (model == null || inferParams == null) {
            emitter.complete(); // nien a envoyer

          } else {
            emitter.emit(new SimpleBoxer("start", "")); // debut de la generation de reponse
            for (var output : model.generate(inferParams)) {
              emitter.emit(new SimpleBoxer("token", output.toString()));
            }
            emitter.emit(new SimpleBoxer("end", "")); // fin de la genereation
            emitter.complete();
            inferParams = null; // nettoie les parametres
          }
        } finally {
          lock.unlock();
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
    modelPath = model;
    return Response.ok(model).build();
  }

}
