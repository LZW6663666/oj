package com.lzw.ojbackendjudgeservice.judge.codesandbox.impl;


import com.lzw.ojbackendjudgeservice.judge.codesandbox.CodeSandBox;
import com.lzw.ojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.lzw.ojbackendmodel.model.codesandbox.ExecuteCodeResponse;

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
