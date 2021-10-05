<template>
  <div class="entertainer-reservation">
    <b-table :fields="tableReservationsFields" :items="reservations" hover striped>
      <template #cell(status)="data">
        <p :class="data.item.status.toLowerCase()">{{ $t(data.value) }}</p>
      </template>
      <template #cell(offerTitle)="data">
        <b-link :to="'offers/' + data.item.offer">{{ data.value }}</b-link>
      </template>
      <template #cell(clientLogin)="data">
        <b-link :to="'/entertainer/clientInfo/' + data.item.client">{{ data.value }}</b-link>
      </template>
      <template #cell(reservationFrom)="data">
        {{ data.value | formatDate }}
      </template>
      <template #cell(reservationTo)="data">
        {{ data.value | formatDate }}
      </template>
      <template #cell(actionKeys)="data">
        <template v-if="data.item.status === 'PENDING'">
          <b-button class="mb-1" size="sm" variant="outline-primary" @click="() => {showAcceptDialog(data.item.id)}">
            <b-icon animation="throb" icon="reply" scale="0.75"></b-icon>
            {{ $t('ACCEPT') }}
          </b-button>
        </template>
        <template v-if="data.item.status === 'ACCEPTED'">
          <b-button class="mb-1" size="sm"
                    variant="outline-primary" @click="() => {showEndDialog(data.item.id)}">
            <b-icon v-if:="data.item.status === 'ACCEPTED'" animation="throb" icon="check-circle" scale="0.75"></b-icon>
            {{ $t('END') }}
          </b-button>
        </template>
        <template v-if="data.item.status === 'PENDING'">
          <b-button class="mb-1" size="sm" variant="outline-primary"
                    @click="cancelModal = {show: true, id: data.item.id}">
            <b-icon animation="throb" icon="x" scale="0.75"></b-icon>
            {{ $t('CANCEL') }}
          </b-button>
        </template>
      </template>
    </b-table>

    <div>
      <b-button v-b-hover="b => isHovered = b" variant="outline-primary" v-on:click="refreshReservations">
        <b-icon v-if="isHovered" icon="arrow-clockwise" animation="spin" font-scale="1" class="slow-transition"></b-icon>
        <b-icon v-else icon="arrow-clockwise" animation="none" font-scale="1"></b-icon>
        {{ $t('REFRESH') }}
      </b-button>
    </div>

    <b-modal v-model="showModal" :cancel-title="$t('NO')" :ok-title="$t('YES')" :title="$t('CONFIRM_HEADER')"
             @ok="acceptReservation(choosenReservationId)">
      {{ $t('CONFIRM_BODY') }}
    </b-modal>
    <b-modal v-model="showModal2" :cancel-title="$t('NO')" :ok-title="$t('YES')" :title="$t('CONFIRM_END_HEADER')"
             @ok="endReservation(choosenReservationId)">
      {{ $t('CONFIRM_END_BODY') }}
    </b-modal>
    <b-modal v-model="cancelModal.show" :cancel-title="$t('NO')" :ok-title="$t('YES')"
             :title="$t('CANCEL_MODAL_TITLE')" @ok="cancelReservation(cancelModal.id)">
      {{ $t('CANCEL_MODAL_BODY') }}
    </b-modal>

  </div>
</template>

<script>
import {api} from '@/api'
import eventBus from '@/event-bus'

export default {
  name: 'EntertainerReservation',
  data() {
    return {
      reservations: [],
      showModal: false,
      showModal2: false,
      choosenReservationId: null,
      tableReservationsFields: [],
      isHovered: false,
      cancelModal: {show: false, id: null},
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
        {
          key: 'actionKeys',
          label: this.$i18n.t('ACTIONS'),
        },
      ]
    },
    refreshReservations() {
      api.get('moo/reservation/allForEntertainer').then(response => {
        this.reservations = response.data;
      }).catch()
    },
    acceptReservation(reservationId) {
      api.put('/moo/reservation/' + reservationId + '/accept').then(response => {
        console.log(response)
      }).then(this.refreshReservations)
    },
    endReservation(reservationId) {
      api.put('/moo/reservation/' + reservationId + '/end').then(response => {
        console.log(response)
      }).then(this.refreshReservations)
    },
    showAcceptDialog(reservationId) {
      this.showModal = true;
      this.choosenReservationId = reservationId
    },
    showEndDialog(reservationId) {
      this.showModal2 = true;
      this.choosenReservationId = reservationId
    },
    cancelReservation(id) {
      api.put(`moo/reservation/${encodeURIComponent(id)}/entertainer-cancel`).then(() => {
        eventBus.$emit('alert', {key: 'RESERVATION_CANCELED', variant: 'success'})
        this.refreshReservations()
      }).catch()
    },
  },
  mounted() {
    this.refreshReservations()
  }
}
</script>

<i18n>
{
  "pl": {
    "CONFIRM_HEADER": "Akceptacja rezerwacji",
    "CONFIRM_BODY": "Czy na pewno chcesz zaakceptować tę rezerwację?",
    "CONFIRM_END_HEADER": "Zakończenie rezerwacji",
    "CONFIRM_END_BODY": "Czy na pewno chcesz zakończyć tę rezerwację?",
    "CANCEL_MODAL_TITLE": "Czy na pewno chcesz anulować rezerwację?",
    "CANCEL_MODAL_BODY": "Twoja rezerwacja zostanie anulowana"
  },
  "en": {
    "CONFIRM_HEADER": "Accept reservation",
    "CONFIRM_BODY": "Do you want accept this reservation?",
    "CONFIRM_END_HEADER": "Ending reservation",
    "CONFIRM_END_BODY": "Do you want end this reservation?",
    "CANCEL_MODAL_TITLE": "Are you sure you want to cancel reservation?",
    "CANCEL_MODAL_BODY": "Your reservation will be canceled"
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