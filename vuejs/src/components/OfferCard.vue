<template>
    <b-card @click="redirectToOfferOrLoginPage(offer.id)" :title="offer.title" img-src="https://picsum.photos/300/300/?image=41" img-alt="Image" img-top>
      <template>
        <star-rating class="star mt-2 mb-3" v-model="avgRating" :increment="0.1" read-only :star-size="30"/>
      </template>
      <b-card-text>
        {{ offer.description }}
      </b-card-text>
      <b-button class="mt-2" variant="primary">
        <b-icon scale="0.75" icon="calendar3"></b-icon>
        {{ $t('BOOK') }}
      </b-button>
    </b-card>
</template>

<script>
import StarRating from 'vue-star-rating'

export default {
  name: "OfferCard",
  props: ['offer'],
  components: {
    StarRating
  },
  methods: {
    redirectToOfferOrLoginPage: function (data) {
        this.$router.push('offers/' + data)
    },
  },
  computed: {
    avgRating() {
      return this.offer.avgRating ?? 0
    },
    computedObj() {
      return this.limit ? this.offers.slice(0, this.limit) : this.offers
    },
  }
}
</script>

<style scoped>

.star {
  display: inline-block;
  text-align: center;
  margin-left: auto;
  margin-right: auto;
}
</style>