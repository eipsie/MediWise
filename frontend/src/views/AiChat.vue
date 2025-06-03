<template>
  <div class="ai-chat-page">
    <!-- 头部导航 -->
    <header class="navbar">
      <div class="nav-brand" @click="goBackToHome">
        <el-icon><back /></el-icon>
        <span class="brand-text">返回首页</span>
      </div>
      <div class="chat-title">
        <span>MediWise AI 智能问诊助手</span>
      </div>
      <div class="nav-actions">
        <el-button type="primary" plain size="small" @click="clearChat">
          <el-icon><delete /></el-icon>
          <span>清空对话</span>
        </el-button>
      </div>
    </header>

    <!-- 聊天容器 -->
    <div class="chat-container" ref="chatContainer">
      <!-- 欢迎消息 -->
      <div class="welcome-container" v-if="chatHistory.length === 0">
        <div class="welcome-message">
          <div class="welcome-title">
            <img src="../asset/glm_logo.png" alt="GLM Logo" class="ai-avatar">
            <h2>MediWise AI 助手</h2>
          </div>
          <p>我是基于GLM大模型的智能医疗助手，可以回答医疗相关问题、提供健康建议和医学知识咨询。请注意我的回答仅供参考，具体诊断和治疗请以专业医生建议为准。</p>
          <div class="sample-questions">
            <h3>您可以尝试这些问题：</h3>
            <div class="question-list">
              <div class="sample-question" @click="askSampleQuestion('高血压患者应该注意哪些饮食习惯？')">
                高血压患者应该注意哪些饮食习惯？
              </div>
              <div class="sample-question" @click="askSampleQuestion('糖尿病的主要症状有哪些？')">
                糖尿病的主要症状有哪些？
              </div>
              <div class="sample-question" @click="askSampleQuestion('心脏病发作时应该如何急救？')">
                心脏病发作时应该如何急救？
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 消息列表 -->
      <div class="messages-wrapper" v-if="chatHistory.length > 0">
        <div 
          v-for="(msg, index) in chatHistory" 
          :key="index" 
          class="message"
          :class="msg.role === 'user' ? 'user-message' : 'ai-message'"
        >
          <!-- 用户消息 -->
          <template v-if="msg.role === 'user'">
            <div class="message-content">
              <div class="message-text">{{ msg.content }}</div>
              <div class="message-time">{{ msg.time }}</div>
            </div>
            <div class="message-avatar">
              <img src="../asset/glm_logo.png" alt="User Avatar">
            </div>
          </template>
          
          <!-- AI消息 -->
          <template v-else>
            <div class="message-avatar">
              <img src="../asset/glm_logo.png" alt="AI Avatar">
            </div>
            <div class="message-content">
              <div class="message-text markdown-body" v-html="renderContent(msg.content)"></div>
              <div class="message-time">{{ msg.time }}</div>
            </div>
          </template>
        </div>
      </div>

      <!-- 加载指示器 -->
      <div class="loading-message" v-if="loading">
        <div class="message-avatar">
          <img src="../asset/glm_logo.png" alt="AI Avatar">
        </div>
        <div class="loading-indicator">
          <el-icon class="is-loading"><loading /></el-icon>
          <span>AI正在思考中...</span>
        </div>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="input-area-wrapper">
      <div class="input-area">
        <el-input
          v-model="userInput"
          type="textarea"
          :rows="2"
          placeholder="输入您的问题，按Enter发送（Shift+Enter换行）"
          @keydown.enter.prevent="handleEnterKey"
          :disabled="loading"
        />
        <el-button 
          type="primary" 
          :icon="loading ? 'Loading' : 'Position'" 
          round 
          @click="sendMessage" 
          :disabled="loading || !userInput.trim()"
        >
          {{ loading ? '发送中...' : '发送' }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { 
  ArrowLeft as Back, 
  Position, 
  Loading,
  Delete
} from '@element-plus/icons-vue';
import { chatWithGlm } from '../api/glm';
import 'element-plus/es/components/message/style/css';
import 'element-plus/es/components/message-box/style/css';
import markdownit from 'markdown-it';

// 初始化markdown渲染器
const md = markdownit({
  html: true,
  linkify: true,
  typographer: true,
  breaks: true
});

// 路由实例
const router = useRouter();

// 聊天相关状态
const userInput = ref('');
const chatHistory = ref([]);
const loading = ref(false);
const chatContainer = ref(null);

// 渲染内容，支持Markdown和结构化文本
const renderContent = (text) => {
  if (!text) return '';
  
  // 使用markdown渲染器直接处理文本
  // 不再做自定义处理以避免干扰原始Markdown语法
  try {
    return md.render(text);
  } catch (e) {
    console.error('Markdown渲染错误:', e);
    // 降级处理，保证至少显示文本
    return text.replace(/\n/g, '<br>');
  }
};

// 获取当前时间
const getCurrentTime = () => {
  const now = new Date();
  const hours = now.getHours().toString().padStart(2, '0');
  const minutes = now.getMinutes().toString().padStart(2, '0');
  return `${hours}:${minutes}`;
};

// 滚动到底部
const scrollToBottom = async () => {
  await nextTick();
  if (chatContainer.value) {
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight;
  }
};

// 发送消息
const sendMessage = async () => {
  const message = userInput.value.trim();
  if (!message || loading.value) return;

  // 添加用户消息到历史记录
  chatHistory.value.push({
    role: 'user',
    content: message,
    time: getCurrentTime()
  });

  // 清空输入框
  userInput.value = '';
  
  // 滚动到底部
  scrollToBottom();
  
  // 设置加载状态
  loading.value = true;

  try {
    // 构建请求数据
    const requestData = {
      model: 'glm-4', // 默认使用 GLM-4 模型
      messages: chatHistory.value.map(msg => ({
        role: msg.role,
        content: msg.content
      })),
      temperature: 0.7 // 设置合适的temperature值
    };

    // 发送请求到后端
    const response = await chatWithGlm(requestData);
    
    // 获取AI响应 - 正确处理响应结构
    // Result对象中包含data字段，data字段中包含GlmChatVO对象
    const aiResponse = response.data.data;

    // 添加AI消息到历史记录
    chatHistory.value.push({
      role: 'assistant',
      content: aiResponse.content,
      time: getCurrentTime()
    });

    // 滚动到底部
    scrollToBottom();

  } catch (error) {
    console.error('调用AI接口出错:', error);
    ElMessage.error(error.response?.data?.message || '无法连接到AI服务，请稍后再试');
  } finally {
    loading.value = false;
  }
};

// 处理Enter键事件
const handleEnterKey = (e) => {
  if (e.shiftKey) {
    // Shift+Enter允许换行
    return;
  }
  // Enter键发送消息
  sendMessage();
};

// 返回主页
const goBackToHome = () => {
  router.push('/home');
};

// 清空对话
const clearChat = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要清空所有聊天记录吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );
    chatHistory.value = [];
  } catch {
    // 用户取消操作
  }
};

