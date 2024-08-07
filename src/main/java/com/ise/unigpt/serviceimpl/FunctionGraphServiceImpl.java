package com.ise.unigpt.serviceimpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.stereotype.Service;

import com.huaweicloud.sdk.core.auth.BasicCredentials;
import com.huaweicloud.sdk.core.auth.ICredential;
import com.huaweicloud.sdk.core.exception.ConnectionException;
import com.huaweicloud.sdk.core.exception.RequestTimeoutException;
import com.huaweicloud.sdk.core.exception.ServiceResponseException;
import com.huaweicloud.sdk.functiongraph.v2.FunctionGraphClient;
import com.huaweicloud.sdk.functiongraph.v2.model.ImportFunctionRequest;
import com.huaweicloud.sdk.functiongraph.v2.model.ImportFunctionRequestBody;
import com.huaweicloud.sdk.functiongraph.v2.model.ImportFunctionResponse;
import com.huaweicloud.sdk.functiongraph.v2.region.FunctionGraphRegion;
import com.ise.unigpt.service.FunctionGraphService;

@Service
public class FunctionGraphServiceImpl implements FunctionGraphService {

    @Override
    public boolean uploadFunction() throws IOException {

        // zip 是根目录下test_latest.zip
        String zipFileName = "test_latest.zip";
        File zipFile = new File(zipFileName);

        // Read ZIP file content
        byte[] zipFileContent = Files.readAllBytes(zipFile.toPath());

        // Create request body
        ImportFunctionRequestBody body = new ImportFunctionRequestBody()
                .withFileCode(Base64.getEncoder().encodeToString(zipFileContent))
                .withFileType("zip")
                .withFileName(zipFileName)
                .withFuncName("test_02");

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
