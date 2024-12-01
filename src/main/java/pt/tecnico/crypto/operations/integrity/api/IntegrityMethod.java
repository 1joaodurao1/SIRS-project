package pt.tecnico.crypto.operations.integrity.api;

public interface IntegrityMethod {
    
    String hash(
        String inputFilename,
        String privateKeyPath) throws Exception;
}
