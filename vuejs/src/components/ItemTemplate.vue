<template>
  <b-list-group-item style="z-index: 1000;">
    <strong>#{{ item.id }}</strong> {{ item.login }}
  </b-list-group-item>
</template>

<script>
export default {
  props: {
    item: {required: true},
  },
}
</script>