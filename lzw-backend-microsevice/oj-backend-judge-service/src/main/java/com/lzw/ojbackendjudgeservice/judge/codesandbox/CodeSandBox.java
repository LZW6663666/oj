package com.lzw.ojbackendjudgeservice.judge.codesandbox;


import com.lzw.ojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.lzw.ojbackendmodel.model.codesandbox.ExecuteCodeResponse;

/**
 * 代码沙箱接口定义
 */
public interface CodeSandBox {
    /**
     * 执行代码
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
