package com.lzw.ojbackendjudgeservice.judge.strategy;


import com.lzw.ojbackendmodel.model.codesandbox.JudgeInfo;
import com.lzw.ojbackendmodel.model.entity.Question;
import com.lzw.ojbackendmodel.model.entity.QuestionSubmit;
import com.lzw.ojbackendmodel.model.question.JudgeCase;
import lombok.Data;

import java.util.List;

/**
 * 上下文（用于定义在策略中传递的参数）
 */
@Data
public class JudgeContext {
    private JudgeInfo judgeInfo;
    private List<String> inputList;
    private List<String> outputList;
    private Question question;
    private List<JudgeCase> judgeCaseList;
    private QuestionSubmit questionSubmit;
}
