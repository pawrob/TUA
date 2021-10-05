<template>
  <div>
    <b-container>
      <h2 class="star mt-5 mb-3"> {{ offer.title }}</h2><br>
      <h3> {{ offer.description }}</h3><br>
      <star-rating class="star mt-2 mb-3" v-model="offer.avgRating" increment="0.1" read-only
                                                        star-size="30"/>
      <b-row>
        <b-col>
          {{ $t('ACTIVE') }}
        </b-col>
        <b-col>
          <bool-badge :active="offer.active"/>
        </b-col>
      </b-row>

      <b-row>
        <b-col>
          {{ $t('VALID_FROM') }}
        </b-col>
        <b-col>
          {{ offer.validFrom | formatDate }}
        </b-col>
      </b-row>
      <b-row>
        <b-col>
          {{ $t('VALID_TO') }}
        </b-col>
        <b-col>
          {{ offer.validTo | formatDate }}
        </b-col>
      </b-row>
      <br>

      <b-row>
        <b-col>
          <b-btn type="button" v-b-hover="handleHover" v-on:click="refreshOffer" class="m-3" variant="outline-primary">
            <b-icon v-if="isHovered" icon="arrow-clockwise" animation="spin" font-scale="1"
                    class="slow-transition"></b-icon>
            <b-icon v-else icon="arrow-clockwise" animation="none" font-scale="1"></b-icon>
            {{ $t('REFRESH') }}
          </b-btn>
          <b-btn type="button" v-on:click="goToExactEntertainer(offer.entertainer.id)" class="m-3"
                 variant="outline-primary">
            {{ $t('LEARN_ABOUT_ENTERT') }}
          </b-btn>
          <b-btn v-on:click="reserveOffer(offer.id)" class="m-3" variant="outline-primary">
            <b-icon scale="0.75" icon="calendar3"></b-icon>
            {{ $t('BOOK') }}
          </b-btn>
        </b-col>

      </b-row>
    </b-container>
    <b-container>
      <b-card-group class="m-4 w-75 mx-auto align-center"  deck>
        <RatingCard
            class="m-5 card mx-auto"
            style="max-width: 30%"
            v-for="rating in notNullReservations"
            :key="rating.id"
            :rating="rating" />
      </b-card-group>
    </b-container>
  </div>
</template>

<script>
import {api} from "@/api";
import ItemTemplate from "@/components/ItemTemplate";
import BoolBadge from "@/components/BoolBadge";
import RatingCard from "@/components/RatingCard";
import StarRating from "vue-star-rating";

export default {
  name: "Offer",
  components: {RatingCard, BoolBadge,StarRating},
  data: () => {
    return {
      offer: {},
      limit: 3,
      itemTemplate: ItemTemplate,
      isHovered: false,
      showLoading: false,
      reservations: [],
      notNullReservations: [],
      tableRatingsFields: [
        {
          key: 'rating',
          sortable: true,
          label: 'Rating',
        },
        {
          key: 'comment',
          sortable: true,
          label: 'Comment',
        },
      ],
      reservationsFields: [
        {
          key: 'id',
          sortable: true,
          label: 'Id',
        },
        {
          key: 'client',
          sortable: true,
          label: 'Client',
        }, {
          key: 'offer',
          sortable: true,
          label: 'Offer',
        }, {
          key: 'reservationFrom',
          sortable: true,
          label: 'Reservation from',
        }, {
          key: 'reservationTo',
          sortable: true,
          label: 'Reservation to',
        },
        {
          key: 'status',
          sortable: true,
          label: 'Status',
        }
      ],
    }
  },
  methods: {
    handleHover(hovered) {
      this.isHovered = hovered
    },
    refreshOffer() {
      this.showLoading = true;
      api.get('moo/offer/withDetails/' + this.$route.params.id).then(response => {
        this.showLoading = false;
        this.offer = response.data;
        this.reservations = this.offer.reservations
        this.filterNullComments(this.reservations)
      }).catch(() => {
      })

    },
    goToExactEntertainer(id) {
      this.$router.push('/entertainerPreview/' + id)
    },
    filterNullComments(reservations) {
      reservations.forEach((x) => {
        if (x.comment != null) {
          this.notNullReservations.push(x)
        }
      });
      console.log(this.notNullReservations)
    },
    reserveOffer(offerId) {
        const path = '/offers/' + offerId + "/reservation"
        this.$router.push(path)
    }

  },
  mounted() {
    this.refreshOffer()
  },
  computed: {
    computedObj() {
      return this.limit ? this.offers.slice(0, this.limit) : this.offers
    }
  }
}
</script>
<i18n>
{
  "pl": {
    "UPDATE": "Zaktualizuj",
    "CONFIRM_HEADER": "Zmiana informacji o ofercie",
    "CONFIRM_BODY": "Czy na pewno chcesz zmodyfikować informacje o ofercie?"
  },
  "en": {
    "UPDATE": "Update",
    "CONFIRM_HEADER": "Change offer information",
    "CONFIRM_BODY": "Do You really want to modify offer information?"
  }
}
</i18n>
<style scoped>
.slow-transition {
  transition: 1s;
}
.card  {
  max-width: 25%;
}
.star {
  display: inline-block;
  text-align: center;
  margin-left: auto;
  margin-right: auto;
}
</style>