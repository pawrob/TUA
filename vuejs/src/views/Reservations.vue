<template>
  <div>
    <b-container>
      <h1>{{ $t('RESERVATIONS') }}</h1>

      <b-row>
        <b-col>
          <b-overlay
              :show="showLoading"
              rounded="sm">
            <b-table :fields="reservationsFields" :items="reservations" hover striped>
              <template #cell(entertainerLogin)="data">
                <b-link :to="'/entertainerPreview/' + data.item.entertainer">{{ data.value }}</b-link>
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
              <template #cell(actionKeys)="data">
                <b-button-group>
                  <b-button v-if="data.item.status === 'PENDING'" v-b-tooltip.hover :title="$t('EDIT_RESERVATION')"
                            class="mb-1 actionBtn bg-transparent" size="sm"
                            v-on:click="goToExactReservation(data.item.id)">
                    <b-icon alt="edit" icon="pencil-fill"></b-icon>
                  </b-button>
                  <b-button v-if="data.item.status === 'PENDING'"
                            v-b-tooltip.hover :title="$t('CANCEL_RESERVATION')"
                            class="mb-1 actionBtn bg-transparent" size="sm"
                            @click="cancelModal = {show: true, id: data.item.id}">
                    <b-icon alt="cancel" icon="calendar-x-fill"></b-icon>
                  </b-button>
                  <b-button v-if="data.item.status === 'ENDED'" v-b-modal.rate-comment
                            v-b-tooltip.hover :title="$t('RATE_RESERVATION')" class="mb-1 actionBtn bg-transparent"
                            size="sm" @click="() => {
                              feedback.id=data.item.id;
                              feedback.rating=data.item.rating;
                              feedback.comment=data.item.comment;
                            }">
                    <b-icon alt="comment" icon="chat-left-dots-fill"></b-icon>
                  </b-button>
                  <b-button v-b-tooltip.hover :title="$t('FAVORITE_RESERVATION')" class="mb-1 actionBtn bg-transparent"
                            size="sm">
                    <b-icon :icon="data.item.isFavorite?'star-fill':'star'" alt="Add to favorites"
                            @click="!data.item.isFavorite?addFavourite(data.item.offer):removeFavourite(data.item.offer)"></b-icon>
                  </b-button>
                </b-button-group>
              </template>
            </b-table>
          </b-overlay>
        </b-col>
      </b-row>

      <div>
        <b-button v-b-hover="handleHover" variant="outline-primary" v-on:click="refreshReservations">
          <b-icon v-if="isHovered" animation="spin" class="slow-transition" font-scale="1" icon="arrow-clockwise"></b-icon>
          <b-icon v-else animation="none" font-scale="1" icon="arrow-clockwise"></b-icon>
          {{ $t('REFRESH') }}
        </b-button>
      </div>

      <b-modal
          id="rate-comment"
          :title="$t('RATE_AND_COMMENT')">
        <div>
          <star-rating star-size=40 animate=true class="star" glow=2 v-model="feedback.rating" @rating-selected ="setRating"></star-rating>
          <hr class="my-4">
          <b-form-group :invalid-feedback="$t('COMMENT_TOO_LONG')" :state="feedback.valid">
          <b-form-textarea
              id="comment-textbox"
              :placeholder="$t('TEXTBOX_PLACEHOLDER')"
              v-model="feedback.comment"
              rows="3"
              max-rows="6"
          ></b-form-textarea>
          </b-form-group>
        </div>
        <template #modal-footer="{ hide, cancel, ok }">
          <b-button class="float-left" variant="outline-primary" @click="deleteModal = {show: true}; hide()">
            <b-icon icon="trash" :alt="$t('DELETE_RATING')"></b-icon>
          </b-button>
          <b-button variant="secondary" @click="cancel()">
            {{ $t('CANCEL') }}
          </b-button>
          <b-button variant="primary" @click="updateRating(); ok()">
            {{ $t('OK') }}
          </b-button>
        </template>
      </b-modal>

      <b-modal v-model="cancelModal.show" :cancel-title="$t('NO')" :ok-title="$t('YES')"
               :title="$t('CANCEL_MODAL_TITLE')" @ok="cancelReservation(cancelModal.id)">
        {{ $t('CANCEL_MODAL_BODY') }}
      </b-modal>

      <b-modal v-model="deleteModal.show" :cancel-title="$t('NO')" :ok-title="$t('YES')"
               :title="$t('DELETE_MODAL_TITLE')" @ok="deleteRatingByClient()">
        {{ $t('DELETE_MODAL_BODY') }}
      </b-modal>

    </b-container>
  </div>
</template>

<script>
import StarRating from 'vue-star-rating'
import {api} from "@/api";
import eventBus from "@/event-bus";
import ItemTemplate from "@/components/ItemTemplate";
import Vue from "vue"

