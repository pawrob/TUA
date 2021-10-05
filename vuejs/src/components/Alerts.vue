<template>
  <!--  <div>-->
  <div style="width: 100%; position: fixed; top: 70px; z-index: 900;">
    <b-container>
      <div class="position-relative">
        <transition-group name="fade">
          <b-alert v-for="(alert, i) in alerts"
                   :key="alert.id"
                   :show="true"
                   :variant="alert.variant"
                   dismissible
                   @dismissed="removeAlert(i)">{{
              $t('ALERTS.' + alert.key)
            }}
          </b-alert>
        </transition-group>
      </div>
    </b-container>
  </div>
</template>

<script>
import eventBus from '@/event-bus'

export default {
  name: 'Alerts',
  data() {
    return {
      alerts: [],
    }
  },
  methods: {
    removeAlert(i) {
      this.alerts.splice(i, 1)
    },
    pushAlert(alert) {
      let id
      if (this.alerts.length > 0) {
        id = this.alerts[this.alerts.length - 1].id
      } else {
        id = 0
      }
      alert.id = id + 1
      this.alerts.push(alert)
    },
  },
  created() {
    eventBus.$on('alert', this.pushAlert)
  },
}
</script>

<style lang="scss" scoped>
.fade {
  &-enter-active, &-leave-active, &-move {
    transition: all .3s;
  }

  &-enter, &-leave-to {
    opacity: 0;
    transform: translateY(-50%);
  }

  &-leave-active {
    position: absolute;
    left: 0;
    right: 0;
  }
}
</style>
