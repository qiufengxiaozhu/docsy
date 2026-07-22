import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('docsy_token') || '')
  const username = ref('')
  const nickname = ref('')

  async function login(loginData: { username: string; password: string }) {
    const res = await request.post('/api/auth/login', loginData)
    if (res.data.code === 200) {
      token.value = res.data.data.token
      username.value = res.data.data.username
      nickname.value = res.data.data.nickname
      localStorage.setItem('docsy_token', token.value)
      return true
    }
    throw new Error(res.data.msg)
  }

  function logout() {
    token.value = ''
    username.value = ''
    nickname.value = ''
    localStorage.removeItem('docsy_token')
  }

  async function fetchInfo() {
    const res = await request.get('/api/auth/info')
    if (res.data.code === 200) {
      username.value = res.data.data.username
    }
  }

  return { token, username, nickname, login, logout, fetchInfo }
})
