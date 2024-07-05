package com.lzw.ojbackendjudgeservice.controller.inner;

import com.lzw.ojbackendjudgeservice.judge.JudgeService;
import com.lzw.ojbackendmodel.model.entity.QuestionSubmit;
import com.lzw.ojbackendserverclient.service.JudgeFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


/**
 * 该服务仅内部调用，不是给前端的。
 */
@RestController
@RequestMapping("/inner")
public class JudgeInnerController implements JudgeFeignClient {
    @Resource
    private JudgeService judgeService;
    /**
     * 判题
     * @param questionSubmitId
     * @return
     */
    @Override
    @PostMapping("/do")
    public QuestionSubmit doJudge(long questionSubmitId){
        return judgeService.doJudge(questionSubmitId);
    }

}
