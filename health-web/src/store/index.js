import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

const store = new Vuex.Store({
  state: {
    token: localStorage.getItem('Token') || '',
    user: JSON.parse(localStorage.getItem('User') || 'null'),
    roles: JSON.parse(localStorage.getItem('Roles') || '[]')
  },
  mutations: {
    SET_TOKEN(state, token) {
      state.token = token
      localStorage.setItem('Token', token)
    },
    SET_USER(state, user) {
      state.user = user
      localStorage.setItem('User', JSON.stringify(user))
    },
    SET_ROLES(state, roles) {
      state.roles = roles
      localStorage.setItem('Roles', JSON.stringify(roles))
    },
    RESET_STATE(state) {
      state.token = ''
      state.user = null
      state.roles = []
      localStorage.removeItem('Token')
      localStorage.removeItem('User')
      localStorage.removeItem('Roles')
    }
  },
  actions: {
    logout({ commit }) {
      commit('RESET_STATE')
    }
  },
  getters: {
    token: state => state.token,
    user: state => state.user,
    roles: state => state.roles,
    isLoggedIn: state => !!state.token
  }
})

export default store