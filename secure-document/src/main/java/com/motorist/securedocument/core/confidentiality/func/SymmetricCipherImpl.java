package com.motorist.securedocument.core.confidentiality.func;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.MalformedJsonException;
import com.motorist.securedocument.core.common.Common;
import com.motorist.securedocument.core.confidentiality.api.CipherMethod;

public class SymmetricCipherImpl implements CipherMethod {

    private static final String SYM_ALGO = "AES";

    private static final String SYM_CIPHER = "AES/CBC/PKCS5Padding";
    
    @Override
    public JsonObject encrypt (
        JsonObject inputJson,
        final String userType, Integer moduleId) throws Exception
    {

        // We only wnat to encrypt the content sub section of the inputJson
        JsonObject contentJson = inputJson.getAsJsonObject("content");

        // generate a symmetric key
        byte[] keyBytes = generateKey();
        SecretKey symmetricKey = new SecretKeySpec(keyBytes, SYM_ALGO);

        // Generate a random IV
        byte[] iv = new byte[16]; // AES block size is 16 bytes
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Create a Gson instance
        Gson gson = new Gson();

        // Serialize document to JSON string
        String jsonString = gson.toJson(contentJson);

        // Cipher the document
        Cipher cipher = Cipher.getInstance(SYM_CIPHER);
        cipher.init(Cipher.ENCRYPT_MODE, symmetricKey, ivParameterSpec);
        String encryptedData = Base64.getEncoder().encodeToString(cipher.doFinal(jsonString.getBytes()));

        inputJson.addProperty("content", encryptedData);

        JsonObject metatada = inputJson.getAsJsonObject("metadata");

        // Add the symmetric and IV key to the metadata
        metatada.addProperty("key", encryptAsym(keyBytes, userType, moduleId));
        metatada.addProperty("iv", encryptAsym(iv, userType,moduleId));


        return inputJson;
    }

    @Override
    public JsonObject decrypt(
        JsonObject inputJson,
        final String userType , Integer moduleId) throws Exception, MalformedJsonException
    {

        //encrypted content with the symmetric key
        String content ;
        if ( inputJson.get("content").isJsonPrimitive())
            content = inputJson.get("content").getAsString();
        else 
            return inputJson;
        byte[] contentBytes = Base64.getDecoder().decode(content);
        // Get the symmetric key and IV from the metadata
        JsonObject metadata = inputJson.getAsJsonObject("metadata");
        String encryptedKey = metadata.get("key").getAsString();
        String encryptedIv = metadata.get("iv").getAsString();

        // Decrypt the symmetric key and IV
        byte[] encryptedKeyBytes = Base64.getDecoder().decode(encryptedKey);
        byte[] encryptedIvBytes = Base64.getDecoder().decode(encryptedIv);

        String symKey = decryptAsym(encryptedKeyBytes, userType, moduleId);
        String iv = decryptAsym(encryptedIvBytes, userType, moduleId);

        // Decrypt the document
        Cipher cipher = Cipher.getInstance(SYM_CIPHER);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(Base64.getDecoder().decode(symKey), SYM_ALGO),
         new IvParameterSpec(Base64.getDecoder().decode(iv)));
        String decryptedData = new String(cipher.doFinal(contentBytes));

        inputJson.remove("content");
        JsonObject contentJson =JsonParser.parseString(decryptedData).getAsJsonObject();
        inputJson.add("content", contentJson);


        return inputJson;
    }

    // DB encryption

    public static String encryptDB ( JsonObject json , byte[] keyBytes , byte[] iv) throws Exception {

        // Create a Gson instance
        Gson gson = new Gson();

        // Serialize document to JSON string
        String jsonString = gson.toJson(json);

        // Cipher the document
        Cipher cipher = Cipher.getInstance(SYM_CIPHER);
        SecretKey symmetricKey = new SecretKeySpec(keyBytes, SYM_ALGO);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, symmetricKey, ivParameterSpec);
        String encryptedData = Base64.getEncoder().encodeToString(cipher.doFinal(jsonString.getBytes()));

        return encryptedData;
    }

    public static JsonObject decryptDB ( String encryptedData , byte[] keyBytes , byte[] iv) throws Exception {

        //encrypted content with the symmetric key
        byte[] contentBytes = Base64.getDecoder().decode(encryptedData);

        // Decrypt the document
        Cipher cipher = Cipher.getInstance(SYM_CIPHER);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, SYM_ALGO),
         new IvParameterSpec(iv));
        String decryptedData = new String(cipher.doFinal(contentBytes));

        JsonObject contentJson =JsonParser.parseString(decryptedData).getAsJsonObject();

        return contentJson;
    }


    // Private Methods

    private byte[] generateKey() throws Exception {
        
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(128, new SecureRandom());
        Key key = keyGen.generateKey();
		byte[] encoded = key.getEncoded();

        return encoded;
    }

    public static String encryptAsym(byte[] data , String userType , Integer moduleId) throws Exception {

        String key_path = Common.getModuleBasePath(moduleId) + "/resources/public/" + userType + ".pubkey";
        System.out.println(key_path);
       
        PublicKey pub = Common.getPublicKeyFromFile(key_path);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pub);
        byte[] encryptedData = null;
        encryptedData = cipher.doFinal(data);
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public static String decryptAsym(byte[] data , String userType, Integer moduleId) throws Exception {

        String key_path = Common.getModuleBasePath(moduleId) + "/resources/private/" + userType + ".key";
        
        PrivateKey privateKey = Common.getPrivateKeyFromFile(key_path);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decrypted_data = cipher.doFinal(data);
        return Base64.getEncoder().encodeToString(decrypted_data);
    }

    public static String encryptAsymFirmware(byte[] data , String userType , Integer moduleId) throws Exception {

        String key_path = Common.getModuleBasePath(moduleId) + "/resources/private/" + userType + ".key";
        System.out.println(key_path);
        
        PrivateKey privateKey = Common.getPrivateKeyFromFile(key_path);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] encryptedData = null;
        encryptedData = cipher.doFinal(data);
        return Base64.getEncoder().encodeToString(encryptedData);
    }

}
