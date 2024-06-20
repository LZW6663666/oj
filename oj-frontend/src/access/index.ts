import router from "@/router";
import store from "@/store";
import ACCESS_ENUM from "@/access/accessEnum";
import checkAccess from "@/access/checkAccess";


router.beforeEach(async (to, from, next) => {
    console.log("全局路由拦截器处：登录用户信息 ", store.state.user.loginUser)

    const loginUser = store.state.user.loginUser;
    const needAccess = to.meta?.access ?? ACCESS_ENUM.NOT_LOGIN;
    // 如果之前没登录过，自动登录
    if(!loginUser||!loginUser.userRole){
        // console.log("登录开始")
      await store.dispatch("user/getLoginUser");
      // console.log("登录结束",store.state.user.loginUser)
    }
    // 要跳转的页面必须登录
    if (needAccess !== ACCESS_ENUM.NOT_LOGIN) {
        //如果没有登录，跳转到登录页面
        if (!loginUser || !loginUser.userRole) {
            next(`/user/login?redirect${to.fullPath}`);
            return;
        }
        //如果已经登陆了，但是权限不足，那么跳转到无权限页面
        if (!checkAccess(loginUser, needAccess as string)) {
            next("/noAuth");
            return;
        }
    }

    next();
    // console.log(to)
})