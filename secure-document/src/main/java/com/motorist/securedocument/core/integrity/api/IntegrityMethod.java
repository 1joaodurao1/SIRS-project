package com.motorist.securedocument.core.integrity.api;

import com.google.gson.JsonObject;

public interface IntegrityMethod {
    
    JsonObject signature(
        JsonObject inputJson,
        String privateKeyPath, Integer moduleId) throws Exception;

    boolean checkDigest(
        JsonObject inputJson,
        String senderUser, Integer moduleId) throws Exception;
}
