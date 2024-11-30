package pt.tecnico.crypto.protection;


import pt.tecnico.crypto.confidentiality.CipherFactory;
import pt.tecnico.crypto.confidentiality.api.CipherMethod;

public class Protection {   
    
    private final CipherMethod cipherMethod;

    public Protection(String method) {
        this.cipherMethod = CipherFactory.getCipherMethod(method);
    }

    public void protect() {
        this.cipherMethod.encrypt();
    }

    public void unprotect() {
        this.cipherMethod.decrypt();
    }
}

/*
 *  protect: private key = KM
             message = M

            AE(KM, Ku) = Ciphered key
            E(M, KM) = Ciphered message
    
    check: AD(C, Ku) = E(M, K) must be equal to hash

    unprotect: AD(Ciphered key, Kr) = KM
               D(M, KM) = M
               


 * document.json
 * Hybrid cipher
 * content: messagem encriptada com secret (mensagem original + timestamp + nounce + IV)
 * signature: DS -> private_key(hash( mensagem + timestamp + nounce))
 */

 /*
  *  input  -> protect input_file secret_key output_file
            -> check input_file public_key secret_key
            -> unprotect input_file secret_key public_key output_file


    * workflow: quando sender quer enviar mensagem:
                -> cria uma secret key
                -> cria um par de chaves publica e privada, onde a publica é enviada para o receiver (CA)
                -> decobre a public key do receiver (CA)
                -> partilha a secret key com o receiver (usando a public key do receiver), encriptando a secret key com a public key do receiver (TLS)
                -> encripta a mensagem com a secret key ( usando tb um IV,nounce e timestamp)
                -> assina mensagem com uma DS que usa a sua private key
            quando o reciever recebe uma mensagem:
                -> decifra a secret key com a sua private key
                -> decifra a mensagem com a secret key
                -> faz check:
                    Calcular o hash da mensagem
                    verifica a assinatura com a public key do sender , onde vai buscar a public key do sender ao CA e saca o hash da mensagem
                    compara os dois hashes e verifica nounces e timestamps
                -> se tudo estiver bem, guarda a mensagem no output file
  */