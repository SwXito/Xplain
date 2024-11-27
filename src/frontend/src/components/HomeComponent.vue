<template>
  <h1 class="display-5 fw-bold text-body-emphasis text-center">Xplain - Java Debugger</h1>
  <div class="d-flex" style="width: 80vw; height: 80vh; display: flex;">

    <!-- Historique -->
    <BoxWrapper>
      <div class="text-center">history</div>
      <div>
        <ul class="dropdown-menu position-static d-grid gap-1 p-2 rounded-3 mx-0 border-0 shadow w-220px mt-3"
            data-bs-theme="dark">
          <li v-for="(item, index) in historyArray" :key="index" class="dynamic-div">
            <a>
              <div>{{ item.timestamp }}</div>
              <div>{{ item.history }}</div>
            </a>
            <div>
              <hr class="dropdown-divider">
            </div>
          </li>
        </ul>
      </div>
    </BoxWrapper>

    <!-- Conteneur flex pour zone de texte et boîte -->
    <BoxWrapper>
      <div class="text-center p-2 bg-light border rounded">
        <!-- Zone de texte (garde toute la place disponible) -->
        <textarea
            v-model="text"
            class="form-control m-auto flex-grow-1"
            placeholder="Enter text here..."
            style="width: 500px;">
        </textarea>

        <!-- Boutons -->
        <div class="mt-3">
          <button @click="sendText" class="btn btn-primary">Send Text</button>
        </div>

        <!-- Message compilateur -->
        <div v-if="serverResponse" class="mt-3 alert alert-success">
          <p>Compiler message :</p>
          <pre>{{ serverResponse }}</pre>
        </div>

        <!-- Dropdown pour modèle -->
        <div class="dropdown mt-3">
          <button
              class="btn btn-secondary dropdown-toggle"
              type="button"
              id="dropdownMenuButton"
              data-bs-toggle="dropdown"
              aria-expanded="false">
            Select Model
          </button>
          <ul class="dropdown-menu" aria-labelledby="dropdownMenuButton">
            <li><a class="dropdown-item" @click="sendModel('light')">Llama-3.2-3B (light)</a></li>
            <li><a class="dropdown-item" @click="sendModel('medium')">Mistral-7B (medium)</a></li>
            <li><a class="dropdown-item" @click="sendModel('heavy')">SILMA-9B (heavy)</a></li>
          </ul>
        </div>
      </div>
    </BoxWrapper>

    <!-- Boutons et conseils -->
    <BoxWrapper>
      <button
          @click="toggleAdviceBox"
          class="btn btn-outline-primary ms-3">
        Show advices
      </button>

      <!-- Boîte de conseils (largeur fixe) -->
      <div v-if="isAdviceBoxVisible" class="alert mt-4">
        <textarea
            v-model="llmResponse"
            class="form-control"
            readonly
            rows="5">
        </textarea>
      </div>
    </BoxWrapper>

  </div>
</template>

<script setup>
import BoxWrapper from './BoxWrapper.vue'; // Importation du composant
import {ref} from 'vue';
import axios from "axios";

// Données réactives
const text = ref("");
const serverResponse = ref(null);
const llmResponse = ref(""); // Réponse à afficher dans la boîte de texte
const isAdviceBoxVisible = ref(false); // Contrôle la visibilité de la boîte
const historyArray = ref([]);

// Envoyer le texte et récupérer les réponses
const sendText = async () => {
  try {
    const response = await axios.post('http://localhost:8081/api/endpoint', {
      contentDescription: 'request',
      content: text.value
    }, {
      headers: {
        'Content-Type': 'application/json'
      }
    });
    const historyResponse = await axios.post('http://localhost:8081/api/history');
    historyArray.value = historyResponse.data;
    historyArray.value.forEach(item => {
      item.timestamp = new Date(item.timestamp).toLocaleString('fr-FR', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: 'numeric',
        minute: 'numeric',
        second: 'numeric',
        hour12: false
      });
    });
    serverResponse.value = response.data.compilerResponse;
    llmResponse.value = response.data.llmResponse || "No advice received."; // Met à jour avec le llmResponse
  } catch (error) {
    console.error('Erreur lors de l\'envoi des données:', error);
  }
};

// Alterne l'affichage de la boîte de conseils
const toggleAdviceBox = () => {
  if (!llmResponse.value) {
    llmResponse.value = "No advice available. Please send text first.";
  }
  isAdviceBoxVisible.value = !isAdviceBoxVisible.value; // Alterne la visibilité
};

// Envoi du modèle sélectionné
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
