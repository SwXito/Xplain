<template>
  <h1 class="display-5 fw-bold text-body-emphasis text-center">Xplain - Java Debugger</h1>
  <div class="d-flex justify-content-center align-items-center"
       style="width: 100%; height: 80vh; display: flex; gap: 1rem;">
    <!-- Historique -->
    <BoxWrapper>
      <div class="history-container">
        <div class="text-center">History</div>
        <ul
            class="dropdown-menu position-static d-grid gap-1 p-2 rounded-3 mx-0 border-0 shadow w-100 mt-3"
            data-bs-theme="dark"
            style="flex-grow: 1; overflow-y: auto; max-height: 100%;"
        >
          <li
              v-for="(item, index) in historyArray"
              :key="index"
              class="dynamic-div"
              @click="loadHistory(item)"
          >
            <a class="dropdown-item">
              <div>{{ item.timestamp }}</div>
              <div>{{ item.history }}</div>
            </a>
            <hr class="dropdown-divider"/>
          </li>
        </ul>
      </div>
    </BoxWrapper>


    <!-- Conteneur flex pour zone de texte et boîte -->
    <BoxWrapper>
      <div class="text-center p-2 bg-light border rounded h-100 d-flex flex-column">
        <!-- Zone de texte -->
        <textarea
            v-model="text"
            class="form-control flex-grow-1 mb-2"
            placeholder="Enter text here..."
            style="resize: none; overflow: auto;">
        </textarea>

        <!-- Boutons -->
        <div>
          <button @click="sendText" class="btn btn-primary w-100">Send Text</button>
        </div>

        <div class="dropdown mt-1">
          <button class="btn btn-secondary dropdown-toggle" type="button" id="dropdownMenuButton"
                  data-bs-toggle="dropdown" aria-expanded="false">
            Select Model
          </button>
          <ul class="dropdown-menu" aria-labelledby="dropdownMenuButton">
            <li><a class="dropdown-item" @click="sendModel('mistral-7b-instruct-v0.2.Q2_K.gguf')"> light </a></li>
            <li><a class="dropdown-item" @click="sendModel('mistral-7b-instruct-v0.2.Q4_K_S.gguf')"> medium </a></li>
            <li><a class="dropdown-item" @click="sendModel('mistral-7b-instruct-v0.2.Q5_K_S.gguf')"> heavy </a></li>
          </ul>
        </div>

        <!-- Message compilateur -->
        <div v-if="serverResponse"
             :class="{
               'mt-3 alert': true,
               'alert-success': serverResponse.success, // Si le compilateur retourne un succès
               'alert-danger': !serverResponse.success  // Sinon, c'est une erreur
             }">
          <p>Compiler message :</p>
          <pre>{{ serverResponse.message }}</pre>
        </div>
      </div>
    </BoxWrapper>

    <!-- Conseils -->
    <BoxWrapper>
      <div class="d-flex flex-column h-100">
        <button
            @click="toggleAdviceBox"
            class="btn btn-outline-primary w-100 mb-2">
          Show advices
        </button>

        <div v-if="isAdviceBoxVisible" class="alert flex-grow-1 p-0">
          <textarea
              v-model="llmResponse"
              class="form-control h-100"
              readonly
              rows="5"
              style="resize: none; overflow: auto;">
          </textarea>
        </div>
      </div>
    </BoxWrapper>
  </div>
</template>


<script setup>
import BoxWrapper from './BoxWrapper.vue'; // Importation du composant
import {ref, onMounted} from 'vue';
import axios from 'axios';

// Données réactives
const text = ref(''); // Texte actuel
const serverResponse = ref(null); // Réponse du compilateur
const llmResponse = ref(''); // Conseils
const isAdviceBoxVisible = ref(false); // Visibilité de la boîte de conseils
const historyArray = ref([]); // Liste des historiques

// Chargement des données à partir de l'historique
const loadHistory = (history) => {
  text.value = history.classText || ''; // Texte à afficher
  serverResponse.value = {
    success: history.success,
    message: history.compilerResponse || 'No message'
  };
  llmResponse.value = history.llmResponse || 'No advice received.'; // Conseils
};

// Récupération de l'historique
const fetchHistory = async () => {
  try {
    const response = await axios.post('http://localhost:8081/api/history');
    historyArray.value = response.data;
    historyArray.value.forEach(item => {
      item.timestamp = new Date(item.timestamp).toLocaleString('fr-FR', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: 'numeric',
        minute: 'numeric',
        second: 'numeric',
        hour12: false,
      });
    });
  } catch (error) {
    console.error('Erreur lors de la récupération de l\'historique:', error);
  }
};

const sendText = async () => {
  try {
    const response = await axios.post('http://localhost:8081/api/endpoint', {
      contentDescription: 'request',
      content: text.value,
    }, {
      headers: {
        'Content-Type': 'application/json',
      },
    });
    await fetchHistory(); // Rafraîchit l'historique
    serverResponse.value = {
      success: response.data.success,
      message: response.data.compilerResponse || 'No message'
    };
    const sse = new EventSource('http://localhost:8081/api/generate/response'); // SERVEUR SENT EVENT
    sse.addEventListener("message", (e) => {
      const boxer = JSON.parse(e.data);
      console.log(boxer);
      if (boxer.contentDescription === "token") { // when token received
        llmResponse.value += boxer.content;
      }
      if (boxer.contentDescription === "start") { // clean the other result
        llmResponse.value = "";
      }
      if (boxer.contentDescription === "end") { // close the sse
        sse.close();
      }
    });
  } catch (error) {
    console.error('Erreur lors de l\'envoi des données:', error);
  }
};

// Alterne l'affichage de la boîte de conseils
const toggleAdviceBox = () => {
  isAdviceBoxVisible.value = !isAdviceBoxVisible.value;
};

// Chargement initial de l'historique
onMounted(async () => {
  await fetchHistory();
});

const sendModel = async (model) => {
  try {
    const response = await axios.post('http://localhost:8081/api/generate/model', model, {
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

<style scoped>
.history-container {
  display: flex;
  flex-direction: column;
  height: 100%; /* Permet à la boîte d’occuper tout l’espace disponible */
}

ul {
  margin: 0; /* Supprime les marges inutiles */
}
</style>