package fr.uge.model;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Objects;

/**
 * The ModelDownloader class is responsible for downloading model files from specified URLs
 * and storing them in a local directory. It supports downloading all models at once or a specific model.
 */
public final class ModelDownloader {

  // A map holding the model information, including the model name and its download URL.
  private final Map<String, ModelInfo> map = Map.of(
    "light", new ModelInfo("Mistral-7B-Instruct-v0.3.IQ2_XS.gguf",
      "https://huggingface.co/MaziyarPanahi/Mistral-7B-Instruct-v0.3-GGUF/resolve/main/Mistral-7B-Instruct-v0.3.IQ2_XS.gguf"),
    "medium", new ModelInfo("mistral-7b-instruct-v0.2.Q4_K_S.gguf",
      "https://huggingface.co/TheBloke/Mistral-7B-Instruct-v0.2-GGUF/resolve/main/mistral-7b-instruct-v0.2.Q4_K_S.gguf"),
    "heavy", new ModelInfo("Mistral-7B-Instruct-v0.3.Q6_K.gguf",
      "https://huggingface.co/MaziyarPanahi/Mistral-7B-Instruct-v0.3-GGUF/resolve/main/Mistral-7B-Instruct-v0.3.Q6_K.gguf")
  );

  /**
   * Downloads all models listed in the 'map'. If a model file does not exist in the local directory,
   * it will be downloaded from the corresponding URL.
   */
  public void downloadAll() {
    map.forEach((k, v) -> {
      try {
        // Calls helper method to download each model
        downloadModel(v.modelName(), v.url());
      } catch (IOException e) {
        System.out.println("Interruption while downloading " + v.modelName());
      } catch (URISyntaxException e) {
        throw new AssertionError(e); // Handle invalid URL
      }
    });
  }

  /**
   * Downloads a specific model based on the model type (e.g., "light", "medium", "heavy").
   *
   * @param modelType The type of the model to download.
   */
  public void download(String modelType) {
    Objects.requireNonNull(modelType, "Model type must not be null");
    try {
      // Retrieve model info based on model type
      var modelInfo = map.get(modelType);
      // Calls helper method to download the specific model
      downloadModel(modelInfo.modelName(), modelInfo.url());
    } catch (IOException e) {
      System.out.println("Interruption while downloading");
    } catch (URISyntaxException e) {
      throw new AssertionError(e); // Handle invalid URL
    }
  }

  /**
   * Downloads a model from the specified URL and saves it to the local file system.
   * If the model already exists locally, it will skip the download.
   *
   * @param modelName The name of the model file.
   * @param modelUrl The URL from which to download the model.
   * @throws IOException If an I/O error occurs during the download.
   * @throws URISyntaxException If the URL is malformed.
   */
  private static void downloadModel(String modelName, String modelUrl) throws IOException, URISyntaxException {
    // Ensure the 'models' directory exists or create it
    var modelDir = new File("models");
    if (!modelDir.exists()) {
      modelDir.mkdir();
    }

    // Define the model file location
    File modelFile = new File("models/" + modelName);

    // Check if the model file exists locally
    if (!modelFile.exists()) {
      System.out.println("Downloading " + modelName + "...");
      URI uri = new URI(modelUrl);
      var url = uri.toURL();
      // Download the file from the URL to the local model file
      Files.copy(url.openStream(), modelFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
      System.out.println(modelName + " downloaded successfully!");
    } else {
      System.out.println(modelName + " already exists, skipping download.");
    }
  }

  /**
   * Returns a copy of the map containing model information.
   *
   * @return A copy of the map of model information.
   */
  public Map<String, ModelInfo> map(){
    return Map.copyOf(map); // Return a copy to avoid external modifications
  }
}
