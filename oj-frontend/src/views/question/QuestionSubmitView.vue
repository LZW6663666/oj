<template>
  <div id="questionSubmitView">

    <a-form :model="searchParams" layout="inline">
      <a-form-item field="questionId" label="题号" style="min-width: 240px">
        <a-input v-model="searchParams.questionId" placeholder="请输入名称"/>
      </a-form-item>

        <a-form-item field="language" label="编程语言" style="min-width: 240px">
          <a-select v-model="searchParams.language" :style="{width:'320px'}" placeholder="选择编程语言">
            <a-option>java</a-option>
            <a-option>cpp</a-option>
            <a-option>go</a-option>
            <a-option>html</a-option>
          </a-select>
        </a-form-item>


      <a-form-item>
        <a-button type="primary" @click="doSubmit">搜索</a-button>
      </a-form-item>
    </a-form>
    <a-divider size="0"/>

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
    <template #judgeInfo="{ record }">
        {{ JSON.stringify(record.judgeInfo) }}
      </template>
      <template #createTime="{ record }">
        {{ moment(record.createTime).format("YYYY-MM-DD") }}
      </template>

    </a-table>
  </div>

</template>

<script setup lang="ts">
import {onMounted, reactive, ref, watch, watchEffect} from "vue";
import {
  Question,
  QuestionControllerService,
  QuestionSubmitQueryRequest
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import {useRouter} from "vue-router";
import moment from "moment";

const show = ref(true)
const total = ref(0)
const dataList = ref([])
const tableRef = ref()
const searchParams = ref<QuestionSubmitQueryRequest>({
  questionId: undefined,
  language: undefined,
  pageSize: 8,
  current: 1,
});

const loadData = async () => {
  console.log("本次的searchParams.value", searchParams.value)
  const res = await QuestionControllerService.listQuestionSubmitByPageUsingPost(
      {
        ...searchParams.value,
        sortField: "createTime",
        sortOrder: "descend",
      }
  )

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
    title: '题交号',
    dataIndex: 'id',
  },
  {
    title: '编程语言',
    dataIndex: 'language',
  },
  {
    title: '判题信息',
    // dataIndex: 'judgeInfo',
    slotName: 'judgeInfo',

  },
  {
    title: '判题状态',
    dataIndex: 'status'
  },
  {
    title: '题目id',
    dataIndex: 'questionId'
  },
  {
    title: '提交者id',
    dataIndex: 'userId'
  },
  {
    title: '创建时间',
    slotName: 'createTime',
  },

];

const onPageChange = (current: number) => {
  searchParams.value = {
    ...searchParams.value,
    current: current
  }

  // loadData();
}

const router = useRouter();
/**
 * 跳转到做题页面
 * @param question
 */
const toQuestionPage = (question: Question) => {
  // console.log(question)
  router.push({
    path: `/view/question/${question.id}`,
  })

}
/**
 * 提交搜索条件
 */
const doSubmit = () => {
  // console.log("搜索条件", searchParams.value)
  searchParams.value = {
    ...searchParams.value,
    current: 1
  }
  console.log("searchParams是：", searchParams.value)
  // loadData();
}


</script>

<style scoped>
#questionSubmitView {
  max-width: 1280px;
  margin: 0 auto;
}

</style>


