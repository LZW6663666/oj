<template>
  <div id="userLayout">
    <a-layout style="min-height: 100vh">
      <a-layout-header class="header">

        <a-space>
          <img src="../assets/oj-logo.jpg" class="logo">
          <div>OJ</div>
        </a-space>
      </a-layout-header>

      <a-layout-content class="content"><router-view /></a-layout-content>
      <a-layout-footer class="footer">版权信息</a-layout-footer>
    </a-layout>
  </div>
</template>

<style scoped>
#userLayout{
  text-align: center;
  background: url("https://gw.alipayobjects.com/zos/rmsportal/FfdJeJRQWjEeGTpqgBKj.png")0% 0% / 100% 100%;
}

#userLayout .logo{
  width: 100px;
  height: 64px;
}

#userLayout .header{
  /*background: red;*/
  margin-top: 16px;
  /*margin-bottom: 16px;*/
  /*box-shadow: #eee 1px 1px 5px;*/
}
#userLayout .content{
  /*background: linear-gradient(to right,#bbb,#fff);*/
  margin-bottom: 16px;
  padding: 20px;
  
}
#userLayout .footer{
  /*background: #efefef;*/
  padding: 16px;
  position: sticky;
  bottom: 0;
  left: 0;
  right: 0;
  text-align: center;
}
</style>
<script lang="ts">

export default {      //有了setup就不需要再定义 components象来注册子组件——在 <script setup> 中，任何从局部导入的组件都会自动可用于模板引用
  name:"UserLayout"
};


</script>
