<template>
  <div class="game-container">
    <!-- 顶部导航栏 -->
    <div class="game-header">
      <div class="header-content">
        <div class="user-info">
          <span class="user-icon">👤</span>
          <span class="user-nickname">{{ nickname }}</span>
        </div>
        <div class="game-title">🧩 2048 游戏</div>
        <div class="header-actions">
          <el-button type="info" size="small" @click="goBackToLobby">返回大厅</el-button>
        </div>
      </div>
    </div>

    <!-- 游戏主内容 -->
    <div class="game-main">
      <div class="game-card">
        <!-- 游戏信息 -->
        <div class="game-info">
          <div class="score-board">
            <div class="score-item">
              <span class="score-label">分数</span>
              <div class="score-value">{{ score }}</div>
            </div>
            <div class="score-item">
              <span class="score-label">最高分</span>
              <div class="score-value">{{ highScore }}</div>
            </div>
            <div class="score-item">
              <span class="score-label">目标</span>
              <div class="score-value target">2048</div>
            </div>
          </div>

          <div class="game-controls-top">
            <el-button type="primary" size="large" @click="startNewGame">新游戏</el-button>
            <el-button type="warning" size="large" @click="undoMove" :disabled="!canUndo">撤销</el-button>
            <el-button type="info" size="large" @click="goBackToLobby">返回大厅</el-button>
          </div>
        </div>

        <!-- 游戏网格 -->
        <div class="game-grid-container">
          <div class="grid-header">
            <h3>使用方向键或按钮移动方块</h3>
            <p class="game-instruction">合并相同数字的方块，目标是获得 2048！</p>
          </div>

          <div class="game-grid">
            <div 
              v-for="(row, rowIndex) in grid" 
              :key="rowIndex" 
              class="grid-row"
            >
              <div 
                v-for="(cell, colIndex) in row" 
                :key="colIndex" 
                class="grid-cell"
                :class="`cell-${cell || 'empty'}`"
              >
                <span v-if="cell" class="cell-value">{{ cell }}</span>
              </div>
            </div>
          </div>

          <!-- 移动按钮（移动端友好） -->
          <div class="mobile-controls">
            <div class="control-row">
              <el-button class="control-btn" @click="move('up')">↑</el-button>
            </div>
            <div class="control-row">
              <el-button class="control-btn" @click="move('left')">←</el-button>
              <el-button class="control-btn" @click="move('down')">↓</el-button>
              <el-button class="control-btn" @click="move('right')">→</el-button>
            </div>
          </div>

          <!-- 游戏状态 -->
          <div v-if="gameState === 'won'" class="game-message success">
            <h3>🎉 恭喜！你达到了 2048！</h3>
            <p>继续游戏挑战更高分！</p>
            <el-button type="success" @click="continueGame">继续游戏</el-button>
          </div>

          <div v-if="gameState === 'lost'" class="game-message error">
            <h3>😔 游戏结束！</h3>
            <p>无法再移动方块了。</p>
            <el-button type="primary" @click="startNewGame">再来一次</el-button>
          </div>

          <!-- 游戏提示 -->
          <div class="game-tips">
            <h4>游戏规则：</h4>
            <ul>
              <li>使用方向键或按钮移动所有方块</li>
              <li>相同数字的方块碰撞时会合并</li>
              <li>每次移动后会在空白处生成一个 2 或 4</li>
              <li>目标是合并出 2048 方块</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

// 用户昵称
const nickname = computed(() => userStore.nickname)

// 游戏状态
const grid = ref(Array(4).fill().map(() => Array(4).fill(0)))
const score = ref(0)
const highScore = computed(() => userStore.highScore)
const gameState = ref('playing') // 'playing', 'won', 'lost'
const history = ref([]) // 用于撤销

// 计算属性：是否可以撤销
const canUndo = computed(() => history.value.length > 0)

// 初始化游戏
const initGame = () => {
  // 清空网格
  grid.value = Array(4).fill().map(() => Array(4).fill(0))
  score.value = 0
  gameState.value = 'playing'
  history.value = []
  
  // 添加两个初始方块
  addRandomTile()
  addRandomTile()
  
  // 保存初始状态到历史记录
  saveState()
}

// 添加随机方块
const addRandomTile = () => {
  const emptyCells = []
  
  // 找到所有空单元格
  for (let i = 0; i < 4; i++) {
    for (let j = 0; j < 4; j++) {
      if (grid.value[i][j] === 0) {
        emptyCells.push({ row: i, col: j })
      }
    }
  }
  
  if (emptyCells.length > 0) {
    // 随机选择一个空单元格
    const randomCell = emptyCells[Math.floor(Math.random() * emptyCells.length)]
    // 90% 概率生成 2，10% 概率生成 4
    const value = Math.random() < 0.9 ? 2 : 4
    grid.value[randomCell.row][randomCell.col] = value
  }
}

