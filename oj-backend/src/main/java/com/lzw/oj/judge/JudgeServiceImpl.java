package com.lzw.oj.judge;

import cn.hutool.json.JSONUtil;
import com.lzw.oj.common.ErrorCode;
import com.lzw.oj.exception.BusinessException;
import com.lzw.oj.judge.codesandbox.CodeSandBox;
import com.lzw.oj.judge.codesandbox.CodeSandBoxProxy;
import com.lzw.oj.judge.codesandbox.CodeSandboxFactory;
import com.lzw.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.lzw.oj.judge.codesandbox.model.ExecuteCodeResponse;
import com.lzw.oj.judge.strategy.JudgeContext;
import com.lzw.oj.judge.strategy.JudgeManager;
import com.lzw.oj.model.dto.question.JudgeCase;
import com.lzw.oj.judge.codesandbox.model.JudgeInfo;
import com.lzw.oj.model.entity.Question;
import com.lzw.oj.model.entity.QuestionSubmit;
import com.lzw.oj.model.enums.QuestionSubmitStatusEnum;
import com.lzw.oj.service.QuestionService;
import com.lzw.oj.service.QuestionSubmitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {
    @Value("${codesandbox.type:example}")
    private String type;
    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionSubmitService questionSubmitService;
    @Resource
    private JudgeManager judgeManager;
    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        //1)传入题目提交id，获取到对应的题目、提交信息（包含代码、编程语言）
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"提交信息不存在");
        }

        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"题目不存在");
        }
        //2)如果题目提交的状态不为等待中，就不用重复执行
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"题目正在判题中");
        }
        //3)更改判题（题目提交）的状态为判题中，防止重复执行
        QuestionSubmit questionSubmitupdate = new QuestionSubmit();
        questionSubmitupdate.setId(questionSubmitId);
        questionSubmitupdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionSubmitService.updateById(questionSubmitupdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"题目状态更新错误");
        }
        //4)调用沙箱，获取到判题结果
        CodeSandBox codeSandBox = CodeSandboxFactory.newInstance(type);
        codeSandBox = new CodeSandBoxProxy(codeSandBox);
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        //获取到题目的输入用例
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList= JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());

        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);

        List<String> outputList = executeCodeResponse.getOutputList();
        //5)根据沙箱的执行结果，设置题目的判题状态和信息
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setQuestion(question);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestionSubmit(questionSubmit);


//        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy(); // 之前是默认策略，现在要去策略管理进行判断看看该用哪些策略
        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);
        //修改数据库中的判题结果
        questionSubmitupdate = new QuestionSubmit();
        questionSubmitupdate.setId(questionSubmitId);
        questionSubmitupdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        questionSubmitupdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        update = questionSubmitService.updateById(questionSubmitupdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"题目状态更新错误");
        }
        QuestionSubmit questionSubmitResult = questionSubmitService.getById(questionId);


        return questionSubmitResult;
    }
}
