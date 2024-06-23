<template>
  <div id="manageQuestionView">
    <a-table
            :ref="tableRef"
            :columns="columns"
             :data="dataList"
             :pagination="{
               showTotal:true,
               pageSize:searchParams.pageSize,
               current:searchParams.current,
               total:total
             }"
            @pageChange="onPageChange"
    >
      <template #optional="{ record }">
        <a-space>
          <a-button type="primary" @click="doUpdate(record)">修改</a-button>
          <a-button status="danger" @click="doDelete(record)">删除</a-button>
        </a-space>

      </template>
    </a-table>
  </div>

</template>

<script setup lang="ts">
import {onMounted, reactive, ref, watch, watchEffect} from "vue";
import {Question, QuestionControllerService} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import {useRouter} from "vue-router";

const show = ref(true)
const total = ref(0)
const dataList = ref([])
const tableRef = ref()
const searchParams = ref({
  pageSize: 10,
  current: 1,
});

const loadData = async () => {
  console.log("本次的searchParams.value",searchParams.value)
  const res = await QuestionControllerService.listQuestionByPageUsingPost(searchParams.value);

  if (res.code === 0) {
    dataList.value = res.data.records;
    total.value = res.data.total;
  } else {
    message.error("加载失败： " + res.message);
  }

}

/**
 * 监听searchParams的变化，重新加载数据
 */
watchEffect(() => {
  // console.log("监听到searchParams发生了变化")
  loadData();
})

/**
 * 页面加载时，请求数据
 */
onMounted(() => {
  loadData();
})


const columns = [
  {
    title: 'id',
    dataIndex: 'id',
  },
  {
    title: '标题',
    dataIndex: 'title',
  },
  {
    title: '内容',
    dataIndex: 'content',
  },
  {
    title: '标签',
    dataIndex: 'tags',
  },
  {
    title: '答案',
    dataIndex: 'answer',
  },
  {
    title: '提交数',
    dataIndex: 'submitNum',
  },
  {
    title: '通过数',
    dataIndex: 'acceptNum',
  },
  {
    title: '判题配置',
    dataIndex: 'judgeConfig',
  },
  {
    title: '判题用例',
    dataIndex: 'judgeCase',
  },
  {
    title: '用户id',
    dataIndex: 'userId',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },

  {
    title: '操作',
    slotName: 'optional'
  }
];

const onPageChange = (current: number) => {
  searchParams.value={
    ...searchParams.value,
    current: current
  }
  // loadData();
}

const doDelete = async (question: Question) => {
  // console.log(question)
  const res = await QuestionControllerService.deleteQuestionUsingPost({id: question.id});
  if(res.code===0){
    message.success("删除成功")
    loadData();
  }
  else {
    message.error("删除失败")
  }
}
const router = useRouter();
const doUpdate =  (question: Question) => {
  // console.log(question)
  router.push({
    path: '/update/question',
    query: {
      id: question.id
    }
  })

}


</script>

<style scoped>

</style>