// 保存当前状态到历史记录
const saveState = () => {
  const state = {
    grid: JSON.parse(JSON.stringify(grid.value)),
    score: score.value
  }
  history.value.push(state)
  
  // 只保留最近10步
  if (history.value.length > 10) {
    history.value.shift()
  }
}

// 撤销上一步
const undoMove = () => {
  if (history.value.length > 1) {
    // 移除当前状态
    history.value.pop()
    // 恢复到上一步状态
    const prevState = history.value[history.value.length - 1]
    grid.value = JSON.parse(JSON.stringify(prevState.grid))
    score.value = prevState.score
    gameState.value = 'playing'
  }
}

// 移动方块
const move = (direction) => {
  if (gameState.value !== 'playing') return
  
  // 保存移动前的状态
  const oldGrid = JSON.parse(JSON.stringify(grid.value))
  const oldScore = score.value
  
  let moved = false
  
  // 根据方向处理移动
  switch (direction) {
    case 'up':
      moved = moveUp()
      break
    case 'down':
      moved = moveDown()
      break
    case 'left':
      moved = moveLeft()
      break
    case 'right':
      moved = moveRight()
      break
  }
  
  // 如果有移动，添加新方块并检查游戏状态
  if (moved) {
    addRandomTile()
    saveState()
    checkGameState()
    
    // 更新最高分
    if (score.value > userStore.highScore) {
      userStore.updateHighScore(score.value)
    }
  }
}

// 向上移动
const moveUp = () => {
  let moved = false
  for (let col = 0; col < 4; col++) {
    const column = []
    for (let row = 0; row < 4; row++) {
      column.push(grid.value[row][col])
    }
    
    const newColumn = mergeLine(column)
    
    for (let row = 0; row < 4; row++) {
      if (grid.value[row][col] !== newColumn[row]) {
        moved = true
      }
      grid.value[row][col] = newColumn[row]
    }
  }
  return moved
}

// 向下移动
const moveDown = () => {
  let moved = false
  for (let col = 0; col < 4; col++) {
    const column = []
    for (let row = 3; row >= 0; row--) {
      column.push(grid.value[row][col])
    }
    
    const newColumn = mergeLine(column)
    
    for (let row = 3; row >= 0; row--) {
      if (grid.value[row][col] !== newColumn[3 - row]) {
        moved = true
      }
      grid.value[row][col] = newColumn[3 - row]
    }
  }
  return moved
}

// 向左移动
const moveLeft = () => {
  let moved = false
  for (let row = 0; row < 4; row++) {
    const line = [...grid.value[row]]
    const newLine = mergeLine(line)
    
    for (let col = 0; col < 4; col++) {
      if (grid.value[row][col] !== newLine[col]) {
        moved = true
      }
      grid.value[row][col] = newLine[col]
    }
  }
  return moved
}

// 向右移动
const moveRight = () => {
  let moved = false
  for (let row = 0; row < 4; row++) {
    const line = [...grid.value[row]].reverse()
    const newLine = mergeLine(line).reverse()
    
    for (let col = 0; col < 4; col++) {
      if (grid.value[row][col] !== newLine[col]) {
        moved = true
      }
      grid.value[row][col] = newLine[col]
    }
  }
  return moved
}

// 合并一行/列
const mergeLine = (line) => {
  // 过滤掉零
  let filtered = line.filter(cell => cell !== 0)
  
  // 合并相同的相邻数字
  for (let i = 0; i < filtered.length - 1; i++) {
    if (filtered[i] === filtered[i + 1]) {
      filtered[i] *= 2
      score.value += filtered[i]
      filtered.splice(i + 1, 1)
    }
  }
  
  // 填充零到长度4
  while (filtered.length < 4) {
    filtered.push(0)
  }
  
  return filtered
}

// 检查游戏状态
const checkGameState = () => {
  // 检查是否达到2048
  for (let row = 0; row < 4; row++) {
    for (let col = 0; col < 4; col++) {
      if (grid.value[row][col] === 2048 && gameState.value !== 'won') {
        gameState.value = 'won'
        ElMessage.success('恭喜！你达到了 2048！')
        return
      }
    }
  }
  
  // 检查是否还有空单元格
  for (let row = 0; row < 4; row++) {
    for (let col = 0; col < 4; col++) {
      if (grid.value[row][col] === 0) {
        return // 还有空单元格，游戏继续
      }
    }
  }
  
  // 检查是否还能移动
  for (let row = 0; row < 4; row++) {
    for (let col = 0; col < 4; col++) {
      const current = grid.value[row][col]
      
      // 检查右侧
      if (col < 3 && grid.value[row][col + 1] === current) {
        return // 可以向右合并
      }
      
      // 检查下方
      if (row < 3 && grid.value[row + 1][col] === current) {
        return // 可以向下合并
      }
    }
  }
  
  // 无法移动，游戏结束
  gameState.value = 'lost'
  ElMessage.error('游戏结束！无法再移动了。')
}

