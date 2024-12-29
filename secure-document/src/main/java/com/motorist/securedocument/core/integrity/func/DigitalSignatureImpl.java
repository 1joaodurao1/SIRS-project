package com.motorist.securedocument.core.integrity.func;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
        String privateKeyFilename = getModuleBasePath(moduleId) + "/resources/private/" + senderUser + ".privkey";
        byte[] keyBytes = readFile(privateKeyFilename);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ASYM_ALGO);
        PrivateKey privateKey = keyFactory.generatePrivate(spec);

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
        String publicKeyFilename =  getModuleBasePath(moduleId) + "/resources/public/" + senderUser + ".pubkey";
        byte[] keyBytes = readFile(publicKeyFilename);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ASYM_ALGO);
        PublicKey publicKey = keyFactory.generatePublic(spec);

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
        
        String privateKeyFilename = getModuleBasePath(moduleId) + "/resources/private/" + senderUser + ".privkey";
        byte[] keyBytes = readFile(privateKeyFilename);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ASYM_ALGO);
        PrivateKey privateKey = keyFactory.generatePrivate(spec);

        Signature sig = Signature.getInstance(SIGNATURE_ALGO);
        sig.initSign(privateKey);
        sig.update(command.getBytes());
        String digest = Base64.getEncoder().encodeToString(sig.sign());

        return digest;
    }

    public static boolean checkGetRequest (String command, String senderUser, String signature, Integer moduleId) throws Exception {
        
        String publicKeyFilename = getModuleBasePath(moduleId) + "/resources/public/" + senderUser + ".pubkey";
        byte[] keyBytes = readFile(publicKeyFilename);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ASYM_ALGO);
        PublicKey publicKey = keyFactory.generatePublic(spec);

        Signature sig = Signature.getInstance(SIGNATURE_ALGO);
        sig.initVerify(publicKey);
        sig.update(command.getBytes());

        return sig.verify(Base64.getDecoder().decode(signature));
    }

    private static byte[] readFile(String path) throws FileNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(path);
		byte[] content = new byte[fis.available()];
		fis.read(content);
		fis.close();
		return content;
	}
    private static String getModuleBasePath(Integer moduleId) {
        // Determine the base path of the module
        // Adjust this method to correctly locate the base path of your module
        if ( moduleId == 1 ) {
            return System.getProperty("user.dir") + "/secure-document/src/java";
        } else if ( moduleId == 2 ) {
            return System.getProperty("user.dir") + "/application-server/src/main/java";
        }
        else {
            return System.getProperty("user.dir") + "/client/src/main/";
        }

    }

}
