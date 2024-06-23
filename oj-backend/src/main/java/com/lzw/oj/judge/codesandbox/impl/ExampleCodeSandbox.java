package com.lzw.oj.judge.codesandbox.impl;

import com.lzw.oj.judge.codesandbox.CodeSandBox;
import com.lzw.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.lzw.oj.judge.codesandbox.model.ExecuteCodeResponse;
import com.lzw.oj.model.dto.questionsubmit.JudgeInfo;
import com.lzw.oj.model.enums.JudgeInfoMessageEnum;
import com.lzw.oj.model.enums.QuestionSubmitStatusEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 示例代码沙箱(仅为了跑通业务流程)
 */
@Slf4j
public class ExampleCodeSandbox implements CodeSandBox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {

        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();
        List<String> inputList = executeCodeRequest.getInputList();
       // log.info("codeSandBox execute code, language = {}, code = {}", language, code);

        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setMessage("测试执行成功");
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        judgeInfo.setTime(100L);
        judgeInfo.setMemory(100L);
        executeCodeResponse.setJudgeInfo(judgeInfo);

        return executeCodeResponse;
    }
}
