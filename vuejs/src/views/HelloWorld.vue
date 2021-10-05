<template>
  <b-container id="content">
    <img alt="Vue logo" src="@/assets/clown.png">
    <h1>{{ $t("title") }}</h1>
    <hello-world msg="Welcome to Your Vue.js App"/>
  </b-container>
</template>

<script>
import HelloWorld from "@/components/HelloWorld";

export default {
  name: "About",
  components: {
    HelloWorld
  }
}
</script>

<i18n>{
  "pl": {
    "title": "Strona główna"
  },
  "en": {
    "title": "Home page"
  }
}</i18n>

<style scoped lang="scss">
@use "sass:color";
@import "../assets/custom";

</style>