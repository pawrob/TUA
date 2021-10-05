<template>
  <div>
    <b-container>
      <h1>{{ $t('FAVORITE_OFFERS') }}</h1>
      <b-row>
        <b-col>
          <b-overlay
              :show="showLoading"
              rounded="sm">
            <b-table :fields="tableOffersFields" :items="offers" hover striped>
              <template #cell(title)="data">
                <b-link :to="'/offers/' + data.item.id">{{ data.value }}</b-link>
              </template>
              <template #cell(validFrom)="data">
                {{ data.value | formatDate }}
              </template>
              <template #cell(validTo)="data">
                {{ data.value | formatDate }}
              </template>
              <template #cell(actionKeys)="data">
                <b-button @click="reserveOffer(data.item.id)" size="sm" class="mb-1" variant="outline-primary">
                  <b-icon scale="0.75" icon="calendar3"></b-icon>
                  {{ $t('BOOK') }}
                </b-button>
                <b-button size="sm" class="mb-1 actionBtn bg-transparent">
                  <b-icon alt="Add to favorites" icon='star-fill' @click="removeFavourite(data.item.id)"></b-icon>
                </b-button>
              </template>
            </b-table>
          </b-overlay>
        </b-col>
      </b-row>

      <div>
        <b-button v-b-hover="handleHover" variant="outline-primary" v-on:click="refreshOffers">
          <b-icon v-if="isHovered" icon="arrow-clockwise" animation="spin" font-scale="1" class="slow-transition"></b-icon>
          <b-icon v-else icon="arrow-clockwise" animation="none" font-scale="1"></b-icon>
          {{ $t('REFRESH') }}
        </b-button>

      </div>

    </b-container>
  </div>
</template>

<script>
import {apiManager} from '@/api/manager'
import {api} from "@/api";
//import Vue from "vue";


export default {
  name: 'Offers',

  data() {
    return {
      isHovered: false,
      showLoading: false,
      activeOffers: [],
      offers: [],
      tableOffersFields: [

      ],
    }
  },
  beforeUpdate() {
    this.updateHeaders()
  },
  methods: {
    updateHeaders(){
      this.tableOffersFields = [
        {
          key: 'title',
          sortable: true,
          label: this.$i18n.t('TITLE'),
        },
        {
          key: 'validFrom',
          sortable: true,
          label: this.$i18n.t('VALID_FROM'),
        },
        {
          key: 'validTo',
          sortable: true,
          label: this.$i18n.t('VALID_TO'),
        },
        {
          key: 'actionKeys',
          label: this.$i18n.t('ACTIONS'),
        },
      ]
    },
    removeFavourite(id){
      api.delete('/moo/offer/'+id+'/delete-favorite').then(() => this.refreshOffers()).catch(() => {})
    },
    handleHover(hovered) {
      this.isHovered = hovered
    },
    refreshOffers() {
      this.showLoading = true;
      apiManager.getFavoriteOffers().then(value => {
        this.showLoading = false;
        this.offers = value;
      })
    },
    reserveOffer(offerId) {
      const path = '/offers/' + offerId + "/reservation"
      console.log(path)
      this.$router.push(path)
    }
  },
  mounted() {
    this.refreshOffers()
  },
}
</script>

<style scoped>
.slow-transition {
  transition: 1s;
}
</style>