import Vue from 'vue'
import VueI18n from 'vue-i18n'
import pl from './pl.json'
import en from './en.json'

Vue.use(VueI18n)

export default new VueI18n({
    messages: {pl, en},
    locale: 'pl',
    silentFallbackWarn: true,
})
