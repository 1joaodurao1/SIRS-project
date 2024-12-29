package com.motorist.securedocument.core.confidentiality.api;

import com.google.gson.JsonObject;

public interface CipherMethod {
    
    JsonObject encrypt(
        JsonObject inputJson,
        String userType, Integer moduleId) throws Exception;

    JsonObject decrypt(
        JsonObject inputJson,
        String userType, Integer moduleId) throws Exception;
}
