<template>
  <div>
    <b-container>
      <h1>{{ $t('MANAGE_ACCOUNT') }}</h1>
      <b-list-group>
        <b-list-group-item>
          <b-link to="/account/aboutAccount">{{ $t('ABOUT_ACCOUNT') }}</b-link>
        </b-list-group-item>
        <b-list-group-item>
          <b-link to="/account/change-personal-data">{{ $t('CHANGE_PERSONAL_DATA') }}</b-link>
        </b-list-group-item>
        <b-list-group-item>
          <b-link to="/account/change-password">{{ $t('CHANGE_PASSWORD') }}</b-link>
        </b-list-group-item>
        <b-list-group-item>
          <b-link to="/account/change-email">{{ $t('CHANGE_EMAIL') }}</b-link>
        </b-list-group-item>
        <b-list-group-item>
          <b-link to="/account/delete-account">{{ $t('DELETE_ACCOUNT') }}</b-link>
        </b-list-group-item>
      </b-list-group>
    </b-container>
  </div>
</template>

<script>
export default {
  name: 'Account',
}
</script>


<style scoped>

</style>