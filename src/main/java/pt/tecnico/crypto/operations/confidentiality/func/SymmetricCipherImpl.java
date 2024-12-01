package pt.tecnico.crypto.operations.confidentiality.func;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import pt.tecnico.crypto.operations.confidentiality.api.CipherMethod;

public class SymmetricCipherImpl implements CipherMethod {

    private final String SYM_ALGO = "AES";

    private final String SYM_CIPHER = "AES/ECB/PKCS5Padding"; // Later change to cbc
    
    @Override
    public String encrypt (
        final String inputFilename,
        final String secretKeyPath) throws Exception
    {
        byte[] keyBytes = Files.readAllBytes(Paths.get(secretKeyPath));
        SecretKey symmetricKey = new SecretKeySpec(keyBytes, SYM_ALGO);

        FileReader fileReader = new FileReader(inputFilename);
        Gson  gson = new Gson();
        JsonObject jsonObject = gson.fromJson(fileReader, JsonObject.class);
        System.out.println("JSON object: " + jsonObject);

        // Add timestamp for freshness - Later add nounce as well
        String timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date());
        jsonObject.addProperty("timestamp", timestamp);

        // Serialize document to JSON string
        String jsonString = gson.toJson(jsonObject);

        // Encrypt the document
        Cipher cipher = Cipher.getInstance(SYM_CIPHER);
        cipher.init(Cipher.ENCRYPT_MODE, symmetricKey);
        String encryptedData = Base64.getEncoder().encodeToString(cipher.doFinal(jsonString.getBytes()));

        return encryptedData;
    }

    @Override
    public String decrypt() { return null; }
}
