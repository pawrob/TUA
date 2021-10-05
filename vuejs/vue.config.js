// vue.config.js

/**
 * @type {import('@vue/cli-service').ProjectOptions}
 */
module.exports = {
  publicPath: process.env.NODE_ENV === 'production'
    ? '/ssbd05/'
    : './',

  chainWebpack: config => {
    config.module
      .rule('i18n')
      .resourceQuery(/blockType=i18n/)
      .type('javascript/auto')
      .use('i18n')
      .loader('@kazupon/vue-i18n-loader')
      .end()
  },

  configureWebpack: {
    devServer: {
      proxy: 'https://localhost:8181/',
    },
  },
}
