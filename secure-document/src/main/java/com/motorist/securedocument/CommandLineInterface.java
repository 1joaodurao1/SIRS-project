package com.motorist.securedocument;

import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import static com.motorist.securedocument.core.CryptographicOperations.check;
import static com.motorist.securedocument.core.CryptographicOperations.protect;
import static com.motorist.securedocument.core.CryptographicOperations.unprotect;
import com.motorist.securedocument.validation.ValidationHandler;

public class CommandLineInterface {
    
    public static void main(String[] args) {

        final Scanner scanner = new Scanner(System.in);

        System.out.println("Recognized commands:\nhelp\nprotect\ncheck\nunprotect\nexit");

        while (true) { 
            
            List<String> userInput = Arrays.asList(scanner.nextLine().split(" "));
            final String operation = userInput.get(0);
            switch (operation) {
                case "help":
                    System.out.println("- protect (input-file) (output-file) (sender) (receiver)  \n- check (sender) (receiver) \n- unprotect (input-file) (output-file) (receiver)\n- exit");
                    break;
                
                case "protect":
                    //protect input_file output_file user/owner/mechanic user/owner/mechanic
                    try{
                        
                        ValidationHandler.validateCommand(userInput);
                        JsonObject result = protect(userInput.get(1), userInput.get(3) , userInput.get(4));
                        writeToFile(result, userInput.get(2));
                        System.out.println("File protected successfully");
                    } catch (Exception e) {
                        System.out.println("Error occured: " + e.getMessage());
                    }
                    break;

                case "check":
                    //check input_file user/owner/mechanic user/owner/mechanic
                    try{
                        ValidationHandler.validateCommand(userInput);
                        if ( check(userInput.get(1), userInput.get(2), userInput.get(3))) {
                            System.out.println("Integrity check passed");
                        } else {
                            System.out.println("Integrity check failed");
                        }
                    
                    }
                    catch (JsonSyntaxException e){
                        System.out.println("Integrity check failed due to corrupted encrypted content");
                    }
                    catch(Exception e){
                        System.out.println("Error occured: " + e.getMessage());
                    }
                    break;
    
                case "unprotect":
                    //unprotect input_file output_file user/owner/mechanic
                    try{
                        ValidationHandler.validateCommand(userInput);
                        JsonObject result = unprotect(userInput.get(1), userInput.get(3));
                        writeToFile(result, userInput.get(2));
                        System.out.println("File unprotected successfully");
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
                    System.out.println("Command not recognized. Recognized commands:\nhelp\nprotect\ncheck\nunprotect\nexit");
                    break;
            }
        }
    }

    private static void writeToFile(JsonObject toWrite, String outputFilename) throws Exception {
        try(FileWriter fileWriter = new FileWriter(outputFilename)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(toWrite, fileWriter);
        }
    }
}
