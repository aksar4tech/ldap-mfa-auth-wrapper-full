package com.example.cli;

import picocli.CommandLine;
import java.util.Scanner;

@CommandLine.Command(
        name = "auth",
        subcommands = { LoginCommand.class, VerifyCommand.class },
        mixinStandardHelpOptions = true
)
public class AuthCli implements Runnable {

    public static void main(String[] args) {
        CommandLine cmd = new CommandLine(new AuthCli());

        // Start interactive shell
        Scanner scanner = new Scanner(System.in);
        System.out.println("Auth CLI started. Type 'help' or 'exit'.");

        while (true) {
            System.out.print("auth> ");
            String line = scanner.nextLine();

            if (line.equalsIgnoreCase("exit")) {
                System.out.println("Exiting Auth CLI.");
                break;
            }

            if (line.isBlank()) {
                continue;
            }

            String[] parsedArgs = line.split("\\s+");
            cmd.execute(parsedArgs);
        }
    }

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }
}
