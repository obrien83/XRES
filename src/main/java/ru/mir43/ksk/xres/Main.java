/*
 * Copyright(c) Obrien83 2018
 * All rights reserved
 */

package ru.mir43.ksk.xres;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * XOCSer is a small program working in two modes:
 * server and client. In both modes the program sets connection with the XMPP server,
 * using the config.proprieties file. (The Smack library is used). The server mode,
 * using the http://checkip.amazonaws.com service, clarifies the external IP address of
 * a host and sends the message to the program working on other host in the client mode.
 * The client mode, receiving the message, checks it for contents. When receiving the IP address,
 * changes value of the line "remote" in a config of OpenVPN of the client, and sends
 * the response message about end of a task.
 *
 * Main class of the program. Runs the program, checks if server or client
 * using command line arguments.
 *
 * @version 0.1 Alpha
 * @author obrien83 obrien83@yandex.ru
 */
public class Main {
    //Log added
    private static final Logger LOGGER = LogManager.getLogger(Main.class.getName());

    /**
     * Entry point of the program.
     * @param args takes "-s"  or "--server" for server mode, nothing for client mode
     *             and "-h" or "--help" for help.
     */
    public static void main(String[] args) {

        // Checking commandline arguments (if "-s":server mode, if "-h": help,
        // if wrong key: error message , if nothing: client mode/
        commandArgsParse(args);

        // Checking necessary directories and files.
        checkFilesAndDirectories();

        // Greeting.
        greet();

        // Calling smackXmppConnector instance
        getConnector();

    }

    /**
     * Static method for handling command line arguments
     * Checking commandline arguments (if "-s":server mode, if "-h": help,
     * if wrong key: error message , if nothing: client mode
     * @param args takes comman line arguments for handling
     */
    private static void commandArgsParse(String[] args) {
        if (args.length != 0) {
            switch (args[0]) {
                case "-s":
                case "--server":
                    WorkingValues.setServer(true);
                    if (args.length == 2) {
                        switch (args[1]) {
                            case "-p":
                            case "--plain":
                                WorkingValues.setPlain(true);
                                break;
                            default:
                                System.out.println("Unknown argument: " + args[0] + args[1] + "\nProgramm is closed\n" +
                                        "Start the program with \"-h\" key");
                                System.exit(1);
                                break;
                        }
                    }
                    break;
                case "-p":
                case "--plain":
                    WorkingValues.setPlain(true);
                case "-h":
                case"--help":
                    System.out.println("Using program: \"-s\" key is " +
                            "started in server mode, \"-h\" key shows " +
                            "this help. Without any key is starting in " +
                            "client mode.");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Unknown argument: " + args[0] + "\nProgramm is closed\n" +
                            "Start the program with \"-h\" key");
                    System.exit(1);
                    break;
            }
        } else {
            WorkingValues.setServer(false);
        }
    }

    /**
     * Checking necessary directories and files.
     */
    private static void checkFilesAndDirectories() {
        try {
            if (!FileChecker.check()) {
                System.out.println("Exit.");
                System.exit(1);
            }
        } catch (IOException e) {
            System.out.println("Impossible to check files and directories: " + e.getMessage());
            LOGGER.fatal("Impossible to check files and directories: ", e.fillInStackTrace());
            System.exit(1);
        }
    }

    /**
     *  Greeting. Shows server or client mode of program.
     */
    private static void greet() {
        System.out.println("Welcome to XOCSer ! Plain openvpn config changer.\n" +
                "Copyright©2018 Sergey K. Konyshev" +
                "Author Konyshev S.K. ksk43@mir.ru\n" +
                "all rights reserved.\n" + new String(new char[50]).replace('\0', '*'));
        // Shows mode(server or client).
        System.out.println("Starting session...");
        if (WorkingValues.isServer()){
            if (WorkingValues.isPlain())  System.out.println(" Plain sever mode.\n"
                    + new String(new char[50]).replace('\0', '*'));
            else System.out.println("Sever mode.\n"
                    + new String(new char[50]).replace('\0', '*'));
        }else {
            if (WorkingValues.isPlain()) System.out.println("Plain client mode.\n"
                    + new String(new char[50]).replace('\0', '*'));
            else System.out.println("Client mode.\n"
                    + new String(new char[50]).replace('\0', '*'));
        }
    }

    /**
     * Calling smackXmppConnector instance and connect to XMPP server/
     */
    private static void getConnector() {
        // Calling smackXmppConnector instance
        SmackXmppConnector smackXmppConnector = SmackXmppConnector.getConnectorInstance();
        // Trying to connect to XMPP server.
        try {
            smackXmppConnector.connect();
        } catch (InterruptedException | IOException e) {
            System.out.println("Connection to XMPP server error: " + e.getMessage());
            LOGGER.fatal("Connection to XMPP server error: ", e.fillInStackTrace());
        }
    }

    /**
     * Returns a string representation of the Main. (If it will be a library some time).
     * @return a string representation of the Main.
     */
    @Override
    public String toString() {
        return "Main class of xres.";
    }
}
