<template>
  <div class="game-select-container">
    <div class="game-select-card">
      <!-- 游戏选择标题 -->
      <h1 class="game-select-title">🎮 游戏选择</h1>
      <p class="welcome-message">欢迎来到游戏大厅，请选择您想玩的游戏</p>

      <!-- 游戏列表 -->
      <div class="game-list">
        <!-- 游戏1 -->
        <div class="game-item">
          <div class="game-icon">🎯</div>
          <h3 class="game-name">射击游戏</h3>
          <p class="game-description">快节奏的射击对战游戏</p>
          <el-button type="primary" size="large" class="play-btn">开始游戏</el-button>
        </div>

        <!-- 游戏2 -->
        <div class="game-item">
          <div class="game-icon">♟️</div>
          <h3 class="game-name">策略游戏</h3>
          <p class="game-description">需要智谋的策略对战游戏</p>
          <el-button type="success" size="large" class="play-btn">开始游戏</el-button>
        </div>

        <!-- 游戏3 -->
        <div class="game-item">
          <div class="game-icon">🎲</div>
          <h3 class="game-name">益智游戏</h3>
          <p class="game-description">锻炼大脑的益智解谜游戏</p>
          <el-button type="warning" size="large" class="play-btn">开始游戏</el-button>
        </div>

        <!-- 游戏4 -->
        <div class="game-item">
          <div class="game-icon">🏎️</div>
          <h3 class="game-name">竞速游戏</h3>
          <p class="game-description">刺激的赛车竞速游戏</p>
          <el-button type="danger" size="large" class="play-btn">开始游戏</el-button>
        </div>
      </div>

      <!-- 返回登录页面 -->
      <div class="back-link">
        <el-button type="text" @click="goBackToLogin">返回登录页面</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

// 返回登录页面
const goBackToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.game-select-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-family: 'Arial', sans-serif;
  padding: 20px;
}

.game-select-card {
  width: 800px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  text-align: center;
  border: 2px solid #4a5568;
}

.game-select-title {
  color: #2d3748;
  font-size: 32px;
  margin-bottom: 10px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.welcome-message {
  color: #718096;
  font-size: 16px;
  margin-bottom: 40px;
}

.game-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 30px;
  margin-bottom: 40px;
}

.game-item {
  padding: 25px;
  background: #f7fafc;
  border-radius: 15px;
  border: 2px solid #e2e8f0;
  transition: all 0.3s ease;
  text-align: center;
}

.game-item:hover {
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

.back-link {
  margin-top: 20px;
}

.back-link .el-button {
  color: #667eea;
  font-size: 14px;
}

.back-link .el-button:hover {
  color: #764ba2;
}
</style>