package pt.tecnico.crypto;
import static pt.tecnico.crypto.core.CryptographicOperations.protect;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import pt.tecnico.crypto.core.confidentiality.CipherFactory;
import pt.tecnico.crypto.core.confidentiality.api.CipherMethod;

public class CommandLineInterface {
    
    public static void main(String[] args) {

        final Scanner scanner = new Scanner(System.in);

        System.out.println("Recognized commands:\n- help\n- protect\n- check\n- unprotect\n- exit");

        while (true) { 
            
            List<String> userInput = Arrays.asList(scanner.nextLine().split(" "));

            final String operation = userInput.get(0);
            switch (operation) {
                case "help":
                    System.out.println("- protect (input-file) (secret-key) (output-file) (private-key)\n- check\n- unprotect");
                    break;
                
                case "protect":
                    //"protect input_file secret_key output_file private_key" 
                    //Validate input
                    try{
                        protect(userInput.get(1), userInput.get(2), userInput.get(3), userInput.get(4));
                    } catch (Exception e) {
                        System.out.println(e.getStackTrace());
                    }

                case "check":
                    System.out.println("Check message");
                    break;
    
                case "unprotect":   
                    try{
                        CipherMethod cipherMethod = CipherFactory.getCipherMethod(userInput.get(1));
                        cipherMethod.decrypt();
                    } catch(IllegalStateException e){
                        System.out.println(e.getMessage());
                    }
                    break;

                case "exit":
                    System.out.println("Exiting...");
                    scanner.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Command not recognized. Possible commands are: help | protect | check | unprotect"); //This is egrogious
                    break;
            }
            
        }
       
    }
}
