import request from './auth' // 复用已有的axios实例
import { getToken } from '../utils/jwt' // 导入getToken函数

/**
 * 发送聊天请求到GLM大模型
 * @param {Object} data - 聊天请求数据
 * @param {string} data.model - 模型名称，如"glm-4"
 * @param {Array} data.messages - 消息历史，包含role和content
 * @param {number} [data.temperature] - 控制输出随机性（可选）
 * @param {boolean} [data.stream] - 是否流式输出（可选）
 * @returns {Promise} - 返回请求Promise
 */
export const chatWithGlm = (data) => {
  // 调试：检查token是否存在
  const token = getToken();
  console.log('AI聊天请求前的Token:', token);
  
  return request({
    url: '/api/chat',
    method: 'post',
    data
  })
} 