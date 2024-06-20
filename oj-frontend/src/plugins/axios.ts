// 添加请求拦截器   axios的全局（拦截）配置，当然，也可以在generated/core/OpenAPI.ts里面修改实现
import axios from "axios";

axios.interceptors.request.use(function (config) {
    // 在发送请求之前做些什么
    return config;
}, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
});

// 添加响应拦截器
axios.interceptors.response.use(function (response) {
    // 2xx 范围内的状态码都会触发该函数。
    // 对响应数据做点什么
    console.log("这是响应拦截器输出：响应",response)
    return response;
}, function (error) {
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误做点什么
    return Promise.reject(error);
});

/*
1、免写axios配置：
    1.1：后端运行后打开swagger文档然后下载到本地    curl -o api-docs.json http://localhost:8101/api/v2/api-docs
    1.2：本地运行                            openapi --input ./api-docs.json --output ./generated --client axios




* */

