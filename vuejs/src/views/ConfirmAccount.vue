<template>
  <div>
    <b-container class="mt-5">
      <b-card bg-variant="info" class="text-center" text-variant="white">
        <template #header>{{ $t('CONFIRM_ACCOUNT') }}</template>

        <b-btn @click="verify">{{ $t('CONFIRM_ACCOUNT_BUTTON') }}</b-btn>
      </b-card>
    </b-container>
  </div>
</template>

<script>
import axios from "axios";
import eventBus from "@/event-bus";

export default {

  name: "ConfirmAccount",
  data(){
    return{
      id: "",
      token: ""
    }
  },
  methods: {
    redirectToHomePage: function () {
      this.$router.push('/');
    },
    verify: function (){
       this.id = this.$route.query.id
       this.token = this.$route.query.token
      axios.put("https://studapp.it.p.lodz.pl:8405/ssbd05/resources/user/verify/?id="+this.id+"&token="+this.token, {

      }, {
        'Access-Control-Allow-Origin': "*",
        'Access-Control-Allow-Headers': origin
      }).then(() => {
          eventBus.$emit('alert', {key:  'ACCOUNT_VERIFIED', variant: 'success'});
        this.redirectToHomePage()
      }).catch(function (error) {
          eventBus.$emit('alert', {key: error.response.data.key, variant: 'danger'});
      });
    },
  }
}
</script>