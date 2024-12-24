package fr.uge.model;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ModelDownloader {

  record ModelInfo(String modelName, String url) {
  }

  private final static Map<String, ModelInfo> map = Map.of(
    "light", new ModelInfo("Llama-3.2-3B-Instruct.IQ2_XS.gguf",
      "https://huggingface.co/MaziyarPanahi/Llama-3.2-3B-Instruct-GGUF/resolve/main/Llama-3.2-3B-Instruct.IQ2_XS.gguf"),
    "medium", new ModelInfo("mistral-7b-instruct-v0.2.Q4_K_S.gguf",
      "https://huggingface.co/TheBloke/Mistral-7B-Instruct-v0.2-GGUF/resolve/main/mistral-7b-instruct-v0.2.Q4_K_S.gguf"),
    "heavy", new ModelInfo("SILMA-9B-Instruct-v1.0-Q4_K_M.gguf",
      "https://huggingface.co/tensorblock/SILMA-9B-Instruct-v1.0-GGUF/resolve/main/SILMA-9B-Instruct-v1.0-Q4_K_M.gguf")
  );

  public static void downloadAll() {
    map.forEach((k, v) -> {
      try {
        downloadModel(v.modelName, v.url);
      } catch (IOException e) {
        System.out.println("Interruption while downloading " + v.modelName);
        return;
      } catch (URISyntaxException e) {
        throw new AssertionError(e);
      }
    });
  }

  public static void download(String modelType) {
    Objects.requireNonNull(modelType);
    try {
      var modelInfo = map.get(modelType);
      downloadModel(modelInfo.modelName, modelInfo.url);
    } catch (IOException e) {
      System.out.println("Interruption while downloading");
      return;
    } catch (URISyntaxException e) {
      throw new AssertionError(e);
    }
  }

  private static void downloadModel(String modelName, String modelUrl) throws IOException, URISyntaxException {
    var modelDir = new File("models");
    if (!modelDir.exists()) {
      modelDir.mkdir();
    }
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

