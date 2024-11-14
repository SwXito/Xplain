<template>
  <div class="container mt-4">
    <textarea v-model="text" class="form-control" placeholder="Enter text here..."></textarea>
    <button @click="sendText" class="btn btn-primary mt-3">Send Text</button>
    <div v-if="serverResponse" class="mt-3 alert alert-success">
      <p>Texte reçu du serveur :</p>
      <pre>{{ serverResponse }}</pre>
    </div>
  </div>
</template>

<script setup>
import {ref} from 'vue';
import axios from "axios";



const text = ref("");
const serverResponse = ref(null);
const sendText = async () => {
  try {
    const response = await axios.post('http://localhost:8081/api/generate', JSON.stringify({'contentDescription' : 'request', 'content' : text.value}), {
      headers: {
        'Content-Type': 'application/json'
      }
    });
    serverResponse.value = response.data.content;
    console.log('Réponse du serveur:', response);
  } catch (error) {
    console.error('Erreur lors de l\'envoi des données:', error);
  }
};
</script>
