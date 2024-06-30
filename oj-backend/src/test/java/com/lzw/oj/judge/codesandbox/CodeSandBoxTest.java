package com.lzw.oj.judge.codesandbox;

import com.lzw.oj.judge.codesandbox.impl.ExampleCodeSandbox;
import com.lzw.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.lzw.oj.judge.codesandbox.model.ExecuteCodeResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CodeSandBoxTest {
    @Value("${codesandbox.type:example}")
    private String type;
    @Test
    void executeCode() {
        CodeSandBox codeSandBox = new ExampleCodeSandbox();
        String code = "public class Main {public static void main(String[] args) {System.out.println(\"Hello World\");}}";
        String language = "java";
        List<String> inputList = Arrays.asList("1 2", "2 4");
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);
        Assertions.assertNotNull(executeCodeResponse);
    }
    @Test
    void executeCodeByValue() {
        CodeSandBox codeSandBox = CodeSandboxFactory.newInstance(type);
       // System.out.println(type);
        String code = "public class Main {public static void main(String[] args) {System.out.println(\"Hello World\");}}";
        String language = "java";
        List<String> inputList = Arrays.asList("1 2", "2 4");
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);
        Assertions.assertNotNull(executeCodeResponse);
    }
    @Test
    void executeCodeByProxy() {
        CodeSandBox codeSandBox = CodeSandboxFactory.newInstance(type);
        codeSandBox = new CodeSandBoxProxy(codeSandBox);
        // System.out.println(type);
        String code = "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        int a=Integer.parseInt(args[0]);\n" +
                "        int b=Integer.parseInt(args[1]);\n" +
                "        System.out.println(\"结果:\"+(a+b));\n" +
                "        return;\n" +
                "    }\n" +
                "}";
        String language = "java";
        List<String> inputList = Arrays.asList("1 2", "2 4");
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);
        Assertions.assertNotNull(executeCodeResponse);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String type = scanner.next();
            CodeSandBox codeSandBox = CodeSandboxFactory.newInstance(type);
            String code = "public class Main {public static void main(String[] args) {System.out.println(\"Hello World\");}}";
            String language = "java";
            List<String> inputList = Arrays.asList("1 2", "2 4");
            ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                    .code(code)
                    .language(language)
                    .inputList(inputList)
                    .build();
            ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);

        }
    }
}