<template>
  <!-- Main wrapper for the component, with a flex column layout and custom styles -->
  <div class="d-flex flex-column h-100 w-100 border p-3 rounded overflow-hidden border-info"
       style="background-color: #607d8b; border-radius: 8px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);">
    <!-- Slot to allow other content to be inserted dynamically into this component -->
    <slot></slot>
  </div>
</template>

<script setup>
// No JavaScript code is needed here, this component is simply a wrapper for other content
</script>
