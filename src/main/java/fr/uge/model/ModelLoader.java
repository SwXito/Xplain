package fr.uge.model;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public final class ModelLoader {

  public static void loadAll() {
    try {
      downloadModel("mistral-7b-instruct-v0.2.Q4_K_S.gguf", "https://huggingface.co/TheBloke/Mistral-7B-Instruct-v0.2-GGUF/resolve/main/mistral-7b-instruct-v0.2.Q4_K_S.gguf");
      downloadModel("mistral-7b-instruct-v0.2.Q2_K.gguf", "https://huggingface.co/TheBloke/Mistral-7B-Instruct-v0.2-GGUF/blob/main/mistral-7b-instruct-v0.2.Q2_K.gguf");
      downloadModel("mistral-7b-instruct-v0.2.Q5_K_S.gguf", "https://huggingface.co/TheBloke/Mistral-7B-Instruct-v0.2-GGUF/blob/main/mistral-7b-instruct-v0.2.Q5_K_S.gguf");
    } catch (IOException | URISyntaxException e) {
      throw new AssertionError(e);
    }
  }

  public static void load(String modelName) {
    try {
      String modelUrl = switch (modelName) {
        case "mistral-7b-instruct-v0.2.Q4_K_S.gguf" -> "https://huggingface.co/TheBloke/Mistral-7B-Instruct-v0.2-GGUF/resolve/main/mistral-7b-instruct-v0.2.Q4_K_S.gguf";
        case "mistral-7b-instruct-v0.2.Q2_K.gguf" -> "https://huggingface.co/TheBloke/Mistral-7B-Instruct-v0.2-GGUF/blob/main/mistral-7b-instruct-v0.2.Q2_K.gguf";
        case "mistral-7b-instruct-v0.2.Q5_K_S.gguf" -> "https://huggingface.co/TheBloke/Mistral-7B-Instruct-v0.2-GGUF/blob/main/mistral-7b-instruct-v0.2.Q5_K_S.gguf";
        default -> throw new IllegalArgumentException("Unknown model name: " + modelName);
      };
      downloadModel(modelName, modelUrl);
    } catch (IOException | URISyntaxException e) {
      throw new AssertionError(e);
    }
  }

  private static void downloadModel(String modelName, String modelUrl) throws IOException, URISyntaxException {
    File modelFile = new File("models/" + modelName);
    if (!modelFile.exists()) {
      System.out.println("Downloading " + modelName + "...");
      URI uri = new URI(modelUrl);
      var url = uri.toURL();
      Files.copy(url.openStream(), modelFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
      System.out.println(modelName + " downloaded successfully!");
    } else {
      System.out.println(modelName + " already exists, skipping download.");
    }
  }
}

