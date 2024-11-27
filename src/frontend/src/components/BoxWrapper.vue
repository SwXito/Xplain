<template>
  <div class="box-wrapper">
    <slot></slot> <!-- Place du contenu -->
  </div>
</template>

<script setup>
</script>

<style scoped>
.box-wrapper {
  background-color: rgb(212, 188, 188);
  flex: 1;
  border: 1px solid black;
  padding: 0.5rem; /* Ajout d'un espace intérieur pour l'esthétique */
  border-radius: 5px; /* Optionnel : rend les coins légèrement arrondis */
}
</style>
