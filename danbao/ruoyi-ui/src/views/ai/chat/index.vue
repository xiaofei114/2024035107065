<template>
  <div class="app-container">
    <el-card class="chat-card">
      <div slot="header" class="clearfix">
        <span><i class="el-icon-chat-dot-round"></i> AI智能助手</span>
        <el-button
          style="float: right; padding: 3px 0"
          type="text"
          icon="el-icon-question"
          @click="showHelp"
        >
          使用帮助
        </el-button>
      </div>
      <AiChat />
    </el-card>
  </div>
</template>

<script>
import AiChat from '@/components/AiChat'

export default {
  name: 'AiChatPage',
  components: {
    AiChat
  },
  methods: {
    showHelp() {
      this.$notify.info({
        title: '使用帮助',
        message: `
          <div style="line-height: 1.8;">
            <p><strong>快捷键：</strong></p>
            <p>• Enter - 发送消息</p>
            <p>• Shift + Enter - 换行</p>
            <br>
            <p><strong>提示：</strong></p>
            <p>• 支持多行文本输入</p>
            <p>• 聊天记录会自动保存会话上下文</p>
            <p>• 如遇网络问题请稍后重试</p>
          </div>
        `,
        dangerouslyUseHTMLString: true,
        duration: 0
      })
    }
  }
}
</script>

<style scoped>
.app-container {
  padding: 20px;
  height: calc(100vh - 84px);
}

.chat-card {
  height: 100%;
}

.chat-card ::v-deep .el-card__body {
  height: calc(100% - 55px);
  padding: 0;
}

.chat-card ::v-deep .ai-chat-container {
  height: 100%;
}
</style>
