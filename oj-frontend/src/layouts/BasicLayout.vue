<template>
  <div id="basicLayout">
    <a-layout style="min-height: 100vh">
      <a-layout-header class="header"><GlobalHeader /></a-layout-header>
      <a-layout-content class="content"><router-view /></a-layout-content>
      <a-layout-footer class="footer">版权信息</a-layout-footer>
    </a-layout>
  </div>
</template>

<style scoped>
#basicLayout{

}
#basicLayout .header{
  /*background: red;*/
  margin-bottom: 16px;
  box-shadow: #eee 1px 1px 5px;
}
#basicLayout .content{
  background: linear-gradient(to right,#bbb,#fff);
  margin-bottom: 16px;
  padding: 20px;
  
}
#basicLayout .footer{
  background: #efefef;
  padding: 16px;
  position: sticky;
  bottom: 0;
  left: 0;
  right: 0;
  text-align: center;
}
</style>
<script setup lang="ts">

import GlobalHeader from "../components/GlobalHeader.vue";
// export default {      //有了setup就不需要再定义 components象来注册子组件——在 <script setup> 中，任何从局部导入的组件都会自动可用于模板引用
//   components: {GlobalHeader},
//   name:"BasicLayout"
// };


</script>
