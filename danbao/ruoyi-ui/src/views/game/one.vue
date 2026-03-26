<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>猜数字游戏</span>
      </div>
      <div class="game-container">
        <div class="game-description">
          <p>我已经想好了一个 1 到 100 之间的整数，请猜猜它是多少。</p>
          <p>你有 <strong>{{ maxAttempts - attempts.length }}</strong> 次机会。</p>
        </div>

        <div class="game-input">
          <el-input
            v-model.number="guess"
            placeholder="请输入你的猜测"
            type="number"
            :min="1"
            :max="100"
            :disabled="gameOver"
            @keyup.enter.native="handleGuess"
            style="width: 200px; margin-right: 10px;"
          />
          <el-button
            type="primary"
            :disabled="!guess || gameOver"
            @click="handleGuess"
          >
            猜一下
          </el-button>
          <el-button
            type="info"
            @click="resetGame"
          >
            重新开始
          </el-button>
        </div>

        <div class="game-feedback" v-if="feedback">
          <el-alert
            :title="feedback"
            :type="feedbackType"
            show-icon
            :closable="false"
          />
        </div>

        <div class="attempts-history" v-if="attempts.length > 0">
          <h3>猜测记录</h3>
          <el-table
            :data="attempts"
            style="width: 100%"
            size="small"
            stripe
          >
            <el-table-column prop="guess" label="猜测值" width="120" align="center" />
            <el-table-column prop="result" label="结果" />
          </el-table>
        </div>

        <div class="game-stats">
          <p>已猜测次数: {{ attempts.length }} / {{ maxAttempts }}</p>
          <p v-if="gameOver && !isWinner">游戏结束！数字是 <strong>{{ targetNumber }}</strong>。</p>
          <p v-if="isWinner">恭喜你！你在第 {{ attempts.length }} 次猜中了！</p>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'GuessNumberGame',
  data() {
    return {
      targetNumber: 0,
      guess: null,
      attempts: [],
      maxAttempts: 10,
      gameOver: false,
      isWinner: false,
      feedback: '',
      feedbackType: 'info'
    }
  },
  created() {
    this.resetGame()
  },
  methods: {
    generateRandomNumber() {
      return Math.floor(Math.random() * 100) + 1
    },
    resetGame() {
      this.targetNumber = this.generateRandomNumber()
      this.guess = null
      this.attempts = []
      this.gameOver = false
      this.isWinner = false
      this.feedback = ''
      this.feedbackType = 'info'
    },
    handleGuess() {
      if (this.gameOver || !this.guess) {
        return
      }

      const guessNum = Number(this.guess)
      if (guessNum < 1 || guessNum > 100) {
        this.feedback = '请输入 1 到 100 之间的数字'
        this.feedbackType = 'warning'
        return
      }

      let result = ''
      let feedback = ''
      let feedbackType = 'info'

      if (guessNum === this.targetNumber) {
        result = '猜中了！'
        feedback = `太棒了！数字 ${guessNum} 是正确的！`
        feedbackType = 'success'
        this.gameOver = true
        this.isWinner = true
      } else if (guessNum < this.targetNumber) {
        result = '太小了'
        feedback = `数字 ${guessNum} 太小了，请再试一次`
        feedbackType = 'warning'
      } else {
        result = '太大了'
        feedback = `数字 ${guessNum} 太大了，请再试一次`
        feedbackType = 'warning'
      }

      this.attempts.unshift({
        guess: guessNum,
        result: result
      })

      this.feedback = feedback
      this.feedbackType = feedbackType

      if (this.attempts.length >= this.maxAttempts && !this.isWinner) {
        this.gameOver = true
        this.feedback = `游戏结束！你已经用了 ${this.maxAttempts} 次机会。正确答案是 ${this.targetNumber}。`
        this.feedbackType = 'error'
      }

      this.guess = null
    }
  }
}
</script>

<style scoped lang="scss">
.game-container {
  padding: 20px;

  .game-description {
    margin-bottom: 20px;
    p {
      margin: 5px 0;
      font-size: 14px;
      color: #606266;
    }
  }

  .game-input {
    margin-bottom: 20px;
    display: flex;
    align-items: center;
  }

  .game-feedback {
    margin-bottom: 20px;
  }

  .attempts-history {
    margin-bottom: 20px;

    h3 {
      margin-bottom: 10px;
      font-size: 16px;
      color: #303133;
    }
  }

  .game-stats {
    p {
      margin: 5px 0;
      font-size: 14px;
      color: #606266;
    }
  }
}
</style>