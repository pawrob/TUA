<template>
  <div>
    <b-container>
      <h1>{{ $t('USERS') }}</h1>
      <b-row>
        <b-col lg="">
          <b-form-group :label="$t('SEARCH_BY_USERNAME') + ':'" label-align="left">
            <vue-suggestion v-model="item"
                            :itemTemplate="itemTemplate"
                            :items="itemsSelected"
                            :setLabel="setLabel"
                            inputClasses="form-control"
                            @changed="inputChange"
                            @selected="itemSelected">
            </vue-suggestion>
          </b-form-group>
        </b-col>
        <b-col lg="">
          <b-form-group :label="$t('inputData') + ':'" label-align="left">
            <b-input v-model="message"/>
          </b-form-group>
        </b-col>
      </b-row>

      <b-row>
        <b-col>
          <b-overlay
              :show="showLoading"
              rounded="sm">
            <b-table :fields="tableUsersFields" :items="usersFound" hover striped>
              <template #cell(login)="data">
                <b-link :to="'users/' + data.item.id">{{ data.value }}</b-link>
              </template>
              <template #cell(isActive)="data">
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
              <template #cell(isVerified)="data">
                <bool-badge :active="data.value"/>
              </template>
              <template #cell(accessLevels)="data">
                <div v-for="item in data.value" :key="item.id" align="right">
                  <b-link @click="AccessLevel.showModal = true, AccessLevel.name=item.accessLevel,
                        AccessLevel.active=item.isActive, AccessLevel.accessLevelId=item.id">
                    {{ $t(item.accessLevel) }}
                    <bool-badge :active="item.isActive"/>
                  </b-link>
                  <b-modal
                      ref="modal"
                      v-model="AccessLevel.showModal"
                      :cancel-title="$t('CANCEL')"
                      :ok-title="$t('OK')"
                      @ok="changeEntertainerAccessLevelStatusByLevelId()">
                    <div v-if="AccessLevel.active">{{ AccessLevel.userId }}{{ $t('deactivateEntertainer') }}</div>
                    <div v-else>{{ AccessLevel.userId }}{{ $t('reactivateEntertainer') }}</div>
                  </b-modal>
                </div>
              </template>
            </b-table>
          </b-overlay>
        </b-col>
      </b-row>

      <div>
        <b-button v-b-hover="handleHover" variant="outline-primary" v-on:click="refreshUsers">
          <b-icon v-if="isHovered" icon="arrow-clockwise" animation="spin" font-scale="1"
                  class="slow-transition"></b-icon>
          <b-icon v-else icon="arrow-clockwise" animation="none" font-scale="1"></b-icon>
          {{ $t('REFRESH') }}
        </b-button>
      </div>

    </b-container>
  </div>
</template>
<script>
import {apiManager} from '@/api/manager'
import BoolBadge from '@/components/BoolBadge'
import ItemTemplate from '@/components/ItemTemplate'

