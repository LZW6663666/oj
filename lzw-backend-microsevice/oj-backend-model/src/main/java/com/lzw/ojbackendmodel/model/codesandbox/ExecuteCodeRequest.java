package com.lzw.ojbackendmodel.model.codesandbox;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExecuteCodeRequest {
    private String code;
    private String language;
    private List<String> inputList;
    //这个接口中，timeLimit可加可不加，可后续扩展，用于及时中断程序。
}
