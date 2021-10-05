import Vue from 'vue'
import Vuex from 'vuex'
import jws from 'jws'
import eventBus from '../event-bus'

Vue.use(Vuex)

let store = new Vuex.Store({
    strict: process.env.NODE_ENV !== 'production', // no state mutation outside mutation handlers (can be slow but safer)
    state: {
        username: null,
        token: null,
        accessLevel: null,
        accessLevels: null,
        tokenExpiry: null,
        askRefresh: false,
        refreshTimeout: null,
        verifierTimeout: null,
    },
    getters: {
        isAuthenticated: state => state.token != null,
        username: state => state.username,
    },
    mutations: {
        setAskRefresh: (state, payload) => state.askRefresh = payload,
        initializeStore: (state) => {
            let newState = localStorage.getItem('ssbd05')
            newState = JSON.parse(newState)
            if (newState != null && newState.token != null) {
                if (newState.tokenExpiry > Date.now()) {
                    Object.assign(state, newState)

                    let timeout = newState.tokenExpiry - Date.now()
                    let refreshTimeoutTimeout = timeout - 300_000

                    state.verifierTimeout = setTimeout(() => {
                        eventBus.$emit('session-ended', newState.token)
                    }, timeout)

                    if (refreshTimeoutTimeout >= 0) {
                        state.refreshTimeout = setTimeout(() => {
                            eventBus.$emit('ask-refresh-session', newState.token)
                        }, refreshTimeoutTimeout)
                    }
                }
            }
        },
        changeAccessLevel: (state, accessLevel) => state.accessLevel = accessLevel,
        logout: (state, token) => {
            if (token == null || token === state.token) {
                state.token = null
                state.accessLevel = null
                state.askRefresh = false

                if (state.verifierTimeout != null) {
                    clearTimeout(state.verifierTimeout)
                    state.verifierTimeout = null
                }
                if (state.refreshTimeout != null) {
                    clearTimeout(state.refreshTimeout)
                    state.refreshTimeout = null
                }
            }
        },

        loginWithToken: (state, token) => {
            let payload = JSON.parse(jws.decode(token).payload)
            state.username = payload.sub
            state.accessLevels = payload.auth.split(',')
            if (state.accessLevels.includes('MANAGEMENT')) {
                state.accessLevel = 'MANAGEMENT'
            } else if (state.accessLevels.includes('CLIENT')) {
                state.accessLevel = 'CLIENT'
            } else {
                state.accessLevel = state.accessLevels[0]
            }
            state.tokenExpiry = payload.exp * 1000
            state.token = token

            let timeout = state.tokenExpiry - Date.now()
            let refreshTimeoutTimeout = timeout - 300_000

            state.verifierTimeout = setTimeout(() => {
                eventBus.$emit('session-ended', token)
            }, timeout)

            if (refreshTimeoutTimeout >= 0) {
                state.refreshTimeout = setTimeout(() => {
                    eventBus.$emit('ask-refresh-session')
                }, refreshTimeoutTimeout)
            }
        },
    },
    actions: {},
})

store.commit('initializeStore')

store.subscribe((mutation, state) => {
    localStorage.setItem('ssbd05', JSON.stringify(state))
})

export default store