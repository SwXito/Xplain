package fr.uge.model;

import de.kherud.llama.ModelParameters;
import de.kherud.llama.InferenceParameters;
import de.kherud.llama.LlamaModel;
import de.kherud.llama.args.MiroStat;
import fr.uge.utilities.SimpleBoxer;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

@ApplicationScoped
public class LLMService {

  private final HashMap<String, LlamaModel> models = new HashMap<>();
  private final HashMap<String, ModelParameters> modelsParams = new HashMap<>();
  private final ExecutorService executor = Executors.newFixedThreadPool(10); // Pool de threads
  private final ReentrantLock lock = new ReentrantLock();
  private String modelPath = "mistral-7b-instruct-v0.2.Q2_K.gguf";

  private final String basePrompt = "You are Llama, an assistant that provides advice for Java programmers.\n"
    + "Llama is helpful and suggests only 5 things upgradable, with short sentences.\n\n";
  private InferenceParameters inferParams;

  public LLMService() {
    ModelDownloader.map.forEach((k, v) -> modelsParams.put(v.modelName(),
      new ModelParameters()
        .setModelFilePath("models/" + v.modelName())
        .setNGpuLayers(10)));

    // Chargement des modèles en arrière-plan
    executor.submit(() -> {
      lock.lock();
      try {
        modelsParams.forEach((k, v) -> models.put(k, new LlamaModel(v)));
      } finally {
        lock.unlock();
      }
    });
  }

  public void newRequest(String classPrompt, String compilerOutputPrompt) {
    Objects.requireNonNull(classPrompt);
    Objects.requireNonNull(compilerOutputPrompt);

    var prompt = basePrompt
      + "User: " + classPrompt + "\n\n"
      + "And this is the answer of the compiler: " + compilerOutputPrompt + "\n\n"
      + "Can you give me some advice to improve my class?\nLlama: ";

    // Configuration des paramètres d'inférence
    lock.lock();
    try {
      inferParams = new InferenceParameters(prompt)
        .setTemperature(0.5f)
        .setPenalizeNl(true)
        .setMiroStat(MiroStat.V2)
        .setStopStrings("User:");
    } finally {
      lock.unlock();
    }
  }

  public Multi<SimpleBoxer> generateCompleteResponse() {
    return Multi.createFrom().emitter(emitter -> executor.submit(() -> {
      lock.lock();
      LlamaModel model;
      InferenceParameters params;

      try {
        model = models.get(modelPath);
        params = inferParams;

        if (model == null || params == null) {
          emitter.emit(new SimpleBoxer("error", "Model not loaded or parameters are missing."));
          emitter.complete();
          return;
        }
      } finally {
        lock.unlock();
      }

      // Émission des tokens générés morceau par morceau
      emitter.emit(new SimpleBoxer("start", "")); // Début de la génération
      try {
        for (var output : model.generate(params)) {
          emitter.emit(new SimpleBoxer("token", output.toString())); // Envoi des morceaux
        }
      } catch (Exception e) {
        emitter.emit(new SimpleBoxer("error", e.getMessage()));
      }
      emitter.emit(new SimpleBoxer("end", "")); // Fin de la génération
      emitter.complete();

      // Nettoyage des paramètres d'inférence
      lock.lock();
      try {
        inferParams = null;
      } finally {
        lock.unlock();
      }
    }));
  }

  public void changeModel(String model) {
    Objects.requireNonNull(model);
    modelPath = model;
  }
}
