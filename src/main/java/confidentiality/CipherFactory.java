package confidentiality;

import confidentiality.api.CipherMethod;
import confidentiality.func.AsymmetricCipher;
import confidentiality.func.HybridCipher;
import confidentiality.func.SymmetricCipher;

public class CipherFactory {
    public static CipherMethod getCipherMethod(String method){
        switch(method.toLowerCase()){
            case "symmetric":
                return new SymmetricCipher();
            case "asymmetric":
                return new AsymmetricCipher();
            case "hybrid":
                return new HybridCipher();
            default:
                throw new IllegalArgumentException("Invalid cipher method: " + method + "Possible cipher methods are: symmetric | asymmetric | hybrid");
        }
    }
    // I think this will not be needed since the protect and unprotect functions will receive the keys from input.
    // That is, the only thing we need to worry is encrypting and decrypting text with a certain key
    // If we want to use symmetric or asymmetric or hybrid, we must call out tools with the correct parameters :)
}
