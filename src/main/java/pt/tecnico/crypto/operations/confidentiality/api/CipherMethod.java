package pt.tecnico.crypto.operations.confidentiality.api;

public interface CipherMethod {
    
    String encrypt(
        String inputFilename,
        String secretKeyPath) throws Exception;

    String decrypt();
}
