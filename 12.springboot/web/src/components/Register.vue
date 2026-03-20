<template>
  <div class="register-container">
    <div class="register-card">
      <!-- 注册标题 -->
      <h1 class="register-title">🎮 游戏注册</h1>

      <!-- 注册表单 -->
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="0"
        class="register-form"
      >
        <!-- 用户名输入框 -->
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            size="large"
            class="register-input"
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
            class="register-input"
            show-password
          />
        </el-form-item>

        <!-- 确认密码输入框 -->
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请确认密码"
            :prefix-icon="Lock"
            size="large"
            class="register-input"
            show-password
          />
        </el-form-item>

        <!-- 邮箱输入框 -->
        <el-form-item prop="email">
          <el-input
            v-model="form.email"
            placeholder="请输入邮箱"
            :prefix-icon="Message"
            size="large"
            class="register-input"
          />
        </el-form-item>

        <!-- 错误提示 -->
        <div v-if="errorMsg" class="error-message">
          {{ errorMsg }}
        </div>

        <!-- 注册按钮 -->
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleRegister"
            class="register-btn"
          >
            {{ loading ? '注册中...' : '立即注册' }}
          </el-button>
        </el-form-item>

        <!-- 返回登录链接 -->
        <div class="login-link">
          <span>已有账号？</span>
          <a href="javascript:void(0)" @click="goToLogin" class="login-text">去登录</a>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Lock, Message } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

// 表单数据
const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: ''
})

// 表单引用，用于验证
const formRef = ref()

// 注册加载状态
const loading = ref(false)

// 错误消息
const errorMsg = ref('')

// 表单验证规则
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const validateEmail = (rule, value, callback) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (value && !emailRegex.test(value)) {
    callback(new Error('请输入有效的邮箱地址'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3-20个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { validator: validateEmail, trigger: 'blur' }
  ]
}

// 注册处理函数
const handleRegister = async () => {
  // 先验证表单
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return

  // 重置错误消息
  errorMsg.value = ''
  loading.value = true

  try {
    // 发送注册请求
    const response = await axios.post('/api/game/register', {
      username: form.username,
      password: form.password,
      email: form.email
    })

    // 注册成功
    if (response.data && response.data.success) {
      ElMessage.success('注册成功！请登录')
      router.push('/login')
    } else {
      // 注册失败
      errorMsg.value = response.data.message || '注册失败，请稍后重试'
      ElMessage.error(errorMsg.value)
    }
  } catch (error) {
    // 网络错误或服务器错误
    errorMsg.value = '注册失败，请检查网络连接'
    ElMessage.error('注册失败，请检查网络连接')
    console.error('注册错误:', error)
  } finally {
    loading.value = false
  }
}

// 跳转到登录页面
const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-family: 'Arial', sans-serif;
}

.register-card {
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  text-align: center;
  border: 2px solid #4a5568;
}

.register-title {
  color: #2d3748;
  font-size: 28px;
  margin-bottom: 30px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.register-form {
  width: 100%;
}

.register-input {
  margin-bottom: 20px;
}

.register-input :deep(.el-input__inner) {
  border-radius: 10px;
  border: 2px solid #cbd5e0;
  transition: all 0.3s ease;
}

.register-input :deep(.el-input__inner:focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.2);
}

.error-message {
  color: #f56565;
  font-size: 14px;
  margin-bottom: 15px;
  text-align: center;
}

.register-btn {
  width: 100%;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  border: none;
  border-radius: 10px;
  font-size: 16px;
  font-weight: bold;
  letter-spacing: 1px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(16, 185, 129, 0.4);
}

.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(16, 185, 129, 0.6);
}

.register-btn:active {
  transform: translateY(1px);
}

.login-link {
  margin-top: 20px;
  color: #718096;
  font-size: 14px;
}

.login-text {
  color: #667eea;
  text-decoration: none;
  font-weight: bold;
  margin-left: 5px;
  transition: color 0.3s ease;
}

.login-text:hover {
  color: #764ba2;
  text-decoration: underline;
}
</style>