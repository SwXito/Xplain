<template>
  <div id="app" class="container">
    <HomeComponent/>
  </div>
</template>

<script>
import { defineAsyncComponent } from 'vue';

export default {
  name: 'App',
  components: {
    HomeComponent: defineAsyncComponent(() => import('./components/HomeComponent.vue'))
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;

  .container {
    max-width: 600px;
    margin: auto;
    padding: 20px;
  }

  textarea {
    width: 100%;
    height: 200px;
    font-family: monospace;
    font-size: 16px;
    border: 1px solid #ddd;
    padding: 10px;
    box-sizing: border-box;
  }
}
</style>