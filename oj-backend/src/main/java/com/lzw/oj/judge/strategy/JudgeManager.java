package com.lzw.oj.judge.strategy;

import com.lzw.oj.model.dto.questionsubmit.JudgeInfo;
import com.lzw.oj.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManager {
   public JudgeInfo doJudge(JudgeContext judgeContext) {
       QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
       String language = questionSubmit.getLanguage();
       JudgeStrategy judgeStrategy = new DefaultJudgeStrategy(); // 默认策略
       if ("java".equals(language)) {
           judgeStrategy = new JavaLanguageJudgeStrategy();
//       } else if ("cpp".equals(language)) {
//           judgeStrategy = new CppLanguageJudgeStrategy(); //后续添加
//       } else if ("python".equals(language)) {
//           judgeStrategy = new PythonLanguageJudgeStrategy();
//       }

       }
       return judgeStrategy.doJudge(judgeContext);
   }
}
