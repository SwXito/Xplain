<template>
  <!-- Main container for the application, centers the content using grid layout -->
  <div id="app" class="container" style="display: grid; place-items: center">
    <!-- HomeComponent is dynamically imported and displayed inside this container -->
    <HomeComponent/>
  </div>
</template>

<script>
// Importing `defineAsyncComponent` from Vue to dynamically import components.
import {defineAsyncComponent} from 'vue';

export default {
  name: 'App', // The name of this Vue component

  // Defining components that will be used within this component
  components: {
    // Dynamically importing the `HomeComponent` component
    // The component will only be loaded when it is needed (i.e., when the app starts).
    HomeComponent: defineAsyncComponent(() => import('./components/HomeComponent.vue'))
  }
}
</script>
