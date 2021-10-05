<template>
  <div class="client-info">
    <b-container class="grid">
      <b-row>
        <b-col cols="2">Login</b-col>
        <b-col>{{ client.login }}</b-col>
      </b-row>
      <b-row>
        <b-col cols="2">E-Mail</b-col>
        <b-col>{{ client.email }}</b-col>
      </b-row>
      <b-row>
        <b-col cols="2">{{ $t('NAME') }}</b-col>
        <b-col>{{ client.personalData.name }}</b-col>
      </b-row>
      <b-row>
        <b-col cols="2">{{ $t('SURNAME') }}</b-col>
        <b-col>{{ client.personalData.surname }}</b-col>
      </b-row>
      <b-row>
        <b-col cols="2">{{ $t('PHONE_NUMBER') }}</b-col>
        <b-col>{{ client.personalData.phoneNumber }}</b-col>
      </b-row>
      <b-row>
        <b-col cols="2">{{ $t('LANGUAGE') }}</b-col>
        <b-col>{{ client.personalData.language }}</b-col>
      </b-row>
      <b-row>
        <b-col cols="2">{{ $t('IS_MAN') }}</b-col>
        <b-col>
          <bool-badge :active="client.personalData.isMan"/>
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script>
import {api} from "@/api";
import BoolBadge from "@/components/BoolBadge";

export default {
  name: "ClientInfo",
  components: {BoolBadge},
  data: () => {
    return {
      client: {}

    }
  },
  methods: {
    refreshClient() {
      api.get('clients/' + this.$route.params.id)
          .then(response => this.client = response.data)
    }
  },
  mounted() {
    this.refreshClient()
  }
}
</script>

<style scoped>

</style>