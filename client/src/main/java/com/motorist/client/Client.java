package com.motorist.client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import com.motorist.client.commands.Command;
import com.motorist.client.communications.HTTPHandler;
import com.motorist.client.factory.CommandsFactory;

public class Client {

    public static void displayOptions() {
        System.out.println("There is 1 owner, 1 mechanic and 1 normal user created");
        System.out.println("The possible commands are as follows:");
        System.out.println("- Normal User commands:");
        System.out.println("    - user read_config");
        System.out.println("    - user change_config [config:value]");
        System.out.println("    - user view_logs");
        System.out.println("    - user set_maintenance on/off password");
        System.out.println("- Owner: ");
        System.out.println("    - owner read_config");
        System.out.println("    - owner change_config [config:value]");
        System.out.println("    - owner update_firmware ");
        System.out.println("    - owner view_logs");
        System.out.println("    - owner set_maintenance on/off password");
        System.out.println("- Mechanic: ");
        System.out.println("    - mechanic read_config no (reads as mechanic)");
        System.out.println("    - mechanic read_config yes (reads as owner)");
        System.out.println("    - mechanic change_config [config:value]");
        System.out.println("    - mechanic update_firmware ");
        System.out.println("    - mechanic view_logs");
        System.out.println("    - mechanic set_maintenance on/off password");
        System.out.println(" - Exit : Exists the program");
        System.out.println(" - Help : Displays the possible commands\n\n");
    }

    public static void readsInput() {

        try (Scanner scanner = new Scanner(System.in)) {
            String line = "";
            HTTPHandler httpHandler = new HTTPHandler("localhost:8443", "localhost:8081");
            CommandsFactory commandsFactory = new CommandsFactory();
            displayOptions();
            while (!line.equals("Exit")) {
                line = scanner.nextLine();
                String[] parts = line.split(" ");
                String role = parts[0];
                String commandName = parts.length > 1 ? parts[1] : "";

                switch (role.toLowerCase()) {
                    case "exit":
                        break;
                    case "help":
                        displayOptions();
                        break;
                    case "owner":
                    case "user":
                    case "mechanic":
                        try {
                            Command command = commandsFactory.getCommand(role, commandName, parts);
                            httpHandler.setRestTemplate(role);
                            command.handleCommand(httpHandler);
                        } catch (IllegalArgumentException e) {
                            System.out.println("Invalid command");
                            displayOptions();
                        }
                        break;
                    default:
                        System.out.println("Invalid command");
                        displayOptions();
                        break;
                }
            }
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
        
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        
        readsInput();
    }
}