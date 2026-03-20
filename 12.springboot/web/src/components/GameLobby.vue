<template>
  <div class="game-lobby-container">
    <div class="lobby-card">
      <!-- 用户欢迎信息 -->
      <div class="user-welcome">
        <h1 class="welcome-title">🎮 游戏大厅</h1>
        <div class="user-info">
          <div class="avatar">👤</div>
          <div class="user-details">
            <h2 class="welcome-message">欢迎回来，{{ nickname }}！</h2>
            <p class="high-score">你的最高分：<span class="score-value">{{ highScore }}</span> 分</p>
          </div>
        </div>
      </div>

      <!-- 游戏入口 -->
      <div class="games-section">
        <h3 class="games-title">选择游戏</h3>
        <p class="games-subtitle">点击图标开始游戏，挑战更高分数！</p>
        
        <div class="games-grid">
          <!-- 猜数字游戏 -->
          <div class="game-card" @click="goToGuessNumber">
            <div class="game-icon">🔢</div>
            <h4 class="game-name">猜数字游戏</h4>
            <p class="game-description">猜一个1-100之间的数字，测试你的直觉！</p>
            <el-button type="primary" size="large" class="play-btn">开始游戏</el-button>
          </div>

          <!-- 2048游戏 -->
          <div class="game-card" @click="goTo2048">
            <div class="game-icon">🧩</div>
            <h4 class="game-name">2048</h4>
            <p class="game-description">合并相同数字，挑战2048！</p>
            <el-button type="success" size="large" class="play-btn">开始游戏</el-button>
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button type="info" size="large" @click="goBackToLogin" class="back-btn">
          返回登录
        </el-button>
        <el-button type="warning" size="large" @click="logout" class="logout-btn">
          退出登录
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

// 计算属性：从store获取用户信息
const nickname = computed(() => userStore.nickname)
const highScore = computed(() => userStore.highScore)

// 跳转到猜数字游戏
const goToGuessNumber = () => {
  router.push('/game/guess-number')
}

// 跳转到2048游戏
const goTo2048 = () => {
  router.push('/game/2048')
}

// 返回登录页面
const goBackToLogin = () => {
  router.push('/login')
}

// 退出登录
const logout = () => {
  ElMessageBox.confirm(
    '确定要退出登录吗？',
    '退出登录',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  }).catch(() => {
    // 用户取消
  })
}
</script>

<style scoped>
.game-lobby-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-family: 'Arial', sans-serif;
  padding: 20px;
}

.lobby-card {
  width: 800px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  text-align: center;
  border: 2px solid #4a5568;
}

.user-welcome {
  margin-bottom: 40px;
  text-align: left;
}

.welcome-title {
  color: #2d3748;
  font-size: 32px;
  margin-bottom: 30px;
  text-align: center;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background: #f7fafc;
  border-radius: 15px;
  border: 2px solid #e2e8f0;
}

.avatar {
  font-size: 48px;
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  color: white;
}

.user-details {
  flex: 1;
  text-align: left;
}

.welcome-message {
  color: #2d3748;
  font-size: 24px;
  margin-bottom: 10px;
}

.high-score {
  color: #718096;
  font-size: 18px;
}

.score-value {
  color: #10b981;
  font-weight: bold;
  font-size: 22px;
}

.games-section {
  margin-bottom: 40px;
}

.games-title {
  color: #2d3748;
  font-size: 26px;
  margin-bottom: 10px;
}

.games-subtitle {
  color: #718096;
  font-size: 16px;
  margin-bottom: 30px;
}

.games-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 30px;
}

.game-card {
  padding: 25px;
  background: #f7fafc;
  border-radius: 15px;
  border: 2px solid #e2e8f0;
  transition: all 0.3s ease;
  text-align: center;
  cursor: pointer;
}

.game-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  border-color: #667eea;
}

.game-icon {
  font-size: 48px;
  margin-bottom: 15px;
}

.game-name {
  color: #2d3748;
  font-size: 20px;
  margin-bottom: 10px;
}

.game-description {
  color: #718096;
  font-size: 14px;
  margin-bottom: 20px;
  line-height: 1.5;
}

.play-btn {
  width: 100%;
  border-radius: 10px;
  font-weight: bold;
  letter-spacing: 1px;
  transition: all 0.3s ease;
}

.play-btn:hover {
  transform: translateY(-2px);
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
}

.back-btn, .logout-btn {
  min-width: 150px;
  border-radius: 10px;
  font-weight: bold;
  transition: all 0.3s ease;
}

.back-btn:hover, .logout-btn:hover {
  transform: translateY(-2px);
}
</style>