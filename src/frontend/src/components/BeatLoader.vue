<template>
  <!-- Main wrapper for the spinner. It will be displayed based on the 'loading' prop -->
  <div class="v-spinner" v-show="loading">
    <!-- Odd beat element, each beat has its own animation style bound to 'spinnerStyle' -->
    <div class="v-beat v-beat-odd" v-bind:style="spinnerStyle">
    </div>
    <!-- Even beat element -->
    <div class="v-beat v-beat-even" v-bind:style="spinnerStyle">
    </div>
    <!-- Another odd beat element -->
    <div class="v-beat v-beat-odd" v-bind:style="spinnerStyle">
    </div>
  </div>
</template>

<script>
export default {
  name: 'BeatLoader',  // The name of the component

  // Props allow customization of the loader
  props: {
    loading: {
      type: Boolean,  // Determines whether the spinner is visible
      default: true    // Default is true (spinner is visible)
    },
    color: {
      type: String,   // Color of the spinner's beats
      default: '#5dc596'  // Default color is a greenish tone
    },
    size: {
      type: String,  // Size of each beat in the spinner
      default: '15px'  // Default size is 15px
    },
    margin: {
      type: String,  // Margin between beats
      default: '2px'  // Default margin is 2px
    },
    radius: {
      type: String,  // Border radius for round beats
      default: '100%' // Default is fully rounded
    }
  },

  // Data function to compute the styles dynamically based on props
  data () {
    return {
      spinnerStyle: {
        backgroundColor: this.color,  // Applies the color passed through the 'color' prop
        height: this.size,            // Applies the height passed through the 'size' prop
        width: this.size,             // Applies the width passed through the 'size' prop
        margin: this.margin,          // Applies the margin passed through the 'margin' prop
        borderRadius: this.radius    // Applies the border radius passed through the 'radius' prop
      }
    }
  }
}
</script>

<style>
/* Base styles for each "beat" in the spinner */
.v-spinner .v-beat {
  -webkit-animation: v-beatStretchDelay 0.7s infinite linear; /* Animation for beat */
  animation: v-beatStretchDelay 0.7s infinite linear;
  -webkit-animation-fill-mode: both; /* Ensures the animation fills in both directions */
  animation-fill-mode: both;
  display: inline-block; /* Each beat is displayed inline */
}

/* Styling for odd beats, without animation delay */
.v-spinner .v-beat-odd {
  animation-delay: 0s;
}

/* Styling for even beats, with a slight delay */
.v-spinner .v-beat-even {
  animation-delay: 0.35s;
}

/* Keyframes for the "beat" animation. It scales the element down to 75% at 50% of the duration and back to 100% */
@-webkit-keyframes v-beatStretchDelay {
  50% {
    -webkit-transform: scale(0.75); /* Scales down to 75% */
    transform: scale(0.75);
    -webkit-opacity: 0.2; /* Makes it more transparent */
    opacity: 0.2;
  }
  100% {
    -webkit-transform: scale(1); /* Scales back to full size */
    transform: scale(1);
    -webkit-opacity: 1; /* Full opacity */
    opacity: 1;
  }
}

/* Same animation for non-webkit browsers */
@keyframes v-beatStretchDelay {
  50% {
    -webkit-transform: scale(0.75);
    transform: scale(0.75);
    -webkit-opacity: 0.2;
    opacity: 0.2;
  }
  100% {
    -webkit-transform: scale(1);
    transform: scale(1);
    -webkit-opacity: 1;
    opacity: 1;
  }
}
</style>
