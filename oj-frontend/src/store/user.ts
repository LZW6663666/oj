import {StoreOptions} from "vuex";
import ACCESS_ENUM from "@/access/accessEnum";
import {UserControllerService} from "../../generated";

export default {
    namespaced: true,
    state: () => ({
        loginUser: {
            userName: "未登录",
            // userRole: ACCESS_ENUM.NOT_LOGIN,
        },
    }),
    getters: {},
    actions: {
        async getLoginUser({commit, state}, payload) {
            //从远程获取登录信息
            const res = await UserControllerService.getLoginUserUsingGet()
            // console.log("不知道成功没有")
            if (res.code === 0) {
                // console.log("信息获取成功",res)
                commit("updateUser", res.data)
            } else {
                // console.log("登录信息获取失败")
                commit("updateUser", {...state, userRole: ACCESS_ENUM.NOT_LOGIN})
            }
        }
    },
    mutations: {
        updateUser(state, payload) {
            state.loginUser = payload;
        }
    },
} as StoreOptions<any>