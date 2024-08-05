package com.ise.unigpt.serviceimpl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huaweicloud.sdk.core.auth.BasicCredentials;
import com.huaweicloud.sdk.core.auth.ICredential;
import com.huaweicloud.sdk.core.exception.ConnectionException;
import com.huaweicloud.sdk.core.exception.RequestTimeoutException;
import com.huaweicloud.sdk.core.exception.ServiceResponseException;
import com.huaweicloud.sdk.functiongraph.v2.FunctionGraphClient;
import com.huaweicloud.sdk.functiongraph.v2.model.InvokeFunctionRequest;
import com.huaweicloud.sdk.functiongraph.v2.model.InvokeFunctionResponse;
import com.huaweicloud.sdk.functiongraph.v2.region.FunctionGraphRegion;
import com.ise.unigpt.service.InvokeFunctionSolutionService;

@Service
public class InvokeFunctionSolutionServiceImpl implements InvokeFunctionSolutionService {

    @Override
    public void CallFunction(String[] args) {
        // The AK and SK used for authentication are hard-coded or stored in plaintext, which has great security risks. It is recommended that the AK and SK be stored in ciphertext in configuration files or environment variables and decrypted during use to ensure security.
        // In this example, AK and SK are stored in environment variables for authentication. Before running this example, set environment variables CLOUD_SDK_AK and CLOUD_SDK_SK in the local environment
        String ak = System.getenv("HUAWEICLOUD_SDK_AK");
        String sk = System.getenv("HUAWEICLOUD_SDK_SK");

        // print the AK and SK
        System.out.println("ak: " + ak);
        System.out.println("sk: " + sk);

        ICredential auth = new BasicCredentials()
                .withAk(ak)
                .withSk(sk);

        FunctionGraphClient client = FunctionGraphClient.newBuilder()
                .withCredential(auth)
                .withRegion(FunctionGraphRegion.valueOf("cn-east-3"))
                .build();
        InvokeFunctionRequest request = new InvokeFunctionRequest();
        request.withFunctionUrn("urn:fss:cn-east-3:040dd9f934e74b52bcb92bb1ab4c9748:function:default:test:latest");
        Map<String, Object> listbodyInvokeFunctionRequestBody = new HashMap<>();
        listbodyInvokeFunctionRequestBody.put("name", "kiwi");
        request.withBody(listbodyInvokeFunctionRequestBody);
        try {
            InvokeFunctionResponse response = client.invokeFunction(request);
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
