<template>
  <div>

    <b-container>

      <b-row>
        <b-col>
          <img v-bind:src="imgurl" class="m-5" alt="gravatar img"/>
        </b-col>
        <b-col>
          <h3 class="m-5">{{ entertainer.login }}<br>{{ entertainer.email }}<br>
            <star-rating class="star mt-2 mb-3" v-model="entertainer.avgRating" increment="0.1" read-only
                         star-size="30"/>
          </h3>
        </b-col>
      </b-row>

    </b-container>
    <div class="preview" v-html="markdownToHTML"></div>
    <b-btn type="button" v-on:click="refreshEntertainer" class="m-3" variant="outline-primary">{{
        $t('REFRESH')
      }}
    </b-btn>

  </div>
</template>

<script>
import {api} from "@/api";
import marked from "marked";
import gravatar from "@/api/gravatar";
import StarRating from "vue-star-rating";

export default {
  name: "EntertainerPreview",
  components: {
    StarRating
  },
  data: () => {
    return {
      description: '',
      imgurl: '',
      entertainer: {},
    }
  },
  methods: {
    refreshEntertainer() {
      this.showLoading = true;
      api.get('moo/entertainer/basicEntertainerInfo/' + this.$route.path.slice(20, 24)).then(response => {
        this.showLoading = false;
        this.entertainer = response.data;
        this.description = this.entertainer.description;
        this.imgurl = gravatar.get_gravatar(this.entertainer.email, 120)
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
    this.refreshEntertainer()
  }
}
</script>

<style scoped>
img {
  border-radius: 50%;
}
.star {
  display: inline-block;
  text-align: center;
  margin-left: auto;
  margin-right: auto;
}
</style>