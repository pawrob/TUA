<template>
  <div>
    <b-container>
      <h1>{{ $t('ENTERTAINER') }}</h1>
      <b-list-group>
        <b-list-group-item>
          <router-link to="/entertainer/offers">{{ $t('OFFERS') }}</router-link>
        </b-list-group-item>
        <b-list-group-item>
          <router-link to="/entertainer/reservation">{{ $t('RESERVATIONS') }}</router-link>
        </b-list-group-item>
        <b-list-group-item>
          <router-link to="/entertainer/changeDescription">{{ $t('CHANGE_DESCRIPTION') }}</router-link>
        </b-list-group-item>
        <b-list-group-item>
          <router-link to="/entertainer/unavailability">{{ $t('ENTERTAINER_UNAVAILABILITY') }}</router-link>
        </b-list-group-item>
      </b-list-group>
    </b-container>
  </div>
</template>

<script>
export default {
  name: "Entertainer",
  data() {
    return {

    }
  },

  methods:{
  }
}
</script>

<style scoped>



</style>