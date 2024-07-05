<template>
  <div id="questionsView">

    <a-form :model="searchParams" layout="inline">
      <a-form-item field="title" label="名称" style="min-width: 240px">
        <a-input v-model="searchParams.title" placeholder="请输入名称"/>
      </a-form-item>
      <a-form-item field="tags" label="标签" style="min-width: 240px">
        <a-input-tag v-model="searchParams.tags" placeholder="请输入标签"/>
      </a-form-item>

      <a-form-item>
        <a-button type="primary" @click="doSubmit">提交</a-button>
      </a-form-item>
    </a-form>
    <a-divider size="0" />

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
      <template #tags="{ record }">
        <a-space wrap>
          <a-tag v-for="(tag, index) of record.tags" :key="index" color="green">{{ tag }}</a-tag>
        </a-space>
      </template>
      <template #acceptedRate="{ record }">
        {{
          `${record.submitNum ? record.acceptedNum / record.submitNum : "0"} %
          (${record.acceptedNum}/${record.submitNum})`
        }}
      </template>
      <template #createTime="{ record }">
        {{ moment(record.createTime).format("YYYY-MM-DD") }}
      </template>
      <template #optional="{ record }">
        <a-space>
          <a-button type="primary" @click="toQuestionPage(record)">做题</a-button>
        </a-space>
      </template>
    </a-table>
  </div>

</template>

<script setup lang="ts">
import {onMounted, reactive, ref, watch, watchEffect} from "vue";
import {Question, QuestionControllerService, QuestionQueryRequest} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import {useRouter} from "vue-router";
import moment from "moment";

const show = ref(true)
const total = ref(0)
const dataList = ref([])
const tableRef = ref()
const searchParams = ref<QuestionQueryRequest>({
  title:"",
  tags:[],
  pageSize: 5,
  current: 1,
});

const loadData = async () => {
  console.log("本次的searchParams.value", searchParams.value)
  const res = await QuestionControllerService.listQuestionVoByPageUsingPost(searchParams.value);

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
    title: '题号',
    dataIndex: 'id',
  },
  {
    title: '题目名称',
    dataIndex: 'title',
  },

  {
    title: '标签',
    slotName: 'tags'
  },
  {
    title: '通过率',
    slotName: 'acceptedRate'
  },
  {
    title: '创建时间',
    slotName: 'createTime',
  },

  {

    slotName: 'optional'
  }
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
    console.log("searchParams是：",searchParams.value)
  // loadData();
}


</script>

<style scoped>
#questionsView {
  max-width: 1280px;
  margin: 0 auto;
}

</style>


