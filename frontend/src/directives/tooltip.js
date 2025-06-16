import { createApp, ref, h } from 'vue'
import { ElTooltip } from 'element-plus'

/**
 * 自定义tooltip指令
 * 用法: v-tooltip="'提示内容'" 或 v-tooltip="{ content: '提示内容', placement: 'top', effect: 'dark' }"
 */
export default {
  install(app) {
    app.directive('tooltip', {
      mounted(el, binding) {
        const value = binding.value
        
        // 处理不同类型的绑定值
        const tooltipContent = typeof value === 'string' ? value : (value?.content || '')
        const placement = typeof value === 'object' ? (value.placement || 'top') : 'top'
        const effect = typeof value === 'object' ? (value.effect || 'dark') : 'dark'
        const offset = typeof value === 'object' ? (value.offset || 8) : 8
        
        // 不显示空内容
        if (!tooltipContent) return
        
        // 保存原始内容和title
        const originalTitle = el.getAttribute('title')
        if (originalTitle) {
          el.setAttribute('data-original-title', originalTitle)
          el.removeAttribute('title')
        }

        // 设置自定义属性
        el.setAttribute('data-tooltip', tooltipContent)
        el.classList.add('has-tooltip')
        
        // 创建tooltip实例
        const tooltip = createApp({
          setup() {
            const visible = ref(false)
            
            return () => h(ElTooltip, {
              content: tooltipContent,
              effect,
              placement,
              offset,
              teleported: true, 
              showAfter: 200,
              'visible': visible.value
            })
          }
        })
        
        // 添加事件监听
        el.addEventListener('mouseenter', () => {
          if (tooltip) {
            tooltip._instance.exposed.show()
          }
        })
        
        el.addEventListener('mouseleave', () => {
          if (tooltip) {
            tooltip._instance.exposed.hide()
          }
        })
        
        // 保存实例到元素
        el._tooltip = tooltip
      },
      
      updated(el, binding) {
        const value = binding.value
        const tooltipContent = typeof value === 'string' ? value : (value?.content || '')
        
        // 更新tooltip内容
        if (tooltipContent !== el.getAttribute('data-tooltip')) {
          el.setAttribute('data-tooltip', tooltipContent)
          
          // 如果存在tooltip实例，需要更新它
          if (el._tooltip && el._tooltip._instance) {
            el._tooltip._instance.props.content = tooltipContent
          }
        }
      },
      
      unmounted(el) {
        // 清理tooltip实例
        if (el._tooltip) {
          el._tooltip.unmount()
          delete el._tooltip
        }
        
        // 恢复原始title
        const originalTitle = el.getAttribute('data-original-title')
        if (originalTitle) {
          el.setAttribute('title', originalTitle)
          el.removeAttribute('data-original-title')
        }
        
        el.removeAttribute('data-tooltip')
        el.classList.remove('has-tooltip')
      }
    })
  }
} 