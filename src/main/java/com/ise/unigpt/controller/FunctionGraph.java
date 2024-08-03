package com.ise.unigpt.controller;

import com.huaweicloud.sdk.core.auth.ICredential;
import com.huaweicloud.sdk.core.auth.BasicCredentials;
import com.huaweicloud.sdk.core.exception.ConnectionException;
import com.huaweicloud.sdk.core.exception.RequestTimeoutException;
import com.huaweicloud.sdk.core.exception.ServiceResponseException;
import com.huaweicloud.sdk.functiongraph.v2.region.FunctionGraphRegion;
import com.huaweicloud.sdk.functiongraph.v2.*;
import com.huaweicloud.sdk.functiongraph.v2.model.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService service;


    public static void main(String[] args) {
        // The AK and SK used for authentication are hard-coded or stored in plaintext, which has great security risks. It is recommended that the AK and SK be stored in ciphertext in configuration files or environment variables and decrypted during use to ensure security.
        // In this example, AK and SK are stored in environment variables for authentication. Before running this example, set environment variables CLOUD_SDK_AK and CLOUD_SDK_SK in the local environment
        String ak = System.getenv("HUAWEICLOUD_SDK_AK");
        String sk = System.getenv("HUAWEICLOUD_SDK_SK");

        ICredential auth = new BasicCredentials()
                .withAk(ak)
                .withSk(sk);

        FunctionGraphClient client = FunctionGraphClient.newBuilder()
                .withCredential(auth)
                .withRegion(FunctionGraphRegion.valueOf("functiongraph.cn-east-3.myhuaweicloud.com"))
                .build();
        ImportFunctionRequest request = new ImportFunctionRequest();
        ImportFunctionRequestBody body = new ImportFunctionRequestBody();
        body.withFileCode("aW1wb3J0IG1hdGhkZWYgaGFuZGxlcihudW1iZXIpOiAgICAjIENvbnZlcnQgdGhlIGlucHV0IHRvIGEgZmxvYXQgaWYgaXQncyBhIHN0cmluZyB0aGF0IHJlcHJlc2VudHMgYSBudW1iZXIgICAgaWYgaXNpbnN0YW5jZShudW1iZXIsIHN0cik6ICAgICAgICB0cnk6ICAgICAgICAgICAgbnVtYmVyID0gZmxvYXQobnVtYmVyKSAgICAgICAgZXhjZXB0IFZhbHVlRXJyb3I6ICAgICAgICAgICAgIyBIYW5kbGUgdGhlIGNhc2Ugd2hlcmUgdGhlIHN0cmluZyBkb2VzIG5vdCByZXByZXNlbnQgYSBudW1iZXIgICAgICAgICAgICByZXR1cm4gIkVycm9yOiBJbnB1dCBpcyBub3QgYSB2YWxpZCBudW1iZXIuIiAgICByZXR1cm4gbWF0aC5leHAobnVtYmVyKQ==");
        body.withFileType("py");
        body.withFileName("test.py");
        body.withFuncName("test");
        request.withBody(body);
        try {
            ImportFunctionResponse response = client.importFunction(request);
            System.out.println(response.toString());
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
    }
}
