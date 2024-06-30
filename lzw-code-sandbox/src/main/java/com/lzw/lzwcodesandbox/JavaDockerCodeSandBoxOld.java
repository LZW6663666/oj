package com.lzw.lzwcodesandbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.lzw.lzwcodesandbox.model.ExecuteCodeRequest;
import com.lzw.lzwcodesandbox.model.ExecuteCodeResponse;
import com.lzw.lzwcodesandbox.model.ExecuteMessage;
import com.lzw.lzwcodesandbox.model.JudgeInfo;
import com.lzw.lzwcodesandbox.utils.ProcessUtils;
import org.springframework.util.StopWatch;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class JavaDockerCodeSandBoxOld implements CodeSandBox {

    private static final String GLOBAL_CODE_DIR_NAME = "tmpCode";
    private static final String GLOBAL_JAVA_FILE_NAME = "Main.java";
    private static final Boolean FIRST_INIT=true;
    private static final long TIME_OUT=5000L;

    ////测试
    public static void main(String[] args) {
        JavaDockerCodeSandBoxOld javaNativeCodeSandBox = new JavaDockerCodeSandBoxOld();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(Arrays.asList("1 2", "3 4"));
        String code = ResourceUtil.readStr("testCode/simpleComputeArgs/Main.java", StandardCharsets.UTF_8);
//        String code = ResourceUtil.readStr("testCode/simpleCompute/Main.java", StandardCharsets.UTF_8);
//        String code = ResourceUtil.readStr("testCode/unsafeCode/Main.java", StandardCharsets.UTF_8);
        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage("java");
        ExecuteCodeResponse executeCodeResponse = javaNativeCodeSandBox.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
    }


    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();
        List<String> inputList = executeCodeRequest.getInputList();

//1、把用户代码保存为文件
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

// 2、编译代码，得到class文件。
        String compileCmd =String.format("javac -encoding utf-8 %s",userCodeFile.getAbsolutePath());
        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(compileProcess, "编译");
            System.out.println(executeMessage);
        }catch (Exception e){
            return getResponse(e);
        }

 //3、创建容器，把文件复制到容器内
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();
        String image="openjdk:8-alpine";
        //拉取镜像
        if(FIRST_INIT){
            PullImageCmd pullImageCmd=dockerClient.pullImageCmd(image);
             PullImageResultCallback pullImageResultCallback=new PullImageResultCallback(){
            @Override
            public void onNext(PullResponseItem item){
                System.out.println("下载镜像："+item.getStatus());
                super.onNext(item);
            }
        };
            try {
                pullImageCmd.exec(pullImageResultCallback).awaitCompletion();
            } catch (InterruptedException e) {
                System.out.println("拉取镜像异常");
                throw new RuntimeException(e);
            }
        }
        //创建容器
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image);
        HostConfig hostConfig=new HostConfig();
        hostConfig.setBinds(new Bind(userCodeParentPath,new Volume("/app")));
        hostConfig.withMemory(100*1000*1000L);         //这段用于限制内存保证安全
        hostConfig.withMemorySwap(0L);
        hostConfig.withCpuCount(1L);
        hostConfig.withSecurityOpts(Arrays.asList("seccomp=安全管理配置字符串"));
        CreateContainerResponse containerResponse = containerCmd
                .withHostConfig(hostConfig)
                .withNetworkDisabled(true)     //这句用于禁止网络保证安全
                .withReadonlyRootfs(true)            //这句用于限制用户不能向根目录写文件
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withTty(true)//创建一个交互终端
//                .withCmd("echo","hello docker")   //和容器交互时，向它输入的命令
                .exec();
        System.out.println(containerResponse);
        String containerId = containerResponse.getId();

        //启动容器
        dockerClient.startContainerCmd(containerId).exec();

        //要执行的命令如：docker exec ContainerName java -cp /app Main 1 3
        //执行命令并获取结果
        List<ExecuteMessage> executeMessageList=new ArrayList<>();
        for(String inputArgs:inputList){
            StopWatch stopWatch=new StopWatch();
            String[] inputArgsArray=inputArgs.split(" ");
            String[] cmdArray= ArrayUtil.append(new String[]{"java","-cp","/app","Main"},inputArgsArray) ;
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                            .withCmd(cmdArray)
                            .withAttachStdin(true)
                            .withAttachStdout(true)
                            .withAttachStderr(true)
                            .exec();
            System.out.println("创建执行命令："+execCreateCmdResponse);

            ExecuteMessage executeMessage=new ExecuteMessage();

            final String[] message = {null};    //因为要在匿名内部类使用，所以必须是final,于是改用引用类型。
            final String[] errormessage = {null};
            long time=0L;
            final boolean[] timeOut = {true};//默认超时
            String execId = execCreateCmdResponse.getId();
            ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback(){

                @Override
                public void onComplete() {//程序正常执行后会调用这个函数
                    timeOut[0] =false;
                    super.onComplete();
                }

                @Override
                public void onNext(Frame frame) {
                    StreamType streamType = frame.getStreamType();
                    if(StreamType.STDERR.equals(streamType)){
                        errormessage[0] =new String(frame.getPayload()).toString();
                        System.out.println("输出错误结果："+ errormessage[0]);
                    }
                    else{
                        message[0] =new String(frame.getPayload());
                        System.out.println("输出结果："+ message[0]);
                    }

                    super.onNext(frame);
                }
            };
            final long[] maxMemory = {0L};
            //获取占用内存
            StatsCmd statsCmd=dockerClient.statsCmd(containerId);
            ResultCallback<Statistics> statisticsResultCallback = statsCmd.exec(new ResultCallback<Statistics>() {
                @Override
                public void onNext(Statistics statistics) {
                    System.out.println("内存占用：" + statistics.getMemoryStats().getUsage());
                    maxMemory[0] =Math.max( statistics.getMemoryStats().getUsage(), maxMemory[0]);
                }

                @Override
                public void onStart(Closeable closeable) {
                }

                @Override
                public void onError(Throwable throwable) {
                }

                @Override
                public void onComplete() {
                }

                @Override
                public void close() throws IOException {
                }
            });
            statsCmd.exec(statisticsResultCallback);
            try{
                stopWatch.start();
                dockerClient.execStartCmd(execId).exec(execStartResultCallback).awaitCompletion(TIME_OUT, TimeUnit.MICROSECONDS);
                stopWatch.stop();
                time=stopWatch.getLastTaskTimeMillis();
                statsCmd.close();
            }catch (InterruptedException e){
                System.out.println("程序执行异常");
                throw new RuntimeException(e);
            }
            executeMessage.setMessage(message[0]);
            executeMessage.setErrorMessage(errormessage[0]);
            executeMessage.setTime(time);
            executeMessage.setMemory(maxMemory[0]);
            executeMessageList.add(executeMessage);
        }

// 4、收集整理输出结果
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
//5、文件清理
        if(userCodeFile.getParentFile()!=null){
            boolean del = FileUtil.del(userCodeParentPath);
            System.out.println("删除"+(del?"成功":"失败"));
        }



        return executeCodeResponse;
    }

    /**
     * 获取错误响应,用于当程序抛出异常时，直接返回错误响应
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
