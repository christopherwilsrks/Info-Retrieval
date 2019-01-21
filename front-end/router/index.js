import Vue from 'vue'
import Router from 'vue-router'
import FileList from '../components/FileList'

Vue.use(Router)

export default new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      name: 'FileList',
      component: FileList
    }
  ]
})
