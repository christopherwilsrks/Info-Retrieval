import Vue from 'vue'
import Router from 'vue-router'
import SearchResult from '../components/SearchResult'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/search',
      name: 'SearchResult',
      component: SearchResult,
      meta: {
        keepAlive: true,
        allowBack: false
      }
    }
  ]
})
