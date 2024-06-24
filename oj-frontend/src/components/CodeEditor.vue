<template>
  <div id="code-editor" ref="codeEditorRef" style="min-height: 600px;height: 80vh"/>
  <!--  <Editor :value="value" :plugins="plugins" @change="handleChange"/>-->
<!--  {{value}}-->
<!--  <a-button @click="fillValue">填充值</a-button>-->
</template>

<script setup lang="ts">
import * as monaco from 'monaco-editor'
import {ref, onMounted, toRaw, withDefaults, defineProps, watch} from "vue";

/**
 * 定义组件属性类型
 */
interface Props  {
  value: string;
  language?: string;
  handleChange: (v: string) => void;
}
/**
 * 给组件属性添加初始值
 */
const props = withDefaults(defineProps<Props>(), {
  value: ()=>'',
  language: ()=>"java",
  handleChange: (v:string) => {console.log(v)},
})


const codeEditorRef = ref();
const codeEditor = ref();
// const value = ref('hello word');


watch(()=>props.language,()=>{
  //修改语言
  if(codeEditor.value){
    monaco.editor.setModelLanguage(toRaw(codeEditor.value).getModel(), props.language);
  }
})

onMounted(() => {
  if (!codeEditorRef.value) {
    return;
  }
  codeEditor.value = monaco.editor.create(codeEditorRef.value, {
    value: props.value,
    language: props.language,
    automaticLayout: true,
    minimap: {
      enabled: true,
    },
    colorDecorators: true,
    // lineNumbers: 'off',
    // roundedSelection:false,
    // scrollBeyondLastLine:false,
    readOnly: false,
    theme: 'vs-dark',
  });

  //编辑监听内容编化
  codeEditor.value.onDidChangeModelContent(() => {
    //console.log('编辑内容变化', toRaw(codeEditor.value).getValue());
    props.handleChange(toRaw(codeEditor.value).getValue());
  });

})


</script>

<!--
集成monaco编译器：
1、引入monaco-editor：npm install monaco-editor
2、引入monaco-editor-webpack-plugin：npm install monaco-editor-webpack-plugin
3、引入@babel/plugin-transform-class-static-block：  npm install --save-dev @babel/plugin-transform-class-static-block
3、配置babel.config.js：
    加入plugins: ['@babel/plugin-transform-class-static-block']
4、配置vue.config.js：
    const MonacoEditorPlugin = require("monaco-editor-webpack-plugin");
    module.exports里面加入 chainWebpack: (config) => {config.plugin("monaco").use(new MonacoEditorPlugin())}

-->