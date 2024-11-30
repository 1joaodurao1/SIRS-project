package pt.tecnico.crypto.confidentiality;

import pt.tecnico.crypto.confidentiality.api.CipherMethod;
import pt.tecnico.crypto.confidentiality.func.AsymmetricCipher;
import pt.tecnico.crypto.confidentiality.func.HybridCipher;
import pt.tecnico.crypto.confidentiality.func.SymmetricCipher;

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
}
