package com.ise.unigpt.service;

import java.util.List;

public interface  DockerService {
    public String invokeFunction(String moduleName, String functionName, List<String> params);
}