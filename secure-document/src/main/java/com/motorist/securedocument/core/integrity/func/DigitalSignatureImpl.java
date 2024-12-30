package com.motorist.securedocument.core.integrity.func;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.motorist.securedocument.core.common.Common;
import com.motorist.securedocument.core.integrity.api.IntegrityMethod;


public class DigitalSignatureImpl implements IntegrityMethod {

    /** Digital signature algorithm. */
	private static final String SIGNATURE_ALGO = "SHA256withRSA";

    /** Asymmetric cryptography algorithm. */
	private static final String ASYM_ALGO = "RSA";
    
    @Override
    public JsonObject signature(
        JsonObject inputJson,
        final String senderUser , Integer moduleId) throws Exception
    {

        JsonObject toHash = inputJson.getAsJsonObject("content");
        JsonObject metadata = inputJson.getAsJsonObject("metadata");

        Gson gson = new Gson();
        // Serialize document to JSON string
        String jsonString = gson.toJson(toHash);

        // Read private key from file
        String privateKeyFilename = Common.getModuleBasePath(moduleId) + "/resources/private/" + senderUser + ".key";
        //byte[] keyBytes = readFile(privateKeyFilename);
        //PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        //KeyFactory keyFactory = KeyFactory.getInstance(ASYM_ALGO);
        //PrivateKey privateKey = keyFactory.generatePrivate(spec);

        PrivateKey privateKey = Common.getPrivateKeyFromFile(privateKeyFilename);
        System.out.println("Here" + privateKey.toString());
        Signature sig = Signature.getInstance(SIGNATURE_ALGO);
        sig.initSign(privateKey);
        sig.update(jsonString.getBytes());
        String digest = Base64.getEncoder().encodeToString(sig.sign());

        metadata.addProperty("signature", digest);


        return inputJson;
    }

    @Override
    public boolean checkDigest(
        final JsonObject inputJson,
        final String senderUser ,Integer moduleId) throws Exception
    {

        JsonObject toCheck = inputJson.getAsJsonObject("content");
        JsonObject metadata = inputJson.getAsJsonObject("metadata");
        String signature = metadata.get("signature").getAsString();

        // Read public key from file
        String publicKeyFilename =  Common.getModuleBasePath(moduleId) + "/resources/public/" + senderUser + ".pubkey";
        //byte[] keyBytes = readFile(publicKeyFilename);
        //X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        //KeyFactory keyFactory = KeyFactory.getInstance(ASYM_ALGO);
        //PublicKey publicKey = keyFactory.generatePublic(spec);

        PublicKey publicKey = Common.getPublicKeyFromFile(publicKeyFilename);
        // Recompute the hash
        Gson gson = new Gson();
        String jsonString = gson.toJson(toCheck);

        Signature sig = Signature.getInstance(SIGNATURE_ALGO);
        sig.initVerify(publicKey);
        sig.update(jsonString.getBytes());

        // Verify the signature
        boolean isVerified = sig.verify(Base64.getDecoder().decode(signature));

        return isVerified;
    }

    public static String signGetRequest (String command, String senderUser , Integer moduleId) throws Exception {
        
        String privateKeyFilename = Common.getModuleBasePath(moduleId) + "/resources/private/" + senderUser + ".key";
        //byte[] keyBytes = readFile(privateKeyFilename);
        //PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        //KeyFactory keyFactory = KeyFactory.getInstance(ASYM_ALGO);
        //PrivateKey privateKey = keyFactory.generatePrivate(spec);
        PrivateKey privateKey = Common.getPrivateKeyFromFile(privateKeyFilename);
        Signature sig = Signature.getInstance(SIGNATURE_ALGO);
        sig.initSign(privateKey);
        sig.update(command.getBytes());
        String digest = Base64.getEncoder().encodeToString(sig.sign());

        return digest;
    }

    public static boolean checkGetRequest (String command, String senderUser, String signature, Integer moduleId) throws Exception {
        
        String publicKeyFilename = Common.getModuleBasePath(moduleId) + "/resources/public/" + senderUser + ".pubkey";
        System.out.println(publicKeyFilename);
        //byte[] keyBytes = readFile(publicKeyFilename);
        //X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        //KeyFactory keyFactory = KeyFactory.getInstance(ASYM_ALGO);
        //PublicKey publicKey = keyFactory.generatePublic(spec);
        PublicKey publicKey = Common.getPublicKeyFromFile(publicKeyFilename);

        Signature sig = Signature.getInstance(SIGNATURE_ALGO);
        sig.initVerify(publicKey);
        sig.update(command.getBytes());

        return sig.verify(Base64.getDecoder().decode(signature));
    }


}
