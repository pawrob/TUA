<template>
  <div>
    <b-overlay
        :show="showLoading"
        rounded="sm" class="mt-3">
      <div class="vue-editor">
          <v-md-editor mode="edit" v-model="description" width="800px" height="400px"></v-md-editor>
      </div>
      <b-btn type="button" v-on:click="updateDescription" class="m-3" variant="outline-primary">{{ $t('SAVE_DESC') }}
      </b-btn>


    </b-overlay>
    <b-container class="preview" v-html="markdownToHTML"></b-container>
  </div>
</template>

<script>

import {api} from "@/api";
import marked from 'marked'
import eventBus from "@/event-bus";

export default {
  name: "ChangeDescription",
  data: () => {
    return {
      description: '',
      showLoading: false,
    }
  },

  methods: {
    refreshOffer() {
      this.showLoading = true;
      api.get('moo/entertainer/description').then(response => {
        this.showLoading = false;
        this.description = response.data;
      }).catch(() => {
      })

    },

    updateDescription() {

      if(this.description.length>2048){
        eventBus.$emit('alert', {key: 'ENTERTAINER_DESCRIPTION_TOO_LONG', variant: 'danger'})
        return
      }
      this.showLoading = true;
      api.put('moo/entertainer/description', {
        description: this.description
      }).then(response => {
        this.showLoading = false;
        this.description = response.data;

        eventBus.$emit('alert', {key: 'DESCRIPTION_CHANGED', variant: 'success'})
      }).catch(() => {
      })
    },
  },
  computed: {
    markdownToHTML() {
      return marked(this.description ?? "", {sanitize: true})
    },
  },


  mounted() {
    this.refreshOffer()
  }
}
</script>

<style>

.preview img {
  max-width: 50%;
}

.preview {
  text-align: center;
  display: block;
  margin-left: auto;
  margin-right: auto;
  height: 100%;
  border: 1px #4e4e4e;
  overflow-wrap: break-word;
}

.vue-editor {
  display: block;
  margin-left: auto;
  margin-right: auto;
  width: 800px;
  height: 100%;

}

</style>