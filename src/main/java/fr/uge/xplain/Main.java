package fr.uge.xplain;

import java.io.IOException;
import java.util.Map;

public class Main {
  public static void main(String... args) throws IOException {
    var models = Map.of("light", "mistral-7b-instruct-v0.2.Q4_K_S.gguf",
                        "medium", "LLaMA2-13B-Tiefighter.Q8_0.gguf",
                        "heavy", "tiiuae-falcon-40b-instruct-Q8_0.gguf");
    var modelName = models.get("light");
  }
}
