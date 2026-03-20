<template>
  <div class="game-container">
    <!-- 顶部导航栏 -->
    <div class="game-header">
      <div class="header-content">
        <div class="user-info">
          <span class="user-icon">👤</span>
          <span class="user-nickname">{{ nickname }}</span>
        </div>
        <div class="game-title">🔢 猜数字游戏</div>
        <div class="header-actions">
          <el-button type="info" size="small" @click="goBackToLobby">返回大厅</el-button>
        </div>
      </div>
    </div>

    <!-- 游戏主内容 -->
    <div class="game-main">
      <div class="game-card">
        <!-- 游戏状态 -->
        <div class="game-status">
          <div class="status-item">
            <span class="status-label">目标数字</span>
            <div class="status-value secret-number">?</div>
          </div>
          <div class="status-item">
            <span class="status-label">剩余次数</span>
            <div class="status-value attempts-left">{{ maxAttempts - attempts.length }}</div>
          </div>
          <div class="status-item">
            <span class="status-label">已猜次数</span>
            <div class="status-value">{{ attempts.length }}</div>
          </div>
        </div>

        <!-- 游戏区域 -->
        <div class="game-area">
          <h3 class="game-instruction">猜一个 1-100 之间的数字</h3>
          
          <!-- 输入区域 -->
          <div class="input-area">
            <el-input
              v-model.number="currentGuess"
              type="number"
              placeholder="输入你的猜测"
              size="large"
              :min="1"
              :max="100"
              class="guess-input"
              @keyup.enter="submitGuess"
            >
              <template #append>
                <el-button type="primary" @click="submitGuess" :disabled="!canGuess">猜！</el-button>
              </template>
            </el-input>
          </div>

          <!-- 提示信息 -->
          <div v-if="message" class="message" :class="messageType">
            {{ message }}
          </div>

          <!-- 历史记录 -->
          <div class="history-section" v-if="attempts.length > 0">
            <h4>猜数字记录</h4>
            <div class="history-list">
              <div 
                v-for="(attempt, index) in attempts" 
                :key="index" 
                class="history-item"
                :class="getAttemptClass(attempt.result)"
              >
                <span class="attempt-number">#{{ index + 1 }}</span>
                <span class="attempt-guess">猜测: {{ attempt.guess }}</span>
                <span class="attempt-result">{{ getResultText(attempt.result) }}</span>
              </div>
            </div>
          </div>

          <!-- 游戏控制 -->
          <div class="game-controls">
            <el-button 
              type="primary" 
              size="large" 
              @click="startNewGame" 
              :disabled="gameState === 'playing'"
            >
              开始新游戏
            </el-button>
            <el-button 
              type="warning" 
              size="large" 
              @click="showHint" 
              :disabled="gameState !== 'playing'"
            >
              提示
            </el-button>
            <el-button 
              type="info" 
              size="large" 
              @click="goBackToLobby"
            >
              返回大厅
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

// 用户昵称
const nickname = computed(() => userStore.nickname)

// 游戏状态
const secretNumber = ref(0)
const currentGuess = ref('')
const attempts = ref([])
const gameState = ref('idle') // 'idle', 'playing', 'won', 'lost'
const message = ref('')
const messageType = ref('') // 'success', 'error', 'info'
const maxAttempts = 10

// 计算属性：是否还可以猜
const canGuess = computed(() => {
  return gameState.value === 'playing' && 
         currentGuess.value >= 1 && 
         currentGuess.value <= 100 &&
         attempts.value.length < maxAttempts
})

// 初始化游戏
const initGame = () => {
  secretNumber.value = Math.floor(Math.random() * 100) + 1
  currentGuess.value = ''
  attempts.value = []
  gameState.value = 'playing'
  message.value = '游戏开始！猜一个1-100之间的数字。'
  messageType.value = 'info'
  console.log('目标数字:', secretNumber.value) // 开发用，实际游戏中应隐藏
}

// 提交猜测
const submitGuess = () => {
  if (!canGuess.value) return

  const guess = parseInt(currentGuess.value)
  if (isNaN(guess) || guess < 1 || guess > 100) {
    message.value = '请输入1-100之间的有效数字！'
    messageType.value = 'error'
    return
  }

  // 检查是否重复猜测
  if (attempts.value.some(attempt => attempt.guess === guess)) {
    message.value = '你已经猜过这个数字了！'
    messageType.value = 'error'
    return
  }

  let result
  if (guess === secretNumber.value) {
    result = 'correct'
    gameState.value = 'won'
    message.value = `🎉 恭喜你猜对了！数字就是 ${secretNumber.value}！`
    messageType.value = 'success'
    
    // 计算分数并更新最高分
    const score = calculateScore()
    userStore.updateHighScore(userStore.highScore + score)
    ElMessage.success(`获得 ${score} 分！`)
  } else if (guess < secretNumber.value) {
    result = 'low'
    message.value = `📈 猜小了！试试更大的数字。`
    messageType.value = 'info'
  } else {
    result = 'high'
    message.value = `📉 猜大了！试试更小的数字。`
    messageType.value = 'info'
  }

  attempts.value.push({ guess, result })
  currentGuess.value = ''

  // 检查是否用完次数
  if (attempts.value.length >= maxAttempts && gameState.value !== 'won') {
    gameState.value = 'lost'
    message.value = `😔 游戏结束！正确数字是 ${secretNumber.value}。`
    messageType.value = 'error'
  }
}

