import { getUserRole } from '../utils/jwt'

export default {
  install(app) {
    app.directive('permission', {
      mounted(el, binding) {
        const { value } = binding
        const role = getUserRole()
        
        if (value && !value.includes(role)) {
          el.parentNode?.removeChild(el)
        }
      }
    })
  }
}