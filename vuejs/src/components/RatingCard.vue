<template>
  <b-card>
    <template>
      <star-rating class="star mt-2 mb-3" v-model="rating.rating" increment="0.1" read-only star-size="30"/>
    </template>
    <b-card-text>
      {{ rating.comment }}
    </b-card-text>

  </b-card>
</template>

<script>
import StarRating from 'vue-star-rating'

export default {
  name: "RatingCard",
  props: ['rating'],
  components: {
    StarRating
  },
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