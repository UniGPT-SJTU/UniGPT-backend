package com.ise.unigpt.serviceimpl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.ise.unigpt.service.FunGraphService;

@Service
public class FunGraphServiceImpl implements FunGraphService {

    public String invokeFunction(String username, String moduleName, String functionName, List<String> params, String urn) {
        try {
            // 构建命令
            String paramsJson = params.toString().replace("[", "[\"").replace("]", "\"]").replace(", ", "\", \"");
            String body = String.format("{\"module_name\": \"%s\", \"function_name\": \"%s\", \"params\": %s}", moduleName, functionName, paramsJson);
            String command = String.format("hcloud FunctionGraph InvokeFunction --cli-region=\"cn-east-3\" --Content-Type=\"application/json\" --function_urn=\"%s\" --project_id=\"040dd9f934e74b52bcb92bb1ab4c9748\" --body='%s'", urn, body);

            // 使用 ProcessBuilder 执行命令
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // 捕获命令输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // 等待进程结束
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                // 解析 JSON 输出并提取 body 的值
                JSONObject jsonResponse = new JSONObject(output.toString());
                return jsonResponse.getString("body");
            } else {
                return "Error: Command exited with code " + exitCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
