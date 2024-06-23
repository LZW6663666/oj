<template>
  <div id="exampleView">
    <!-- <img alt="Vue logo" src="../assets/logo.png" /> -->
    <CodeEditor :value="codeValue" :handle-change="onCodeChange"/>
    <MdEditor :value="MdValue" :handle-change="onMdChange"/>

  </div>
</template>

<script setup lang="ts">
// import { defineComponent } from "vue";
// import HelloWorld from "@/components/HelloWorld.vue";
import MdEditor from "@/components/MdEditor.vue"; // @ is an alias to /src
import {ref} from "vue";
import CodeEditor from "@/components/CodeEditor.vue";

const MdValue = ref('');
const CodeValue = ref('');
const onMdChange = (v: string) => {
  MdValue.value = v;
  console.log(v);
}
const onCodeChange = (v: string) => {
  CodeValue.value = v;
  console.log(v);
}

// export default defineComponent({
//   name: "HomeView",
//   components: {
//     MdEditor,
//     HelloWorld,
//   },
// });
</script>
