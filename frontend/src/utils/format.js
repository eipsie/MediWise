import { DATE_FORMAT_OPTIONS } from '../config/constants'

/**
 * 格式化日期时间
 * @param {string|Date} date - 日期对象或ISO日期字符串
 * @param {string} format - 格式化模式，默认为 YYYY-MM-DD HH:mm:ss
 * @returns {string} - 格式化后的日期字符串
 */
export function formatDateTime(date, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!date) return '';
  
  const d = typeof date === 'string' ? new Date(date) : date;
  
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  const hours = String(d.getHours()).padStart(2, '0');
  const minutes = String(d.getMinutes()).padStart(2, '0');
  const seconds = String(d.getSeconds()).padStart(2, '0');
  
  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds);
}

/**
 * 格式化日期（不含时间）
 * @param {string|Date} date - 日期对象或ISO日期字符串
 * @returns {string} - 格式化后的日期字符串 (YYYY-MM-DD)
 */
export function formatDate(date) {
  return formatDateTime(date, 'YYYY-MM-DD');
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