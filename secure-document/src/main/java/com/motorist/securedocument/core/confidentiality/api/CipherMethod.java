package com.motorist.securedocument.core.confidentiality.api;

public interface CipherMethod {
    
    String encrypt(
        String inputFilename,
        String secretKeyPath,
        String timestamp) throws Exception;

    String decrypt(
        String inputFilename,
        String secretKeyPath) throws Exception;
}
