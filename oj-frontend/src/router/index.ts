import { createRouter, createWebHistory } from "vue-router";
// import HomeView from "../views/HomeView.vue";
import {routes} from "@/router/router";




const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
