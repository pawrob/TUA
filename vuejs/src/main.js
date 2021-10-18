import Vue from 'vue'
import VueSuggestion from 'vue-suggestion'
import App from './App.vue'
import VueMeta from 'vue-meta'
import {BootstrapVue, IconsPlugin} from 'bootstrap-vue'
import i18n from './i18n'
import router from './router'
import store from './store'

import { VueReCaptcha } from 'vue-recaptcha-v3'
// noinspection ES6UnusedImports
import {} from '@/vee-validate'
import VueMarkdownEditor from '@kangc/v-md-editor';
import '@kangc/v-md-editor/lib/style/base-editor.css';
import vuepressTheme from '@kangc/v-md-editor/lib/theme/vuepress.js';
import enUS from '@kangc/v-md-editor/lib/lang/en-US';

VueMarkdownEditor.lang.use('en-US', enUS);

VueMarkdownEditor.use(vuepressTheme);



Vue.use(VueMarkdownEditor);

Vue.config.productionTip = false
// Make BootstrapVue available throughout your project
Vue.use(BootstrapVue)
// Optionally install the BootstrapVue icon components plugin
Vue.use(IconsPlugin)
// Vue suggestions
Vue.use(VueSuggestion)


Vue.filter('formatDate', function(value) {
  console.log(value);
  if (value) {
    return new Date(value)
  }
})
Vue.filter('formatTime', function(value) {
  if (value) {
    let slice = value.split(':');
    let offset = 0;
    if(slice[2][2]==='+')
      offset += parseInt(slice[2].substring(3,5))
    if(slice[2][2]==='-')
      offset -= parseInt(slice[2].substring(3,5))
    let diff = new Date().getTimezoneOffset() /60 + offset
    let hour = parseInt(slice[0]) + diff;
    return hour + ':' + slice[1]
    }

})

Vue.use(VueMeta)
// Google ReCaptcha
Vue.use(VueReCaptcha, { siteKey: '6LdVE_MaAAAAAHczvKCSaMrkg8o9vNrOrbpIkwky' })

new Vue({
  i18n,
  router,
  store,
  render: h => h(App),
}).$mount('#app')
