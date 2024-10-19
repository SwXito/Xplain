package fr.uge.model;

import de.kherud.llama.LlamaOutput;
import de.kherud.llama.ModelParameters;

import de.kherud.llama.InferenceParameters;
import de.kherud.llama.LlamaModel;
import de.kherud.llama.args.MiroStat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LLMService {
    public static void start(String modelName) throws IOException {
      ModelParameters modelParams = new ModelParameters()
        .setModelFilePath("models/" + modelName)
        .setNGpuLayers(43);
      String system = "This is a conversation between User and Llama, a friendly chatbot.\n" +
        "Llama is helpful, kind, honest, good at writing, and never fails to answer any " +
        "requests immediately and with precision.\n\n" +
        "User: Hello Llama\n" +
        "Llama: Hello.  How may I help you today?";
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
      try (LlamaModel model = new LlamaModel(modelParams)) {
        System.out.print(system);
        String prompt = system;
        while (true) {
          prompt += "\nUser: ";
          System.out.print("\nUser: ");
          String input = reader.readLine();
          prompt += input;
          System.out.print("Llama: ");
          prompt += "\nLlama: ";
          InferenceParameters inferParams = new InferenceParameters(prompt)
            .setTemperature(0.7f)
            .setPenalizeNl(true)
            .setMiroStat(MiroStat.V2)
            .setStopStrings("User:");
          for (LlamaOutput output : model.generate(inferParams)) {
            System.out.print(output);
            prompt += output;
          }
        }
      }
    }
}
