<template>
  <div>
    <ValidationObserver v-slot="{ invalid }">
      <b-container>
        <b-row>
          <b-col>
            {{ $t('NAME') }}:
          </b-col>
          <b-col>
            <validation-provider v-slot="validationContext"
                                 rules="alpha_spaces|max:30">
              <b-input v-model="user.personalData.name" :state="getValidationState(validationContext)"/>
              <b-form-invalid-feedback>{{ $t('NAME_VALIDATION') }}</b-form-invalid-feedback>
            </validation-provider>
          </b-col>
        </b-row>
        <b-row>
          <b-col>
            {{ $t('SURNAME') }}:
          </b-col>
          <b-col>
            <validation-provider v-slot="validationContext"
                                 rules="alpha_spaces|max:30">
              <b-input v-model="user.personalData.surname" :state="getValidationState(validationContext)"/>
              <b-form-invalid-feedback>{{ $t('SURNAME_VALIDATION') }}</b-form-invalid-feedback>
            </validation-provider>
          </b-col>
        </b-row>
        <b-row>
          <b-col>
            {{ $t('IS_MAN') }}:
          </b-col>
          <b-col>
              <b-checkbox switch v-model=user.personalData.isMan></b-checkbox>
          </b-col>
        </b-row>
        <b-row>
          <b-col>
            {{ $t('PHONE_NUMBER') }}:
          </b-col>
          <b-col>
            <validation-provider v-slot="validationContext"
                                 rules="numeric|min:9|max:15">
              <b-input v-model="user.personalData.phoneNumber" :state="getValidationState(validationContext)"/>
              <b-form-invalid-feedback>{{ $t('PHONE_VALIDATION') }}</b-form-invalid-feedback>
            </validation-provider>
          </b-col>
        </b-row>
        <b-row>
          <b-col>
            {{ $t('LANGUAGE') }}:
          </b-col>
          <b-col>
            <validation-provider v-slot="validationContext"
                                 rules="required|alpha|min:1|max:3">
              <b-input v-model="user.personalData.language" :state="getValidationState(validationContext)"/>
              <b-form-invalid-feedback>{{ $t('LANGUAGE_VALIDATION') }}</b-form-invalid-feedback>
            </validation-provider>
          </b-col>
        </b-row>
        <b-row>
          <b-col>
            <b-btn type="button" v-on:click="refreshData">{{ $t("RERFESH") }}</b-btn>
          </b-col>
          <b-col>
            <b-btn type="button" :disabled="invalid" v-on:click="submit">{{ $t("SUBMIT") }}</b-btn>
          </b-col>
        </b-row>
      </b-container>
    </ValidationObserver>
    <b-modal v-model="showModal" :cancel-title="$t('NO')" :ok-title="$t('YES')" :title="$t('CONFIRM_PERSONAL_DATA')"
             @ok="handleOk">
      {{ $t('MODAL_CONFIRMATION') }}
    </b-modal>
  </div>
</template>

<script>
import {api} from "@/api";
import eventBus from "@/event-bus";

export default {
  name: "ChangePersonalData",
  data() {
    return {
      showModal: false,
      user: {},
      accessLevelsFields: [
        {
          key: 'accessLevel',
          sortable: true,
          label: 'Access Level',
        },
        {
          key: 'isActive',
          sortable: true,
          label: 'isActive',
        }],
    }
  },
  methods: {
    refreshData() {
      api.get('user/self').then(response => {
        this.user = response.data;
        if (response.data.personalData.isMan == null)
          this.user.personalData.isMan = false
      }).catch(() => {});
    },
    switchGender() {
      this.user.personalData.isMan = !this.user.personalData.isMan;
    },
    updatePersonalData() {
      api.put('user/update/' + this.user.id, this.user.personalData).then(() => {
        eventBus.$emit('alert', {key: 'DATA_UPDATED', variant: 'success'});
        this.refreshData();
      });
    },
    getValidationState({dirty, validated, valid = null}) {
      return dirty || validated ? valid : null
    },
    submit() {
      this.showModal = true;
    },
    recaptcha() {
      this.$recaptcha('submit').then(() => {})
    },
    handleOk() {
      this.updatePersonalData()
      this.recaptcha()
    },
  },
  mounted() {
    this.refreshData();
  },
}
</script>

<style scoped>

</style>
<i18n>{
  "pl": {
    "CONFIRM_PERSONAL_DATA": "Edytujesz dane personalne",
    "SUBMIT": "Zatwierdź",
    "NAME_VALIDATION": "Imię musi być krótsze niż 30 znaków",
    "SURNAME_VALIDATION": "Nazwisko musi być krótsze niż 30 znaków",
    "PHONE_VALIDATION": "Podaj prawidłowy numer telefonu",
    "LANGUAGE_VALIDATION": "Podaj język jako 3-literowy symbol",
    "RERFESH": "Odśwież"
  },
  "en": {
    "CONFIRM_PERSONAL_DATA": "Changing personal data",
    "SUBMIT": "Submit",
    "NAME_VALIDATION": "Name must not be longer than 30 characters",
    "SURNAME_VALIDATION": "Surname must not be longer than 30 characters",
    "PHONE_VALIDATION": "Type correct phone number",
    "LANGUAGE_VALIDATION": "Type language as 3-character symbol",
    "RERFESH": "Refresh"
  }
}</i18n>