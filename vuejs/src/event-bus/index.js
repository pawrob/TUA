import Vue from "vue"
import store from '../store'
import router from '../router'

let eventBus = new Vue()

eventBus.$on('session-ended', token => {
  if (store.state.token === token) {
    store.commit('logout', token)
    eventBus.$emit('alert', {key: 'SESSION_ENDED', variant: 'info'})
    router.push('/')
  }
})

eventBus.$on('ask-refresh-session', () => {
  store.commit('setAskRefresh', true)
})

export default eventBus