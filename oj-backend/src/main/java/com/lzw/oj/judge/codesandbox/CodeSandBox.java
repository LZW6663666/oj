package com.lzw.oj.judge.codesandbox;

import com.lzw.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.lzw.oj.judge.codesandbox.model.ExecuteCodeResponse;

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
