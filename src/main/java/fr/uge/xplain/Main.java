package fr.uge.xplain;

import fr.uge.model.ModelDownloader;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

import java.io.IOException;
import java.util.Map;

@QuarkusMain
public class Main implements QuarkusApplication {

  @Override
  public int run(String... args) throws IOException {
    var models = Map.of("light", "mistral-7b-instruct-v0.2.Q4_K_S.gguf",
                        "medium", "LLaMA2-13B-Tiefighter.Q8_0.gguf",
                        "heavy", "tiiuae-falcon-40b-instruct-Q8_0.gguf");
    var modelName = models.get("light");
    ModelDownloader.download(modelName);
    //LLMService.start(modelName);
    return 0;
  }
}
