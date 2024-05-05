<template>

  <a-row id="globalHeader" align="center" :wrap="false">
    <a-col flex="auto">
      <a-menu mode="horizontal" :selected-keys="selecteKeys" @menu-item-click="doMenuClick">
        <a-menu-item key="0" :style="{ padding: 0, marginRight: '38px' }" disabled>
          <div class="title-bar">
            <img class="logo" src="../assets/oj-logo.jpg">
            <div class="title">这是 OJ</div>
          </div>
        </a-menu-item>

        <a-menu-item v-for="item in visibleRoutes " :key="item.path">{{ item.name }}</a-menu-item>
      </a-menu>
    </a-col>
    <a-col flex="100px">
      <div>
        {{ store.state.user?.loginUser?.userName ?? "未登录"   /*这里的？是可选操作符*/ }}
      </div>
    </a-col>
  </a-row>

</template>


<script setup lang="ts">
import {routes} from "@/router/router";
import {useRouter} from "vue-router";
import {computed, ref} from "vue";
import {useStore} from "vuex";
import checkAccess from "@/access/checkAccess";
import ACCESS_ENUM from "@/access/accessEnum";

const router = useRouter();
const store = useStore();
// const loginUser = store.state.user.loginUser;//取出当前登录用户
const selecteKeys = ref(['/']);

//路由跳转后，更新选中的菜单项
router.afterEach((to, from, failure) => {
  selecteKeys.value = [to.path]
});


const doMenuClick = (key: string) => {
  router.push({
    path: key,
  })
}

//展示在菜单的路由数组
const visibleRoutes = computed(() => {//visibleRoutes计算属性依赖于loginUser，确保每当loginUser更新时，visibleRoutes也会重新计算。
  return routes.filter((item) => {
    if (item.meta?.hideInMenu) {
      return false;
    }
    //todo 根据权限过滤菜单          通过直接在computed内部访问store.state.user.loginUser(而不是在外面把loginUser拿出来)，确保Vue能够跟踪这个依赖。
    if (!checkAccess(store.state.user.loginUser, item?.meta?.access as string)) {
      return false;
    }
    return true;
  })
})

setTimeout(() => {
  store.dispatch("user/getLoginUser", {
    userName: "lzw6666",
    userRole: ACCESS_ENUM.ADMIN,
  })
}, 3000)

</script>


<style scoped>
.title-bar {
  display: flex;
  align-items: center;
}

.title {
  color: #444;
  margin-left: 16px;
}

.logo {
  height: 48px;
}
</style>
