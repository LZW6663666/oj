<template>
  <div id="app">
    <BasicLayout/>
  </div>


</template>

<style>
#app {
}
</style>

<script setup>
import BasicLayout from "@/layouts/BasicLayout";

import {useRouter} from "vue-router";
import {useStore} from "vuex";
import {onMounted} from "vue";

/**
 * 全局初始化函数，有全局单次调用的代码都可以写在这里。
 */
const doInit=()=>{
  console.log("hello 欢迎来到我的项目")
}

onMounted(()=>{
  doInit();
})

const router = useRouter();
const store = useStore();
router.beforeEach((to, from, next) => {
  if (to.meta?.access === "canAdmin") {   //是不是只有管理员才能访问的权限
    if (store.state.user.loginUser?.role !== "admin") {  //是不是管理员
      next("/noAuth");
      return;
    }
  }
  next();
  // console.log(to)
})

</script>
