package pt.tecnico.crypto.operations;

import java.io.FileWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import pt.tecnico.crypto.operations.confidentiality.api.CipherMethod;
import pt.tecnico.crypto.operations.confidentiality.func.SymmetricCipherImpl;
import pt.tecnico.crypto.operations.integrity.api.IntegrityMethod;
import pt.tecnico.crypto.operations.integrity.func.DigitalSignatureImpl;

public class CryptographicOperations {   
    
    private final static CipherMethod cipherMethod = new SymmetricCipherImpl();
    private final static IntegrityMethod integrityMethod = new DigitalSignatureImpl();

    public static void protect(
        final String inputFilename,
        final String secretKeyPath,
        final String outputFilename,
        final String privateKeyPath) throws Exception
    {
        String encryptedData = cipherMethod.encrypt(inputFilename, secretKeyPath);
        String signature = integrityMethod.hash(inputFilename, privateKeyPath);

        // Create the final JSON structure
        JsonObject finalJson = new JsonObject();
        finalJson.addProperty("encryptedData", encryptedData);
        finalJson.addProperty("signature", signature);

        // Write the JSON into output file
        FileWriter fileWriter = new FileWriter(outputFilename);
        Gson gson = new GsonBuilder().create();
        gson.toJson(finalJson, fileWriter);
    }

    public static void check() {}

    public static void unprotect() {
        cipherMethod.decrypt();
    }
}