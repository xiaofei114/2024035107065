<template>
  <div class="not-found-container">
    <div class="not-found-card">
      <h1 class="error-code">404</h1>
      <h2 class="error-title">页面未找到</h2>
      <p class="error-message">您访问的页面不存在或已被移除</p>
      <div class="action-buttons">
        <el-button type="primary" size="large" @click="goToLogin">返回登录页面</el-button>
        <el-button type="info" size="large" @click="goBack">返回上一页</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

// 返回登录页面
const goToLogin = () => {
  router.push('/login')
}

// 返回上一页
const goBack = () => {
  router.go(-1)
}
</script>

<style scoped>
.not-found-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-family: 'Arial', sans-serif;
}

.not-found-card {
  width: 500px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  text-align: center;
  border: 2px solid #4a5568;
}

.error-code {
  color: #f56565;
  font-size: 80px;
  margin-bottom: 10px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.error-title {
  color: #2d3748;
  font-size: 32px;
  margin-bottom: 20px;
}

.error-message {
  color: #718096;
  font-size: 18px;
  margin-bottom: 40px;
  line-height: 1.5;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.action-buttons .el-button {
  min-width: 150px;
  border-radius: 10px;
  font-weight: bold;
  transition: all 0.3s ease;
}

.action-buttons .el-button:hover {
  transform: translateY(-2px);
}
</style>