// 继续游戏（达到2048后）
const continueGame = () => {
  gameState.value = 'playing'
}

// 开始新游戏
const startNewGame = () => {
  initGame()
  ElMessage.info('新游戏开始！')
}

// 返回游戏大厅
const goBackToLobby = () => {
  router.push('/game-lobby')
}

// 键盘事件处理
const handleKeyDown = (event) => {
  if (gameState.value !== 'playing') return
  
  switch (event.key) {
    case 'ArrowUp':
      event.preventDefault()
      move('up')
      break
    case 'ArrowDown':
      event.preventDefault()
      move('down')
      break
    case 'ArrowLeft':
      event.preventDefault()
      move('left')
      break
    case 'ArrowRight':
      event.preventDefault()
      move('right')
      break
  }
}

// 组件挂载时初始化游戏和键盘事件
onMounted(() => {
  initGame()
  window.addEventListener('keydown', handleKeyDown)
})

// 组件卸载时移除键盘事件
onUnmounted(() => {
  window.removeEventListener('keydown', handleKeyDown)
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

.game-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px;
  background: #f7fafc;
  border-radius: 15px;
  border: 2px solid #e2e8f0;
}

.score-board {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.score-item {
  text-align: center;
  padding: 10px 20px;
  background: white;
  border-radius: 10px;
  border: 2px solid #e2e8f0;
}

.score-label {
  display: block;
  color: #718096;
  font-size: 14px;
  margin-bottom: 5px;
}

.score-value {
  font-size: 24px;
  font-weight: bold;
  color: #2d3748;
}

.target {
  color: #10b981;
  font-size: 20px;
}

.game-controls-top {
  display: flex;
  gap: 15px;
}

.game-grid-container {
  text-align: center;
}

.grid-header {
  margin-bottom: 20px;
}

.grid-header h3 {
  color: #2d3748;
  font-size: 20px;
  margin-bottom: 10px;
}

.game-instruction {
  color: #718096;
  font-size: 14px;
}

.game-grid {
  display: inline-block;
  background: #bbada0;
  border-radius: 10px;
  padding: 10px;
  margin: 20px 0;
}

.grid-row {
  display: flex;
}

.grid-cell {
  width: 80px;
  height: 80px;
  margin: 5px;
  background: rgba(238, 228, 218, 0.35);
  border-radius: 5px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: bold;
  color: #776e65;
}

.cell-2 { background: #eee4da; color: #776e65; }
.cell-4 { background: #ede0c8; color: #776e65; }
.cell-8 { background: #f2b179; color: #f9f6f2; }
.cell-16 { background: #f59563; color: #f9f6f2; }
.cell-32 { background: #f67c5f; color: #f9f6f2; }
.cell-64 { background: #f65e3b; color: #f9f6f2; }
.cell-128 { background: #edcf72; color: #f9f6f2; font-size: 20px; }
.cell-256 { background: #edcc61; color: #f9f6f2; font-size: 20px; }
.cell-512 { background: #edc850; color: #f9f6f2; font-size: 20px; }
.cell-1024 { background: #edc53f; color: #f9f6f2; font-size: 18px; }
.cell-2048 { background: #edc22e; color: #f9f6f2; font-size: 18px; }

.cell-empty { background: rgba(238, 228, 218, 0.35); }

.cell-value {
  font-weight: bold;
}

.mobile-controls {
  margin: 20px 0;
  display: none; /* 默认隐藏，在移动端显示 */
}

.control-row {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin: 10px 0;
}

.control-btn {
  width: 60px;
  height: 60px;
  font-size: 24px;
  font-weight: bold;
}

.game-message {
  padding: 20px;
  border-radius: 10px;
  margin: 20px 0;
  text-align: center;
}

.success {
  background: #c6f6d5;
  color: #22543d;
  border: 2px solid #9ae6b4;
}

.error {
  background: #fed7d7;
  color: #742a2a;
  border: 2px solid #fc8181;
}

.game-tips {
  margin-top: 30px;
  padding: 20px;
  background: #f7fafc;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  text-align: left;
}

.game-tips h4 {
  color: #2d3748;
  margin-bottom: 10px;
}

.game-tips ul {
  color: #718096;
  padding-left: 20px;
}

.game-tips li {
  margin-bottom: 5px;
  line-height: 1.5;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .game-info {
    flex-direction: column;
    gap: 20px;
  }
  
  .score-board {
    width: 100%;
  }
  
  .mobile-controls {
    display: block;
  }
  
  .grid-cell {
    width: 60px;
    height: 60px;
    font-size: 18px;
  }
  
  .cell-128, .cell-256, .cell-512 {
    font-size: 16px;
  }
  
  .cell-1024, .cell-2048 {
    font-size: 14px;
  }
}
</style>