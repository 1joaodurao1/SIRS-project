package com.motorist.securedocument;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static com.motorist.securedocument.core.CryptographicOperations.*;

public class CommandLineInterface {
    
    public static void main(String[] args) {

        final Scanner scanner = new Scanner(System.in);

        System.out.println("Recognized commands:\nhelp\nprotect\ncheck\nunprotect\nexit");

        while (true) { 
            
            List<String> userInput = Arrays.asList(scanner.nextLine().split(" "));

            final String operation = userInput.get(0);
            switch (operation) {
                case "help":
                    System.out.println("- protect (input-file) (secret-key) (output-file) (private-key)\n- check\n- unprotect (input-file) (secret-key) (output-file)");
                    break;
                
                case "protect":
                    //protect input_file secret_key output_file private_key
                    //Validate input
                    try{
                        protect(userInput.get(1), userInput.get(2), userInput.get(3), userInput.get(4));
                    } catch (Exception e) {
                        System.out.println("Error occured: " + e.getMessage());
                    }
                    break;

                case "check":
                    //check input_file secret_key public_key
                    //Validate input
                    try{
                        check(userInput.get(1), userInput.get(2), userInput.get(3));
                    } catch (Exception e) {
                        System.out.println("Error occured: " + e.getMessage());
                    }
                    break;
    
                case "unprotect":
                    //unprotect input_file secret_key output_file
                    //Validate input
                    try{
                        unprotect(userInput.get(1), userInput.get(2), userInput.get(3));
                    } catch (Exception e) {
                        System.out.println("Error occured: " + e.getMessage());
                    }
                    break;

                case "exit":
                    System.out.println("Exiting...");
                    scanner.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Command not recognized.Recognized commands:\nhelp\nprotect\ncheck\nunprotect\nexit");
                    break;
            }
        }
    }
}
