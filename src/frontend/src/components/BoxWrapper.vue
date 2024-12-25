<template>
  <div class="box-wrapper">
    <slot></slot> <!-- Place du contenu -->
  </div>
</template>

<script setup>
</script>

<style scoped>
textarea {
  box-sizing: border-box;
}

.box-wrapper {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
  border: 1px solid black;
  padding: 0.5rem;
  border-radius: 5px;
  overflow: hidden;
}

</style>
