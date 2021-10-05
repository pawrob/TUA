<template>
  <div>
    <b-container class="mt-5 pt-5">
      <b-row align-h="center">
        <b-col cols="md-6">
          <validation-observer ref="form" v-slot="{handleSubmit}">
            <b-form @submit.stop.prevent="handleSubmit(submit)">

              <validation-provider v-slot="validationContext"
                                   :rules="{ required: true }">
                <b-form-group :label="$t('USERNAME') + ':'" label-align="left">
                  <b-input
                      v-model="form.login"
                      :state="getValidationState(validationContext)"
                      name="username"
                  />
                  <b-form-invalid-feedback>{{ validationContext.errors[0] }}</b-form-invalid-feedback>
                </b-form-group>
              </validation-provider>

              <validation-provider v-slot="validationContext"
                                   :rules="{ required: true }">
                <b-form-group :label="$t('PASSWORD') + ':'" label-align="left">
                  <b-input
                      v-model="form.password"
                      :state="getValidationState(validationContext)"
                      name="password" type="password"/>
                  <b-form-invalid-feedback>{{ validationContext.errors[0] }}</b-form-invalid-feedback>
                </b-form-group>
              </validation-provider>

              <b-row>
                <b-col class="text-left">
                  <b-btn type="submit" variant="primary">{{ $t('LOGIN') }}</b-btn>
                </b-col>
                <b-col class="text-center">
                  <b-btn to="/resetPassword" variant="outline-primary">{{ $t('RESET_PASSWORD') + '...' }}</b-btn>
                </b-col>
                <b-col class="text-right">
                  <b-btn to="/register" variant="outline-primary">{{ $t('REGISTER') + '...' }}</b-btn>
                </b-col>
              </b-row>

            </b-form>
          </validation-observer>
        </b-col>
      </b-row>
    </b-container>

  </div>
</template>

<script>
import eventBus from '../event-bus'
import {api} from '@/api'

export default {
  name: 'Login',
  data() {
    return {
      form: {
        login: null,
        password: null,
      },
    }
  },
  methods: {
    submit() {
      api.post('auth', this.form).then(response => {
        let token = response.data
        this.$store.commit('loginWithToken', token)
        eventBus.$emit('alert', {key: 'LOGIN', variant: 'success'})
        this.$router.push(this.$route.query?.redirectUrl ?? '/')
      }).catch()
    },
    getValidationState({dirty, validated, valid = null}) {
      return dirty || validated ? valid : null
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
    "USERNAME": "Nazwa użytkownika",
    "PASSWORD": "Hasło",
    "LOGIN": "Zaloguj się"
  },
  "en": {
    "USERNAME": "Username",
    "PASSWORD": "Password"
  }
}</i18n>
