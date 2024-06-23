const { defineConfig } = require("@vue/cli-service");
const MonacoEditorPlugin = require("monaco-editor-webpack-plugin");
module.exports = defineConfig({
  transpileDependencies: true,
  // lintOnSave:false,//关闭eslint检测
  chainWebpack(config){
    config.plugin("monaco").use(new MonacoEditorPlugin())
  },
});
