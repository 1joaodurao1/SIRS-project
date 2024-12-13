package com.motorist.securedocument.core.integrity.func;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
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
    public String signature(
        final String inputFilename,
        final String privateKeyFilename,
        final String timestamp) throws Exception
    {
        System.out.println("Hashing started...");

        FileReader fileReader = new FileReader(inputFilename);
        Gson  gson = new Gson();
        JsonObject jsonObject = gson.fromJson(fileReader, JsonObject.class);
        System.out.println("JSON object we are signing: " + jsonObject);

        // Add timestamp for freshness - Later add nounce as well
        jsonObject.addProperty("timestamp", timestamp);
        System.out.println("Added field timestamp with value: " + timestamp);

        // Serialize document to JSON string
        String jsonString = gson.toJson(jsonObject);

        byte[] keyBytes = Files.readAllBytes(Paths.get(privateKeyFilename));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ASYM_ALGO);
        PrivateKey privateKey = keyFactory.generatePrivate(spec);

        Signature sig = Signature.getInstance(SIGNATURE_ALGO);
        sig.initSign(privateKey);
        sig.update(jsonString.getBytes());
        String digest = Base64.getEncoder().encodeToString(sig.sign());

        System.out.println("Hashing finished !\nThis is the digest: " + digest);

        return digest;
    }

    @Override
    public boolean checkDigest(
        final String digest,
        final String publicKeyFilename) throws Exception
    {
        return false;
    }
}
