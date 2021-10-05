<template>
  <div>
    <b-container class="mt-5 pt-5">
      <b-row align-h="center">
        <b-col v-if="loaded" cols="md-6">
          <validation-observer ref="form" v-slot="{handleSubmit}">
            <b-form @submit.stop.prevent="handleSubmit(submit)">

              <validation-provider v-slot="validationContext"
                                   rules="">
                <b-form-group :label="$t('TITLE') + '*:'" label-align="left">
                  <b-input
                      v-model="form.title"
                      :disabled="!active"
                      :state="getValidationState(validationContext)"
                      name="title"
                  />
                  <b-form-invalid-feedback>{{ validationContext.errors[0] }}</b-form-invalid-feedback>
                </b-form-group>
              </validation-provider>

              <validation-provider v-slot="validationContext"
                                   rules="">
                <b-form-group :label="$t('DESCRIPTION') + '*:'" label-align="left">
                  <b-input
                      v-model="form.description"
                      :disabled="!active"
                      :state="getValidationState(validationContext)"
                      name="description"
                  />
                  <b-form-invalid-feedback>{{ validationContext.errors[0] }}</b-form-invalid-feedback>
                </b-form-group>
              </validation-provider>

              <validation-provider v-slot="validationContext"
                                   rules="">
                <b-form-group :label="$t('VALID_FROM') + '*:'" label-align="left">

                  <date-picker v-model="form.validFrom" :clearable="false" :disabled="!active"
                               :disabled-date="validDateFrom"
                               :disabled-time="validTimeFrom"
                               :minute-step="15"

                               class="date-picker"
                               format="HH:mm"
                               name="validFrom"
                               type="datetime" v-on:change="form.validTo=form.validFrom"></date-picker>

                  <b-form-invalid-feedback>{{ validationContext.errors[0] }}</b-form-invalid-feedback>
                </b-form-group>
              </validation-provider>


              <validation-provider v-slot="validationContext"
                                   rules="">
                <b-form-group :label="$t('VALID_TO') + '*:'" label-align="left">
                  <date-picker v-model="form.validTo" :clearable="false" :disabled="!active"
                               :disabled-date="validDateTo"
                               :disabled-time="validTimeTo"
                               :minute-step="15" class="date-picker"
                               format="HH:mm"
                               name="validTo" type="datetime"></date-picker>
                  <b-form-invalid-feedback>{{ validationContext.errors[0] }}</b-form-invalid-feedback>
                </b-form-group>
              </validation-provider>

              <b-row>
                <b-col class="text-left">
                  <b-btn :disabled="!active" type="submit" variant="primary">{{ $t('UPDATE') }}</b-btn>
                </b-col>
              </b-row>

            </b-form>
          </validation-observer>
        </b-col>
        <b-col v-if="!loaded" cols="md-6">
          <b-container>
            <b-row>
              {{ $t('PLEASE_WAIT') }}
            </b-row>
          </b-container>
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script>

import {apiOffer} from "@/api/offer";
import DatePicker from 'vue2-datepicker';
import 'vue2-datepicker/index.css';

import 'vue2-datepicker/locale/pl';

export default {
  name: "OfferEdit",
  components: {
    DatePicker
  },
  data: () => {
    return {
      loaded: false,
      offer: {},
      form: {
        title: null,
        description: null,
        validFrom: new Date(),
        validTo: null,
        offerAvailabilities: null
      },
    }
  },
  computed: {
    active() {
      return this.offer.active;
    }
  },
  methods: {
    getOfferInfo() {
      apiOffer.getOffer(this.$route.params.id).then(value => {
        this.offer = value;
        this.fillForm();
        this.loaded = true;
      })
    },

    fillForm() {
      this.form.title = this.title();
      this.form.description = this.description();
      this.form.validTo = this.validTo()
      this.form.validFrom = this.validFrom()
      this.form.offerAvailabilities = this.offerAvailabilities();
    },
    getValidationState({dirty, validated, valid = null}) {
      return dirty || validated ? valid : null
    },
    id() {
      return this.offer.id;
    },
    version() {
      return this.offer.version;
    },
    description() {
      return this.offer.description;
    },
    title() {
      return this.offer.title;
    },
    validFrom() {
      return new Date(this.offer.validFrom);
    },
    validTo() {
      return new Date(this.offer.validTo);
    },
    offerAvailabilities() {
      return this.offer.offerAvailabilities;
    },
    /**
     *
     * @param {Date} date
     * @returns {boolean}
     */
    validDateFrom(date) {
      var currentDate = new Date().getTime();
      if (currentDate >= date.getTime()) {
        return true
      }
      return false;
    },
    /**
     *
     * @param {Date} date
     * @returns {boolean}
     */
    validDateTo(date) {
      console.log(date);
      var fromDate = this.form.validFrom;
      if (fromDate.getDate() > date.getDate()) {
        return true;
      } else {
        return false;
      }
    },


    /**
     *
     * @param {Date} date
     * @returns {boolean}
     */
    validTimeFrom(date) {
      var currentDate = new Date().getTime();
      if (currentDate > date.getTime()) {
        return true
      }
      return false;
    },

    /**
     *
     * @param {Date} date
     * @returns {boolean}
     */
    validTimeTo(date) {
      let fromDate = new Date();
      if (this.form.validFrom !== undefined) {
        fromDate = new Date(this.form.validFrom);
      }
      var time = date.getHours() * 60 + date.getMinutes();
      var fromTime = fromDate.getHours() * 60 + fromDate.getMinutes();
      console.log({time, fromTime})
      if (fromTime > time) {
        return true;
      } else {
        return false;
      }
    },

    submit() {
      let editedOfferDTO = {
        title: this.form.title,
        description: this.form.description,
        validFrom: this.form.validFrom.toISOString(),
        validTo: this.form.validTo.toISOString(),
        offerAvailabilities: this.offerAvailabilities(),
        id: this.id(),
        version: this.version()
      };
      let notChanged = editedOfferDTO == this.title();
      notChanged = notChanged && editedOfferDTO.description == this.description();
      notChanged = notChanged && editedOfferDTO.validFrom == this.validFrom();
      notChanged = notChanged && editedOfferDTO.validTo == this.validTo();
      if (notChanged) {
        return;
      }

      apiOffer.editOffer(editedOfferDTO.id, editedOfferDTO.version, editedOfferDTO.title,
          editedOfferDTO.description, editedOfferDTO.validFrom,
          editedOfferDTO.validTo, editedOfferDTO.offerAvailabilities).then(() => {
        this.$router.back();
      })
    },

  },
  mounted() {
    this.getOfferInfo();
  }
}
</script>
<i18n>
{
  "pl": {
    "PLEASE_WAIT": "Proszę czekać",
    "VALID_FROM": "ważne od",
    "VALID_TO": "ważne do",
    "UPDATE": "Zapisz",
    "TITLE": "nazwa",
    "DESCRIPTION": "opis"
  },
  "en": {
    "PLEASE_WAIT": "Please wait",
    "VALID_FROM": "valid from",
    "VALID_TO": "valid to",
    "UPDATE": "Update",
    "TITLE": "title",
    "DESCRIPTION": "description"
  }
}
</i18n>
<style scoped>
.date-picker {
  width: 100%;
}
</style>