export default {
  name: 'Reservations',
  components: {
    StarRating
  },
  data: () => {
    return {
      reservations: [],
      itemTemplate: ItemTemplate,
      feedback: {
        id: '',
        rating: 0,
        comment: '',
        valid: true,
      },
      isHovered: false,
      showLoading: false,
      reservationsFields: [],
      cancelModal: {
        show: false,
        id: null,
      },
      deleteModal: {
        show: false,
      },
    }
  },
  beforeUpdate() {
    this.reservationsFields = [
      {
        key: 'entertainerLogin',
        sortable: true,
        label: this.$i18n.t('ENTERTAINER'),
      }, {
        key: 'offerTitle',
        sortable: true,
        label: this.$i18n.t('OFFER'),
      }, {
        key: 'reservationFrom',
        sortable: true,
        label: this.$i18n.t('RESERVATION_FROM'),
      }, {
        key: 'reservationTo',
        sortable: true,
        label: this.$i18n.t('RESERVATION_TO'),
      }, {
        key: 'status',
        sortable: true,
        label: 'Status',
      }, {
        key: 'actionKeys',
        label: this.$i18n.t('ACTIONS'),
      },
    ]
  },
  methods: {
    handleHover(hovered) {
      this.isHovered = hovered
    },
    addFavourite(id){
      api.post('/moo/offer/'+id+'/add-favorite').then(() => this.refreshReservations()).catch(() => {})
    },
    removeFavourite(id){
      api.delete('/moo/offer/'+id+'/delete-favorite').then(() => this.refreshReservations()).catch(() => {})
    },
    goToExactReservation(id) {
      this.$router.push('reservation/' + id + "/edit")
    },
    refreshFavourites(){
      api.get('moo/offer/favorites/').then(response => {
        this.showLoading = false
        for (const r of this.reservations) {
          let bool = false
          for (const f of response.data) {
            if (r.offer === f.id) {
              bool = true
            }
          }
          Vue.set(r, 'isFavorite', bool)
        }
      }).catch(() => {
      })
    },
    refreshReservations() {
      this.showLoading = true
      api.get('moo/reservation/self/').then(response => {
        this.showLoading = false
        this.reservations = response.data
        this.refreshFavourites()
      }).catch(() => {
      })
    },
    setRating: function (rating) {
      this.feedback.rating = rating
    },
    updateRating(modal) {
      this.feedback.valid = this.feedback.comment.length <= 2048
      if (!this.feedback.valid) {
        modal.preventDefault()
        return
      }
      api.put('moo/reservation/' + this.feedback.id + '/rating', this.feedback).then(() => {
        eventBus.$emit('alert', {key: 'RESERVATION_RATED', variant: 'success'})
        this.refreshReservations()
      })
    },
    deleteRatingByClient() {
      api.delete('/moo/reservation/' + this.feedback.id + '/rating').then(() => {
        eventBus.$emit('alert', {key: 'RESERVATION_RATING_REMOVED', variant: 'success'});
        this.refreshReservations();
      }).catch(() => {})
    },
    cancelReservation(id) {
      api.put(`moo/reservation/${encodeURIComponent(id)}/client-cancel`).then(() => {
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
<i18n>{
  "pl": {
    "COMMENT_TOO_LONG": "Komentarz nie może być dłuższy niż 250 znaków",
    "RATING_IS_NULL": "Wymagana ocena",
    "RATE_RESERVATION": "Oceń usługę",
    "FAVORITE_RESERVATION": "Dodaj/usuń z ulubionych",
    "EDIT_RESERVATION": "Edytuj rezerwację",
    "CANCEL_RESERVATION": "Anuluj rezerwację",
    "CANCEL_MODAL_TITLE": "Czy na pewno chcesz anulować rezerwację?",
    "CANCEL_MODAL_BODY": "Twoja rezerwacja zostanie anulowana",
    "DELETE_MODAL_TITLE": "Czy na pewno chcesz usunąć ocenę?",
    "DELETE_MODAL_BODY": "Twoja ocena zostanie usunięta"
  },
  "en": {
    "COMMENT_TOO_LONG": "Comment cannot be longer than 250 characters",
    "RATING_IS_NULL": "Rating is required",
    "RATE_RESERVATION": "Rate service",
    "FAVORITE_RESERVATION": "Add/remove from favorites",
    "EDIT_RESERVATION": "Edit reservation",
    "CANCEL_RESERVATION": "Cancel reservation",
    "CANCEL_MODAL_TITLE": "Are you sure you want to cancel reservation?",
    "CANCEL_MODAL_BODY": "Your reservation will be canceled",
    "DELETE_MODAL_TITLE": "Are you sure you want to delete rating?",
    "DELETE_MODAL_BODY": "Your rating will be deleted"
  }
}</i18n>
<style scoped>
.slow-transition {
  transition: 1s;
}

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