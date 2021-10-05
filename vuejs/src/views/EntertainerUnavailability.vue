<template>
  <div>
    <b-overlay
        :show="showLoading"
        rounded="sm">
      <h1>{{ $t('UNAVAILABILITY') }}</h1>
      <button class="btn btn-primary" @click="reloadMyUnavailabilities()"><span
          class="glyphicon glyphicon-refresh"></span>
        {{ $t('REFRESH') }}
      </button>
      <b-table :fields="tableFields" :items="myUnavailability" hover striped>
        <template #cell(valid)="data">
          <b-link @click="show = true, saveId(data.item.id, data.value)">
            <bool-badge :active="data.value"/>
          </b-link>
          <b-modal
              ref="modal"
              v-model="show"
              :cancel-title="$t('CANCEL')"
              :ok-title="$t('OK')"
              @ok="changeActivnessStatus(currentId, currentActive)">
            {{ $t('MODAL_CONFIRMATION') }}
          </b-modal>
        </template>
        <template #cell(describtion)="data">
          {{ data.value }}
        </template>
        <template #cell(dateTimeFrom)="data">
          {{ data.value | formatDate }}
        </template>
        <template #cell(dateTimeTo)="data">
          {{ data.value | formatDate }}
        </template>
      </b-table>
      <div>
        <b-button variant="primary"
                  @click="showCreatingNewAnavailability = !showCreatingNewAnavailability">
          {{ $t('ADD_UNAVAILABILITY') }}
        </b-button>
      </div>
      <b-modal
          size="xl"
          v-model="showCreatingNewAnavailability"
          :cancel-title="$t('CANCEL')"
          :ok-title="$t('CONFIRM')"
          @ok="addUnavailability()">
        <div>
          <table class="table table-striped">
            <thead>
            <tr>
              <th></th>
              <th>{{ $t('DAY') }}</th>
              <th>{{ $t('HOUR') }}</th>
            </tr>
            </thead>
            <tbody>
            <tr>
              <th>{{ $t('DATE_FROM') }}</th>
              <th>
                <b-form-datepicker id="date-from-day" v-model="dateFrom.date"></b-form-datepicker>
              </th>
              <th>
                <b-time v-model="dateFrom.hour"></b-time>
              </th>
            </tr>
            <tr>
              <th>{{ $t('DATE_TO') }}</th>
              <th>
                <b-form-datepicker id="date-from-day" v-model="dateTo.date"></b-form-datepicker>
              </th>
              <th>
                <b-time v-model="dateTo.hour"></b-time>
              </th>
            </tr>
            </tbody>
          </table>
          <div>{{ $t('SUMMARY') }}:
            {{ dateFrom.date }} {{ dateFrom.hour }} - {{ dateTo.date }} {{ dateTo.hour }}
          </div>
          <div class="form-group">
            <label for="descriptionInput">{{ $t('DESCRIPTION') }}</label>
            <input type="text" class="form-control" id="descriptionInput" v-model="description">
          </div>
        </div>
      </b-modal>
    </b-overlay>
    <b-modal
        v-model="wrongDataInfo"
        :ok-title="OK"
    >
      {{ $t('WRONG_DATA_PERIODS') }}
    </b-modal>
  </div>
</template>

<script>
import {apiEntertainer} from '@/api/entertainer'
import BoolBadge from '@/components/BoolBadge'

export default {
  name: "EntertainerUnavailability",
  components: {BoolBadge},

  data() {
    return {
      show: false,
      showLoading: false,
      showCreatingNewAnavailability: false,
      locale: "en",
      dateFrom: {
        date: null,
        hour: "00:00:00",
      },
      dateTo: {
        date: null,
        hour: "00:00:00",
      },
      myUnavailability: [],
      wrongDataInfo: false,
      tableFields: [
        {
          key: 'dateTimeFrom',
          sortable: true,
          label: 'Data od',
        },
        {
          key: 'dateTimeTo',
          sortable: true,
          label: 'Data do',
        },
        {
          key: 'valid',
          sortable: true,
          label: 'Aktywne',
        },
        {
          key: 'description',
          sortable: true,
          label: 'Opis',
        },
      ],
      currentId: null,
      currentActive: null,
      description: "",
    }
  },

  methods: {
    loadingAnimationSwitch(status) {
      this.showLoading = status
    },
    async reloadMyUnavailabilities() {
      this.loadingAnimationSwitch(true)
      await apiEntertainer.getEntertainerOwnUnavailabilities().then(response => {
            this.myUnavailability = response
            this.loadingAnimationSwitch(false)
          }
      )
    },
    saveId(currentId, currentActive) {
      this.currentId = currentId
      this.currentActive = currentActive
    },
    changeActivnessStatus(currentId, currentActive) {
      this.loadingAnimationSwitch(true)
      apiEntertainer.updateActivenessOfUnavailability(currentId, !currentActive).then(() => {
        this.loadingAnimationSwitch(false)
        this.reloadMyUnavailabilities()
      })
    },

    addUnavailability() {
      let timezone = "+00:00"
      let dateF = this.dateFrom.date + "T" + this.dateFrom.hour + timezone
      let dateT = this.dateTo.date + "T" + this.dateTo.hour + timezone
      if(this.dateFrom.date ==null || this.dateTo.date==null){
        this.wrongDataInfo = true
        return
      }
      if (this.dateFrom.date >= this.dateTo.date) {
        if (this.dateFrom.hour >= this.dateTo.hour) {
          this.wrongDataInfo = true
          return
        }
      }
      this.loadingAnimationSwitch(true)
      apiEntertainer.addUnavailability(dateF, dateT, this.description).then(() => {
        this.loadingAnimationSwitch(false)
        this.reloadMyUnavailabilities()
      })
    }

  },
  mounted: function () {
    this.reloadMyUnavailabilities()
  },

}
</script>
<i18n>{
  "pl": {
    "ADD_UNAVAILABILITY": "Dodaj okres niedostępności",
    "LANG": "pl",
    "SUMMARY": "Niedostępność w okresie",
    "UNAVAILABILITY": "Zadeklarowane okresy niedostępności",
    "DAY": "Dzień",
    "HOUR": "Godzina",
    "CONFIRM": "Potwierdź",
    "DESCRIPTION": "Opis"
  },
  "en": {
    "ADD_UNAVAILABILITY": "Add unavailability period",
    "LANG": "en",
    "SUMMARY": "Unavalability in period",
    "UNAVAILABILITY": "Declared unavailability periods",
    "DAY": "Day",
    "HOUR": "Hour",
    "CONFIRM": "Confirm",
    "DESCRIPTION": "Description"
  }
}
</i18n>

<style scoped>

</style>