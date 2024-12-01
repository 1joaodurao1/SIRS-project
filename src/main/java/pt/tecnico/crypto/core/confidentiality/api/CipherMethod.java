package pt.tecnico.crypto.core.confidentiality.api;

public interface CipherMethod {
    
    String encrypt(
        String inputFilename,
        String secretKeyPath,
        String timestamp) throws Exception;

    String decrypt();
}
