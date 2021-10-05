<template>
  <div>
    <b-container>
      <h2 class="mt-5"> {{ offer.title }}</h2><br>
      <h3> {{ offer.description }}</h3><br>
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
          <b-overlay
              :show="showLoading"
              rounded="sm">
            <b-table :fields="reservationsFields" :items="offer.reservations" hover striped>
              <template #cell(id)="data">
                <b-link :to="'reservation/' + data.item.id">{{ data.value }}</b-link>
              </template>
              <template #cell(clientLogin)="data">
                <b-link :to="'/entertainer/offers/clientInfo/' + data.item.client">{{ data.value }}</b-link>
              </template>
              <template #cell(reservationFrom)="data">
                {{ data.value | formatDate }}
              </template>
              <template #cell(reservationTo)="data">
                {{ data.value | formatDate }}
              </template>
              <template #cell(status)="data">
                <p :class="data.item.status.toLowerCase()">{{ $t(data.value) }}</p>
              </template>
              <template #cell(actionKeys)>
                <b-button-group>
                  <b-button class="mb-1 actionBtn bg-transparent" size="sm">
                    <b-icon icon="pencil-fill"></b-icon>
                  </b-button>
                  <b-button class="mb-1 actionBtn bg-transparent" size="sm">
                    <b-icon icon="trash-fill"></b-icon>
                  </b-button>
                </b-button-group>
              </template>
            </b-table>
          </b-overlay>
        </b-col>
      </b-row>

      <b-row>
        <b-col>
          <b-btn class="m-3" type="button" variant="outline-primary" v-on:click="refreshOffer">{{
              $t('REFRESH')
            }}
          </b-btn>
          <b-btn  class="m-3" type="button" variant="outline-primary" v-on:click="editOffer">{{ $t('EDIT') }}</b-btn>
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script>
import {api} from "@/api";
import ItemTemplate from "@/components/ItemTemplate";
import BoolBadge from "@/components/BoolBadge";

export default {
  name: "EntertainerOffer",
  components: {BoolBadge},
  data: () => {
    return {
      offer: {},
      itemTemplate: ItemTemplate,
      showLoading: false,
      reservationsFields: [
        {
          key: 'clientLogin',
          sortable: true,
          label: 'Client',
        },
        {
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
        },
        {
          key: 'actionKeys',
          label: 'Actions',
        },
      ],
    }
  },
  methods: {
    refreshOffer() {
      this.showLoading = true;
      api.get('moo/offer/withDetails/' + this.$route.params.id).then(response => {
        this.showLoading = false;
        this.offer = response.data;
        this.reservations = this.offer.reservations;
      }).catch(() => {
      })

    },
    editOffer() {
      this.$router.push(this.$route.params.id + '/edit')
    },

  },
  mounted() {
    this.refreshOffer()
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

.canceled {
  color: #ff0000;

}

.ended {
  color: #cdcdcd;
}

.pending {
  color: #e7ba10;
}

.accepted {
  color: #09cd24;
}
</style>