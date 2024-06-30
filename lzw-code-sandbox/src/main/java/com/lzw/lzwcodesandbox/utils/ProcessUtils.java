package com.lzw.lzwcodesandbox.utils;

import cn.hutool.core.util.StrUtil;
import com.lzw.lzwcodesandbox.model.ExecuteMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StopWatch;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 进程工具类
 */
public class ProcessUtils {
    /**
     * 运行进程并获取执行结果
     * @param runProcess
     * @param onName
     * @return
     */
    public static ExecuteMessage runProcessAndGetMessage(Process runProcess,String onName) {
        
        ExecuteMessage executeMessage = new ExecuteMessage();
        
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            //等待程序执行，获取退出值
            int exitValue = runProcess.waitFor();
            executeMessage.setExitValue(exitValue);
            if (exitValue == 0) {
                System.out.println(onName+"成功");
                //分批获取进程正常输出
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));

                List<String> outputStrList = new ArrayList<>();
                StringBuilder compileOutputStringBuilder = new StringBuilder();
                String compileOutputLine;
                while ((compileOutputLine = bufferedReader.readLine()) != null) {
                    outputStrList.add(compileOutputLine);
                    //compileOutputStringBuilder.append(compileOutputLine).append("\n");
                    // System.out.println(compileOutputLine);
                }

                executeMessage.setMessage( StringUtils.join(outputStrList, "\n"));
//                System.out.println(compileOutputStringBuilder);
            } else {
                //异常退出
                System.out.println(onName+"失败,错误码: " + exitValue);
                //分批获取进程正常输出
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
                //StringBuilder compileOutputStringBuilder = new StringBuilder();

                List<String> outputStrList = new ArrayList<>();
//                StringBuilder compileOutputStringBuilder = new StringBuilder();
                String compileOutputLine;
                while ((compileOutputLine = bufferedReader.readLine()) != null) {
                    outputStrList.add(compileOutputLine);
                    //compileOutputStringBuilder.append(compileOutputLine).append("\n");
                    // System.out.println(compileOutputLine);
                }

                executeMessage.setMessage( StringUtils.join(outputStrList, "\n"));


                //分批获取进程输出
                BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(runProcess.getErrorStream()));
                List<String> errorOutputStrList = new ArrayList<>();
                String errorCompileOutputLine;
                while ((errorCompileOutputLine = errorBufferedReader.readLine()) != null) {
                    errorOutputStrList.add(errorCompileOutputLine);
                }
                executeMessage.setErrorMessage(StringUtils.join(errorOutputStrList, "\n"));
            }
            stopWatch.stop();
            executeMessage.setTime(stopWatch.getTotalTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return executeMessage;

    }

    /**
     * 运行交互式进程并获取执行结果
     * @param runProcess
     * @param args
     * @return
     */
    public static ExecuteMessage runInteractProcessAndGetMessage(Process runProcess,String args) {

        ExecuteMessage executeMessage = new ExecuteMessage();

        try {
            //站在我的角度向运行进程写入数据，对我来说是输出所以用输出流
            OutputStream outputStream = runProcess.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            String[] s=args.split(" ");
            String join = StrUtil.join("\n", s)+"\n";
            outputStreamWriter.write(join);
//            outputStreamWriter.write("");
            outputStreamWriter.flush();

            InputStream inputStream = runProcess.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder compileOutputStringBuilder = new StringBuilder();
            String compileOutputLine;
            while ((compileOutputLine = bufferedReader.readLine()) != null) {
                compileOutputStringBuilder.append(compileOutputLine);
                // System.out.println(compileOutputLine);
            }
            executeMessage.setMessage(compileOutputStringBuilder.toString());
            outputStreamWriter.close();
            outputStream.close();
            inputStream.close();
            runProcess.destroy();

        }catch (Exception e){
            e.printStackTrace();
        }
        return executeMessage;

    }
}
