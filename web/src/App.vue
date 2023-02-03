<template>
  <NavBar />
  <div>
    <div>name: {{ my_name }}</div>
    <div>score: {{ my_score }}</div>
  </div>
  
  <router-view/>
</template>

<script>
import NavBar from './components/NavBar.vue'
import "bootstrap/dist/css/bootstrap.min.css"
import "bootstrap/dist/js/bootstrap"

import $ from 'jquery';
import { ref } from 'vue';

export default {
  components: {
    NavBar
  },

  name: "App",
  setup: () => {
    let my_name = ref("");
    let my_score = ref("");

    $.ajax({
    url: "http://127.0.0.1:1644/pk/getbotinfo/",
    type: "get",
    success: resp => {
      console.log(resp);
      my_name.value = resp.name;
      my_score.value = resp.rating;
    }
  });

    return {
      my_name,
      my_score
    }
  }
}
</script>

<style>
body {
  background-image: url("@/assets/images/background.jpg");
  background-size: cover;
}
</style>
