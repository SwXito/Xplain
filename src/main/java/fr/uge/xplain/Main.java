package fr.uge.xplain;

import de.kherud.llama.InferenceParameters;
import de.kherud.llama.LlamaModel;
import de.kherud.llama.LlamaOutput;
import de.kherud.llama.ModelParameters;
import de.kherud.llama.args.MiroStat;
import fr.uge.model.LLMService;
import fr.uge.model.ModelLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Main {
    public static void main(String... args) throws IOException {
      ModelParameters modelParams = new ModelParameters()
              .setModelFilePath("models/mistral-7b-instruct-v0.2.Q4_K_S.gguf")
              .setNGpuLayers(20);

      String systemMessage = "This is not a conversation between User and Llama\n" +
              "Llama is helpful and will suggest only one thing upgradable in your java class.\n\n";
      System.out.print(systemMessage);

      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

      try (LlamaModel model = new LlamaModel(modelParams)) {
        String conversationHistory = systemMessage; // Stores full conversation for reference if needed

        while (true) {
          System.out.print("\nUser: ");
          String input = reader.readLine();
          if (input == null || input.trim().equalsIgnoreCase("exit")) break; // Optional exit condition

          conversationHistory += "\nUser: " + input + "\nLlama: ";
          String prompt = conversationHistory;

          InferenceParameters inferParams = new InferenceParameters(prompt)
                  .setTemperature(0.5f)
                  .setPenalizeNl(true)
                  .setMiroStat(MiroStat.V2)
                  .setStopStrings("User:");

          StringBuilder llamaResponse = new StringBuilder();
          for (LlamaOutput output : model.generate(inferParams)) {
            llamaResponse.append(output);
            System.out.println(output);
          }
          System.out.println("Llama: " + llamaResponse.toString());

          conversationHistory += llamaResponse.toString(); // Update history only with Llama’s full response
        }
      }
    }
  public static void main2(String... args) throws IOException {
    var models = Map.of("light", "mistral-7b-instruct-v0.2.Q4_K_S.gguf",
                        "medium", "LLaMA2-13B-Tiefighter.Q8_0.gguf",
                        "heavy", "tiiuae-falcon-40b-instruct-Q8_0.gguf");
    var modelName = models.get("medium");
    ModelLoader.load(modelName);
  }
}
