<template>
  <div>
    <b-container>
      <h1>{{ $t('OFFERS') }}</h1>
      <b-row>
        <b-col>
          <b-overlay
              :show="showLoading"
              rounded="sm">
            <b-table :fields="tableOffersFields" :items="offers" hover striped>
              <template #cell(title)="data">
                <b-link @click="redirectToHomePage(data.item)" >{{ data.value }}</b-link>
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
                  <b-icon alt="Add to favorites" :icon="data.item.isFavorite?'star-fill':'star'" @click="!data.item.isFavorite?addFavourite(data.item.id):removeFavourite(data.item.id)"></b-icon>
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
import Vue from "vue";
import eventBus from '@/event-bus'


export default {
  name: 'Offers',

  data() {
    return {
      isHovered: false,
      showLoading: false,
      showAddModal: false,
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
    addFavourite(id){
      api.post('/moo/offer/'+id+'/add-favorite').then(() => this.refreshFavourites()).catch(() => {})
    },
    removeFavourite(id){
      api.delete('/moo/offer/'+id+'/delete-favorite').then(() => this.refreshFavourites()).catch(() => {})
    },
    refreshFavourites(){
      api.get('moo/offer/favorites/').then(response => {
        this.showLoading = false;
        for (const r of this.offers) {
          let bool = false;
          for (const f of response.data) {
            if(r.id === f.id){
              bool=true
            }
          }
          Vue.set(r, "isFavorite", bool)
        }}).catch(() => {})
    },
    handleHover(hovered) {
      this.isHovered = hovered
    },
    redirectToHomePage: function (data) {
      if(this.$store.state.accessLevel == null){
        this.$router.push('/login')
        eventBus.$emit('alert', {key: 'UNAUTHORIZED', variant: 'danger'})
      }
      else
        this.$router.push('offers/' + data.id)

    },
    refreshOffers() {
      this.showLoading = true;
      apiManager.getAllOffers().then(value => {
        this.showLoading = false;
        this.offers = value;
        this.refreshFavourites();
      })

    },
    reserveOffer(offerId) {
      if (this.$store.state.accessLevel === null) {
        this.$router.push('/login')
        eventBus.$emit('alert', {key: 'UNAUTHENTICATED', variant: 'success'})
      }
      else {
        const path = '/offers/' + offerId + "/reservation"
        console.log(path)
        this.$router.push(path)
      }
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