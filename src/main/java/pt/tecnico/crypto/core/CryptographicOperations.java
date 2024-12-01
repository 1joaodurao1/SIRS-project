package pt.tecnico.crypto.core;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import pt.tecnico.crypto.core.confidentiality.api.CipherMethod;
import pt.tecnico.crypto.core.confidentiality.func.SymmetricCipherImpl;
import pt.tecnico.crypto.core.integrity.api.IntegrityMethod;
import pt.tecnico.crypto.core.integrity.func.DigitalSignatureImpl;

public class CryptographicOperations {   
    
    private final static CipherMethod cipherMethod = new SymmetricCipherImpl();
    private final static IntegrityMethod integrityMethod = new DigitalSignatureImpl();

    public static void protect(
        final String inputFilename,
        final String secretKeyPath,
        final String outputFilename,
        final String privateKeyPath) throws Exception
    {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date());
        String encryptedData = cipherMethod.encrypt(inputFilename, secretKeyPath, timestamp);
        String signature = integrityMethod.signature(inputFilename, privateKeyPath, timestamp);

        // Create the final JSON structure
        JsonObject finalJson = new JsonObject();
        finalJson.addProperty("encrypted_data", encryptedData);
        finalJson.addProperty("signature", signature);

        writeToFile(finalJson, outputFilename);
    }

    public static void check(
        final String inputFilename,
        final String secretKeyPath,
        final String privateKeyPath) throws Exception
    {
        
    }

    public static void unprotect(
        final String inputFilename,
        final String secretKeyPath,
        final String outputFilename) throws Exception
    {
        String decryptedData = cipherMethod.decrypt(inputFilename, secretKeyPath);

        JsonObject finalJson = JsonParser.parseString(decryptedData).getAsJsonObject();
        
        writeToFile(finalJson, outputFilename);
    }

    /*  Private methods    */
    private static void writeToFile(JsonObject toWrite, String outputFilename) throws Exception {
        try(FileWriter fileWriter = new FileWriter(outputFilename)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(toWrite, fileWriter);
        }
    }
}