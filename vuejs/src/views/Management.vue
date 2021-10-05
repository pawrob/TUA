<template>
  <div>
    <b-container>
      <h1>{{ $t('MANAGEMENT') }}</h1>
      <b-list-group>
        <b-list-group-item>
          <router-link to="/management/users">{{ $t('USERS') }}</router-link>
        </b-list-group-item>
        <b-list-group-item>
          <router-link to="/management/register_entertainer">{{ $t('REGISTER_ENTERTAINER_ACCOUNT') }}</router-link>
        </b-list-group-item>
        <b-list-group-item>
          <router-link to="/management/offers">{{ $t('OFFERS') }}</router-link>
        </b-list-group-item>
        <b-list-group-item>
          <router-link to="/management/reservation">{{ $t('RESERVATIONS') }}</router-link>
        </b-list-group-item>
      </b-list-group>
    </b-container>
  </div>
</template>

<script>
export default {
  name: "Management"
}
</script>

<style scoped>

</style>