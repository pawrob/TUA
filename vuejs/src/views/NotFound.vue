<template>
  <div>
    <b-container class="mt-5">
      <b-card bg-variant="info" class="text-center" text-variant="white">
        <template #header>{{ $t('NOT_FOUND') }}</template>
      </b-card>
    </b-container>
  </div>
</template>

<script>
export default {
  name: "NotFound"
}
</script>

<style scoped>

</style>