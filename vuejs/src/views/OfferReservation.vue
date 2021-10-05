<template>
  <div class="offerReservation">
    <b-container>
      <h2> {{ offer.title }}</h2><br>
      <h4>{{ $t('OFFER') }}</h4>
      <p>{{ $t('VALID_FROM') }}: {{ offer.validFrom | formatDate }}</p>
      <p>{{ $t('VALID_TO') }}: {{ offer.validTo | formatDate }}</p>
      <p>{{ $t('IN_DAYS') }}:</p>
      <p v-for="availability in offer.offerAvailabilities" :key="availability.id">
        {{ $t(weekday[availability.weekDay]) }}, {{ availability.hoursFrom | formatTime }} -
        {{ availability.hoursTo | formatTime }}
      </p>
      <date-picker v-model="time1" v-on:change="time2=time1" format="HH:mm" type="datetime" :disabled-date="validDate"
                   :disabled-time="validTime" :placeholder="$t('VALID_FROM')" :minute-step="15"></date-picker>
      <date-picker v-model="time2" format="HH:mm" type="datetime" :disabled-date="validDateSecond"
                   :disabled-time="validTimeSecond" :placeholder="$t('VALID_TO')" :minute-step="15"></date-picker>
      <p>{{ $t('RESERVATION') }} {{ $t('FROM') }}: {{ time1 | formatDate }}</p>
      <p>{{ $t('RESERVATION') }} {{ $t('TO') }}: {{ time2 | formatDate }}</p>
      <b-button :disabled="time1==null || time2==null" v-on:click="() => {this.showModal=true}">{{
          $t('RESERVATION')
        }}
      </b-button>
      <div class="sidebar">
        <div class="inabilities">
          <p class="header"> {{ $t('INABILITIES') }}</p>
          <div v-for="inability in inabilities" :key="inability.id">
            <p class="header"> {{ $t('INACTIVE') }}</p>
            <p> {{ inability.description }}</p>
            <p> {{ inability.dateTimeFrom | formatDate }} - {{ inability.dateTimeTo | formatDate }}</p>
          </div>
        </div>
        <div class="reservations">
          <p class="header"> {{ $t('RESERVATIONS') }}</p>
          <div v-for="reservation in offer.reservations" :key="reservation.id"
               :class="reservation.status.toLowerCase()">
            <p class="header">{{ $t(reservation.status) }}</p>
            <p> {{ reservation.reservationFrom | formatDate }} - {{ reservation.reservationTo | formatDate }}</p>
          </div>
        </div>
      </div>
    </b-container>
    <b-modal v-model="showModal" :cancel-title="$t('NO')" :ok-title="$t('YES')" :title="$t('CONFIRM_HEADER')"
             @ok="submitReservation">
      {{ $t('CONFIRM_BODY') }}
    </b-modal>
  </div>
</template>

<script>
import {api} from "@/api";
import DatePicker from 'vue2-datepicker';
import 'vue2-datepicker/index.css';

import 'vue2-datepicker/locale/pl';
import {apiManager} from "@/api/manager";
import eventBus from "@/event-bus";

