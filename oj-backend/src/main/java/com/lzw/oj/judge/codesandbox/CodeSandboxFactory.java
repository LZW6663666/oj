package com.lzw.oj.judge.codesandbox;

import com.lzw.oj.judge.codesandbox.impl.ExampleCodeSandbox;
import com.lzw.oj.judge.codesandbox.impl.RemoteCodeSandbox;
import com.lzw.oj.judge.codesandbox.impl.ThirdPartyCodeSandbox;

/**
 * 代码沙箱工厂(根据字符串参数创建指定的代码沙箱实例)
 */
public class CodeSandboxFactory {
    /**
     * 根据字符串参数创建指定的代码沙箱实例
     * @param type
     * @return
     */
    public static CodeSandBox newInstance(String type) {
        switch (type){
            case "example":
                return new ExampleCodeSandbox();
            case "remote":
                return new RemoteCodeSandbox();
            case "thirdParty":
                return new ThirdPartyCodeSandbox();
            default:
                return new ExampleCodeSandbox();
        }
    }
}
