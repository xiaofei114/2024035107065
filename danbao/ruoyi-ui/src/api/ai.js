import request from '@/utils/request'

/**
 * AI聊天请求（支持上下文）
 * @param {string} message - 用户输入的消息内容
 * @param {string} sessionId - 会话ID（用于上下文记忆）
 * @param {number} timeout - 超时时间（毫秒，默认60000）
 * @returns {Promise} - 返回Promise对象
 */
export function requestAiChat(message, sessionId = null, timeout = 60000) {
  return new Promise((resolve, reject) => {
    const data = {
      message: message,
      sessionId: sessionId,
      temperature: 0.7,
      maxTokens: 2048
    }

    request({
      url: '/ai/chat',
      method: 'post',
      data: data,
      timeout: timeout,
      headers: {
        'Content-Type': 'application/json'
      }
    })
      .then(response => {
        if (response.code === 200 && response.data) {
          if (response.data.status === 'success') {
            resolve({
              success: true,
              content: response.data.content,
              model: response.data.model,
              responseTime: response.data.responseTime,
              raw: response.data
            })
          } else {
            reject({
              success: false,
              message: response.data.errorMessage || 'AI服务返回错误',
              raw: response.data
            })
          }
        } else {
          reject({
            success: false,
            message: response.msg || '请求失败',
            raw: response
          })
        }
      })
      .catch(error => {
        let errorMessage = '网络请求失败'

        if (error.code === 'ECONNABORTED' || error.message.includes('timeout')) {
          errorMessage = '请求超时，AI服务响应较慢，请稍后重试'
        } else if (error.message.includes('Network Error')) {
          errorMessage = '网络连接失败，请检查网络设置'
        } else if (error.response) {
          const status = error.response.status
          switch (status) {
            case 401:
              errorMessage = '未授权，请重新登录'
              break
            case 403:
              errorMessage = '拒绝访问'
              break
            case 404:
              errorMessage = '请求地址不存在'
              break
            case 500:
              errorMessage = '服务器内部错误'
              break
            case 503:
              errorMessage = '服务不可用，请稍后重试'
              break
            default:
              errorMessage = `请求失败 (${status})`
          }
        } else if (error.request) {
          errorMessage = '服务器无响应'
        } else {
          errorMessage = error.message || '请求发生错误'
        }

        reject({
          success: false,
          message: errorMessage,
          raw: error
        })
      })
  })
}

/**
 * AI流式聊天请求（SSE）
 * @param {string} message - 用户输入的消息内容
 * @param {string} sessionId - 会话ID
 * @param {Function} onMessage - 收到消息回调
 * @param {Function} onComplete - 完成回调
 * @param {Function} onError - 错误回调
 * @returns {EventSource} - EventSource对象，可用于关闭连接
 */
export function requestAiChatStream(message, sessionId = null, onMessage, onComplete, onError) {
  const data = {
    message: message,
    sessionId: sessionId,
    temperature: 0.7,
    maxTokens: 2048
  }

  // 使用 fetch API 发送 POST 请求并处理 SSE
  const controller = new AbortController()

  fetch(process.env.VUE_APP_BASE_API + '/ai/chat/stream', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + getToken()
    },
    credentials: 'include', // 携带 cookie
    body: JSON.stringify(data),
    signal: controller.signal
  })
    .then(response => {
      if (!response.ok) {
        throw new Error('Network response was not ok')
      }

      const reader = response.body.getReader()
      const decoder = new TextDecoder()
      let buffer = ''

      let currentEvent = null

      function read() {
        reader.read().then(({ done, value }) => {
          if (done) {
            onComplete && onComplete()
            return
          }

          buffer += decoder.decode(value, { stream: true })
          const lines = buffer.split('\n')
          buffer = lines.pop()

          lines.forEach(line => {
            if (line.startsWith('event:')) {
              // 记录当前事件类型
              currentEvent = line.substring(6).trim()
            } else if (line.startsWith('data:')) {
              const data = line.substring(5).trim()
              if (data === '[DONE]') {
                onComplete && onComplete()
                return
              }
              // 根据事件类型处理
              if (currentEvent === 'message') {
                onMessage && onMessage(data)
              } else if (currentEvent === 'error') {
                onError && onError(data)
              } else if (currentEvent === 'complete') {
                onComplete && onComplete()
              } else {
                // 默认直接输出
                onMessage && onMessage(data)
              }
            }
          })

          read()
        }).catch(error => {
          onError && onError(error.message)
        })
      }

      read()
    })
    .catch(error => {
      onError && onError(error.message)
    })

  return controller
}

/**
 * 清空会话历史
 * @param {string} sessionId - 会话ID
 * @returns {Promise}
 */
export function clearChatSession(sessionId) {
  return request({
    url: '/ai/chat/session/' + sessionId,
    method: 'delete'
  })
}

/**
 * 检查AI服务健康状态
 * @returns {Promise}
 */
export function checkAiHealth() {
  return request({
    url: '/ai/chat/health',
    method: 'get'
  })
}

// 获取token的辅助函数
function getToken() {
  // 优先从 localStorage 获取
  let token = localStorage.getItem('Admin-Token')
  if (!token) {
    // 从 cookie 获取
    const match = document.cookie.match(new RegExp('Admin-Token=([^;]+)'))
    token = match ? match[1] : ''
  }
  return token || ''
}