// 直接提问样例问题
const askSampleQuestion = (question) => {
  userInput.value = question;
  sendMessage();
};

// 组件挂载后初始化
onMounted(() => {
  // 确保聊天容器开始是空的
  scrollToBottom();
});

</script>

<style scoped>
/* 整个聊天页面 */
.ai-chat-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100vw; /* 确保撑满整个视口宽度 */
  background-color: #f7f8fa;
  overflow: hidden; /* 防止整体滚动 */
  position: fixed; /* 固定位置 */
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  max-width: 100%; /* 确保不超过视口宽度 */
}

/* 头部导航 */
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 30px; /* 与消息区域保持一致 */
  background-color: #fff;
  border-bottom: 1px solid #ebeef5;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  width: 100%; /* 撑满宽度 */
  box-sizing: border-box; /* 确保padding不会增加总宽度 */
}

.nav-brand {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #409eff;
  flex: 0 0 auto; /* 不伸缩 */
  margin-right: 10px; /* 添加右侧间距 */
}

.chat-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  flex: 1; /* 占据中间空间 */
  text-align: center; /* 标题居中 */
  white-space: nowrap; /* 防止标题换行 */
  overflow: hidden; /* 超出部分隐藏 */
  text-overflow: ellipsis; /* 添加省略号 */
}

.nav-actions {
  flex: 0 0 auto; /* 不伸缩 */
  white-space: nowrap; /* 防止按钮文字换行 */
  margin-left: 10px; /* 添加左侧间距 */
}

.nav-actions .el-button {
  display: flex;
  align-items: center;
  gap: 5px;
}

/* 聊天容器 */
.chat-container {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden; /* 防止水平滚动 */
  padding: 20px 0; /* 移除左右内边距，改由子元素控制 */
  display: flex;
  flex-direction: column;
  gap: 16px;
  scroll-behavior: smooth;
  width: 100%; /* 确保撑满整个宽度 */
  box-sizing: border-box; /* 确保padding不增加总宽度 */
}

.messages-wrapper {
  display: flex;
  flex-direction: column;
  width: 100%;
  padding: 0 30px; /* 增加左右内边距 */
  box-sizing: border-box; /* 确保padding不增加总宽度 */
  max-width: 100%; /* 确保不超过父容器宽度 */
}

