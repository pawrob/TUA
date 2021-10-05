<template>
  <div>
    <b-container>
      <h1>{{ $t('MAN_OFF_HEADER') }}</h1>
      <b-row>
        <b-col>
          <b-overlay
              :show="showLoading"
              rounded="sm">
            <b-table :fields="tableOffersFields" :items="offers" hover striped>
              <template #cell(title)="data">
                <b-link @click="goToExactOffer(data.item.id)">{{ data.value }}</b-link>
              </template>
              <template #cell(validFrom)="data">
                {{ data.value | formatDate }}
              </template>
              <template #cell(validTo)="data">
                {{ data.value | formatDate }}
              </template>
              <template #cell(active)="data">
                <bool-badge :active="data.value"/>
              </template>
              <template #cell(actionKeys)>
                <b-button-group>
<!--                  <b-button size="sm" class="mb-1 actionBtn bg-transparent">
                    <b-icon icon="pencil-fill"></b-icon>
                  </b-button>-->
                  <b-button class="mb-1 actionBtn bg-transparent" size="sm">
                    <b-icon icon="trash-fill"></b-icon>
                  </b-button>
                </b-button-group>
              </template>
            </b-table>
          </b-overlay>
        </b-col>
      </b-row>
      <div>
        <b-button variant="outline-primary" v-on:click="refreshOffers">{{ $t('REFRESH') }}</b-button>
      </div>
    </b-container>
  </div>
</template>

<script>
import {api} from "@/api";
import BoolBadge from '@/components/BoolBadge'

export default {
  name: 'ManagementOffers',
  components: {BoolBadge},


  data() {
    return {
      showLoading: false,
      activeOffers: [],
      offers: [],
      entertainer: [],
      tableOffersFields: [],
    }
  },
  beforeUpdate() {
    this.updateLabels()
  },
  methods: {
    updateLabels() {
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
          key: 'active',
          sortable: true,
          label: this.$i18n.t('ACTIVE'),
        },
        {
          key: 'actionKeys',
          label: this.$i18n.t('ACTIONS'),
        },
      ]
    },
    refreshOffers() {
      this.showLoading = true;
      api.get('moo/offer/allWithDetails').then(response => {
        this.showLoading = false;
        this.offers = response.data;
      }).catch(() => {
      })

    },
    goToExactOffer(id) {
      this.$router.push('offers/' + id)
    },
  },
  mounted() {
    this.refreshOffers()
  },
}
</script>

<style scoped>
.actionBtn {
  border: transparent;
}
</style>