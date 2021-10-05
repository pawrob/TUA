<template>
  <div id="app">
    <my-menu/>
    <alerts/>
    <transition :name="transitionName">
      <router-view class="w-100"/>
    </transition>
    <b-modal v-model="askRefresh" :cancel-title="$t('NO')" :ok-title="$t('YES')" :title="$t('MODAL_HEADER')"
             @ok="refreshToken">
      {{ $t('MODAL_BODY') }}
    </b-modal>
  </div>
</template>

<script>
import MyMenu from '@/components/MyMenu'
import Alerts from '@/components/Alerts'
import {api} from '@/api'
import eventBus from '@/event-bus'

export default {
  name: 'App',
  components: {
    Alerts,
    MyMenu,
  },
  data() {
    return {
      transitionName: null,
      showTokenRefreshDialog: false,
    }
  },
  computed: {
    askRefresh: {
      get() {
        return this.$store.state.askRefresh
      },
      set(value) {
        this.$store.commit('setAskRefresh', value)
      },
    },
  },
  methods: {
    refreshToken() {
      api.post('/auth/refresh').then(response => {
        this.$store.commit('loginWithToken', response.data)
        eventBus.$emit('alert', {key: 'REFRESHED', variant: 'success'})
      }).catch()
    },
  },
  metaInfo() {
    return {
      // if no subcomponents specify a metaInfo.title, this title will be used
      title: this.$t('TITLE'),
      // all titles will be injected into this template
      titleTemplate: '%s | ' + this.$t('APP_NAME'),
    }
  },
  watch: {
    '$route'(to, from) {
      const toDepth = to.path.split('/').filter(p => p !== '').length
      const fromDepth = from.path.split('/').filter(p => p !== '').length
      this.transitionName = toDepth < fromDepth ? 'slide-right' : 'slide-left'
    }
  },
}
</script>

<style lang="scss">
@use "sass:color";
@import "assets/custom";

.slide-left-enter-active,
.slide-left-leave-active,
.slide-right-enter-active,
.slide-right-leave-active {
  transition: all .3s ease-in-out;
}

.slide-right-enter, .slide-left-leave-to {
  transform: translateX(-100%);
  position: absolute;
}

.slide-right-leave-to, .slide-left-enter {
  transform: translateX(100%);
  position: absolute;
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  //color: $dark;
  //color: $primary;
  //color: #2c3e50;
  /*margin-top: 60px;*/
  //background: linear-gradient($primary, color.scale($primary, $lightness: 20%));
}

a {
  //color: $primary;
  //color: $secondary;
}
</style>

<i18n>{
  "pl": {
    "TITLE": "Strona główna",
    "MODAL_HEADER": "Sesja zakończy się za mniej niż 5 minut",
    "MODAL_BODY": "Gdy zakończy się sesja, nastąpi wylogowanie z konta. Czy chcesz ją przedłużyć?"
  },
  "en": {
    "TITLE": "Home page",
    "MODAL_HEADER": "Session will end in less than 5 minutes",
    "MODAL_BODY": "When session ends, you will be logged out. Do you want to renew it?"
  }
}</i18n>

