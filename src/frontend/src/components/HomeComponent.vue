<template>
  <h1 class="display-5 fw-bold text-body-emphasis">Xplain - Java Debugger</h1>
  <div class="container mt-4">
    <textarea v-model="text" class="form-control" placeholder="Enter text here..."></textarea>
    <button @click="sendText" class="btn btn-primary mt-3">Send Text</button>
    <div v-if="serverResponse" class="mt-3 alert alert-success">
      <p>Texte reçu du serveur :</p>
      <pre>{{ serverResponse }}</pre>
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
import {ref} from 'vue';
import axios from "axios";

const text = ref("");
const serverResponse = ref(null);

const sendText = async () => {
  try {
    const response = await axios.post('http://localhost:8081/api/endpoint', {
      ContentDescription: 'request',
      content: text.value
    }, {
      headers: {
        'Content-Type': 'application/json'
      }
    });
    serverResponse.value = response.data.content;
    console.log('Réponse du serveur:', response.data.content);
  } catch (error) {
    console.error('Erreur lors de l\'envoi des données:', error);
  }
};

const sendModel = async (model) => {
  try {
    const response = await axios.post('http://localhost:8081/api/model', model, {
      headers: {
        'Content-Type': 'text/plain'
      }
    });
    console.log('Response:', response.data);
  } catch (error) {
    console.error('Error sending model:', error);
  }
};

</script>