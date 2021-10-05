<template>
  <div>
    <b-container class="mt-5 pt-5">
      <b-row align-h="center">
        <b-col cols="md-6">
          <validation-observer ref="form" v-slot="{handleSubmit}">
            <b-form @submit.stop.prevent="handleSubmit(submit)">

              <validation-provider v-slot="validationContext"
                                   rules="required|min:8|max:16">
                <b-form-group :label="$t('USERNAME') + '*:'" label-align="left">
                  <b-input
                      v-model="form.login"
                      :state="getValidationState(validationContext)"
                      name="username"
                  />
                  <b-form-invalid-feedback>{{ validationContext.errors[0] }}</b-form-invalid-feedback>
                </b-form-group>
              </validation-provider>

              <validation-provider v-slot="validationContext"
                                   rules="required|email">
                <b-form-group :label="$t('EMAIL') + '*:'" label-align="left">
                  <b-input
                      v-model="form.email"
                      :state="getValidationState(validationContext)"
                      name="email"
                  />
                  <b-form-invalid-feedback>{{ validationContext.errors[0] }}</b-form-invalid-feedback>
                </b-form-group>
              </validation-provider>

              <validation-provider v-slot="validationContext" vid="password"
                                   :rules="{ required: true, regex: /^(?!.*[\s])^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@$%^&*]).{8,}$/ }">
                <b-form-group :label="$t('PASSWORD') + '*:'" label-align="left">
                  <b-input id="password"
                      v-model="form.password"
                      :state="getValidationState(validationContext)"
                      name="password" type="password"/>
                  <label>
                    <input type="checkbox" @click='showOrHidePassword("password")'>Show Password
                  </label>
                  <b-form-invalid-feedback>{{ validationContext.errors[0] }}</b-form-invalid-feedback>
                </b-form-group>
              </validation-provider>

              <validation-provider v-slot="validationContext"
                                   rules="required|confirmed:password">
                <b-form-group :label="$t('CONFIRM_PASSWORD') + '*:'" label-align="left">
                  <b-input id="password2"
                      v-model="form.confirm_password"
                      :state="getValidationState(validationContext)"
                      name="password_confirmation" type="password" data-vv-as="password"/>
                  <label>
                    <input type="checkbox" @click='showOrHidePassword("password2")'>Show Password
                  </label>
                  <b-form-invalid-feedback>{{ validationContext.errors[0] }}</b-form-invalid-feedback>
                </b-form-group>
              </validation-provider>

              <b-row>
                <b-col class="text-left">
                  <b-btn type="submit"  variant="primary" @click="recaptcha">{{ $t('REGISTER') }}</b-btn>
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
  name: 'RegisterAccount',
  data() {
    return {
      form: {
        login: null,
        email: null,
        password: null,
        confirm_password: null
      },
    }
  },
  methods: {
    showOrHidePassword(name) {
      // var x = document.getElementById(elementId);
      var x = document.getElementById(name);

      if (x.type === "password") {
        x.type = "text";
      } else {
        x.type = "password";
      }
    },
    submit() {
      api.post('clients/add/' + this.form.password, {
        login: this.form.login,
        email: this.form.email
      }).then(() => {
        eventBus.$emit('alert', {key: 'SUCCESSFUL_REGISTRATION', variant: 'success'})
        this.$router.push('/')
      }).catch()
    },
    getValidationState({dirty, validated, valid = null}) {
      return dirty || validated ? valid : null
    },
    recaptcha() {
      this.$recaptcha('submit').then(() => {})
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

<style scoped>

</style>

<i18n>{
  "pl": {
    "USERNAME": "Nazwa użytkownika",
    "PASSWORD": "Hasło",
    "REGISTER": "Zarejestruj się"
  },
  "en": {
    "USERNAME": "Username",
    "PASSWORD": "Password"
  }
}</i18n>