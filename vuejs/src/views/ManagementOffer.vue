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
                {{ data.value }}
              </template>
              <template #cell(clientLogin)="data">
                <b-link :to="'/management/users/' + data.item.client">{{ data.value }}</b-link>
              </template>
              <template #cell(offerTitle)="data">
                <b-link :to="'/offers/' + data.item.offer">{{ data.value }}</b-link>
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
                  <b-button size="sm" class="mb-1 actionBtn bg-transparent">
                    <b-icon icon="pencil-fill"></b-icon>
                  </b-button>
                  <b-button size="sm" class="mb-1 actionBtn bg-transparent">
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
          <b-btn type="button" v-on:click="refreshOffer" class="m-3" variant="outline-primary">{{
              $t('REFRESH')
            }}
          </b-btn>
          <b-btn type="button" v-on:click="refreshOffer" class="m-3" variant="outline-primary">{{ $t('EDIT') }}</b-btn>
          <b-btn type="button" v-on:click="goToExactEntertainer(offer.entertainer.id)" class="m-3"
                 variant="outline-primary">
            {{ $t('LEARN_ABOUT_ENTERT') }}
          </b-btn>
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
  name: "ManagementOffer",
  components: {BoolBadge},
  data: () => {
    return {
      offer: {},
      itemTemplate: ItemTemplate,
      showLoading: false,
      reservationsFields: [
        {
          key: 'id',
          sortable: true,
          label: 'Id',
        },
        {
          key: 'clientLogin',
          sortable: true,
          label: 'Client',
        }, {
          key: 'offerTitle',
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
        },
        {
          key: 'actionKeys',
          label: 'Actions',
        },
      ],
    }
  },
  beforeUpdate() {
    this.updateLabels()
  },
  methods: {
    updateLabels() {
      this.reservationsFields = [
        {
          key: 'id',
          sortable: true,
          label: 'Id',
        },
        {
          key: 'clientLogin',
          sortable: true,
          label: this.$i18n.t('CLIENT'),
        }, {
          key: 'offerTitle',
          sortable: true,
          label: this.$i18n.t('OFFER'),
        }, {
          key: 'reservationFrom',
          sortable: true,
          label: this.$i18n.t('RESERVATION') + ' ' + this.$i18n.t('FROM'),
        }, {
          key: 'reservationTo',
          sortable: true,
          label: this.$i18n.t('RESERVATION') + ' ' + this.$i18n.t('TO'),
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
      ]
    },
    refreshOffer() {
      this.showLoading = true;
      api.get('moo/offer/withDetails/' + this.$route.params.id).then(response => {
        this.showLoading = false;
        this.offer = response.data;
        this.reservations = this.offer.reservations;
      }).catch(() => {
      })

    },
    goToExactEntertainer(id) {
      this.$router.push('/entertainerPreview/' + id)
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