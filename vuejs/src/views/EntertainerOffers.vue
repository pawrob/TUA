<template>
  <div>
    <b-container>
      <h1 class="m-5">{{ $t('YOUR') }} {{ $t('OFFERS') }}, {{ this.$store.state.username }}</h1>
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
              <template #cell(actionKeys)="data">
                <b-button-group>
                  <b-button class="mb-1 actionBtn bg-transparent" size="sm" @click="goToExactOffer(data.item.id)">
                    <b-icon icon="pencil-fill"></b-icon>
                  </b-button>
                  <b-button class="mb-1 actionBtn bg-transparent" size="sm">
                    <b-icon icon="trash-fill" v-on:click="showDeletingModal=true, toDeleteId=data.item.id"></b-icon>
                  </b-button>
                  <b-modal
                      ref="modal"
                      v-model="showDeletingModal"
                      :cancel-title="$t('CANCEL')"
                      :ok-title="$t('OK')"
                      @ok="deactivateOffer(toDeleteId)">
                    {{ $t('MODAL_CONFIRMATION') }}
                  </b-modal>
                </b-button-group>
              </template>
            </b-table>
          </b-overlay>
        </b-col>
      </b-row>
      <div>
        <b-button variant="outline-primary" v-on:click="refreshOffers">{{ $t('REFRESH') }}</b-button>
      </div>
      <div>
        <b-button variant="outline-primary"
                  v-on:click="showAddModal=true">
          {{ $t('ADD_OFFER') }}
        </b-button>
      </div>
      <div>
        <b-modal
            v-model="showAddModal"
            :cancel-title="$t('CANCEL')"
            :ok-title="$t('ADD_OFFER')"
            size="xl"
            @ok="addNewOffer()">
          <label for="titleinput">{{ $t('TITLE') }}</label>
          <input id="titleinput" v-model="offerToCreate.title" class="form-control" type="email">

<!--            <label for="desc">{{ $t('DESCRIPTION') }}</label>-->
            {{ $t('DESCRIPTION') }}
          <v-md-editor id="desc" v-model="description" height="300px" width="800px"></v-md-editor>
<!--            <textarea id="desc" class="span8" rows="6" v-model="offerToCreate.description"></textarea></div>-->

          <table class="table table-striped">
            <thead>
            <tr>
              <th></th>
              <th></th>
              <th></th>
            </tr>
            </thead>
            <tbody>
            <tr>
              <th>{{ $t('DATE_FROM') }}</th>
              <th>
                <b-form-datepicker id="date-from-day" v-model="offerToCreate.validFrom.day"></b-form-datepicker>
              </th>
              <th>
                <b-time v-model="offerToCreate.validFrom.hour"></b-time>
              </th>
            </tr>
            <tr>
              <th>{{ $t('DATE_TO') }}</th>
              <th>
                <b-form-datepicker id="date-from-day" v-model="offerToCreate.validTo.day"></b-form-datepicker>
              </th>
              <th>
                <b-time v-model="offerToCreate.validTo.hour"></b-time>
              </th>
            </tr>
            </tbody>
          </table>

        </b-modal>
      </div>
    </b-container>
  </div>
</template>

<script>
import {api} from "@/api";
import BoolBadge from '@/components/BoolBadge'
import {apiEntertainer} from "../api/entertainer";
import eventBus from "../event-bus";

export default {
  name: 'EntertainerOffers',
  components: {BoolBadge},


  data() {
    return {
      showLoading: false,
      showAddModal: false,
      toDeleteId: null,
      showDeletingModal: false,
      offerToCreate: {
        active: true,
        description: "",
        title: "",
        validFrom: {
          day: "",
          hour: "00:00:00"
        },
        validTo: {
          day: "",
          hour: "00:00:00"
        },
      },
      activeOffers: [],
      offers: [],
      entertainer: [],
      tableOffersFields: [{
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
        },],
    }
  },
  beforeUpdate() {
    this.updateLabels()
  },
  methods: {
    updateLabels() {
      this.tableOffersField= [
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
      api.get('moo/offer/allEntertainer').then(response => {
        this.showLoading = false;
        this.offers = response.data;
      }).catch(() => {
      })

    },
    goToExactOffer(id) {
      this.$router.push('offers/' + id + '/edit')
    },

    addNewOffer() {
      this.showLoading = true
      if (this.offerToCreate.validFrom.day >= this.offerToCreate.validTo.day) {
        if (this.offerToCreate.validFrom.hour >= this.offerToCreate.validTo.hour) {
          eventBus.$emit('alert', {key: 'WRONG_DATA_PERIODS', variant: 'danger'})
          this.showLoading = false
          return
        }
      }

      apiEntertainer.addNewOffer(true, this.offerToCreate.title, this.offerToCreate.description,
          this.offerToCreate.validFrom.day + "T" + this.offerToCreate.validFrom.hour + "+00:00",
          this.offerToCreate.validTo.day + "T" + this.offerToCreate.validTo.hour + "+00:00"
      ).then(() => {
        this.refreshOffers()
        this.showLoading = false
      })
    },

    async deactivateOffer(id) {
      this.showLoading = true
      await apiEntertainer.deactivateOffer(id).then(() => {
        this.showLoading = false
      })
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