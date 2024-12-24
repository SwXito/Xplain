<template>
  <h1 class="display-5 fw-bold text-body-emphasis">Xplain - Java Debugger</h1>
  <div class="container mt-4">
    <textarea v-model="text" class="form-control" placeholder="Enter text here..."></textarea>
    <p>{{text}}</p>
    <button @click="sendText" class="btn btn-primary mt-3">Send Text</button>
    <button  @click="getText" class="btn btn-primary mt-3">Get Text</button>
    <div class="mt-3 alert alert-success">
      <p>Texte reçu du serveur :</p>
      <p>{{ serverResponse }}</p>

    </div>
    <div class="mt-3 alert alert-success">
      <p>Texte reçu du serveur :</p>
      <p>{{ serverTest}}</p>
    </div>
  </div>
  <div class="dropdown">
    <button class="btn btn-secondary dropdown-toggle" type="button" id="dropdownMenuButton" data-bs-toggle="dropdown" aria-expanded="false">
      Select Model
    </button>
    <ul class="dropdown-menu" aria-labelledby="dropdownMenuButton">
      <li><a class="dropdown-item" @click="sendModel('mistral-7b-instruct-v0.2.Q4_K_S.gguf')">Mistral (light)</a></li>
      <li><a class="dropdown-item" @click="sendModel('LLaMA2-13B-Tiefighter.Q8_0.gguf')">LLama2 (medium)</a></li>
      <li><a class="dropdown-item" @click="sendModel('tiiuae-falcon-40b-instruct-Q8_0.gguf')">Falcon (heavy)</a></li>
    </ul>
  </div>
</template>

<script setup>
import { ref} from 'vue';
import axios from "axios";




const text = ref("");
const serverResponse = ref(null);
const serverTest = ref("");
const response = ref(null);
//const obj = reactive(data);
// const  mydata = ref(data.value);



// Watch for new data
/*
watchEffect(() => {
  if (data.value) {
    console.log('daata :',data)
  }
})
*/
/*
watch( () => eventSource.status, () =>{
  if(eventSource.status.value === 'CLOSED') {
    console.log("reconnecting...")
    eventSource.open();
  }
}, {deep : true})

 */

const sendText = async () => {
  try {
    const respoe = await axios.post('http://localhost:8081/api/generate/request', {
      ContentDescription: 'request',
      content: text.value
    }, {
      headers: {
        'Content-Type': 'application/json'
      }
    });
    const sse = new EventSource('http://localhost:8081/api/generate/response'); // SERVEUR SENT EVENT
    sse.addEventListener("message", (e) => {
      const boxer = JSON.parse(e.data);
      console.log(boxer);
      if (boxer.contentDescription === "token") { // when token received
        serverTest.value += boxer.content;
      }
      if (boxer.contentDescription === "start") { // clean the other result
        serverTest.value = "";
      }
      if (boxer.contentDescription === "end") { // close the sse
        sse.close();
      }
    });
    serverResponse.value = respoe.data.content;
    console.log('serveurTest:', serverTest.value);
  } catch (error) {
    console.error('Erreur lors de l\'envoi des données:', error);
  }
};

const getText = async () => {
  try {
    response.value = await axios.get('http://localhost:8081/api/llmresponse', {
      headers: {
        'Content-Type': 'application/json'
      }
    });
    console.log('Réponse du serveur:', response.value);
  } catch (error) {
    console.error('Erreur lors de l\'envoi des données:', error);
  }
};

const sendModel = async (model) => {
  try {
    const respoe = await axios.post('http://localhost:8081/api/model', model, {
      headers: {
        'Content-Type': 'text/plain'
      }
    });
    console.log('Response:', respoe.data);
  } catch (error) {
    console.error('Error sending model:', error);
  }
};




</script>