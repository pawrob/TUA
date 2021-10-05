<template>
  <div id="front-card" class="vh-100">
    <b-container class="h-100">
      <b-row class="h-100">
        <b-col class="my-auto">
          <h1 v-if="this.$store.state.accessLevel==null" class="text-light">{{ $t("APP_NAME") }}</h1>
          <h1  v-else class="text-light">{{ $t("WELCOME") }}, {{this.$store.state.username}}</h1>
          <hr class="my-8"/>
          <h5 class="text-light mt-4">

            {{$t('WELCOME_TEXT')}}
          </h5>
          <b-button-group v-if="this.$store.state.accessLevel==null" class="mt-3">
            <b-button pill @click="goToLogin" class="m-3 custom" size="lg" variant="outline-light">{{ $t('LOGIN') }}</b-button>
            <b-button pill  @click="goToRegister" class="m-3 custom" size="lg" variant="outline-light">{{ $t('REGISTER') }}</b-button>
          </b-button-group>
          <b-button-group v-else class="mt-3">
            <b-button pill @click="goToOffers" class="m-3 custom" size="lg" variant="outline-light">{{ $t('OFFERS') }}</b-button>
          </b-button-group>
        </b-col>
      </b-row>


      <b-row>


      </b-row>

    </b-container>
    <div >
      <b-card-group class="m-4 w-75 mx-auto align-center" deck>
        <OfferCard
            class="m-5 card mx-auto"
            style="max-width: 25rem; "
            v-for="offer in computedObj"
            :key="offer.id"
            :offer="offer"

        />
      </b-card-group>
    </div>
  </div>


</template>

<script>
import {apiManager} from "@/api/manager";
import OfferCard from "@/components/OfferCard";

export default {
  name: "FrontCard",

  components: {OfferCard},
  data() {
    return {
      offers: [],
      limit: 3
    }
  },
  methods: {
    goToLogin() {
        this.$router.push('/login')
    },
    goToRegister() {
        this.$router.push('/register')
    },
    goToOffers() {
      this.$router.push('/offers')
    },
    refreshOffers() {
      this.showLoading = true;
      apiManager.getAllOffers().then(value => {
        this.showLoading = false;
        this.offers = value.sort((a, b) => b.avgRating ?? 0 - a.avgRating ?? 0)
      })
    },
  },
  mounted() {
    this.refreshOffers()
  },
  computed: {
    computedObj() {
      return this.limit ? this.offers.slice(0, this.limit) : this.offers
    }
  }
}
</script>

<style lang="scss">
@use "sass:color";
@import "../assets/custom";

#front-card {
  background-image: url("../assets/bg_front_card.jpg");
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-attachment: fixed;
  box-shadow: inset 0 0 0 2000px rgba(0, 0, 0, 0.5);

  //background: linear-gradient($primary, color.scale($primary, $lightness: 20%));
}

.card{
  border-radius: 4px;
  background: #fff;
  box-shadow: 0 6px 10px rgba(0,0,0,.08), 0 0 6px rgba(0,0,0,.05);
  transition: .3s transform cubic-bezier(.155,1.105,.295,1.12),.3s box-shadow,.3s -webkit-transform cubic-bezier(.155,1.105,.295,1.12);
  cursor: pointer;
}

.card:hover{
  transform: scale(1.03);
  box-shadow: 0 10px 20px rgba(0,0,0,.12), 0 4px 8px rgba(0,0,0,.06);
}

.custom {
  width: 160px !important;
  height: 60px !important;
}

hr {
  margin-top: 1rem;
  margin-bottom: 1rem;
  width: 45%;
  border-top: 2px solid rgb(255, 255, 255) !important;
}
</style>