export default {
  name: "OfferReservation",
  components: {
    DatePicker
  },
  data: () => {
    return {
      offer: {},
      time1: null,
      time2: null,
      inabilities: [],
      weekday: ["SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"],
      showModal: false

    }
  },
  methods: {
    refreshOffer() {
      api.get('moo/offer/withDetails/' + this.$route.params.id).then(response => {
        this.offer = response.data;
      }).catch(() => {
      }).then(() =>
          api
              .get('/moo/entertainer/unavailability/' + this.offer.entertainer.id)
              .then(response => {
                this.inabilities = response.data
              }))
    },
    validDate(date) {
      // Valid From and TO check
      let from = new Date(Date.parse(this.offer.validFrom))
      from.setHours(0, 0, 0, 0)
      let to = new Date(Date.parse(this.offer.validTo))
      to.setHours(0, 0, 0, 0)
      date = new Date(date)

      // Entertainer Unavailability check
      let validationResult = date < from || date > to

      // date is after now
      validationResult = validationResult || date < new Date().setHours(0, 0, 0, 0)

      validationResult = validationResult || this.inabilities.some(x => this.dateIncludesInPeriods(x.dateTimeFrom, x.dateTimeTo, date))

      // Reservations ovelaps check
      validationResult = validationResult || this.offer.reservations.filter(x => {
        return x.status === 'PENDING' || x.status === 'ACCEPTED'
      })
          .some(x => this.dateIncludesInPeriods(x.reservationFrom, x.reservationTo, date))

      //Offer availabilities
      validationResult = validationResult || this.dateDayOfWeekCheck(date, this.offer.offerAvailabilities)

      return validationResult
    },
    validDateSecond(date) {
      let dateFirst = new Date(this.time1)
      dateFirst.setHours(0, 0, 0, 0)
      return this.validDate(date) || date < dateFirst || date > dateFirst
    },
    validTimeSecond(date) {
      let timeFirst = new Date(this.time1)
      return this.validTime(date) || date < timeFirst
    },
    validTime(date) {
      let validationResult = date < Date.parse(this.offer.validFrom) || date > Date.parse(this.offer.validTo)
      // date is after now
      validationResult = validationResult || date < new Date()

      validationResult = validationResult || this.inabilities.some(x => this.timeIncludesInPeriods(x.dateTimeFrom, x.dateTimeTo, date))
      console.log('RESERVATIONS')
      validationResult = validationResult || this.offer.reservations.filter(x => {
        return x.status === 'PENDING' || x.status === 'ACCEPTED'
      })
          .some(x => this.timeIncludesInPeriods(x.reservationFrom, x.reservationTo, date))
      validationResult = validationResult || this.timeDayOfWeekCheck(date, this.offer.offerAvailabilities)
      return validationResult
    },
    dateIncludesInPeriods(dateStarts, dateEnds, date) {
      let from = new Date(Date.parse(dateStarts))
      from.setHours(0, 0, 0, 0)
      let to = new Date(Date.parse(dateEnds))
      to.setHours(0, 0, 0, 0)
      return date > from && date < to
    },
    timeIncludesInPeriods(dateStarts, dateEnds, date) {
      // console.log(dateStarts)
      let from = new Date(Date.parse(dateStarts))
      let to = new Date(Date.parse(dateEnds))
      // console.log(from)
      return date.getTime() >= from.getTime() && date.getTime() < to.getTime()
    },
    dateDayOfWeekCheck(date, availbilities) {
      return !availbilities.some(availbility => availbility.weekDay === date.getDay())
    },
    timeDayOfWeekCheck(date, availbilities) {
      return !availbilities.some(availbility => {
        let parsedHourFrom = this.parseHour(availbility.hoursFrom)
        console.log(parsedHourFrom)
        let parsedHourTo = this.parseHour(availbility.hoursTo)
        let from = new Date(date)
        let to = new Date(date)
        from.setHours(parsedHourFrom.hour, parsedHourFrom.minute, 0, 0)
        to.setHours(parsedHourTo.hour, parsedHourTo.minute, 0, 0)
        return availbility.weekDay === date.getDay() &&
            date >= from &&
            date <= to
      })
    },
    parseHour(value) {
      let slice = value.split(':');
      let offset = 0;
      if (slice[2][2] === '+')
        offset += parseInt(slice[2].substring(3, 5))
      if (slice[2][2] === '-')
        offset -= parseInt(slice[2].substring(3, 5))
      let diff = new Date().getTimezoneOffset() / 60 + offset
      let hour = parseInt(slice[0]) + diff;
      return {
        hour: hour,
        minute: parseInt(slice[1]),
        offset: new Date().getTimezoneOffset() / 60
      }
    },
    submitReservation() {
      apiManager.submitReservation(this.offer.id, this.time1, this.time2, this.offer.version)
          .then(response => {
            this.refreshOffer();
            if (response.status === 200) {
              eventBus.$emit('alert', {key: 'RESERVATION_CREATED', variant: 'success'})
            }
          })
    }
  },

  mounted() {
    this.refreshOffer();
  }
}
</script>

<i18n>
{
  "pl": {
    "CONFIRM_HEADER": "Potwierdzenie rezerwacji",
    "CONFIRM_BODY": "Czy na pewno chcesz zarezerwować tę usługę?"
  },
  "en": {
    "CONFIRM_HEADER": "Confirm reservation",
    "CONFIRM_BODY": "Do you want reserve this offer?"
  }
}
</i18n>

<style scoped>
.ended > .header {
  color: #807f7f;
  font-weight: bolder;
}

.reservations > div {
  border-radius: 10px;
  border: solid 5px rgba(160, 160, 160, 0.2);
  margin: 5px;
}

.canceled > .header {
  color: #cdcdcd;
}

.pending > .header {
  color: #e7ba10;
}

.accepted > .header {
  color: #09cd24;
}

.inabilities > div {
  border-radius: 10px;
  border: solid 5px rgba(160, 160, 160, 0.2);
}

.inabilities > div > .header {
  color: crimson;
}

.header {
  font-weight: bolder;
  font-size: 1.2em;
}

.sidebar {
  width: 25%;
  height: 93.5%;
  position: absolute;
  padding-top: 2%;
  top: 6.5%;
  left: 0;
  background-color: rgba(160, 160, 160, 0.1);
  overflow: auto;
}
</style>