// 计算分数
const calculateScore = () => {
  const attemptsUsed = attempts.value.length
  // 使用的次数越少，分数越高
  return Math.max(1, 100 - attemptsUsed * 10)
}

// 获取尝试结果的文本
const getResultText = (result) => {
  const texts = {
    'correct': '🎯 正确',
    'low': '📈 太小',
    'high': '📉 太大'
  }
  return texts[result] || result
}

// 获取尝试结果的CSS类
const getAttemptClass = (result) => {
  return `attempt-${result}`
}

// 显示提示
const showHint = () => {
  if (gameState.value !== 'playing') return
  
  // 根据历史记录给出提示
  if (attempts.value.length === 0) {
    message.value = '提示：数字在1-100之间，先猜一个试试！'
  } else {
    const lastAttempt = attempts.value[attempts.value.length - 1]
    if (lastAttempt.result === 'low') {
      message.value = `提示：上次猜小了，试试 ${lastAttempt.guess + 1} 到 100 之间的数字。`
    } else if (lastAttempt.result === 'high') {
      message.value = `提示：上次猜大了，试试 1 到 ${lastAttempt.guess - 1} 之间的数字。`
    }
  }
  messageType.value = 'info'
}

// 开始新游戏
const startNewGame = () => {
  initGame()
}

// 返回游戏大厅
const goBackToLobby = () => {
  router.push('/game-lobby')
}

// 组件挂载时初始化游戏
onMounted(() => {
  initGame()
})
</script>

<style scoped>
.game-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-family: 'Arial', sans-serif;
}

.game-header {
  background: rgba(255, 255, 255, 0.95);
  padding: 15px 0;
  border-bottom: 2px solid #4a5568;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.header-content {
  max-width: 1000px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  color: #2d3748;
}

.user-icon {
  font-size: 20px;
}

.user-nickname {
  font-weight: bold;
  color: #667eea;
}

.game-title {
  font-size: 24px;
  font-weight: bold;
  color: #2d3748;
}

.game-main {
  max-width: 800px;
  margin: 30px auto;
  padding: 0 20px;
}

.game-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  border: 2px solid #4a5568;
}

.game-status {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 30px;
  padding: 20px;
  background: #f7fafc;
  border-radius: 15px;
  border: 2px solid #e2e8f0;
}

.status-item {
  text-align: center;
}

.status-label {
  display: block;
  color: #718096;
  font-size: 14px;
  margin-bottom: 8px;
}

.status-value {
  font-size: 24px;
  font-weight: bold;
  color: #2d3748;
}

.secret-number {
  font-size: 36px;
  color: #f56565;
}

.attempts-left {
  color: #10b981;
}

.game-area {
  text-align: center;
}

.game-instruction {
  color: #2d3748;
  font-size: 20px;
  margin-bottom: 30px;
}

.input-area {
  margin-bottom: 20px;
}

.guess-input {
  max-width: 400px;
  margin: 0 auto;
}

.guess-input :deep(.el-input__inner) {
  text-align: center;
  font-size: 18px;
  font-weight: bold;
}

.message {
  padding: 15px;
  border-radius: 10px;
  margin: 20px 0;
  font-size: 16px;
  font-weight: bold;
}

.message.success {
  background: #c6f6d5;
  color: #22543d;
  border: 2px solid #9ae6b4;
}

.message.error {
  background: #fed7d7;
  color: #742a2a;
  border: 2px solid #fc8181;
}

.message.info {
  background: #bee3f8;
  color: #2a4365;
  border: 2px solid #90cdf4;
}

.history-section {
  margin: 30px 0;
  text-align: left;
}

.history-section h4 {
  color: #2d3748;
  margin-bottom: 15px;
  font-size: 18px;
}

.history-list {
  max-height: 200px;
  overflow-y: auto;
  padding: 10px;
  background: #f7fafc;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
}

.history-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  margin-bottom: 8px;
  border-radius: 8px;
  font-size: 14px;
}

.attempt-correct {
  background: #c6f6d5;
  color: #22543d;
  border: 1px solid #9ae6b4;
}

.attempt-low {
  background: #fed7d7;
  color: #742a2a;
  border: 1px solid #fc8181;
}

.attempt-high {
  background: #fed7d7;
  color: #742a2a;
  border: 1px solid #fc8181;
}

.attempt-number {
  font-weight: bold;
  min-width: 40px;
}

.attempt-guess {
  flex: 1;
  text-align: center;
  font-weight: bold;
}

.attempt-result {
  min-width: 80px;
  text-align: right;
  font-weight: bold;
}

.game-controls {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
}

.game-controls .el-button {
  min-width: 150px;
  border-radius: 10px;
  font-weight: bold;
}
</style>