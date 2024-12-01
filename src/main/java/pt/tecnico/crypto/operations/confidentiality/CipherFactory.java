package pt.tecnico.crypto.operations.confidentiality;

import pt.tecnico.crypto.operations.confidentiality.api.CipherMethod;
import pt.tecnico.crypto.operations.confidentiality.func.SymmetricCipherImpl;

public class CipherFactory {
    public static CipherMethod getCipherMethod(String method){
        switch(method.toLowerCase()){
            case "symmetric":
                return new SymmetricCipherImpl();
            default:
                throw new IllegalArgumentException("Invalid cipher method: " + method + "Possible cipher methods are: symmetric | asymmetric | hybrid");
        }
    }
}
