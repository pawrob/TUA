<template>
  <b-navbar v-bind="theme" class="shadow" sticky toggleable="lg">
    <b-container>
      <b-navbar-brand :to="brandLinkTo" class="mr-0">
        <img alt="Logo" class="mr-2" height="30px" src="../assets/clown.png"/>
        {{ $t('APP_NAME') }}
      </b-navbar-brand>

      <b-navbar-nav class="mr-auto flex-row">
      <span v-for="(item, i) in breadcrumb" :key="i" class="d-inline-flex">
        <span class="separator mx-2 my-auto" style="font-size: .8rem;"> / </span>
        <b-nav-item :to="item.path" class="my-auto">
          {{ $t(item.name) }}
        </b-nav-item>
      </span>
      </b-navbar-nav>

      <b-navbar-toggle target="nav-collapse"/>
      <b-collapse id="nav-collapse" is-nav>

        <!--to the right-->
        <b-navbar-nav class="ml-auto">
          <!--level-->
          <b-nav-item-dropdown v-if="isAuthenticated && accessLevels.length > 1" right>
            <template #button-content>
              {{ $t(accessLevel) }}
            </template>
            <b-dropdown-item v-for="level in accessLevels" :key="level"
                             :disabled="level === accessLevel"
                             @click="changeAccessLevel(level)">
              {{ $t(level) }}
            </b-dropdown-item>
          </b-nav-item-dropdown>
          <b-nav-text v-else>{{ $t(accessLevel) }}</b-nav-text>
          <!--lang-->
          <b-nav-item-dropdown right>
            <template #button-content>
              <b-icon icon="globe"/>
              {{ $t('LANG') }}
            </template>
            <b-dropdown-item @click="$root.$i18n.locale='pl'">
              <country-flag country="pl"/>
              PL
            </b-dropdown-item>
            <b-dropdown-item @click="$root.$i18n.locale='en'">
              <country-flag country="gb"/>
              EN
            </b-dropdown-item>
          </b-nav-item-dropdown>
          <!--login/user-->
          <b-nav-item-dropdown v-if="isAuthenticated" right>
            <template #button-content>{{ username }}</template>
            <b-dropdown-item  v-if="this.accessLevel === ('CLIENT')" to="/reservations">{{ $t('RESERVATIONS') }}</b-dropdown-item>
            <b-dropdown-item  v-if="this.accessLevel === ('CLIENT')" to="/client/favorite-offers">{{ $t('FAVORITE_OFFERS') }}</b-dropdown-item>
            <b-dropdown-item to="/account">{{ $t('MANAGE_ACCOUNT') }}</b-dropdown-item>
            <b-dropdown-item @click="logout">{{ $t('LOGOUT') }}</b-dropdown-item>
          </b-nav-item-dropdown>
          <b-nav-item v-else to="/login">{{ $t('LOGIN') }}</b-nav-item>
        </b-navbar-nav>
      </b-collapse>
    </b-container>
  </b-navbar>
</template>

<script>
import CountryFlag from 'vue-country-flag'
import {mapGetters, mapState} from 'vuex'
import eventBus from '../event-bus'
import {api} from "@/api";
import {apiOffer} from "@/api/offer";

export default {
  name: "MyMenu",
  components: {
    CountryFlag
  },
  computed: {
    ...mapState([
      'accessLevel',
      'accessLevels',
    ]),
    ...mapGetters([
      'isAuthenticated',
      'username'
    ]),
    theme() {
      return {
        CLIENT: {
          variant: 'primary',
          type: 'dark'
        },
        MANAGEMENT: {
          variant: 'warning',
        },
        ENTERTAINER: {
          variant: 'info',
        },
      }[this.accessLevel || 'CLIENT']
    },
    brandLinkTo() {
      return '/'
    },
    breadcrumb() {
      return this.$route.meta.breadcrumb
    },
  },
  methods: {
    todo_remove() {//TODO remove
      /*apiReservation.editReservation(-73, 1,
          "2016-10-25T12:00-06:00",
          "2016-10-26T12:00-06:00", "Kebab");*/
      apiOffer.editOffer(-52, 1,
          "Taniec na kolanie",
          "Obroty, piruety",
          true,
          "2016-10-25T12:00-06:00",
          "2016-10-26T12:00-06:00",
          []
      )
    },
    changeAccessLevel(level) {
      api.post('user/logChangeAccessLevel', {
        login: this.$store.state.username,
        accessLevel: level
      }).catch();
      this.$store.commit('changeAccessLevel', level)
      this.$router.push(level === 'CLIENT' ? '/' : '/' + level.toLowerCase())
    },
    logout() {
      this.$store.commit('logout')
      eventBus.$emit('alert', {key: 'LOGOUT', variant: 'success'})
      this.$router.push('/')
    }
  },
}
</script>

<i18n>{
  "pl": {
    "LANG": "Język"
  },
  "en": {
    "LANG": "Language"
  }
}</i18n>

<style lang="scss" scoped>
@use "sass:color";
@import "../assets/custom";

.nav-link {
  @extend .px-0;
}

.navbar {
  &.navbar-dark {
    .separator {
      color: color.change($white, $alpha: 0.5);
    }

    .navbar-nav .nav-link.router-link-exact-active {
      color: $white;
    }
  }

  .separator {
    color: color.change($black, $alpha: 0.5);
  }

  .navbar-nav .nav-link.router-link-exact-active {
    color: $black;
  }
}
</style>