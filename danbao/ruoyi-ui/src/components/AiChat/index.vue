<template>
  <div class="ai-chat-container">
    <!-- 工具栏 -->
    <div class="chat-toolbar">
      <div class="session-info">
        <el-tag size="small" type="info">
          <i class="el-icon-chat-dot-round"></i>
          会话: {{ sessionId ? sessionId.slice(-8) : '新会话' }}
        </el-tag>
        <el-tag v-if="messages.length > 0" size="small" type="success" style="margin-left: 8px;">
          {{ messageCount }} 条消息
        </el-tag>
      </div>
      <div class="toolbar-actions">
        <el-button
          size="mini"
          :type="streamMode ? 'primary' : 'default'"
          @click="streamMode = !streamMode"
        >
          <i :class="streamMode ? 'el-icon-video-play' : 'el-icon-video-pause'"></i>
          {{ streamMode ? '流式输出' : '普通输出' }}
        </el-button>
        <el-button
          size="mini"
          type="danger"
          icon="el-icon-delete"
          :disabled="messages.length === 0"
          @click="clearChat"
        >
          清空对话
        </el-button>
      </div>
    </div>

    <!-- 聊天消息列表区 -->
    <div ref="messageList" class="message-list">
      <!-- 欢迎消息 -->
      <div v-if="messages.length === 0" class="welcome-message">
        <el-empty description="开始与AI助手对话">
          <template #image>
            <i class="el-icon-chat-dot-round" style="font-size: 60px; color: #409EFF;"></i>
          </template>
        </el-empty>
      </div>

      <!-- 消息列表 -->
      <div
        v-for="(message, index) in messages"
        :key="index"
        :class="['message-item', message.type === 'user' ? 'user-message' : 'ai-message']"
      >
        <!-- 头像 -->
        <div class="avatar">
          <el-avatar
            :size="40"
            :icon="message.type === 'user' ? 'el-icon-user' : 'el-icon-s-custom'"
            :style="{ backgroundColor: message.type === 'user' ? '#409EFF' : '#67C23A' }"
          />
        </div>

        <!-- 消息内容 -->
        <div class="message-content">
          <div class="message-bubble">
            <div v-if="message.type === 'ai' && message.loading" class="loading-content">
              <i class="el-icon-loading"></i>
              <span>思考中...</span>
            </div>
            <div v-else class="message-text" v-html="formatMessage(message.content)"></div>
          </div>
          <div class="message-time">{{ message.time }}</div>
        </div>
      </div>

      <!-- 错误提示 -->
      <el-alert
        v-if="errorMessage"
        :title="errorMessage"
        type="error"
        :closable="true"
        @close="errorMessage = ''"
        show-icon
        class="error-alert"
      />
    </div>

    <!-- 输入框区 -->
    <div class="input-area">
      <div class="input-wrapper">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="3"
          placeholder="请输入您的问题，按Enter发送，Shift+Enter换行"
          :disabled="isLoading"
          @keydown.native="handleKeyDown"
          resize="none"
        />
      </div>
      <div class="button-group">
        <el-button
          type="primary"
          icon="el-icon-s-promotion"
          :loading="isLoading"
          :disabled="!inputMessage.trim() || isLoading"
          @click="sendMessage"
        >
          {{ isLoading ? '发送中...' : '发送' }}
        </el-button>
        <el-button
          icon="el-icon-delete"
          :disabled="messages.length === 0 || isLoading"
          @click="clearChat"
        >
          清空
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { requestAiChat, requestAiChatStream, clearChatSession } from '@/api/ai'

