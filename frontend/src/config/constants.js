/**
 * MediWise 系统常量配置
 */

// 血常规检查配置
export const BLOOD_TEST_CONFIG = {
  fields: [
    { field: 'wbc', shortName: 'WBC', label: '白细胞计数', unit: '× 10^9/L', reference: '4.0-10.0', min: 4.0, max: 10.0 },
    { field: 'rbc', shortName: 'RBC', label: '红细胞计数', unit: '× 10^12/L', reference: '3.5-5.5', min: 3.5, max: 5.5 },
    { field: 'hgb', shortName: 'HGB', label: '血红蛋白', unit: 'g/L', reference: '110-160', min: 110, max: 160 },
    { field: 'plt', shortName: 'PLT', label: '血小板计数', unit: '× 10^9/L', reference: '100-300', min: 100, max: 300 },
    { field: 'neutp', shortName: 'NEUT%', label: '中性粒细胞百分比', unit: '%', reference: '50-70', min: 50, max: 70 },
    { field: 'lymp', shortName: 'LYM%', label: '淋巴细胞百分比', unit: '%', reference: '20-40', min: 20, max: 40 }
  ]
}

// 状态类型常量
export const STATUS_TYPES = {
  HIGH: 'high',
  LOW: 'low',
  NORMAL: null
}

// 系统主题色配置
export const THEME_COLORS = {
  primary: '#409EFF',
  success: '#67C23A',
  warning: '#E6A23C',
  danger: '#F56C6C',
  info: '#909399',
  
  // 医疗相关颜色
  medicalPrimary: '#3f8cda',
  medicalSuccess: '#45B787',
  medicalWarning: '#E6A23C',
  medicalDanger: '#F56C6C',
  medicalNeutral: '#909399'
}

// 分页默认配置
export const PAGINATION_CONFIG = {
  defaultPageSize: 10,
  pageSizes: [10, 20, 50, 100],
  layout: 'total, sizes, prev, pager, next, jumper'
}

// 日期时间格式化选项
export const DATE_FORMAT_OPTIONS = {
  // 日期时间格式（2023-06-12 14:30）
  dateTime: {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  },
  // 仅日期格式（2023-06-12）
  dateOnly: {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  },
  // 仅时间格式（14:30）
  timeOnly: {
    hour: '2-digit',
    minute: '2-digit'
  }
} 