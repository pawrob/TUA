<template>
  <div>
    <b-container class="mt-5 pt-5">
      <b-row align-h="center">
        <b-col cols="md-6">
          <h1>{{ $t('CHANGE_PASSWORD') }}</h1>
          <validation-observer ref="form" v-slot="{handleSubmit}">
            <b-form @submit.stop.prevent="handleSubmit(submit)">

              <validation-provider v-slot="validationContext" :rules="{ required: true }"
                                   name="oldPassword">
                <b-form-group :label="$t('PASSWORD_OLD') + ':'" label-align="left">
                  <b-input
                      v-model="form.oldPassword"
                      :state="getValidationState(validationContext)"
                      type="password"
                  />
                  <b-form-invalid-feedback>{{ validationContext.errors[0] }}</b-form-invalid-feedback>
                </b-form-group>
              </validation-provider>

              <validation-provider v-slot="validationContext"
                                   :rules="{ required: true, differentPassword: '@oldPassword', passwordPattern: true }"
                                   name="newPassword">
                <b-form-group :label="$t('PASSWORD_NEW') + ':'" label-align="left">
                  <b-input
                      v-model="form.newPassword"
                      :state="getValidationState(validationContext)"
                      type="password"
                  />
                  <b-form-invalid-feedback>{{ validationContext.errors[0] }}</b-form-invalid-feedback>
                </b-form-group>
              </validation-provider>

              <validation-provider v-slot="validationContext"
                                   :rules="{ required: true, confirmed: 'newPassword' }">
                <b-form-group :label="$t('PASSWORD_CONFIRM') + ':'" label-align="left">
                  <b-input
                      v-model="form.confirmPassword"
                      :state="getValidationState(validationContext)"
                      type="password"
                  />
                  <b-form-invalid-feedback>{{ validationContext.errors[0] }}</b-form-invalid-feedback>
                </b-form-group>
              </validation-provider>

              <b-row>
                <b-col class="text-right">
                  <b-btn type="submit" variant="primary">{{ $t('CHANGE_PASSWORD') }}</b-btn>
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
import {api} from '@/api'
import eventBus from '@/event-bus'

export default {
  name: 'ChangePassword',
  data() {
    return {
      form: {
        oldPassword: null,
        newPassword: null,
        confirmPassword: null,
      },
      showModal: false,
    }
  },
  methods: {
    submit() {
      this.showModal = true
    },
    sendRequest() {
      api.put('/user/change-password', {
        oldPassword: this.form.oldPassword,
        newPassword: this.form.newPassword,
      }).then(() => {
        eventBus.$emit('alert', {key: 'PASSWORD_CHANGED', variant: 'success'})
        this.$router.push('/account')
      }).catch()
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
    "CONFIRM_HEADER": "Twoje hasło zostanie zmienione",
    "CONFIRM_BODY": "Czy na pewno chcesz, aby twoje stare hasło zostało zmienione na nowe?",
    "VALIDATION": {
      "CONFIRMED": "confirm"
    }
  },
  "en": {
    "CONFIRM_HEADER": "Your password is about to be changed",
    "CONFIRM_BODY": "Are you sure that you want to change your password?"
  }
}</i18n>