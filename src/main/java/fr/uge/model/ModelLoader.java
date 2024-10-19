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
      downloadModel("LLaMA2-13B-Tiefighter.Q8_0.gguf", "https://huggingface.co/KoboldAI/LLaMA2-13B-Tiefighter-GGUF/resolve/main/LLaMA2-13B-Tiefighter.Q8_0.gguf");
      downloadModel("tiiuae-falcon-40b-instruct-Q8_0.gguf", "https://huggingface.co/maddes8cht/tiiuae-falcon-40b-instruct-gguf/resolve/main/tiiuae-falcon-40b-instruct-Q8_0.gguf");
    } catch (IOException | URISyntaxException e) {
      throw new AssertionError(e);
    }
  }

  public static void load(String modelName) {
    try {
      downloadModel(modelName, "https://huggingface.co/TheBloke/Mistral-7B-Instruct-v0.2-GGUF/resolve/main/mistral-7b-instruct-v0.2.Q4_K_S.gguf");
    } catch (IOException | URISyntaxException e) {
      throw new AssertionError(e);
    }
  }

  private static void downloadModel(String modelName, String modelUrl) throws IOException, URISyntaxException {
    File modelFile = new File("src/main/resources/" + modelName);
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

