import {configure, extend, ValidationObserver, ValidationProvider} from 'vee-validate'
import * as rules from 'vee-validate/dist/rules'
import Vue from 'vue'
import i18n from '@/i18n'

// Install VeeValidate rules
Object.keys(rules).forEach(rule => {
  extend(rule, rules[rule])
})

extend('differentPassword', {
  params: ['oldPassword'],
  validate(value, {oldPassword}) {
    return value !== oldPassword
  },
})

extend('passwordPattern', {
  validate(value) {
    return /^(?!.*[\s])^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$/.test(value)
  },
})

configure({
  defaultMessage: (field, values) => {
    // // override the field name.
    // values._field_ = i18n.t(`FIELDS.${field}`)

    return i18n.t(`VALIDATION.${values._rule_.toUpperCase()}`, values)
  },
})

// Install VeeValidate components globally
Vue.component('ValidationObserver', ValidationObserver)
Vue.component('ValidationProvider', ValidationProvider)
