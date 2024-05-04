<template>
  <div class="home">
    你没权限
  </div>
</template>

<script  lang="ts">        //scrip什么都不写也可以运行，但ts会报语法错；只写setup也可以，但idea不好识别提示；
import { defineComponent } from "vue";
export default defineComponent({
  name: "NoAuthView",         //  或者不写setup，写export default,有name的话，idea可以更好识别(在路由那边)，更好给提示
});

</script>