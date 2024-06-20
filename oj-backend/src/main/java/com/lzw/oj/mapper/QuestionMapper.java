package com.lzw.oj.mapper;

import com.lzw.oj.model.entity.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzw.oj.model.entity.QuestionSubmit;

/**
 * @author lzw
 * @description 针对表【question(题目)】的数据库操作Mapper
 * @createDate 2024-06-18 16:01:41
 * @Entity com.lzw.oj.model.entity.Question
 */
public interface QuestionMapper extends BaseMapper<Question> {

}