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
                      v-model="form.username"
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
                                   :rules="{ required: true, regex: /((?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9])(?=.{8,}))/ }">
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
              <validation-provider v-slot="validationContext"
                                   rules="max:256">
                <b-form-group :label="$t('DESCRIPTION') + ':'" label-align="left">
                  <b-input id="description"
                           v-model="form.description"
                           :state="getValidationState(validationContext)"
                           name="description"/>
                  <b-form-invalid-feedback>{{ validationContext.errors[0] }}</b-form-invalid-feedback>
                </b-form-group>
              </validation-provider>
              <!--              TODO: TEN WALIDATOR NIE DZIAŁA-->
              <!--              TODO: Odkomentować jesli nadawanie oceny przy tworzeniu ma sens-->
              <!--              <validation-provider v-slot="validationContext"-->
              <!--                                   :rules="{ decimal: [2, ','], min_value: { value: 0, separator: ',' }, max_value: { value: 10, separator: ',' }}">-->
              <!--                <b-form-group :label="$t('AVERAGE_RATING') + ':'" label-align="left">-->
              <!--                  <b-input id="description"-->
              <!--                           v-model="form.avg_rating"-->
              <!--                           :state="getValidationState(validationContext)"-->
              <!--                           name="average_rating"/>-->
              <!--                  <b-form-invalid-feedback>{{ validationContext.errors[0] }}</b-form-invalid-feedback>-->
              <!--                </b-form-group>-->
              <!--              </validation-provider>-->

              <b-row>
                <b-col class="text-left">
                  <b-btn type="submit" variant="primary">{{ $t('REGISTER') }}</b-btn>
                </b-col>
              </b-row>

            </b-form>
          </validation-observer>
        </b-col>
      </b-row>
    </b-container>

    <b-modal v-model="showModal" :cancel-title="$t('NO')" :ok-title="$t('YES')" :title="$t('CONFIRM_REGISTER')"
             @ok="handleOk">
      <b-row>
        <b-col>{{ $t('USERNAME') }}:</b-col>
        <b-col>{{ this.form.username }}</b-col>
      </b-row>
      <b-row>
        <b-col>{{ $t('EMAIL') }}:</b-col>
        <b-col>{{ this.form.email }}</b-col>
      </b-row>
      <b-row>
        <b-col>{{ $t('DESCRIPTION') }}:</b-col>
        <b-col>{{ this.form.description }}</b-col>
      </b-row>
      <!--              TODO: Odkomentować jesli nadawanie oceny przy tworzeniu ma sens-->
      <!--      <b-row>-->
      <!--        <b-col>{{ $t('AVERAGE_RATING') }}:</b-col>-->
      <!--        <b-col>{{ this.form.avg_rating }}</b-col>-->
      <!--      </b-row>-->
      <b-row>
        <b-col>{{ $t('MODAL_CONFIRMATION') }}</b-col>
      </b-row>
    </b-modal>
  </div>
</template>

<script>
import eventBus from "@/event-bus";
import {api} from "@/api";

export default {
  name: "RegisterEntertainer",
  data() {
    return {
      showModal: false,
      form: {
        username: null,
        email: null,
        password: null,
        confirm_password: null,
        description: null,
        avg_rating: null,
      },
    }
  },
  methods: {
    submit() {
      this.showModal = true;
    },
    registerEntertainer() {
      api.post('entertainer/add/' + this.form.password, {
        login: this.form.username,
        email: this.form.email,
        description: this.form.description,
        avgRating: this.form.avg_rating
      }).then(() => {
        eventBus.$emit('alert', {key: 'SUCCESSFUL_REGISTRATION', variant: 'success'})
        this.$router.push('/')
      }).catch()
    },
    getValidationState({dirty, validated, valid = null}) {
      return dirty || validated ? valid : null
    },
    showOrHidePassword(name) {
      const x = document.getElementById(name);

      if (x.type === "password") {
        x.type = "text";
      } else {
        x.type = "password";
      }
    },
    recaptcha() {
      this.$recaptcha('submit').then(() => {})
    },
    handleOk() {
      this.registerEntertainer()
      this.recaptcha()
    },
  }
}
</script>

<style scoped>

</style>
<i18n>{
  "pl": {
    "CONFIRM_REGISTER": "Tworzysz pracownika o poniższych danych",
    "USERNAME": "Nazwa użytkownika",
    "PASSWORD": "Hasło"
  },
  "en": {
    "CONFIRM_REGISTER": "Create entertainer with following data",
    "USERNAME": "Username",
    "PASSWORD": "Password"
  }
}</i18n>