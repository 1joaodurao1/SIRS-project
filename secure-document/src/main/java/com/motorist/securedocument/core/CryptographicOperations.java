package com.motorist.securedocument.core;

import java.io.FileReader;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

import javax.crypto.Cipher;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.MalformedJsonException;
import com.motorist.securedocument.core.common.Common;
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
        final String receiverUser) throws Exception, MalformedJsonException
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

    public static boolean doCheck ( JsonObject inputJson, String senderUser, String receiverUser , Integer moduleId) throws Exception, MalformedJsonException {
        
        JsonObject decryptedJson = cipherMethod.decrypt(inputJson, receiverUser , moduleId);
        return integrityMethod.checkDigest(decryptedJson, senderUser, moduleId);
        
    }

    public static JsonObject protectFirmware(byte[] firmware, JsonObject inputJson) throws Exception {
        
        String encryptedFirmware = SymmetricCipherImpl.encryptAsymFirmware(firmware,"manufacturer",4);
        inputJson.addProperty("firmware", encryptedFirmware);
        System.out.println("Firmware added to JSON " + encryptedFirmware);

        String privateKeyFilename = Common.getModuleBasePath(4) + "/resources/private/manufacturer.key";
        PrivateKey privateKey = Common.getPrivateKeyFromFile(privateKeyFilename);
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privateKey);
        sig.update(firmware);
        String digest = Base64.getEncoder().encodeToString(sig.sign());
        inputJson.addProperty("firmware_digest", digest);
        return inputJson;
    }

    public static String unprotectFirmware(JsonObject inputJson) throws Exception {
        // check the digest
        String digest = inputJson.get("firmware_digest").getAsString();
        String publicKeyFilename =  Common.getModuleBasePath(4) + "/resources/public/manufacturer.pubkey";
        PublicKey publicKey = Common.getPublicKeyFromFile(publicKeyFilename);
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(inputJson.get("firmware").getAsString()));
        String encryptedFirmware = Base64.getEncoder().encodeToString(decryptedData);
       
        sig.update(decryptedData);
        boolean isVerified = sig.verify(Base64.getDecoder().decode(digest));
        
        if (!isVerified) {
            return "";
        }
        
        return decryptedData.toString();
    }
}