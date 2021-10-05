<template>
  <div>
    <b-container class="mt-5 pt-5">
      <b-row align-h="center">
        <b-col cols="md-6">
          <h1>{{ $t('DELETE_ACCOUNT') }}</h1>
          <validation-observer ref="form" v-slot="{handleSubmit}">
            <b-form @submit.stop.prevent="handleSubmit(submit)">

              <validation-provider v-slot="validationContext"
                                   :rules="{ required: true, passwordPattern: false }"
                                   name="password">
                <b-form-group :label="$t('PASSWORD') + ':'" label-align="left">
                  <b-input
                      v-model="form.password"
                      :state="getValidationState(validationContext)"
                      type="password"
                  />
                  <b-form-invalid-feedback>{{ validationContext.errors[0] }}</b-form-invalid-feedback>
                </b-form-group>
              </validation-provider>

              <validation-provider v-slot="validationContext"
                                   :rules="{ required: true, confirmed: 'password' }">
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
                  <b-btn type="submit" variant="primary">{{ $t('DELETE_ACCOUNT') }}</b-btn>
                </b-col>
              </b-row>

            </b-form>
          </validation-observer>
        </b-col>
      </b-row>
    </b-container>

    <b-modal v-model="showModal" :cancel-title="$t('NO')" :ok-title="$t('YES')" :title="$t('CONFIRM_HEADER')"
             @ok="sendRequest">
      {{ $t('CONFIRM_BODY') }}
    </b-modal>

  </div>
</template>

<script>
import eventBus from '@/event-bus'
import {apiClient} from "@/api/client";
import {mapGetters} from 'vuex'

export default {
  name: 'DeleteAccountByUser',
  data() {
    return {
      form: {
        password: null,
        confirmPassword: null,
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
    deleteAccountFail() {
      eventBus.$emit('alert', {key: 'ACCOUNT_DELETED_NOT_SUCCEED', variant: 'info'})
    },
    deleteAccountPass() {
      eventBus.$emit('alert', {key: 'ACCOUNT_DELETED', variant: 'success'})
      this.$store.commit('logout')
      this.$router.push('/')
    },
    sendRequest() {
      apiClient.deleteAccount(this.username, this.form.password).then((response) => {
        if (response) {
          this.deleteAccountPass();
        } else {
          this.deleteAccountFail();
        }
      }).catch(this.deleteAccountFail);
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
    "CONFIRM_HEADER": "Twoje konto zostanie usunięte",
    "CONFIRM_BODY": "Czy na pewno chcesz usunąć konto?"
  },
  "en": {
    "CONFIRM_HEADER": "Your account will be deleted",
    "CONFIRM_BODY": "Are you sure you want to delete the account?"
  }
}</i18n>