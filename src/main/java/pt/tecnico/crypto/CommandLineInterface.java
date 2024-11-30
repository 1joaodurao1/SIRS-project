package pt.tecnico.crypto;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import pt.tecnico.crypto.confidentiality.CipherFactory;
import pt.tecnico.crypto.confidentiality.api.CipherMethod;

public class CommandLineInterface {
    
    public static void main(String[] args) {
        // Might add pom.xml later

        final Scanner scanner = new Scanner(System.in);

        System.out.println("Help message - Might be in a dev.application file because it might be big");

        while (true) { 
            
            List<String> userInput = Arrays.asList(scanner.nextLine().split(" "));

            final String operation = userInput.get(0);
            switch (operation) {
                case "help":
                    System.out.println("Help message - Might be in a dev.application file because it might be big");
                    break;
                
                case "protect":
                    try{
                        CipherMethod cipherMethod = CipherFactory.getCipherMethod(userInput.get(1)); // Don't know the position yet
                        cipherMethod.protect();
                    } catch(IllegalStateException e){
                        System.out.println(e.getMessage());
                    }
                    break;
    
                case "check":
                    System.out.print("Check message");
    
                case "unprotect":   
                    try{
                        CipherMethod cipherMethod = CipherFactory.getCipherMethod(userInput.get(1)); // Don't know the position yet
                        cipherMethod.unprotect();
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
