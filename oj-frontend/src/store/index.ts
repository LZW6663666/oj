import { createStore } from "vuex";
import user from "@/store/user";

export default createStore({
  state: {},
  getters: {},
  mutations: {},//commit调用，同步操作
  actions: {},//dispatch调用，异步操作
  modules: {
    user
  },
});
