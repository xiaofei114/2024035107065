<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

// 表单数据
const form = reactive({
  username: '',
  password: ''
})

// 表单引用，用于验证
const formRef = ref()

// 登录加载状态
const loading = ref(false)

// 错误消息
const errorMsg = ref('')

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

// 登录处理函数
const handleLogin = async () => {
  // 先验证表单
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return

  // 重置错误消息
  errorMsg.value = ''
  loading.value = true

  try {
    // 创建表单数据，使用URLSearchParams发送application/x-www-form-urlencoded格式
    const formData = new URLSearchParams()
    formData.append('username', form.username)
    formData.append('password', form.password)

    // 发送登录请求（Vite代理会将/api转发到localhost:8080）
    const response = await axios.post('/api/game/login', formData, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    })

    // 登录成功：检查code字段是否为200
    if (response.data && response.data.code === 200) {
      const username = form.username
      const nickname = response.data.data?.nickname || username
      const highScore = response.data.data?.highScore || 0
      
      // 保存用户信息到Pinia store和localStorage
      userStore.login({
        username,
        nickname,
        highScore
      })
      
      ElMessage.success(`欢迎回来，${nickname}！`)
      // 跳转到游戏大厅页面
      router.push('/game-lobby')
    } else {
      // 登录失败：检查code字段是否为401或其他错误
      errorMsg.value = response.data.msg || '用户名或密码错误'
      ElMessage.error(response.data.msg || '用户名或密码错误')
    }
  } catch (error) {
    // 网络错误或服务器错误
    errorMsg.value = '登录失败，请检查网络连接'
    ElMessage.error('登录失败，请检查网络连接')
    console.error('登录错误:', error)
  } finally {
    loading.value = false
  }
}

// 跳转到注册页面
const goToRegister = () => {
  router.push('/register')
}
</script>

<template>
  <div class="game-login-container">
    <div class="login-card">
      <!-- 游戏风格标题 -->
      <h1 class="game-title">🎮 游戏登录</h1>

      <!-- 登录表单 -->
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="0"
        class="login-form"
      >
        <!-- 用户名输入框 -->
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            size="large"
            class="game-input"
          />
        </el-form-item>

        <!-- 密码输入框 -->
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            size="large"
            class="game-input"
            show-password
          />
        </el-form-item>

        <!-- 错误提示 -->
        <div v-if="errorMsg" class="error-message">
          {{ errorMsg }}
        </div>

        <!-- 登录按钮 -->
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleLogin"
            class="game-login-btn"
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>

        <!-- 注册链接 -->
        <div class="register-link">
          <span>还没有账号？</span>
          <a href="javascript:void(0)" @click="goToRegister" class="register-text">去注册</a>
        </div>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.game-login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-family: 'Arial', sans-serif;
}

.login-card {
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  text-align: center;
  border: 2px solid #4a5568;
}

.game-title {
  color: #2d3748;
  font-size: 28px;
  margin-bottom: 30px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.login-form {
  width: 100%;
}

.game-input {
  margin-bottom: 20px;
}

.game-input :deep(.el-input__inner) {
  border-radius: 10px;
  border: 2px solid #cbd5e0;
  transition: all 0.3s ease;
}

.game-input :deep(.el-input__inner:focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.2);
}

.error-message {
  color: #f56565;
  font-size: 14px;
  margin-bottom: 15px;
  text-align: center;
}

.game-login-btn {
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 10px;
  font-size: 16px;
  font-weight: bold;
  letter-spacing: 1px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.game-login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
}

.game-login-btn:active {
  transform: translateY(1px);
}

.register-link {
  margin-top: 20px;
  color: #718096;
  font-size: 14px;
}

.register-text {
  color: #667eea;
  text-decoration: none;
  font-weight: bold;
  margin-left: 5px;
  transition: color 0.3s ease;
}

.register-text:hover {
  color: #764ba2;
  text-decoration: underline;
}
</style>