package com.lzw.lzwcodesandbox.unsafe;

import com.lzw.lzwcodesandbox.utils.ProcessUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class RunFileError {
    public static void main(String[] args) throws IOException, InterruptedException {
        String userDir = System.getProperty("user.dir");
//        String filePath = userDir + File.separator + "src/main/resources/application.yml";
//        List<String> allLines = Files.readAllLines(Paths.get(filePath));
//        System.out.println(String.join("\n", allLines));

        String filePath = userDir + File.separator + "src/main/resources/木马程序.bat";
        String errorProgram = "java -version 2>&1";
        Files.write(Paths.get(filePath), Arrays.asList(errorProgram));
        System.out.println("写木马成功，你完了哈哈哈哈");

        Process process= Runtime.getRuntime().exec(filePath);
        process.waitFor();

        //
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String compileOutputLine;
        while ((compileOutputLine = bufferedReader.readLine()) != null) {
            System.out.println(compileOutputLine);
        }
        System.out.println("执行程序成功");

    }
}