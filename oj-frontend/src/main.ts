import { createApp } from "vue";
import App from "./App.vue";
import ArcoVue from '@arco-design/web-vue';
import '@arco-design/web-vue/dist/arco.css';
import router from "./router";
import store from "./store";
import "@/plugins/axios"  //导入全局axios响应拦截器
import "@/access"
import 'bytemd/dist/index.css'//md文档的css

createApp(App).use(ArcoVue).use(store).use(router).mount("#app");

