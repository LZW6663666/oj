<template>
  <Editor :value="value" :mode="mode" :plugins="plugins" @change="handleChange"/>
</template>

<script setup lang="ts">
import gfm from '@bytemd/plugin-gfm'
import highlight from "@bytemd/plugin-highlight";
import {Editor, Viewer} from '@bytemd/vue-next'
import {ref,withDefaults,defineProps} from "vue";

/**
 * 定义组件属性类型
 */
interface Props  {
  value: string;
  mode?: string;
  handleChange: (v: string) => void;
}

const plugins = [
  gfm(),
  highlight(),
  // Add more plugins here
]

/**
 * 给组件属性添加初始值
 */
const props = withDefaults(defineProps<Props>(), {
  value: ()=>'',
  mode: ()=>'split',
  handleChange: (v:string) => {console.log(v)},
})


// const value = ref('');
// const handleChange = (v: string) => {
//   value.value = v
// }

</script>
<style >
/*scoped去掉是为了让下面这句全局样式生效。作用是隐藏md文档右上角的github广告图标。*/
.bytemd-toolbar-icon.bytemd-tippy.bytemd-tippy-right:last-child {
  display:none;
}
</style>