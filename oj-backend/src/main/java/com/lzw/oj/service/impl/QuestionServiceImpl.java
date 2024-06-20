package com.lzw.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.lzw.oj.common.ErrorCode;
import com.lzw.oj.constant.CommonConstant;
import com.lzw.oj.exception.BusinessException;
import com.lzw.oj.exception.ThrowUtils;

import com.lzw.oj.model.dto.question.QuestionQueryRequest;
import com.lzw.oj.model.entity.*;
import com.lzw.oj.model.vo.QuestionVO;
import com.lzw.oj.model.vo.UserVO;
import com.lzw.oj.service.QuestionService;
import com.lzw.oj.mapper.QuestionMapper;
import com.lzw.oj.service.UserService;
import com.lzw.oj.utils.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author lzw
* @description 针对表【question(题目)】的数据库操作Service实现
* @createDate 2024-06-18 16:01:41
*/
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
    implements QuestionService{

    private final static Gson GSON = new Gson();

    @Resource
    private UserService userService;
    /**
     * 验证题目的合法性。
     * 当添加题目时，检查标题、内容和标签是否为空或超出长度限制。
     * 当编辑题目时，仅在相关字段存在时检查其长度。
     *
     * @param question 待验证的题目对象。
     * @param add 指示当前操作是添加还是编辑题目。
     * @throws BusinessException 如果题目参数无效或超出长度限制，则抛出此异常。
     */
    @Override
    public void validQuestion(Question question, boolean add) {
        // 检查题目对象是否为空，如果为空，则抛出业务异常。
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String title = question.getTitle();
        String content = question.getContent();
        String tags = question.getTags();
        String answer = question.getAnswer();
        String judgeCase = question.getJudgeCase();
        String judgeConfig = question.getJudgeConfig();

        // 如果是添加操作，检查标题、内容和标签是否至少有一个为空，如果是，则抛出业务异常。
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags), ErrorCode.PARAMS_ERROR);
        }
        // 如果标题存在且长度超过80个字符，抛出业务异常。
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        // 如果内容存在且长度超过8192个字符，抛出业务异常。
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if (StringUtils.isNotBlank(answer) && answer.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "答案过长");
        }
        if (StringUtils.isNotBlank(judgeCase) && judgeCase.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题用例过长");
        }
        if (StringUtils.isNotBlank(judgeConfig) && judgeConfig.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题配置过长");
        }
    }


    /**
     * 获取查询包装类（用户根据哪些字段查询，根据前端传来的请求对象，得到mybatis框架支持的查询QueryWrapper类）
     *
     * @param questionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }
        Long id = questionQueryRequest.getId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        List<String> tags = questionQueryRequest.getTags();
        String answer = questionQueryRequest.getAnswer();
        Long userId = questionQueryRequest.getUserId();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();
        // 拼接查询条件
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.like(StringUtils.isNotBlank(answer), "content", answer);
        if (CollectionUtils.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 根据题目对象和HttpServletRequest，获取题目的视图对象（QuestionVO）。
     * 此方法通过将题目对象转换为视图对象，并附加与题目相关的用户信息和用户交互状态（如点赞、收藏）来实现。
     *
     * @param question 题目对象，包含题目的基本信息。
     * @param request HttpServletRequest对象，用于获取当前登录的用户信息。
     * @return 返回填充了用户信息和用户交互状态的题目视图对象。
     */
    @Override
    public QuestionVO getQuestionVO(Question question, HttpServletRequest request) {
        // 将题目对象转换为视图对象
        QuestionVO questionVO = QuestionVO.objToVo(question);
        // 1. 关联查询用户信息
        Long userId = question.getUserId();
        // 根据用户ID查询用户对象
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        // 将用户对象转换为视图对象
        UserVO userVO = userService.getUserVO(user);
        // 将发布者信息设置到题目视图对象中
        questionVO.setUserVO(userVO);
        // 返回填充完成的题目视图对象
        return questionVO;
    }

    /**
     * 根据题目分页对象和HTTP请求，获取题目的视图对象分页。
     * 此方法主要用于将数据库中查询到的题目对象转换为包含更多关联信息的题目视图对象。
     * 
     * @param questionPage 题目分页对象，包含当前页的题目列表。
     * @param request HTTP请求对象，可能用于未来的扩展，当前未使用。
     * @return 返回转换后的問題視圖對象分頁。
     */
    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
        // 获取题目列表
        List<Question> questionList = questionPage.getRecords();
        // 初始化题目视图对象分页，复用题目分页的页码、大小和总数信息
        Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        
        // 如果题目列表为空，直接返回空的视图对象分页
        if (CollectionUtils.isEmpty(questionList)) {
            return questionVOPage;
        }
        
        // 统计所有题目对应的用户ID，以便批量查询用户信息
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionList.stream().map(Question::getUserId).collect(Collectors.toSet());
        // 根据用户ID批量查询用户信息，并按ID分组，以便后续映射
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        // 将题目对象转换为题目视图对象，并填充用户信息
        // 填充信息
        List<QuestionVO> questionVOList = questionList.stream().map(question -> {
            QuestionVO questionVO = QuestionVO.objToVo(question);
            Long userId = question.getUserId();
            User user = null;
            // 如果用户ID对应的用户列表非空，则取第一个用户（假设一个ID只对应一个用户）
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            // 将用户对象转换为用户视图对象，并设置到题目视图对象中
            questionVO.setUserVO(userService.getUserVO(user));
            return questionVO;
        }).collect(Collectors.toList());
        
        // 设置转换后的视图对象列表到分页对象中
        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }


}




