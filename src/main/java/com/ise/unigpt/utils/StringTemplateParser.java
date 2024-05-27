package com.ise.unigpt.utils;

import java.util.Map;

public class StringTemplateParser {

    /**
     * @brief 替换字符串中的占位符
     * @param input       输入字符串
     * @param keyValuePairs  占位符和值的映射
     * @return
     */
    public static String interpolate(String input, Map<String, String> keyValuePairs) {
        // 定义正则表达式模式以匹配占位符++{}
        String regexPattern = "\\+\\+\\{(.*?)\\}";
        StringBuilder result = new StringBuilder();
        int lastMatchEnd = 0;
        
        // 使用正则表达式查找匹配项
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regexPattern);
        java.util.regex.Matcher matcher = pattern.matcher(input);
        
        while (matcher.find()) {
            // 在找到匹配项之前，追加前面的非匹配部分
            result.append(input, lastMatchEnd, matcher.start());
            
            // 获取占位符名称
            String placeholder = matcher.group(1);
            
            // 查找变量映射中的值
            String replacement = keyValuePairs.getOrDefault(placeholder, "");
            
            // 追加替换后的值
            result.append(replacement);
            
            // 更新上一个匹配结束的位置
            lastMatchEnd = matcher.end();
        }
        
        // 追加剩余的非匹配部分
        result.append(input.substring(lastMatchEnd));

        System.out.println("template: " + input);
        System.out.println("promptList: " + keyValuePairs.toString());
        System.out.println("result: " + result.toString());

        
        return result.toString();
    }
}