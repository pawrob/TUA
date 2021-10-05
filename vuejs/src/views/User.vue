<template>
  <div>
    <b-container>
      <b-row>
        <b-col>
          Email:
        </b-col>
        <b-col>
          {{ user.email }}
        </b-col>
      </b-row>
      <b-row>
        <b-col>
          Login:
        </b-col>
        <b-col>
          {{ user.login }}
        </b-col>
      </b-row>
      <b-row>
        <b-col>
          {{ $t('ACTIVE') }}
        </b-col>
        <b-col>
          {{ user.active }}
        </b-col>
      </b-row>
      <b-row>
        <b-col>
          {{ $t('VERIFIED') }}
        </b-col>
        <b-col>
          {{ user.verified }}
        </b-col>
      </b-row>
      <b-row>
        <b-col>
          {{ $t('LAST_FAILED_LOGIN') }}
        </b-col>
        <b-col>
          {{ sessionLog.actionTimestamp| formatDate }}
        </b-col>
      </b-row>
      <b-row>
        <b-col>
          {{ $t('LAST_SUCCESSFUL_LOGIN') }}
        </b-col>
        <b-col>
          {{ sessionLogSuccess.actionTimestamp| formatDate }}
        </b-col>
      </b-row>
      <b-row v-for="accessLevel in user.accessLevels" :key="accessLevel.id">
        <b-col>
          <b-checkbox switch v-model=accessLevel.active>{{ $t(accessLevel.accessLevel) }}</b-checkbox>
        </b-col>
      </b-row>
      <b-row>
        <b-col>
          <b-btn type="button" v-on:click="() => {this.showModal=true}" variant="primary">{{ $t('UPDATE') }}</b-btn>
        </b-col>
      </b-row>
      <b-row>
        <b-col>
          <b-btn type="button" v-on:click="() => {this.showModal2=true}" variant="primary">{{
              $t('RESET_PASSWORD')
            }}
          </b-btn>
        </b-col>
      </b-row>
      <b-row>
        <b-col>
          <b-btn type="button" v-b-hover="handleHover" v-on:click="refreshUser" variant="primary">
            <b-icon v-if="isHovered" icon="arrow-clockwise" animation="spin" font-scale="1" class="slow-transition"></b-icon>
            <b-icon v-else icon="arrow-clockwise" animation="none" font-scale="1"></b-icon>
            {{ $t('REFRESH') }}
          </b-btn>
        </b-col>
      </b-row>
    </b-container>
    <b-modal v-model="showModal" :cancel-title="$t('NO')" :ok-title="$t('YES')" :title="$t('CONFIRM_HEADER')"
             @ok="updatePrivileges">
      {{ $t('CONFIRM_BODY') }}
    </b-modal>
    <b-modal v-model="showModal2" :cancel-title="$t('NO')" :ok-title="$t('YES')" :title="$t('CONFIRM_HEADER_RESET')"
             @ok="resetUserPassword">
      {{ $t('CONFIRM_BODY_RESET') }}
    </b-modal>
  </div>
</template>

<script>
import {api} from "@/api";
import eventBus from '@/event-bus'

export default {
  name: "User",
  data: () => {
    return {
      user: {},
      sessionLog: {},
      sessionLogSuccess: {},
      isHovered: false,
      showModal: false,
      showModal2: false
    }
  },
  methods: {
    handleHover(hovered) {
      this.isHovered = hovered
    },
    refreshUser() {
      api.get('user/' + this.$route.params.id).then(response => {
        this.user = response.data;
      }).catch(() => {
      })

      api.get('user/failedLogin/' + this.$route.params.id).then(response => {
        this.sessionLog = response.data;
      }).catch(() => {
        this.sessionLog.actionTimestamp = "No failed logins found"
      })


      api.get('user/successLogin/' + this.$route.params.id).then(response => {
        this.sessionLogSuccess = response.data;
      }).catch(() => {
        this.sessionLogSuccess.actionTimestamp = "No successful logins found"
      })
    },
    resetUserPassword() {
      api.get('user/requestResetPassword/?email=' + this.user.email).then(() => {
        eventBus.$emit('alert', {key: 'SUCCESSFUL_RESET', variant: 'success'})
        this.$router.push('/')
      }).catch()
    },

    updatePrivileges() {
      api.put('user/change-privileges/' + this.$route.params.id, this.user.accessLevels).then(() => {
        eventBus.$emit('alert', {key: 'PRIVILEGES_CHANGED', variant: 'success'});
        this.refreshUser();
      })
    }
  },
  mounted() {
    this.refreshUser()
  }
}
</script>
<i18n>
{
  "pl": {
    "CheckRoles": "Wybierz role użytkownika",
    "UPDATE": "Zaktualizuj",
    "CONFIRM_HEADER": "Zmiana ról użytkownika",
    "CONFIRM_HEADER_RESET": "Reset hasła użytkownika",
    "CONFIRM_BODY": "Czy na pewno chcesz zaktualizować role użytkownika?",
    "CONFIRM_BODY_RESET": "Czy na pewno chcesz zresetować użytkownika?"
  },
  "en": {
    "CheckRoles": "Choose privileges for user",
    "UPDATE": "Update",
    "CONFIRM_HEADER": "Change user privileges",
    "CONFIRM_HEADER_RESET": "User password reset",
    "CONFIRM_BODY": "Do You really want to change this user privileges?",
    "CONFIRM_BODY_RESET": "Do You really want to reset this user password?"
  }
}
</i18n>
<style scoped>
.slow-transition {
  transition: 1s;
}
</style>