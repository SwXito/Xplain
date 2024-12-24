package fr.uge.model;

import de.kherud.llama.ModelParameters;

import de.kherud.llama.InferenceParameters;
import de.kherud.llama.LlamaModel;
import de.kherud.llama.args.MiroStat;
import io.smallrye.mutiny.Multi;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestStreamElementType;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static jakarta.ws.rs.core.Response.status;

@Path("/api/generate")
public class LLMService {
    private final ReentrantLock lock = new ReentrantLock();
    private final String modelPath = "mistral-7b-instruct-v0.2.Q2_K.gguf";
    private final ModelParameters modelParams = new ModelParameters()
            .setModelFilePath("models/" +modelPath)
            .setNGpuLayers(10);
    private String prompt = "You are Llama an assistant that provide advice for java programmer\n" +
            "Llama is helpful, and suggest only 5 things upgradable, with short sentences" +
            "Once the User submit a java class.\n\n";
    private InferenceParameters inferParams = null;
    private LlamaModel model;

    public LLMService() {
        Thread.ofPlatform().start(() -> {
            lock.lock();
            model = new LlamaModel(modelParams); // Initialisation llm
            lock.unlock();
        });
    }


    @POST
    @Path("request")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newRequest(Boxer classPrompt) throws IOException {
        Objects.requireNonNull(classPrompt);

        this.prompt += "\nUser: " + classPrompt.content() + "\n\n" ;
        this.prompt += "\nLama: ";
        lock.lock();
        try {
            this.inferParams = new InferenceParameters(this.prompt)
                    .setTemperature(0.5f)
                    .setPenalizeNl(true)
                    .setMiroStat(MiroStat.V2)
                    .setStopStrings("User:");
        } finally {
            lock.unlock();
        }
        return Response.ok(new Boxer("receiveData", "ok")).build();
    }
    @Path("response")
    @GET
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<Boxer> generateCompleteResponse() throws IOException {
        return Multi.createFrom().emitter(emitter ->  {
            Thread.ofPlatform().start(() -> {
                lock.lock();
                try {
                    if (model == null || inferParams == null) {
                        emitter.complete(); // nien a envoyer

                    } else  {
                        emitter.emit(new Boxer("start", "")); // debut de la generation de reponse
                        for(var output : model.generate(inferParams)) {
                            emitter.emit(new Boxer("token", output.toString()));
                        }
                        emitter.emit(new Boxer("end", "")); // fin de la genereation
                        emitter.complete();
                        inferParams = null; // nettoie les parametres
                    }
                } finally {
                    lock.unlock();
                }
                });
        });
    }

}
