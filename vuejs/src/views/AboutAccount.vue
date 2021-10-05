<template>
  <div>
    <b-container>
      <b-row>
        <b-col>
          {{ $t('LAST_FAILED_LOGIN') }}
        </b-col>
        <b-col>
          {{ sessionLog.actionTimestamp | formatDate }}
        </b-col>
      </b-row>
      <b-row>
        <b-col>
          {{ $t('LAST_SUCCESSFUL_LOGIN') }}
        </b-col>
        <b-col>
          {{ sessionLogSuccess.actionTimestamp| formatDate  }}
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script>
import {api} from "@/api";

export default {
  name: "Account",
  data: () => {
    return {
      user: {},
      sessionLog: {},
      sessionLogSuccess: {},
      showModal: false
    }
  },
  methods: {
    refreshUser() {

      api.get('user/failedOwnLogin').then(response => {
        this.sessionLog = response.data;
      }).catch(() => {
        this.sessionLog.actionTimestamp = "No failed logins found"
      })


      api.get('user/successOwnLogin').then(response => {
        this.sessionLogSuccess = response.data;
      }).catch(() => {
        this.sessionLogSuccess.actionTimestamp = "No successful logins found"
      })
    },

  },
  mounted() {
    this.refreshUser()
  }
}
</script>


<style scoped>

</style>