package com.motorist.securedocument.core.common;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Common {

    /** Asymmetric cryptography algorithm. */
	private static final String ASYM_ALGO = "RSA";

    public static PrivateKey getPrivateKeyFromFile(String filename) throws Exception {
        String key = new String(Files.readAllBytes(Paths.get(filename)));
        key = key.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", ""); // Remove all whitespace characters
        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ASYM_ALGO);
        
        return keyFactory.generatePrivate(spec);
    }

    public static PublicKey getPublicKeyFromFile(String filename) throws Exception {
        String key = new String(Files.readAllBytes(Paths.get(filename)));
        key = key.replace("-----BEGIN PUBLIC KEY-----", "")
                 .replace("-----END PUBLIC KEY-----", "")
                 .replaceAll("\\s", ""); // Remove all whitespace characters
        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ASYM_ALGO);
        return keyFactory.generatePublic(spec);
    }


    public static String getModuleBasePath(Integer moduleId) {
        // Determine the base path of the module
        // Adjust this method to correctly locate the base path of your module
        if ( moduleId == 1 ) {
            return System.getProperty("user.dir") + "/secure-document/src/main/java";
        } else if ( moduleId == 2 ) {
            System.out.println(System.getProperty("user.dir"));
            return System.getProperty("user.dir") + "/src/main/";
        }
        else {
            return System.getProperty("user.dir") + "/client/src/main/";
        }

    }
    
}
