package com.motorist.client.factory;

import com.motorist.client.commands.ChangeCommand;
import com.motorist.client.commands.Command;
import com.motorist.client.commands.ReadCommand;
import com.motorist.client.commands.SetMaintenanceCommand;
import com.motorist.client.commands.UpdateCommand;
import com.motorist.client.commands.ViewLogsCommand;

public class CommandsFactory {
    
    public Command getCommand(String role , String command, String[] parts) throws IllegalArgumentException {
        Command commandObj = null;
        switch (command) {
            case "read_config":
                commandObj = new ReadCommand(role, parts );
                break;
            case "change_config":
                commandObj = new ChangeCommand(role, parts);
                break;
            case "update_firmware":
                commandObj = new UpdateCommand(role , null);
                break;
            case "view_logs":
                commandObj = new ViewLogsCommand(role, null);
                break;
            case "set_maintenance":
                commandObj = new SetMaintenanceCommand(role, parts);
                break;
            default:
                System.out.println("Invalid command");
                break;
        }
        return commandObj;
    }
}
