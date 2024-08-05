package com.ise.unigpt.serviceimpl;

import com.huaweicloud.sdk.core.auth.ICredential;
import org.springframework.stereotype.Service;
import com.huaweicloud.sdk.core.auth.BasicCredentials;
import com.huaweicloud.sdk.core.exception.ConnectionException;
import com.huaweicloud.sdk.core.exception.RequestTimeoutException;
import com.huaweicloud.sdk.core.exception.ServiceResponseException;
import com.huaweicloud.sdk.functiongraph.v2.region.FunctionGraphRegion;
import com.huaweicloud.sdk.functiongraph.v2.*;
import com.huaweicloud.sdk.functiongraph.v2.model.*;
import com.ise.unigpt.service.FunctionGraphService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class FunctionGraphServiceImpl implements FunctionGraphService {

    @Override
    public boolean uploadFunction() throws IOException {
        String fileName = "test.py";
        String fileContent = "import math\ndef handler(number):\n    if isinstance(number, str):\n        try:\n            number = float(number)\n        except ValueError:\n            return \"Error: Input is not a valid number.\"\n    return math.exp(number)";
        String yamlFileName = "function.yaml";
        String yamlContent = """
        edition: 1.0.0
        name: fg-test
        access: "default"
        vars:
            region: "cn-east-3"
            functionName: "start-fg-event-python39"
        services:
            component-test:
                component: fgs
                props:
                    region: ${vars.region}
                    function:
                        functionName: ${vars.functionName}
                        handler: test.handler
                        memorySize: 256
                        timeout: 30
                        runtime: Python3.9
                        agencyName: fgs-vpc-test
                        environmentVariables:
                            test: test
                            hello: world
                        vpcId: xxx-xxx
                        subnetId: xxx-xxx
                        concurrency: 10
                        concurrentNum: 10
                        codeType: zip
                        dependVersionList:
                            - xxx-xxx
                        code:
                            codeUri: ./code
                    trigger:
                        triggerTypeCode: APIG
                        status: DISABLED
                        eventData:
                            name: APIG_test
                            groupName: APIGroup_xxx
                            auth: IAM
                            protocol: HTTP
                            timeout: 5000
        """;
        String zipFileName = "function.zip";

        // Create temporary directory
        File tempDir = Files.createTempDirectory("function").toFile();

        // Write Python file
        File pythonFile = new File(tempDir, fileName);
        try (FileWriter writer = new FileWriter(pythonFile)) {
            writer.write(fileContent);
        }

        // Write YAML file
        File yamlFile = new File(tempDir, yamlFileName);
        try (FileWriter writer = new FileWriter(yamlFile)) {
            writer.write(yamlContent);
        }

        // Create ZIP file
        File zipFile = new File(tempDir, zipFileName);
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile.toPath()))) {
            addToZipFile(pythonFile, zos);
            addToZipFile(yamlFile, zos);
        }

        // Read ZIP file content
        byte[] zipFileContent = Files.readAllBytes(zipFile.toPath());

        // Create request body
        ImportFunctionRequestBody body = new ImportFunctionRequestBody()
                .withFileCode(Base64.getEncoder().encodeToString(zipFileContent))
                .withFileType("zip")
                .withFileName(zipFileName)
                .withFuncName("test");

        String ak = System.getenv("HUAWEICLOUD_SDK_AK");
        String sk = System.getenv("HUAWEICLOUD_SDK_SK");

        ICredential auth = new BasicCredentials()
                .withAk(ak)
                .withSk(sk);

        FunctionGraphClient client = FunctionGraphClient.newBuilder()
                .withCredential(auth)
                .withRegion(FunctionGraphRegion.valueOf("cn-east-3"))
                .build();
        ImportFunctionRequest request = new ImportFunctionRequest();

        request.withBody(body);
        try {
            ImportFunctionResponse response = client.importFunction(request);
            System.out.println(response.toString());
            return true;
        } catch (ConnectionException e) {
            e.printStackTrace();
        } catch (RequestTimeoutException e) {
            e.printStackTrace();
        } catch (ServiceResponseException e) {
            e.printStackTrace();
            System.out.println(e.getHttpStatusCode());
            System.out.println(e.getRequestId());
            System.out.println(e.getErrorCode());
            System.out.println(e.getErrorMsg());
        }
        return false;
    }

    private void addToZipFile(File file, ZipOutputStream zos) throws IOException {
        try (var fis = Files.newInputStream(file.toPath())) {
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zos.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }
            zos.closeEntry();
        }
    }

    public String CallFunction() {
        return "temp";
    }
}
