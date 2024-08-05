package com.ise.unigpt.service;

import java.io.IOException;

public interface FunctionGraphService {

    public boolean uploadFunction() throws IOException;

    public String CallFunction();
}
