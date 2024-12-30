package fr.uge.model;

import de.kherud.llama.ModelParameters;
import de.kherud.llama.InferenceParameters;
import de.kherud.llama.LlamaModel;
import de.kherud.llama.args.MiroStat;
import fr.uge.database.DBService;
import jakarta.inject.Inject;
import fr.uge.utilities.SimpleBoxer;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@ApplicationScoped
public class LLMService {
  private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
  private final Lock readLock = rwl.readLock();
  private final Lock writeLock = rwl.writeLock();
  private String modelPath = ModelDownloader.map.get("light").modelName(); // critique
  private final HashMap<String, ModelParameters> modelsParams = new HashMap<>();
    private final ConcurrentHashMap<String, LlamaModel> models = new ConcurrentHashMap<>(); // critique
  private final ArrayBlockingQueue<RequestData> queue = new ArrayBlockingQueue<>(10);
    private final ExecutorService executor = Executors.newFixedThreadPool(10); // Pool de threads
  /***
   * This record is used to manage the request data
   * @param id the id of the in the database
   * @param parameters paramaters required to generate the response
   * @param model name of the model currently used
   */
  private record RequestData(long id, InferenceParameters parameters, String model){}
  @Inject
  DBService dbService;

  public LLMService() {
    ModelDownloader.map.forEach((k, v) -> modelsParams.put(v.modelName(),
      new ModelParameters()
        .setModelFilePath("models/" + v.modelName())
              .setNGpuLayers(0)
              .setNSequences(5) // genere jusqu'à 5 requete simultané
              .setNParallel(5)));
      executor.submit(() -> {
          writeLock.lock();
          try {
              //ModelDownloader.map.forEach((k, v) -> { models.put(v.modelName(), /*new LlamaModel(modelsParams.get(v.modelName()))*/);});
        models.put(modelPath, new LlamaModel(modelsParams.get(modelPath)));
      } finally {
          writeLock.unlock();
      }

    });
  }

  public void newRequest(String classPrompt, String compilerOutputPrompt, long id) {
    Objects.requireNonNull(classPrompt);
    Objects.requireNonNull(compilerOutputPrompt);
    var prompt = getPrompt(classPrompt, compilerOutputPrompt);
    readLock.lock(); // pour le modelPath
    queue.offer(new RequestData(id,new InferenceParameters(prompt)
      .setTemperature(0.5f)
      .setPenalizeNl(true)
      .setMiroStat(MiroStat.V2)
      .setStopStrings("User:"), modelPath));
    readLock.unlock();
  }

  private static @NotNull String getPrompt(String classPrompt, String compilerOutputPrompt) {
    String basePrompt = """
            You are Llama an assistant that provide advice for java programmer
            Llama is helpful, and suggest only 5 things upgradable, with short sentences
            Once the User submit a java class with a compiler message.
            """;
    var prompt = basePrompt + "\nUser: " + classPrompt + "\n\n" + "And this is the answer of the compiler :" +
            compilerOutputPrompt + "\n\nCan you give me some advices to improve my class ?";
    prompt += "\nLama: ";
    return prompt;
  }

  public Multi<SimpleBoxer> generateCompleteResponse() {
    return Multi.createFrom().emitter(emitter -> executor.submit(() -> {
        readLock.lock(); // pour le model
      try {
        var data = queue.poll();
        if (data == null || models.get(data.model) == null) {
          emitter.complete(); // rien a envoyer
        } else {
          var model = models.get(data.model);
          emitter.emit(new SimpleBoxer("start", "")); // debut de la generation de la reponse
          var builder = new StringBuilder();

          for (var output : model.generate(data.parameters)) {
            emitter.emit(new SimpleBoxer("token", output.toString()));
            builder.append(output.toString());
          }
          dbService.updateLlmResponse(data.id, builder.toString()); // ajout de la reponse complete dans la bdd
          emitter.emit(new SimpleBoxer("end", "")); // fin de la genereation
          emitter.complete();
        }
      } finally {
        readLock.unlock();
      }
    }));
  }

  public void changeModel(String model) {
//    Objects.requireNonNull(model);
//    modelPath = ModelDownloader.map.get(model).modelName();
//    return Response.ok(model).build();
      writeLock.lock(); // pour le model
      try {
          var newModel = ModelDownloader.map.get(model).modelName();
          System.out.println(model);
          models.get(modelPath).close(); // ferme le vieux model
          models.put(model, new LlamaModel(modelsParams.get(newModel))); // lance le nouveau model
          modelPath = model; // nouveau model path
      } finally {
          writeLock.unlock();
      }
  }
}
