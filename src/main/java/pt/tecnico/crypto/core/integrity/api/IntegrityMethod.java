package pt.tecnico.crypto.core.integrity.api;

public interface IntegrityMethod {
    
    String signature(
        String inputFilename,
        String privateKeyPath,
        String timestamp) throws Exception;

    boolean checkDigest(
        String digest,
        String publicKeyPath) throws Exception;
}
