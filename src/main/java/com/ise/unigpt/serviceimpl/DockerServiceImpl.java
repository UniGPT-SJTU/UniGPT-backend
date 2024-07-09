package com.ise.unigpt.serviceimpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.ise.unigpt.service.DockerService;

@Service
public class DockerServiceImpl implements DockerService {
    public String invokeFunction(String moduleName, String functionName, List<String> params) {
        try {

            // 获取当前工作目录
            String currentDir = new File("").getAbsolutePath();
            String moduleScriptPath = new File(currentDir, "src/main/resources/func/" + moduleName + ".py").getAbsolutePath();
            String runScriptPath = new File(currentDir, "src/main/resources/func/run.py").getAbsolutePath();
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("params", params);
            
            // 构建Docker命令
            String[] command = {
                "docker", "run", "--rm",
                "-v", moduleScriptPath + ":/app/" + moduleName + ".py",
                "-v", runScriptPath + ":/app/run.py",
                "mytest_py",
                "python3", "run.py",
                moduleName,
                functionName,
                jsonParams.toString()
            };

            // 执行命令
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(currentDir, "src/main/resources/func"));
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