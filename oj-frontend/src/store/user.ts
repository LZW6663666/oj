import {StoreOptions} from "vuex";

export default {
    namespaced: true,
    state: () => ({
        loginUser: {
            userName: "未登录",
            role:"notLogin",
        },
    }),
    getters: {},
    actions: {
        getLoginUser({commit, state}, payload) {
            //todo 改为从远程获取登录信息
            commit("updateUser", {userName: "lzw"})
        }
    },
    mutations: {
        updateUser(state, payload) {
            state.loginUser = payload;
        }
    },
} as StoreOptions<any>