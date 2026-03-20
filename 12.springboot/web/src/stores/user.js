import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

// 从localStorage加载用户信息的辅助函数
const loadUserFromStorage = () => {
  const stored = localStorage.getItem('game-user')
  if (stored) {
    try {
      return JSON.parse(stored)
    } catch (error) {
      console.error('解析localStorage中的用户信息失败:', error)
      return null
    }
  }
  return null
}

// 保存用户信息到localStorage的辅助函数
const saveUserToStorage = (user) => {
  if (user) {
    localStorage.setItem('game-user', JSON.stringify(user))
  } else {
    localStorage.removeItem('game-user')
  }
}

export const useUserStore = defineStore('user', () => {
  // 用户信息状态
  const user = ref(null)

  // 初始化时从localStorage加载
  const storedUser = loadUserFromStorage()
  if (storedUser) {
    user.value = storedUser
  }

  // 计算属性：是否已登录
  const isLoggedIn = computed(() => !!user.value)

  // 计算属性：用户昵称
  const nickname = computed(() => user.value?.nickname || '')

  // 计算属性：最高分
  const highScore = computed(() => user.value?.highScore || 0)

  // 登录：保存用户信息
  const login = (userData) => {
    user.value = {
      username: userData.username,
      nickname: userData.nickname || userData.username,
      highScore: userData.highScore || 0
    }
    saveUserToStorage(user.value)
  }

  // 更新最高分
  const updateHighScore = (newScore) => {
    if (user.value && newScore > user.value.highScore) {
      user.value.highScore = newScore
      saveUserToStorage(user.value)
    }
  }

  // 登出：清除用户信息
  const logout = () => {
    user.value = null
    saveUserToStorage(null)
  }

  return {
    user,
    isLoggedIn,
    nickname,
    highScore,
    login,
    updateHighScore,
    logout
  }
})