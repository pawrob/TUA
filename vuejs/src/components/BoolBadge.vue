<template>
  <b-badge v-bind="$props" :variant="active ? 'success' : 'danger'" pill>
    <b-icon v-if="active" icon="check"/>
    <b-icon v-else icon="x"/>
    <slot/>
  </b-badge>
</template>

<script>
import {BBadge} from 'bootstrap-vue'
export default {
  name: "BoolBadge",
  props: {
    active: Boolean,
    ...BBadge.prototype.$props
  }
}
</script>

<style scoped>

</style>