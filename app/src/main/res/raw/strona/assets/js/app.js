import router from './_router.js'
import './fixes/android.fix.js'

// App
const app = new Vue({
  router,
}).$mount('#app');