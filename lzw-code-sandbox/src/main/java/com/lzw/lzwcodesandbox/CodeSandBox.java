package com.lzw.lzwcodesandbox;

import com.lzw.lzwcodesandbox.model.ExecuteCodeRequest;
import com.lzw.lzwcodesandbox.model.ExecuteCodeResponse;


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
