package pt.tecnico.crypto.core.integrity.api;

public interface IntegrityMethod {
    
    String hash(
        String inputFilename,
        String privateKeyPath,
        String timestamp) throws Exception;
}
