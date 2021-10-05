<template>
  <div>
    <b-container class="mt-5 pt-5">
      <b-row align-h="center">
        <b-col cols="md-6">
          <h1>{{ $t('CHANGE_EMAIL') }}</h1>
          <validation-observer ref="form" v-slot="{handleSubmit}">
            <b-form @submit.stop.prevent="handleSubmit(submit)">

              <validation-provider v-slot="validationContext"
                                   :rules="{ required: true, passwordPattern: false }"
                                   name="newEmail">
                <b-form-group :label="$t('EMAIL_NEW') + ':'" label-align="left">
                  <b-input
                      v-model="form.newEmail"
                      :state="getValidationState(validationContext)"
                      type="email"
                  />
                  <b-form-invalid-feedback>{{ validationContext.errors[0] }}</b-form-invalid-feedback>
                </b-form-group>
              </validation-provider>

              <b-row>
                <b-col class="text-right">
                  <b-btn type="submit" variant="primary">{{ $t('CHANGE_EMAIL') }}</b-btn>
                </b-col>
              </b-row>

            </b-form>
          </validation-observer>
        </b-col>
      </b-row>
    </b-container>

    <b-modal v-model="showModal" :cancel-title="$t('NO')" :ok-title="$t('YES')" :title="$t('CONFIRM_HEADER')"
             @ok="handleOk">
      {{ $t('CONFIRM_BODY') }}
    </b-modal>

  </div>
</template>

<script>

import {mapGetters} from "vuex";
import {apiManager} from "@/api/manager";
import eventBus from "@/event-bus";

export default {
  name: 'ChangeEmail',
  data() {
    return {
      form: {
        newEmail: null,
      },
      showModal: false,
    }
  },
  computed: {
    ...mapGetters([
      'username'
    ]),
  },
  methods: {
    submit() {
      this.showModal = true
    },
    requestChangeEmailPass() {
      eventBus.$emit('alert', {key: 'REQUEST_EMAIL_CHANGE_PASS', variant: 'success'})
      this.$router.push('/')
    },
    requestChangeEmailFail() {
      eventBus.$emit('alert', {key: 'REQUEST_EMAIL_CHANGE_FAIL', variant: 'danger'})
      this.$router.push('/')
    },
    sendRequest() {
      apiManager.requestChangeEmail(this.username, this.form.newEmail).then((response) => {
        if (response) {
          this.requestChangeEmailPass();
        } else {
          this.requestChangeEmailFail();
        }
      }).catch(this.requestChangeEmailFail);
      /*
      api.put('/user/change-password', {
        oldPassword: this.form.oldPassword,
        newPassword: this.form.newPassword,
      }).then(() => {
        eventBus.$emit('alert', {key: 'PASSWORD_CHANGED', variant: 'success'})
        this.$router.push('/account')
      }).catch()
       */
    },
    getValidationState({dirty, validated, valid = null}) {
      return dirty || validated ? valid : null
    },
    recaptcha() {
      this.$recaptcha('submit')
    },
    handleOk() {
      this.sendRequest()
      this.recaptcha()
    },
  },
  watch: {
    '$root.$i18n.locale'() {
      // re-validate to re-generate messages.
      this.$refs.form.validate()
    },
  },
}
</script>

<i18n>{
  "pl": {
    "CONFIRM_HEADER": "Twój adres email zostanie zmieniony",
    "CONFIRM_BODY": "Czy na pewno chcesz, aby twój stary email został zmieniony na nowy?",
    "VALIDATION": {
      "CONFIRMED": "confirm"
    }
  },
  "en": {
    "CONFIRM_HEADER": "Your email address will be changed",
    "CONFIRM_BODY": "Are you sure you want your old email to be changed to the new one? "
  }
}</i18n>