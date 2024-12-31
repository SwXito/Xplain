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
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * LLMService class manages requests for the Llama language model and handles the interaction with the database.
 * It manages models, queues requests, and provides a way to generate and handle responses.
 */
@ApplicationScoped
public class LLMService {

  // Locks for thread synchronization, allowing read and write operations on shared resources
  private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
  private final Lock readLock = rwl.readLock();
  private final Lock writeLock = rwl.writeLock();

  // Model downloader to download model files
  private final ModelDownloader modelDownloader = new ModelDownloader();

  // Map to hold information about available models
  private final Map<String, ModelInfo> modelsMap = modelDownloader.map();

  // Model path for the selected model (default is "light")
  private String modelPath = modelsMap.get("light").modelName();

  // HashMap to hold parameters for each model
  private final HashMap<String, ModelParameters> modelsParams = new HashMap<>();

  // ConcurrentHashMap to store models, allowing thread-safe access
  private final ConcurrentHashMap<String, LlamaModel> models = new ConcurrentHashMap<>();

  // Queue for managing incoming requests
  private final ArrayBlockingQueue<RequestData> queue = new ArrayBlockingQueue<>(10);

  // ExecutorService for handling requests with a pool of threads
  private final ExecutorService executor = Executors.newFixedThreadPool(10);

  /**
   * Record to manage request data.
   * It holds the id of the database record, inference parameters, and model name.
   *
   * @param id the id of the request in the database
   * @param parameters the parameters required to generate the response
   * @param model the name of the model currently used
   */
  private record RequestData(long id, InferenceParameters parameters, String model) {
  }

  // Injecting DBService for database interactions
  @Inject
  DBService dbService;

  /**
   * LLMService constructor. It initializes the service by downloading models
   * and setting up model parameters. It also starts a background thread to
   * initialize the models.
   */
  public LLMService() {
    modelDownloader.downloadAll(); // Download all models
    var modelsInfo = modelsMap.values(); // Get all model information

    // Setting model parameters for each model
    modelsInfo.forEach(v -> modelsParams.put(v.modelName(),
      new ModelParameters()
        .setModelFilePath("models/" + v.modelName())
        .setNGpuLayers(0)
        .setNSequences(5) // Allows up to 5 simultaneous requests
        .setNParallel(5)));

    // Initialize models in a background thread
    executor.submit(() -> {
      writeLock.lock();
      try {
        modelsInfo.forEach(v -> models.put(v.modelName(), new LlamaModel(modelsParams.get(v.modelName()))));
      } finally {
        writeLock.unlock();
      }
    });
  }

  /**
   * Creates a new request by adding it to the queue for processing.
   * This method constructs the prompt for the Llama model and prepares the request data.
   *
   * @param classPrompt the user's Java class prompt
   * @param compilerOutputPrompt the output from the compiler
   * @param id the id of the database record associated with this request
   */
  public void newRequest(String classPrompt, String compilerOutputPrompt, long id) {
    Objects.requireNonNull(classPrompt);
    Objects.requireNonNull(compilerOutputPrompt);

    // Build the prompt for the model
    var prompt = getPrompt(classPrompt, compilerOutputPrompt);

    // Acquire read lock for modelPath access
    readLock.lock();
    try {
      queue.offer(new RequestData(id, new InferenceParameters(prompt)
        .setTemperature(0.5f)
        .setPenalizeNl(true)
        .setMiroStat(MiroStat.V2)
        .setStopStrings("User:"), modelPath));
    } finally {
      readLock.unlock(); // Release the read lock
    }
  }

  /**
   * Builds the prompt that will be passed to the Llama model.
   *
   * @param classPrompt the user's Java class prompt
   * @param compilerOutputPrompt the output from the compiler
   * @return a string formatted as a prompt for the model
   */
  private static @NotNull String getPrompt(String classPrompt, String compilerOutputPrompt) {
    String basePrompt = """
      You are Llama, an assistant that provides advice for Java programmers.
      Llama is helpful and suggests only 5 things upgradable, with short sentences.
      Once the User submits a Java class with a compiler message.
      """;
    // Construct the final prompt
    return basePrompt + "\nUser: " + classPrompt + "\n\n" + "And this is the answer of the compiler :" +
      compilerOutputPrompt + "\n\nCan you give me some advice to improve my class?" + "\nLlama: ";
  }

  /**
   * Generates a complete response from the Llama model. This method processes
   * the requests in the queue, generates the response tokens, and updates the
   * response in the database.
   *
   * @return a Multi stream of SimpleBoxer objects representing the response
   */
  public Multi<SimpleBoxer> generateCompleteResponse() {
    return Multi.createFrom().emitter(emitter -> executor.submit(() -> {
      readLock.lock(); // Acquire the read lock to access the model
      try {
        var data = queue.poll(); // Poll a request from the queue
        if (data == null || models.get(data.model) == null) {
          emitter.complete(); // Nothing to process
        } else {
          var model = models.get(data.model); // Get the selected model
          emitter.emit(new SimpleBoxer("start", "")); // Start of response generation

          // Generate response tokens and emit each one
          var builder = new StringBuilder();
          for (var output : model.generate(data.parameters)) {
            emitter.emit(new SimpleBoxer("token", output.toString()));
            builder.append(output);
          }

          // Update the complete response in the database
          dbService.updateLlmResponse(data.id, builder.toString());
          emitter.emit(new SimpleBoxer("end", "")); // End of response generation
          emitter.complete(); // Complete the response stream
        }
      } finally {
        readLock.unlock(); // Release the read lock
      }
    }));
  }

  /**
   * Changes the model being used for requests.
   *
   * @param model the name of the new model
   */
  public void changeModel(String model) {
    Objects.requireNonNull(model);
    modelPath = modelsMap.get(model).modelName(); // Update the model path
  }
}
