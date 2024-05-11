package com.ise.unigpt.utils;

import java.util.Map;

public class StringTemplateParser {

    public static String interpolate(String input, Map<String, String> promptList) {
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
            String replacement = promptList.getOrDefault(placeholder, "");
            
            // 追加替换后的值
            result.append(replacement);
            
            // 更新上一个匹配结束的位置
            lastMatchEnd = matcher.end();
        }
        
        // 追加剩余的非匹配部分
        result.append(input.substring(lastMatchEnd));
        
        return result.toString();
    }
}