export default {
  name: 'AiChat',
  data() {
    return {
      // 消息列表
      messages: [],
      // 输入框内容
      inputMessage: '',
      // 是否正在加载
      isLoading: false,
      // 错误信息
      errorMessage: '',
      // 会话ID（用于上下文记忆）
      sessionId: null,
      // 是否使用流式输出
      streamMode: true,
      // 流式请求控制器
      streamController: null
    }
  },
  computed: {
    messageCount() {
      return this.messages.filter(m => !m.loading).length
    }
  },
  methods: {
    /**
     * 发送消息
     */
    async sendMessage() {
      const message = this.inputMessage.trim()
      if (!message || this.isLoading) {
        return
      }

      // 清空错误信息
      this.errorMessage = ''

      // 生成会话ID（如果是新会话）
      if (!this.sessionId) {
        this.sessionId = this.generateSessionId()
      }

      // 添加用户消息
      this.messages.push({
        type: 'user',
        content: message,
        time: this.getCurrentTime()
      })

      // 清空输入框
      this.inputMessage = ''

      // 滚动到底部
      this.$nextTick(() => {
        this.scrollToBottom()
      })

      // 添加AI加载消息
      const loadingMessage = {
        type: 'ai',
        content: '',
        time: this.getCurrentTime(),
        loading: true
      }
      this.messages.push(loadingMessage)
      this.isLoading = true

      try {
        if (this.streamMode) {
          // 流式输出
          await this.sendStreamMessage(message, loadingMessage)
        } else {
          // 普通输出
          await this.sendNormalMessage(message, loadingMessage)
        }
      } catch (error) {
        // 移除加载消息
        this.messages.pop()
        this.errorMessage = error.message || '请求失败，请稍后重试'
        this.$message.error(this.errorMessage)
      } finally {
        this.isLoading = false
        this.$nextTick(() => {
          this.scrollToBottom()
        })
      }
    },

    /**
     * 发送普通消息
     */
    async sendNormalMessage(message, loadingMessage) {
      const response = await requestAiChat(message, this.sessionId)
      loadingMessage.loading = false
      loadingMessage.content = response.content
      this.$message.success(`AI回复耗时: ${response.responseTime}ms`)
    },

    /**
     * 发送流式消息
     */
    sendStreamMessage(message, loadingMessage) {
      return new Promise((resolve, reject) => {
        let fullContent = ''

        this.streamController = requestAiChatStream(
          message,
          this.sessionId,
          // onMessage - 收到流式数据
          (content) => {
            fullContent += content
            loadingMessage.loading = false
            loadingMessage.content = fullContent
            this.$nextTick(() => {
              this.scrollToBottom()
            })
          },
          // onComplete - 完成
          () => {
            loadingMessage.loading = false
            resolve()
          },
          // onError - 错误
          (error) => {
            reject(new Error(error))
          }
        )
      })
    },

    /**
     * 处理键盘事件
     */
    handleKeyDown(event) {
      // Enter发送，Shift+Enter换行
      if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault()
        this.sendMessage()
      }
    },

    /**
     * 清空聊天记录
     */
    clearChat() {
      this.$confirm('确定要清空所有聊天记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 如果有会话ID，调用后端清空
        if (this.sessionId) {
          clearChatSession(this.sessionId).catch(() => {})
        }
        this.messages = []
        this.sessionId = null
        this.errorMessage = ''
        this.$message.success('聊天记录已清空')
      }).catch(() => {})
    },

    /**
     * 滚动到最新消息
     */
    scrollToBottom() {
      const messageList = this.$refs.messageList
      if (messageList) {
        messageList.scrollTop = messageList.scrollHeight
      }
    },

    /**
     * 获取当前时间
     */
    getCurrentTime() {
      const now = new Date()
      return now.toLocaleTimeString('zh-CN', {
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })
    },

    /**
     * 生成会话ID
     */
    generateSessionId() {
      return 'session-' + Date.now() + '-' + Math.random().toString(36).substr(2, 9)
    },

    /**
     * 格式化消息内容（处理Markdown、换行和转义字符）
     */
    formatMessage(content) {
      if (!content) return ''

      // 1. 处理字符串转义的换行符（\n -> 真正的换行符）
      let formatted = content.replace(/\\n/g, '\n')

      // 2. 处理字符串转义的制表符（\t -> 真正的制表符）
      formatted = formatted.replace(/\\t/g, '\t')

      // 3. 处理Unicode转义字符（如 \u003e -> >）- 先处理，避免影响HTML
      formatted = formatted.replace(/\\u([0-9a-fA-F]{4})/g, (match, hex) => {
        return String.fromCharCode(parseInt(hex, 16))
      })

      // 4. 先处理代码块（```code```），在HTML转义之前
      // 使用占位符保护代码块，避免后续处理破坏代码块标签
      const codeBlocks = []
      let codeBlockIndex = 0
      formatted = formatted.replace(/```(\w*)\n?([\s\S]*?)```/g, (match, lang, code) => {
        // 代码块内部：
        // 1. 先把 \\n 转回 \n（因为后端转义了换行符）
        // 2. 进行HTML转义，防止代码中的标签被解析
        const processedCode = code
          .replace(/\\n/g, '\n')  // 还原换行符
          .replace(/\\t/g, '\t')  // 还原制表符
          .replace(/&/g, '&amp;')
          .replace(/</g, '&lt;')
          .replace(/>/g, '&gt;')
        // 语言标签
        const language = lang || 'text'
        // 生成代码块HTML并保存到数组（带复制按钮和语言标签）
        const codeBlockHtml = `
          <div class="code-block-wrapper">
            <div class="code-block-header">
              <span class="code-language">${language}</span>
              <button class="copy-code-btn" data-action="copy">复制</button>
            </div>
            <pre class="code-block"><code>${processedCode}</code></pre>
          </div>
        `
        const placeholder = `CODEBLOCK${codeBlockIndex}`
        codeBlocks.push({ placeholder, html: codeBlockHtml })
        codeBlockIndex++
        return placeholder
      })

      // 5. 处理行内代码（`code`）- 也在HTML转义之前
      const inlineCodes = []
      let inlineCodeIndex = 0
      formatted = formatted.replace(/`([^`]+)`/g, (match, code) => {
        const processedCode = code
          .replace(/&/g, '&amp;')
          .replace(/</g, '&lt;')
          .replace(/>/g, '&gt;')
        const inlineCodeHtml = `<code class="inline-code">${processedCode}</code>`
        const placeholder = `INLINECODE${inlineCodeIndex}`
        inlineCodes.push({ placeholder, html: inlineCodeHtml })
        inlineCodeIndex++
        return placeholder
      })

      // 6. 处理HTML转义字符（代码块和行内代码已经被保护）
      formatted = formatted
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#x27;')

      // 6. 处理行内代码（`code`）
      // 7. 处理粗体（**text**）
      formatted = formatted.replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>')

      // 8. 处理斜体（*text* 或 _text_）
      formatted = formatted.replace(/\*([^*]+)\*/g, '<em>$1</em>')
      formatted = formatted.replace(/_([^_]+)_/g, '<em>$1</em>')

      // 9. 处理删除线（~~text~~）
      formatted = formatted.replace(/~~([^~]+)~~/g, '<del>$1</del>')

      // 10. 处理链接 [text](url) - 先处理，避免和其他格式冲突
      formatted = formatted.replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2" target="_blank" style="color: #409EFF; text-decoration: underline;">$1</a>')

      // 11. 处理水平分割线（--- 或 ***）- 需要在标题之前处理
      formatted = formatted.replace(/^(---|\*\*\*)$/gim, '<hr style="border: none; border-top: 1px solid #e0e0e0; margin: 10px 0;">')

      // 12. 处理引用（> text）
      formatted = formatted.replace(/^&gt; (.*$)/gim, '<blockquote style="border-left: 3px solid #409EFF; padding-left: 10px; margin: 5px 0; color: #666;">$1</blockquote>')

      // 13. 处理无序列表（- item 或 * item）- 需要在处理 * 斜体之前
      // 使用多行模式，匹配行首的 - 或 *，支持 -item 或 - item 两种格式
      formatted = formatted.replace(/^[\s]*[-*]\s*(.+)$/gim, '<li>$1</li>')

      // 14. 处理有序列表（1. item）
      // 支持 1.item 或 1. item 两种格式
      formatted = formatted.replace(/^[\s]*\d+\.\s*(.+)$/gim, '<li>$1</li>')

      // 15. 处理标题（# ## ###）- 从最长的开始匹配
      // 支持 #标题 或 # 标题 两种格式（允许紧跟文字或表情）
      formatted = formatted.replace(/^####\s*(.+)$/gim, '<h5>$1</h5>')
      formatted = formatted.replace(/^###\s*(.+)$/gim, '<h4>$1</h4>')
      formatted = formatted.replace(/^##\s*(.+)$/gim, '<h3>$1</h3>')
      formatted = formatted.replace(/^#\s*(.+)$/gim, '<h2>$1</h2>')

      // 16. 处理 Markdown 表格
      // 表格格式：| 列1 | 列2 | 列3 |
      //          | --- | --- | --- |
      //          | 内容 | 内容 | 内容 |
      formatted = formatted.replace(/((?:^\|.*\|(?:\r?\n)?)+)/gm, (match) => {
        const lines = match.trim().split('\n').filter(line => line.trim())
        if (lines.length < 2) return match

        // 检查第二行是否是分隔符（| --- | --- |）
        const separatorLine = lines[1].trim()
        if (!separatorLine.match(/^\|?\s*:?-+:?\s*(\|\s*:?-+:?\s*)*\|?$/)) {
          return match
        }

        // 解析表头
        const headerCells = lines[0].split('|').map(cell => cell.trim()).filter(cell => cell)
        // 解析对齐方式
        const alignments = separatorLine.split('|').map(cell => {
          const trimmed = cell.trim()
          if (trimmed.startsWith(':') && trimmed.endsWith(':')) return 'center'
          if (trimmed.endsWith(':')) return 'right'
          return 'left'
        }).filter((_, i) => i > 0 && i <= headerCells.length)

        // 构建表格 HTML
        let tableHtml = '<table style="border-collapse: collapse; margin: 10px 0; width: 100%;">'

        // 表头
        tableHtml += '<thead><tr>'
        headerCells.forEach((cell, index) => {
          const align = alignments[index] || 'left'
          tableHtml += `<th style="border: 1px solid #ddd; padding: 8px; text-align: ${align}; background-color: #f5f5f5; font-weight: 600;">${cell}</th>`
        })
        tableHtml += '</tr></thead>'

        // 表体
        tableHtml += '<tbody>'
        for (let i = 2; i < lines.length; i++) {
          const rowCells = lines[i].split('|').map(cell => cell.trim()).filter(cell => cell)
          if (rowCells.length > 0) {
            tableHtml += '<tr>'
            rowCells.forEach((cell, index) => {
              const align = alignments[index] || 'left'
              tableHtml += `<td style="border: 1px solid #ddd; padding: 8px; text-align: ${align};">${cell}</td>`
            })
            tableHtml += '</tr>'
          }
        }
        tableHtml += '</tbody></table>'

        return tableHtml
      })

      // 17. 将连续的 li 包裹在 ul/ol 中
      // 先收集所有 li，然后包裹
      const lines = formatted.split('\n')
      let result = []
      let inList = false
      let listType = null
      let listItems = []

      for (let i = 0; i < lines.length; i++) {
        const line = lines[i]
        const isListItem = line.match(/^<li>(.+)<\/li>$/)

        if (isListItem) {
          if (!inList) {
            inList = true
            // 判断是 ul 还是 ol（简单判断：如果前面有数字就是 ol）
            listType = 'ul'
          }
          listItems.push(line)
        } else {
          if (inList) {
            // 结束列表
            result.push(`<${listType}>${listItems.join('')}</${listType}>`)
            listItems = []
            inList = false
          }
          result.push(line)
        }
      }

      // 处理末尾的列表
      if (inList && listItems.length > 0) {
        result.push(`<${listType}>${listItems.join('')}</${listType}>`)
      }

      formatted = result.join('\n')

      // 18. 将换行符转换为<br>（跳过已经是HTML标签的行）
      const paragraphs = formatted.split('\n')
      formatted = paragraphs.map(p => {
        const trimmed = p.trim()
        // 如果已经是块级元素，不添加 <br>
        if (trimmed.match(/^<(pre|h[1-6]|ul|ol|li|hr|blockquote|table)/)) {
          return p
        }
        // 空行不添加 <br>
        if (trimmed === '') {
          return ''
        }
        return p + '<br>'
      }).join('\n')

      // 19. 恢复代码块占位符
      codeBlocks.forEach(({ placeholder, html }) => {
        // 使用 split/join 避免正则特殊字符问题
        formatted = formatted.split(placeholder).join(html)
      })

      // 20. 恢复行内代码占位符
      inlineCodes.forEach(({ placeholder, html }) => {
        formatted = formatted.split(placeholder).join(html)
      })

      return formatted
    },

    // 复制代码到剪贴板
    copyCode(btn) {
      const codeBlock = btn.closest('.code-block-wrapper').querySelector('code')
      const code = codeBlock.textContent

      // 创建临时 textarea 来复制文本
      const textarea = document.createElement('textarea')
      textarea.value = code
      textarea.style.position = 'fixed'
      textarea.style.opacity = '0'
      document.body.appendChild(textarea)
      textarea.select()

      try {
        document.execCommand('copy')
        // 显示复制成功状态
        const originalText = btn.textContent
        btn.textContent = '已复制!'
        btn.classList.add('copied')
        setTimeout(() => {
          btn.textContent = originalText
          btn.classList.remove('copied')
        }, 2000)
      } catch (err) {
        console.error('复制失败:', err)
        btn.textContent = '复制失败'
        setTimeout(() => {
          btn.textContent = '复制'
        }, 2000)
      }

      document.body.removeChild(textarea)
    }
  },

  updated() {
    // 绑定复制按钮事件
    this.$nextTick(() => {
      const copyButtons = this.$el.querySelectorAll('[data-action="copy"]')
      copyButtons.forEach(btn => {
        if (!btn._copyHandler) {
          btn._copyHandler = () => this.copyCode(btn)
          btn.addEventListener('click', btn._copyHandler)
        }
      })
    })
  },

  beforeDestroy() {
    // 组件销毁时取消流式请求
    if (this.streamController) {
      this.streamController.abort()
    }
    // 清理复制按钮事件
    const copyButtons = this.$el.querySelectorAll('[data-action="copy"]')
    copyButtons.forEach(btn => {
      if (btn._copyHandler) {
        btn.removeEventListener('click', btn._copyHandler)
      }
    })
  }
}
</script>

<style scoped>
.ai-chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: #f5f7fa;
  border-radius: 8px;
  overflow: hidden;
}

/* 工具栏 */
.chat-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background-color: #fff;
  border-bottom: 1px solid #ebeef5;
}

.session-info {
  display: flex;
  align-items: center;
}

.toolbar-actions {
  display: flex;
  gap: 8px;
}

/* 消息列表区 */
.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background-color: #fff;
}

.welcome-message {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

/* 消息项 */
.message-item {
  display: flex;
  margin-bottom: 20px;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 用户消息 - 靠右 */
.user-message {
  flex-direction: row-reverse;
}

.user-message .message-content {
  align-items: flex-end;
}

.user-message .message-bubble {
  background-color: #409EFF;
  color: #fff;
  border-bottom-right-radius: 4px;
}

/* AI消息 - 靠左 */
.ai-message .message-bubble {
  background-color: #f4f4f5;
  color: #303133;
  border-bottom-left-radius: 4px;
}

/* 头像 */
.avatar {
  margin: 0 12px;
  flex-shrink: 0;
}

/* 消息内容 */
.message-content {
  display: flex;
  flex-direction: column;
  max-width: 70%;
}

/* 消息气泡 */
.message-bubble {
  padding: 12px 16px;
  border-radius: 12px;
  word-wrap: break-word;
  word-break: break-all;
  line-height: 1.6;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.message-text {
  white-space: normal;
}

.message-text ::v-deep br {
  display: block;
  content: "";
  margin-top: 4px;
}

/* 标题样式 */
.message-text ::v-deep h2,
.message-text ::v-deep h3,
.message-text ::v-deep h4,
.message-text ::v-deep h5 {
  margin: 12px 0 8px 0;
  font-weight: 600;
  line-height: 1.4;
}

.message-text ::v-deep h2 {
  font-size: 1.4em;
  border-bottom: 1px solid #e0e0e0;
  padding-bottom: 4px;
}

.message-text ::v-deep h3 {
  font-size: 1.25em;
}

.message-text ::v-deep h4 {
  font-size: 1.1em;
}

.message-text ::v-deep h5 {
  font-size: 1em;
}

/* 列表样式 */
.message-text ::v-deep ul,
.message-text ::v-deep ol {
  margin: 8px 0;
  padding-left: 20px;
}

.message-text ::v-deep li {
  margin: 4px 0;
  line-height: 1.6;
}

/* 水平分割线样式 */
.message-text ::v-deep hr {
  border: none;
  border-top: 1px solid #e0e0e0;
  margin: 12px 0;
}

/* 代码块容器 */
.message-text ::v-deep .code-block-wrapper {
  margin: 8px 0;
  border-radius: 6px;
  overflow: hidden;
  background-color: #282c34;
}

/* 代码块头部 */
.message-text ::v-deep .code-block-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background-color: #21252b;
  border-bottom: 1px solid #3e4451;
}

.message-text ::v-deep .code-language {
  color: #abb2bf;
  font-size: 12px;
  text-transform: uppercase;
}

.message-text ::v-deep .copy-code-btn {
  padding: 4px 12px;
  font-size: 12px;
  color: #abb2bf;
  background-color: #3e4451;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.message-text ::v-deep .copy-code-btn:hover {
  background-color: #4b5263;
  color: #fff;
}

.message-text ::v-deep .copy-code-btn.copied {
  background-color: #28a745;
  color: #fff;
}

/* 代码块样式 */
.message-text ::v-deep .code-block {
  background-color: #282c34;
  color: #abb2bf;
  padding: 12px;
  overflow-x: auto;
  margin: 0;
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 13px;
  line-height: 1.5;
  white-space: pre;  /* 保留换行符和空格 */
  border-radius: 0;
}

.message-text ::v-deep .code-block code {
  white-space: pre;  /* 确保 code 标签内也保留格式 */
  display: block;
}

.message-text ::v-deep .inline-code {
  background-color: rgba(175, 184, 193, 0.2);
  padding: 2px 6px;
  border-radius: 3px;
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 0.9em;
  color: #e83e8c;
}

/* 加载动画 */
.loading-content {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #909399;
}

.loading-content i {
  font-size: 16px;
}

/* 消息时间 */
.message-time {
  font-size: 12px;
  color: #909399;
  margin-top: 6px;
  padding: 0 4px;
}

/* 错误提示 */
.error-alert {
  margin-top: 10px;
}

/* 输入框区 */
.input-area {
  padding: 16px 20px;
  background-color: #fff;
  border-top: 1px solid #ebeef5;
}

.input-wrapper {
  margin-bottom: 12px;
}

.button-group {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 滚动条样式 */
.message-list::-webkit-scrollbar {
  width: 6px;
}

.message-list::-webkit-scrollbar-thumb {
  background-color: #c0c4cc;
  border-radius: 3px;
}

.message-list::-webkit-scrollbar-track {
  background-color: #f5f7fa;
}
</style>