/* 欢迎消息容器 */
.welcome-container {
  width: 100%;
  display: flex;
  justify-content: stretch; /* 改为拉伸以填满宽度 */
  padding: 0 30px; /* 与消息区域保持一致 */
  box-sizing: border-box; /* 确保padding不增加总宽度 */
}

.welcome-message {
  background-color: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 2px 16px rgba(0,0,0,0.08);
  animation: fadeIn 0.5s ease-out;
  width: 100%;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.welcome-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 18px;
}

.welcome-title h2 {
  margin: 0;
  font-size: 20px;
  color: #303133;
  font-weight: 600;
}

.welcome-message p {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 24px;
  font-size: 15px;
}

.ai-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.sample-questions h3 {
  font-size: 15px;
  color: #606266;
  margin-bottom: 14px;
}

.question-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.sample-question {
  background-color: #ecf5ff;
  color: #409eff;
  padding: 10px 16px;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.sample-question:hover {
  background-color: #d9ecff;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

/* 消息样式 */
.message {
  display: flex;
  margin-bottom: 22px;
  width: 100%;
  animation: messageSlide 0.3s ease-out;
  box-sizing: border-box; /* 确保不超出容器 */
}

@keyframes messageSlide {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.user-message {
  justify-content: flex-end; /* 用户消息靠右 */
}

.ai-message {
  justify-content: flex-start; /* AI消息靠左 */
  width: 100%; /* 确保AI消息撑开整个容器 */
}

.message-avatar {
  flex-shrink: 0; /* 防止头像被压缩 */
}

.user-message .message-content {
  margin-right: 14px;
  max-width: 70%; 
  min-width: 150px; /* 减小最小宽度 */
  display: flex;
  flex-direction: column;
  align-items: flex-end; /* 右对齐 */
}

.ai-message .message-content {
  margin-left: 14px;
  max-width: 70%;
  min-width: 150px; /* 减小最小宽度 */
  display: flex;
  flex-direction: column;
  align-items: flex-start; /* 左对齐 */
}

.message-text {
  padding: 14px 18px;
  border-radius: 12px;
  line-height: 1.6;
  word-break: break-word;
  text-align: left;
  width: fit-content; /* 根据内容调整宽度 */
  max-width: 100%; /* 但不超过父容器 */
  overflow-wrap: break-word; /* 确保长单词可以换行 */
}

.user-message .message-text {
  background-color: #409eff;
  color: white;
  border-top-right-radius: 0;
}

.ai-message .message-text {
  background-color: white;
  color: #333;
  border-top-left-radius: 0;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
}

/* Markdown内容样式 */
.markdown-body {
  font-size: 15px;
  text-align: left;
  min-width: 150px; /* 减小最小宽度 */
  max-width: 100%; /* 确保不超过父容器 */
  overflow-wrap: break-word; /* 确保长单词可以换行 */
  word-break: break-word; /* 打破长单词 */
}

.markdown-body h1, 
.markdown-body h2, 
.markdown-body h3, 
.markdown-body h4 {
  margin-top: 16px;
  margin-bottom: 10px;
  font-weight: 600;
  line-height: 1.4;
  text-align: left;
}

.markdown-body h1 {
  font-size: 20px;
  padding-bottom: 4px;
  border-bottom: 1px solid #eaecef;
}

.markdown-body h2 {
  font-size: 18px;
  padding-bottom: 4px;
  border-bottom: 1px solid #eaecef;
}

.markdown-body h3 {
  font-size: 16px;
}

.markdown-body p {
  margin-top: 8px;
  margin-bottom: 8px;
}

/* 改进Markdown列表样式 */
.markdown-body ul,
.markdown-body ol {
  padding-left: 20px;
  margin-top: 8px;
  margin-bottom: 8px;
  list-style-position: outside;
}

.markdown-body ul {
  list-style-type: disc;
}

.markdown-body ul ul {
  list-style-type: circle;
}

.markdown-body ol {
  list-style-type: decimal;
}

.markdown-body li {
  margin-bottom: 6px;
  line-height: 1.6;
}

.markdown-body li > p {
  margin-top: 0;
  margin-bottom: 0;
}

.markdown-body li + li {
  margin-top: 4px;
}

.markdown-body strong {
  font-weight: 600;
}

.list-item {
  margin: 8px 0;
}

.numbered-item, 
.bullet-item {
  padding-left: 4px;
  display: block;
}

.message-time {
  font-size: 12px;
  color: #909399;
  margin-top: 6px;
  text-align: right;
}

.ai-message .message-time {
  text-align: left;
}

/* 加载指示器 */
.loading-message {
  display: flex;
  align-items: flex-start;
  margin-bottom: 22px;
  animation: messageSlide 0.3s ease-out;
  width: 100%;
}

.loading-indicator {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 24px;
  background-color: #f0f9ff;
  border-radius: 12px;
  font-size: 14px;
  color: #409eff;
  margin-left: 14px;
  text-align: left;
  border-top-left-radius: 0;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  animation: pulse 1.5s infinite;
  min-width: 150px; /* 减小最小宽度 */
  max-width: calc(70% - 14px); /* 与消息内容宽度保持一致 */
}

@keyframes pulse {
  0% { opacity: 0.6; }
  50% { opacity: 1; }
  100% { opacity: 0.6; }
}

/* 输入区域 */
.input-area-wrapper {
  width: 100%;
  display: flex;
  justify-content: center;
  background-color: #fff;
  border-top: 1px solid #ebeef5;
  box-shadow: 0 -2px 10px rgba(0,0,0,0.05);
  padding: 18px 30px; /* 增加左右内边距 */
  box-sizing: border-box; /* 确保padding不增加总宽度 */
}

.input-area {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  width: 100%;
  max-width: 100%; /* 确保不超过父容器宽度 */
}

.input-area .el-textarea {
  flex: 1;
  width: 100%; /* 确保输入框占据可用空间 */
}

.input-area .el-textarea__inner {
  border-radius: 8px;
  padding: 12px;
  resize: none;
  box-shadow: 0 0 0 1px rgba(0,0,0,0.1);
  transition: box-shadow 0.3s;
}

.input-area .el-textarea__inner:focus {
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.3);
}

.input-area .el-button {
  flex-shrink: 0; /* 防止按钮被压缩 */
  white-space: nowrap; /* 防止文字换行 */
  height: 44px;
  padding: 0 24px;
  border-radius: 22px;
  font-weight: 500;
  box-shadow: 0 2px 6px rgba(64, 158, 255, 0.3);
  transition: all 0.3s;
  min-width: 90px; /* 确保按钮有足够宽度 */
}

.input-area .el-button:hover:not([disabled]) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .chat-container {
    padding: 15px;
  }

  .messages-wrapper, 
  .welcome-container,
  .input-area-wrapper,
  .navbar {
    padding-left: 15px;
    padding-right: 15px;
  }

  .message {
    max-width: 95%;
  }

  .welcome-message {
    padding: 18px;
  }

  .sample-question {
    padding: 8px 14px;
    font-size: 13px;
  }
  
  .message-text {
    padding: 12px 16px;
  }
  
  /* 导航栏响应式调整 */
  .navbar {
    padding-left: 15px;
    padding-right: 15px;
  }
  
  .nav-brand .brand-text {
    display: none; /* 小屏幕隐藏返回首页文字 */
  }
  
  .chat-title {
    font-size: 14px;
    margin: 0 5px; /* 减小标题两侧边距 */
  }
  
  .nav-actions {
    margin-left: 5px; /* 减小左侧边距 */
  }
  
  .nav-actions .el-button {
    padding: 6px 8px;
    font-size: 12px;
  }
  
  .input-area {
    gap: 8px; /* 减小间距 */
  }
  
  .input-area .el-button {
    padding: 0 15px;
    min-width: 70px;
  }
}

@media (max-width: 480px) {
  .action-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .messages-wrapper, 
  .welcome-container,
  .input-area-wrapper,
  .navbar {
    padding-left: 10px;
    padding-right: 10px;
  }
  
  .user-message .message-content,
  .ai-message .message-content {
    min-width: 100px;
    max-width: 80%;
  }
  
  .message-text {
    padding: 10px 14px;
  }
  
  .markdown-body {
    min-width: 100px;
  }
  
  .loading-indicator {
    min-width: 100px;
  }
  
  /* 超小屏幕隐藏按钮文字，只显示图标 */
  .nav-actions .el-button span {
    display: none;
  }
  
  .nav-actions .el-button {
    padding: 6px;
  }
  
  .input-area .el-button {
    padding: 0 12px;
    min-width: 50px;
  }
}

/* 头像样式 */
.message-avatar img {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: 0 2px 6px rgba(0,0,0,0.1);
}

/* 全局确保不会横向滚动 */
* {
  box-sizing: border-box;
}

/* 确保代码块不会导致横向滚动 */
.markdown-body pre {
  overflow-x: auto;
  max-width: 100%;
}

.markdown-body code {
  white-space: pre-wrap;
  word-break: break-word;
}

/* 修复表格可能导致的横向滚动 */
.markdown-body table {
  display: block;
  overflow-x: auto;
  max-width: 100%;
}

/* 确保输入按钮始终显示正常 */
.input-area .el-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

/* 确保按钮图标显示正确 */
.input-area .el-button .el-icon {
  margin-right: 4px;
}

@media (max-width: 480px) {
  .input-area .el-button .el-icon {
    margin-right: 0;
  }
}
</style> 