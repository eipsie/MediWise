import { DATE_FORMAT_OPTIONS } from '../config/constants'

/**
 * 格式化日期时间
 * @param {string|Date} dateTime 日期时间
 * @param {Object} options 格式化选项
 * @returns {string} 格式化后的日期时间字符串
 */
export const formatDateTime = (dateTime, options = DATE_FORMAT_OPTIONS.dateTime) => {
  if (!dateTime) return ''
  
  try {
    const date = new Date(dateTime)
    return date.toLocaleString('zh-CN', options)
  } catch (e) {
    return dateTime
  }
}

/**
 * 格式化仅日期
 * @param {string|Date} date 日期
 * @returns {string} 格式化后的日期字符串
 */
export const formatDate = (date) => {
  return formatDateTime(date, DATE_FORMAT_OPTIONS.dateOnly)
}

/**
 * 格式化仅时间
 * @param {string|Date} time 时间
 * @returns {string} 格式化后的时间字符串
 */
export const formatTime = (time) => {
  return formatDateTime(time, DATE_FORMAT_OPTIONS.timeOnly)
}

/**
 * 将日期时间转换为ISO格式字符串
 * @param {Date|string} dateTime 日期时间
 * @returns {string} ISO格式日期时间字符串
 */
export const toISOString = (dateTime) => {
  if (!dateTime) return ''
  
  try {
    const date = dateTime instanceof Date ? dateTime : new Date(dateTime)
    return date.toISOString()
  } catch (e) {
    return ''
  }
}

/**
 * 格式化数字
 * @param {number} num 数字
 * @param {number} digits 小数位数
 * @returns {string} 格式化后的数字字符串
 */
export const formatNumber = (num, digits = 2) => {
  if (num === undefined || num === null) return '--'
  
  try {
    return Number(num).toFixed(digits)
  } catch (e) {
    return num.toString()
  }
}

/**
 * 格式化货币
 * @param {number} amount 金额
 * @param {string} currency 货币符号
 * @returns {string} 格式化后的货币字符串
 */
export const formatCurrency = (amount, currency = '¥') => {
  if (amount === undefined || amount === null) return '--'
  
  try {
    return `${currency} ${Number(amount).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
  } catch (e) {
    return `${currency} ${amount}`
  }
}

/**
 * 生成唯一ID
 * @returns {string} 唯一ID
 */
export const generateUniqueId = () => {
  return Date.now().toString(36) + Math.random().toString(36).substr(2, 5)
} 