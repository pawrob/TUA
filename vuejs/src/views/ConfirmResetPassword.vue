<template>
  <div>
    <b-container class="mt-5 pt-5">
      <b-row align-h="center">
        <b-col cols="md-6">
          <h3>{{ $t('CONFIRM_RESET_PASSWORD_HEADER') }}</h3>
          <validation-observer ref="form" v-slot="{handleSubmit}">
            <b-form @submit.stop.prevent="handleSubmit(submit)">

              <validation-provider v-slot="validationContext" vid="password"
                                   :rules="{ required: true, regex: /^(?!.*[\s])^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$/ }">
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
                  <b-btn type="submit" variant="primary">{{ $t('RESET_PASSWORD') }}</b-btn>
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
  name: 'ConfirmResetPassword',
  data() {
    return {
      queryToken: "",
      queryId: "",
      form: {
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
      this.id = this.$route.query.id
      this.token = this.$route.query.token
      this.queryToken = encodeURI(this.token)
      this.queryId = encodeURI(this.id)
      api.put('user/resetPassword/?id=' + this.queryId + '&token=' + this.queryToken, {
        password: this.form.password
      }).then(() => {
        eventBus.$emit('alert', {key: 'SUCCESSFUL_RESET_CONF', variant: 'success'})
        this.$router.push('/')
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

<style scoped>

</style>