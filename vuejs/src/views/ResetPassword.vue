<template>
  <div>
    <b-container class="mt-5 pt-5">
      <b-row align-h="center">
        <b-col cols="md-6">
          <validation-observer ref="form" v-slot="{handleSubmit}">
            <b-form @submit.stop.prevent="handleSubmit(submit)">



              <validation-provider v-slot="validationContext"
                                   rules="required|email">
                <b-form-group :label="$t('INFO_RESET') + '*:'" label-align="left">
                  <b-input
                      v-model="form.email"
                      :state="getValidationState(validationContext)"
                      name="email"
                  />
                  <b-form-invalid-feedback>{{ validationContext.errors[0] }}</b-form-invalid-feedback>
                </b-form-group>
              </validation-provider>


              <b-row>
                <b-col class="text-left">
                  <b-btn type="submit"  variant="primary">{{ $t('RESET_PASSWORD') }}</b-btn>
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
  name: 'ResetPassword',
  data() {
    return {
      form: {
        email: null,
      },
    }
  },
  methods: {
    submit() {

      api.get('user/requestResetPassword/?email='+this.form.email).then(() => {
        eventBus.$emit('alert', {key: 'SUCCESSFUL_RESET', variant: 'success'})
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