package fr.uge.model;

import de.kherud.llama.LlamaOutput;
import de.kherud.llama.ModelParameters;

import de.kherud.llama.InferenceParameters;
import de.kherud.llama.LlamaModel;
import de.kherud.llama.args.MiroStat;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jdk.jfr.EventFactory;

import javax.swing.text.html.parser.Entity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static jakarta.ws.rs.core.Response.status;

@Path("/api/generate")
public class LLMService {
    private final String modelPath = "mistral-7b-instruct-v0.2.Q4_K_S.gguf";
   // private final String modelPath = "llama2-13b-tiefighter.Q4_0.gguf"; //<--- vraiment nul
    private final ModelParameters modelParams = new ModelParameters()
            .setModelFilePath("models/" +modelPath)
            .setNGpuLayers(43);
    private final LlamaModel model = new LlamaModel(modelParams);

    private String prompt = "This is not a conversation between User and Llama\n" +
            "Llama is helpful, and suggest only one thing upgradable, with short sentences" +
            "please enter your java class :\n\n";

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response llmResponse(Boxer boxer) throws IOException {
        var prompt = boxer.content();
        if (prompt.isEmpty()) {
            return Response.status(jakarta.ws.rs.core.Response.Status.BAD_REQUEST).entity(Json.createObjectBuilder().add("error", "Prompt is empty").build()).build();

        }
        System.out.println("wow");
        StringBuilder output = new StringBuilder();
        this.prompt += "\nUser: " + prompt; // ajout de l'entré
        this.prompt += "\nLama: ";
        InferenceParameters inferParams = new InferenceParameters(this.prompt)
                .setTemperature(0.7f)
                .setPenalizeNl(true)
                .setMiroStat(MiroStat.V2)
                .setStopStrings("User:");
                //.set("\n");
        System.out.println("may be here");
        for (LlamaOutput out : model.generate(inferParams)) {
            //System.out.println(out);
            output.append(out.toString());
        }
        System.out.println("incredible");
        this.prompt += output.toString();
        System.out.println(output.toString());

        return Response.ok(new Boxer("response LLm", output.toString())).build();

    }


//    public LLMService() {
//        ModelParameters modelParams = new ModelParameters()
//                .setModelFilePath("models/" + "mistral-7b-instruct-v0.2.Q4_K_S.gguf")
//                .setNGpuLayers(43);
//        this.model = new LlamaModel(modelParams);
//    }
//    public String start(String input) throws IOException {
//        //Console.log(system);
//          String prompt = "User: ";
//           //System.out.print("\nUser: ");
//          prompt += input;
//          //System.out.print("Llama: ");
//          prompt += "\nLlama: ";
//          InferenceParameters inferParams = new InferenceParameters(prompt)
//            .setTemperature(0.7f)
//            .setPenalizeNl(true)
//            .setMiroStat(MiroStat.V2)
//            .setStopStrings("User:");
//          for (LlamaOutput output : this.model.generate(inferParams)) {
//            //System.out.print(output);
//            prompt += output;
//          }
//          return prompt;
//    }
}
