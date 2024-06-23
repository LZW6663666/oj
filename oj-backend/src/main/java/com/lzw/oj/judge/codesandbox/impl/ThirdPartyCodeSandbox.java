package com.lzw.oj.judge.codesandbox.impl;

import com.lzw.oj.judge.codesandbox.CodeSandBox;
import com.lzw.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.lzw.oj.judge.codesandbox.model.ExecuteCodeResponse;

/**
 * 第三方代码沙箱（调用网上现成的代码沙箱）
 */
public class ThirdPartyCodeSandbox implements CodeSandBox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
