package pt.tecnico.crypto.operations.integrity.func;

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

import pt.tecnico.crypto.operations.integrity.api.IntegrityMethod;

public class DigitalSignatureImpl implements IntegrityMethod {

    /** Digital signature algorithm. */
	private static final String SIGNATURE_ALGO = "SHA256withRSA";

    /** Asymmetric cryptography algorithm. */
	private static final String ASYM_ALGO = "RSA";
    
    @Override
    public String hash(
        final String inputFilename,
        final String privateKeyFilename) throws Exception
    {
        FileReader fileReader = new FileReader(inputFilename);
        Gson  gson = new Gson();
        JsonObject jsonObject = gson.fromJson(fileReader, JsonObject.class);
        String jsonString = gson.toJson(jsonObject);

        byte[] keyBytes = Files.readAllBytes(Paths.get(privateKeyFilename));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ASYM_ALGO);
        PrivateKey privateKey = keyFactory.generatePrivate(spec);

        Signature sig = Signature.getInstance(SIGNATURE_ALGO);
        sig.initSign(privateKey);
        sig.update(jsonString.getBytes());
        String hashedData = Base64.getEncoder().encodeToString(sig.sign());

        return hashedData;
    }
}
