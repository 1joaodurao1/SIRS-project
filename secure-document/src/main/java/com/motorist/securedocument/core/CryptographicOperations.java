package com.motorist.securedocument.core;

import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.motorist.securedocument.core.confidentiality.api.CipherMethod;
import com.motorist.securedocument.core.confidentiality.func.SymmetricCipherImpl;
import com.motorist.securedocument.core.integrity.api.IntegrityMethod;
import com.motorist.securedocument.core.integrity.func.DigitalSignatureImpl;


public class CryptographicOperations {
    
    private final static CipherMethod cipherMethod = new SymmetricCipherImpl();
    private final static IntegrityMethod integrityMethod = new DigitalSignatureImpl();

    public static JsonObject protect(
        final String inputFilename,
        final String senderUser ,
        final String receiverUser) throws Exception
    {
        JsonObject inputJson = getJsonObjectFromFile(inputFilename);
        System.out.println(inputJson);
        JsonObject protectedJson = addSecurity(inputJson, senderUser, receiverUser , 1);
        return protectedJson;
    }

    public static boolean check(
        final String inputFilename,
        final String senderUser,
        final String receiverUser) throws Exception
    {
        JsonObject inputJson = getJsonObjectFromFile(inputFilename);
        return doCheck(inputJson, senderUser, receiverUser, 1);
    }

    public static JsonObject unprotect(
        final String inputFilename,
        final String receiverUser) throws Exception
    {
        JsonObject inputJson = getJsonObjectFromFile(inputFilename);
        JsonObject decryptedJson = removeSecurity(inputJson, receiverUser, 1);
        return decryptedJson;
    }

    
    /*  Private methods    */

    private static JsonObject getJsonObjectFromFile(String filename) throws Exception {

        FileReader fileReader = new FileReader(filename);
        Gson  gson = new Gson();
        JsonObject jsonObject = gson.fromJson(fileReader, JsonObject.class);
        return jsonObject;
        
    }

    /* Public methods */

    public static JsonObject addSecurity(JsonObject inputJson, String senderUser, String receiverUser , Integer moduleId) throws Exception {

        JsonObject signedJson = integrityMethod.signature(inputJson, senderUser, moduleId);
        JsonObject encriptedInput = cipherMethod.encrypt(signedJson, receiverUser , moduleId);
        return encriptedInput;
    }

    public static JsonObject removeSecurity(JsonObject inputJson, String receiverUser , Integer moduleId) throws Exception {
        
        return cipherMethod.decrypt(inputJson, receiverUser , moduleId);
    }

    public static boolean doCheck ( JsonObject inputJson, String senderUser, String receiverUser , Integer moduleId) throws Exception {
        JsonObject decryptedJson = cipherMethod.decrypt(inputJson, receiverUser , moduleId);
        return integrityMethod.checkDigest(decryptedJson, senderUser, moduleId);
    }
}