// 导入Vue框架
import { createApp } from 'vue';

// 导入根组件
import App from './App.vue';

// 导入路由
import router from './router';

// 导入全局样式
import './assets/css/style.css';

// 创建Vue应用实例
const app = createApp(App);

// 使用路由
app.use(router);

// 挂载应用到DOM元素
app.mount('#app');