import axios from 'axios'
import eventBus from '@/event-bus'
import store from '@/store'

export const api = axios.create({
    baseURL: '/ssbd05/resources/',
})

api.interceptors.response.use(response => {
    return response
}, error => {
    if (error.response == null) {
        eventBus.$emit('alert', {key: 'CONNECTION_ERROR', variant: 'danger'})
    } else {
        let responseClass = Math.floor(error.response.status / 100)
        if (error.response.data.key != null) {
            eventBus.$emit('alert', {key: error.response.data.key, variant: 'danger'})
        } else if (responseClass === 5) {
            eventBus.$emit('alert', {key: 'SERVER_ERROR', variant: 'danger'})
        } else if (error.response.status === 401) {
            eventBus.$emit('alert', {key: 'UNAUTHENTICATED', variant: 'danger'})
        } else if (error.response.status === 403) {
            eventBus.$emit('alert', {key: 'UNAUTHORIZED', variant: 'danger'})
        } else {
            eventBus.$emit('alert', {key: 'UNKNOWN_ERROR', variant: 'danger'})
        }
    }
    throw error
})

api.interceptors.request.use(request => {
    let token = store.state.token
    if (token != null) request.headers['Authorization'] = `Bearer ${token}`

    return request
}, error => {
    throw error
})
