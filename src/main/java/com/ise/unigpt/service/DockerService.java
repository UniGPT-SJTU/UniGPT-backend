package com.ise.unigpt.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;

public class DockerService {
    public static String invokeFunction(String functionName, List<Object> params) {
        try {
            // 将参数列表转换为JSON字符串
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("params", params);

            // 获取当前工作目录
            String currentDir = new File("").getAbsolutePath();
            String addScriptPath = new File(currentDir, "utils/function/add.py").getAbsolutePath();
            String runScriptPath = new File(currentDir, "utils/function/run.py").getAbsolutePath();

            // Docker安装路径（请根据实际情况调整）
            String dockerPath = "C:\\Program Files\\Docker\\Docker\\resources\\bin\\docker.exe";

            // 构建Docker命令
            String[] command = {
                dockerPath, "run", "--rm",
                "-v", addScriptPath + ":/app/add.py",
                "-v", runScriptPath + ":/app/run.py",
                "my-python-function-image",
                functionName,
                jsonParams.toString()
            };

            // 执行命令
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(currentDir, "utils/function"));
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String output = reader.lines().collect(Collectors.joining("\n"));

            // 等待进程完成
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String errorOutput = errorReader.lines().collect(Collectors.joining("\n"));
                return new JSONObject().put("error", "Docker execution failed").put("details", errorOutput).toString();
            }

            return output;
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject().put("error", "Exception occurred").put("details", e.getMessage()).toString();
        }
    }
}