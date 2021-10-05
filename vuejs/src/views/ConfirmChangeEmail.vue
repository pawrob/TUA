<template>
  <div>
    <b-card>
      <b-card-text>
        <b-button variant="danger" v-on:click="confirm()">
          {{ $t('CONFIRM_CHANGE_EMAIL_ADDRES_TO') }}: {{ $route.params.email }}
        </b-button>
      </b-card-text>
    </b-card>
  </div>
</template>

<script>
import {apiManager} from "@/api/manager";
import eventBus from "@/event-bus";

export default {
  name: "ConfirmChangeEmail",
  computed: {
    paramId() {
      return this.$route.params.id;
    },
    paramToken() {
      return this.$route.params.token;
    },
    paramEmail() {
      return this.$route.params.email;
    }
  },
  methods: {
    changeEmailPass() {
      eventBus.$emit('alert', {key: 'CHANGE_EMAIL_PASS', variant: 'success'})
      this.$router.push('/')
    },
    changeEmailFail() {
      eventBus.$emit('alert', {key: 'CHANGE_EMAIL__FAIL', variant: 'danger'})
      this.$router.push('/')
    },
    confirm() {
      apiManager.changeEmail(this.paramId, this.paramToken, this.paramEmail).then(response => {
        if (response) {
          this.changeEmailPass();
        } else {
          this.changeEmailFail();
        }
      }).catch(this.changeEmailFail);
    }
  }
}
</script>

<style scoped>

</style>
<i18n>{
  "pl": {
    "CONFIRM_CHANGE_EMAIL_ADDRES_TO": "Potwierdź zmiane adresu e-mail na"
  },
  "en": {
    "CONFIRM_CHANGE_EMAIL_ADDRES_TO": "Confirm change email address to "
  }
}</i18n>