export default {
  name: 'Users',
  components: {BoolBadge},

  data() {
    return {
      message: '',
      item: {},
      itemsSelected: [],
      itemTemplate: ItemTemplate,
      show: false,
      isHovered: false,
      showLoading: false,
      AccessLevel: {
        showModal: false,
        name: null,
        userId: null,
        active: false,
        accessLevelId: null,
      },
      usersFound: [],
      users: [],
      currentId: '',
      currentActive: '',
      tableUsersFields: [],
    }
  },
  beforeUpdate() {
    this.updateLabels()
  },
  methods: {
    updateLabels() {
      this.tableUsersFields = [
        {
          key: 'login',
          sortable: true,
          label: 'Login',
        },
        {
          key: 'email',
          sortable: true,
          label: 'Email',
        },
        {
          key: 'isActive',
          sortable: true,
          label: this.$i18n.t('ACTIVE'),
        },
        {
          key: 'isVerified',
          sortable: true,
          label: this.$i18n.t('VERIFIED'),
        },
        {
          key: 'accessLevels',
          sortable: true,
          label: this.$i18n.t('ACCESS_LEVELS'),
        },
      ]
    },
    handleHover(hovered) {
      this.isHovered = hovered
    },
    changeActivnessStatus(userId, active) {
      if (active) {
        apiManager.deactivateUser(userId).then(this.refreshUsers)
      } else {
        apiManager.activateUser(userId).then(this.refreshUsers)
      }

    },
    saveId(id, active) {
      this.currentId = id;
      this.currentActive = active;
    },
    refreshUsers() {
      this.showLoading = true;
      apiManager.getAllUsers().then(value => {
        this.showLoading = false;
        this.users = value
        this.usersFound = value
      })
    },
    getUserByPiece(piece) {
      if (piece.length > 2) {
        this.showLoading = true
        apiManager.getUsersByPieceOfPersonalData(piece).then(value => {
          this.showLoading = false
          this.users = value
          this.usersFound = value
        })
      } else if (piece.length === 0) {
        this.refreshUsers()
      }
    },
    async changeEntertainerAccessLevelStatusByLevelId() {
      this.showLoading = true;
      if (this.AccessLevel.name === 'ENTERTAINER') {
        await apiManager.changeAccessLevelStatusForEntertainer(this.AccessLevel.accessLevelId, this.AccessLevel.active == false)
            .then(() => {
              this.showLoading = false;
              this.refreshUsers()
            })
      } else {
        await apiManager.changeAccessLevelStatus(this.AccessLevel.accessLevelId, this.AccessLevel.active == false)
            .then(() => {
              this.showLoading = false
              this.refreshUsers()
            })
      }
      this.AccessLevel.showModal = false
    },
    itemSelected(item) {
      this.itemsSelected = [item]
      this.usersFound = this.itemsSelected
    },
    setLabel(item) {
      return item.login
    },
    inputChange(text) {
      if (text.length === 0) {
        this.refreshUsers()
      }
      this.message = ''
      this.itemsSelected = this.users.filter(o => o.login.toLowerCase().includes(text.toLowerCase()))
    },
    showModal() {
      this.$refs['confirm-modal'].show()
    },
  },
  mounted() {
    this.refreshUsers()
  },
  watch: {
    message() {
      this.getUserByPiece(this.message)
    },
  },
}
</script>
<i18n>{
  "pl": {
    "isVerifiedLabel": "Zweryfikowano",
    "accessLevels": "Poziom dostępu",
    "deactivateEntertainer": "Czy na pewno dezaktywowac ten poziom dostępu?",
    "reactivateEntertainer": "Czy na pewno reaktywowac ten poziom dostępu?",
    "SEARCH_BY_USERNAME": "Wyszukaj po nazwie użytkownika",
    "inputData": "Wyszukaj po imieniu lub nazwisku"
  },
  "en": {
    "isVerifiedLabel": "isVerifiedLabel",
    "accessLevels": "Access Level",
    "deactivateEntertainer": "Are you sure about deactivating this access level?",
    "reactivateEntertainer": "Are you sure about reactivating this access level?",
    "SEARCH_BY_USERNAME": "Search by username",
    "inputData": "Search by name or surname"
  }
}</i18n>
<style lang="scss" scoped>
.vue-suggestion .vs__list {
  width: 100%;
  text-align: left;
  border: none;
  border-top: none;
  max-height: 400px;
  overflow-y: auto;
  border-bottom: 1px solid #023d7b;
  position: relative;
}

.vue-suggestion .vs__list .vs__list-item {
  background-color: #fff;
  padding: 10px;
  border-left: 1px solid #023d7b;
  border-right: 1px solid #023d7b;
}

.vue-suggestion .vs__list .vs__list-item:last-child {
  border-bottom: none;
}

.vue-suggestion .vs__list .vs__list-item:hover {
  background-color: #eee !important;
}

.vue-suggestion .vs__list,
.vue-suggestion .vs__loading {
  position: absolute;
}

.vue-suggestion .vs__list .vs__list-item {
  cursor: pointer;
}

.vue-suggestion .vs__list .vs__list-item.vs__item-active {
  background-color: #f3f6fa;
}

.slow-transition {
  transition: 1s;
}
</style>