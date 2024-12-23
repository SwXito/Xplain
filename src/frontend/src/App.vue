<template>
  <div id="app" class="container" style="display: grid; place-items: center">
    <HomeComponent/>
  </div>
</template>

<script>
import {defineAsyncComponent} from 'vue';

export default {
  name: 'App',
  components: {
    HomeComponent: defineAsyncComponent(() => import('./components/HomeComponent.vue'))
  }
}
</script>