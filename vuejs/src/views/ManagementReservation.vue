<template>
  <div class="management-reservation">
    <b-table :fields="tableReservationsFields" :items="reservations" hover striped>
      <template #cell(status)="data">
        <p :class="data.item.status.toLowerCase()">{{ $t(data.value) }}</p>
      </template>
      <template #cell(offerTitle)="data">
        <b-link :to="'offers/' + data.item.offer">{{ data.value }}</b-link>
      </template>
      <template #cell(clientLogin)="data">
        <b-link :to="'/management/users/' + data.item.client">{{ data.value }}</b-link>
      </template>
      <template #cell(reservationFrom)="data">
        {{ data.value | formatDate }}
      </template>
      <template #cell(reservationTo)="data">
        {{ data.value | formatDate }}
      </template>
    </b-table>
  </div>
</template>

<script>
import {api} from "@/api";

export default {
  name: "ManagementReservation",
  data: () => {
    return {
      reservations: [],
      tableReservationsFields: [
       
      ]
    }
  },
  beforeUpdate() {
    this.updateLabels()
  },
  methods: {
    updateLabels() {
      this.tableReservationsFields = [
        {
          key: 'status',
          sortable: true,
          label: this.$i18n.t('STATUS'),
        },
        {
          key: 'offerTitle',
          sortable: true,
          label: this.$i18n.t('OFFER'),
        },
        {
          key: 'clientLogin',
          sortable: true,
          label: this.$i18n.t('CLIENT'),
        },
        {
          key: 'reservationFrom',
          sortable: true,
          label: this.$i18n.t('FROM'),
        },
        {
          key: 'reservationTo',
          sortable: true,
          label: this.$i18n.t('TO'),
        },
      ]
    },
    refreshReservations() {
      api.get('moo/reservation/all').then(response => {
        this.reservations = response.data;
      }).catch()
    },
  },
  mounted() {
    this.refreshReservations()
  }
}
</script>


<style scoped>

.canceled{
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