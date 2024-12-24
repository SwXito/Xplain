package fr.uge.model;

import de.kherud.llama.LlamaOutput;
import de.kherud.llama.ModelParameters;

import de.kherud.llama.InferenceParameters;
import de.kherud.llama.LlamaModel;
import de.kherud.llama.args.MiroStat;

import java.io.IOException;
import java.util.Objects;

final public class LLMService {

  private final String modelPath = "mistral-7b-instruct-v0.2.Q4_K_S.gguf";
  private final ModelParameters modelParams = new ModelParameters()
    .setModelFilePath("models/" + modelPath)
    .setNGpuLayers(43);
  private final LlamaModel model = new LlamaModel(modelParams);

  private String prompt = "This is not a conversation between User and Llama\n" +
    "Llama is helpful, and suggest only one thing upgradable, with short sentences" +
    "This is my java class :\n\n";

  public String llmResponse(String classPrompt, String compilerOutputPrompt) throws IOException {
    Objects.requireNonNull(classPrompt);
    Objects.requireNonNull(compilerOutputPrompt);
    StringBuilder output = new StringBuilder();
    this.prompt += "\nUser: " + classPrompt + "\n\n" + "And this is the answer of the compiler :" +
      compilerOutputPrompt + "\n\nCan you give me some advices to improve my class ?";
    this.prompt += "\nLama: ";
    InferenceParameters inferParams = new InferenceParameters(this.prompt)
      .setTemperature(0.7f)
      .setPenalizeNl(true)
      .setMiroStat(MiroStat.V2)
      .setStopStrings("User:");
    //.set("\n");
    for (LlamaOutput out : model.generate(inferParams)) {
      output.append(out.toString());
    }
    this.prompt += output.toString();

    return "LLM response :\n" + output.toString();
  }
}