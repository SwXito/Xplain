<template>
  <h1 class="display-5 fw-bold text-center" style="color:#37474f">Xplain - Java Debugger</h1>
  <h4 class="fw-bold text-center" style="color:#546e7a">Current model : {{ modelName }}</h4>

  <div class="d-flex justify-content-center align-items-center"
       style="width: 100%; height: 80vh; gap: 1rem;">

    <!-- Historique -->
    <BoxWrapper>
      <div class="d-flex flex-column h-100 p-2"
           style="background-color: #b0bec5; border-radius: 8px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);">
        <div class="text-center mb-2 fw-bold" style="color: #eceff1;">History</div>
        <ul class="list-group overflow-auto"
            style="max-height: calc(100% - 2rem);">
          <li
              v-for="(item, index) in historyArray"
              :key="index"
              class="list-group-item"
              @click="loadHistory(item)"
              style="background-color: #90a4ae; color: #ffffff;">
            <div>{{ item.timestamp }}</div>
            <div v-html="item.history.replace(/\n/g, '<br>')"></div>
          </li>
        </ul>
      </div>
    </BoxWrapper>

    <!-- Zone de texte -->
    <BoxWrapper>
      <div class="text-center p-2 border rounded h-100 d-flex flex-column"
           style="background-color: #eceff1; border-radius: 8px; border-color: #eceff1">
        <textarea v-model="text"
                  class="form-control flex-grow-1 mb-2"
                  placeholder="Enter text here..."
                  style="resize: none; overflow: auto; background-color: #607d8b; color: #ffffff; border: 1px solid #b0bec5;">
        </textarea>
        <button @click="sendText"
                class="btn btn-primary w-100"
                style="background-color: #00acc1; color: #fff; border: none; border-radius: 5px; transition: all 0.3s ease-in-out;">
          Send Text
        </button>
        <div class="dropdown mt-2">
          <button class="btn btn-secondary dropdown-toggle w-100" type="button" id="dropdownMenuButton"
                  data-bs-toggle="dropdown" aria-expanded="false"
                  style="background-color: #37474f; border: none; color: #fff;">
            Select Model
          </button>
          <ul class="dropdown-menu" aria-labelledby="dropdownMenuButton">
            <li><a class="dropdown-item" @click="sendModel('light')">Light</a></li>
            <li><a class="dropdown-item" @click="sendModel('medium')">Medium</a></li>
            <li><a class="dropdown-item" @click="sendModel('heavy')">Heavy</a></li>
          </ul>
        </div>
        <div v-if="serverResponse"
             :class="{
               'mt-3 alert': true,
               'alert-success': serverResponse.success,
               'alert-danger': !serverResponse.success
             }">
          <p>Compiler message :</p>
          <pre>{{ serverResponse.message }}</pre>
        </div>
      </div>
    </BoxWrapper>

    <!-- Conseils -->
    <BoxWrapper>
      <div class="d-flex flex-column h-100">
        <button @click="toggleAdviceBox"
                class="btn btn-outline-info w-100 mb-2"
                style="border-color: #00acc1; color: #00acc1;">
          Show advices
        </button>
        <div v-if="isAdviceBoxVisible" class="alert flex-grow-1 p-0">
          <textarea v-model="llmResponse"
                    class="form-control h-100"
                    readonly
                    rows="5"
                    style="resize: none; overflow: auto; background-color: #607d8b; color: #fff; border: 1px solid #b0bec5;">
          </textarea>
        </div>
      </div>
      <BeatLoader v-if="isResponseGenerating"></BeatLoader>
    </BoxWrapper>

  </div>
</template>


<script setup>
import BoxWrapper from './BoxWrapper.vue'; // Importation du composant
import BeatLoader from "@/components/BeatLoader.vue";
import {ref, onMounted} from 'vue';
import axios from 'axios';

// Données réactives
const text = ref(''); // Texte actuel
const serverResponse = ref(null); // Réponse du compilateur
const llmResponse = ref(''); // Conseils
const isAdviceBoxVisible = ref(false); // Visibilité de la boîte de conseils
const historyArray = ref([]); // Liste des historiques
const modelName = ref("light");
const generating = ref(false);

// Chargement des données à partir de l'historique
const loadHistory = (history) => {
  if(!generating.value){
    text.value = history.classText || ''; // Texte à afficher
    serverResponse.value = {
      success: history.success,
      message: history.compilerResponse || 'No message'
    };
    llmResponse.value = history.llmResponse || 'No advice received.'; // Conseils
  }
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
  if(!generating.value){
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
      const sse = new EventSource('http://localhost:8081/api/response'); // SERVEUR SENT EVENT
      sse.onmessage = (e) => {
        const boxer = JSON.parse(e.data);
        console.log(boxer);
        if (boxer.contentDescription === "token") { // when token received
          llmResponse.value += boxer.content;
        }
        if (boxer.contentDescription === "start") { // clean the other result
          llmResponse.value = "";
          generating.value = true;
        }
        if (boxer.contentDescription === "end") { // close the sse
          sse.close();
          fetchHistory();
          generating.value = false;
        }
      };
    } catch (error) {
      console.error('Erreur lors de l\'envoi des données:', error);
    }
  }
};

// Alterne l'affichage de la boîte de conseils
const toggleAdviceBox = () => {
  isAdviceBoxVisible.value = !isAdviceBoxVisible.value;
};

// Chargement initial de l'historique
onMounted(async () => {
  document.body.style.backgroundColor = '#eceff1';
  await fetchHistory();
});

const sendModel = async (model) => {
  if(!generating.value){
    modelName.value = model;
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
  }
};
</script>
