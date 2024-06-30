package com.lzw.lzwcodesandbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import com.lzw.lzwcodesandbox.model.ExecuteCodeRequest;
import com.lzw.lzwcodesandbox.model.ExecuteCodeResponse;
import com.lzw.lzwcodesandbox.model.ExecuteMessage;
import com.lzw.lzwcodesandbox.model.JudgeInfo;
import com.lzw.lzwcodesandbox.utils.ProcessUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
public abstract class JavaCodeSandboxTemplate implements CodeSandBox {
    private static final String GLOBAL_CODE_DIR_NAME = "tmpCode";
    private static final String GLOBAL_JAVA_FILE_NAME = "Main.java";
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();
        List<String> inputList = executeCodeRequest.getInputList();
//1、把用户代码报存为文件
        File userCodeFile = saveCodeToFile(code);
// 2、编译代码，得到class文件。
        ExecuteMessage compileFileExecuteMessage = compileFile(userCodeFile);
        System.out.println(compileFileExecuteMessage);
// 3、执行代码，获取输出
        List<ExecuteMessage> executeMessageList = runFile(userCodeFile, inputList);
// 4、收集整理输出结果
        ExecuteCodeResponse outPutResponse = getOutPutResponse(executeMessageList);
//5、文件清理
        boolean b = deleteFile(userCodeFile);
        if(!b){
            log.error("删除文件失败,userCodeFile={}",userCodeFile.getAbsolutePath());
        }
        return outPutResponse;
    }
    /**
     * 1、把用户代码报存为文件
     * @param code
     * @return
     */
    public File saveCodeToFile(String code) {
        String userDir = System.getProperty("user.dir");// 获取当前项目路径
        String globalCodePathName = userDir+ File.separator+GLOBAL_CODE_DIR_NAME;
        //判断全局代码目录是否存在,不存在则创建
        if(!FileUtil.exist(globalCodePathName)){
            FileUtil.mkdir(globalCodePathName);
        }
        //把用户代码隔离存放
        String userCodeParentPath = globalCodePathName+File.separator+ UUID.randomUUID();// /tmpCode/uuid
        String userCodePath = userCodeParentPath+File.separator+GLOBAL_JAVA_FILE_NAME;// /tmpCode/uuid/Main.java
        File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);
        return userCodeFile;
    }
    /**
     * 2、编译代码
     * @param userCodeFile
     * @return
     */
    public ExecuteMessage compileFile(File userCodeFile){
        String compileCmd =String.format("javac -encoding utf-8 %s",userCodeFile.getAbsolutePath());
        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(compileProcess, "编译");
//            System.out.println(executeMessage);
            if(executeMessage.getExitValue() != 0){
                throw new RuntimeException("编译错误");
            }
            return executeMessage;
        }catch (Exception e){
//            return getResponse(e);
            throw new RuntimeException(e);
        }
    }
    /**
     *3、 执行文件，获得执行结果列表
     * @param userCodeFile
     * @param inputList
     * @return
     */
    public List<ExecuteMessage> runFile(File userCodeFile, List<String> inputList){

        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();// /tmpCode/uuid
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        for (String inputArgs : inputList) {
            String runCmd = String.format("java -Dfile.encoding=UTF-8 -cp %s Main %s",userCodeParentPath,inputArgs);
            try {
                Process runProcess = Runtime.getRuntime().exec(runCmd);
                ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(runProcess, "运行");
                System.out.println(executeMessage);
                executeMessageList.add(executeMessage);
            }catch (Exception e){
//                return getResponse(e);
                throw new RuntimeException("执行错误",e);
            }
        }
        return executeMessageList;
    }
    /**
     * 4、获取输出结果
     * @param executeMessageList
     * @return
     */
    public ExecuteCodeResponse getOutPutResponse(List<ExecuteMessage> executeMessageList){
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        List<String> outputList = new ArrayList<>();
        //取用时最大值，用于判断是否超时
        Long maxTime = 0L;
        for (ExecuteMessage executeMessage : executeMessageList) {

            String errorMessage = executeMessage.getErrorMessage();
            if(StrUtil.isNotBlank(errorMessage)){
                executeCodeResponse.setMessage(errorMessage);
                //用户提交的代码执行中存在错误
                executeCodeResponse.setStatus(3);
                break;
            }
            Long time = executeMessage.getTime();
            if(time!=null){
                maxTime = Math.max(maxTime,time);
            }
            outputList.add(executeMessage.getMessage());
        }
        //正常运行完成（根据上面那个for循环，如果没有错误信息，outputList.size()== executeMessageList.size()会成立）
        if(outputList.size()== executeMessageList.size()){
            executeCodeResponse.setStatus(1);
        }
        executeCodeResponse.setOutputList(outputList);

        JudgeInfo judgeInfo = new JudgeInfo();
        //要借助第三方库来获取内存占用，非常麻烦，此处不做实践
        //judgeInfo.setMemory();
        judgeInfo.setTime(maxTime);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }
    /**
     * 5、删除文件
     * @param userCodeFile
     * @return
     */
    public boolean deleteFile(File userCodeFile){
        if(userCodeFile.getParentFile()!=null){
            String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();
            boolean del = FileUtil.del(userCodeParentPath);
            System.out.println("删除"+(del?"成功":"失败"));
            return del;
        }
        return true;
    }


    /**
     * 6、获取错误响应,用于当程序抛出异常时，直接返回错误响应
     * @param e
     * @return
     */
    private ExecuteCodeResponse getResponse(Throwable e){
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(new ArrayList<>());
        executeCodeResponse.setMessage(e.getMessage());
        //表示代码沙箱错误
        executeCodeResponse.setStatus(2);
        executeCodeResponse.setJudgeInfo(new JudgeInfo());
        return executeCodeResponse;
    }

}
