/*
 * Copyright(c) Obrien83 2018
 * All rights reserved
 */

package ru.mir43.ksk.xres;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;
import java.net.InetAddress;

import static org.jivesoftware.smack.ConnectionConfiguration.SecurityMode.disabled;

/**
 * SmackXmppConnector - public singleton class, which established connection with XMPP server and calls
 * an exemplar of SmackXmppChatter class.
 */
class SmackXmppConnector {
    //Log added
    private static final Logger LOGGER = LogManager.getLogger(SmackXmppConnector.class.getName());
    /**
     * The single exemplar of SmackXmppConnector class.
     */
    private static SmackXmppConnector instance;

    private SmackXmppConnector() {

    }

    /**
     * The single exemplar of SmackXmppConnector class getter.
     * @return the single exemplar of SmackXmppConnector class.
     */
    static synchronized SmackXmppConnector getConnectorInstance() {
        if (instance == null) {
            instance = new SmackXmppConnector();
            return instance;

        }else return instance;
    }

    /**
     * Establishes connection with XMPP server using static values from WorkingValues class.
     * Creates a singleton exemplar of SmackXmppChatter class.
     * @throws IOException - If values from config file are null.
     * @throws InterruptedException - If connection is interrupted.
     */
    void connect() throws IOException, InterruptedException {
        // Creating a connection to the jabber.org server on a specific port.
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setUsernameAndPassword(WorkingValues.getJabberUsername(), WorkingValues.getJabberPassword())
                .setXmppDomain(WorkingValues.getJabberDomain())
                .setResource(WorkingValues.getJabberResource())
                .setHostAddress(InetAddress.getByName(WorkingValues.getJabberDomain()))
                .setPort(Integer.parseInt(WorkingValues.getJabberPort()))
                .setDebuggerEnabled(false)
                .setConnectTimeout(1000)
                .setSecurityMode(disabled)
                .build();

        AbstractXMPPConnection connection = new XMPPTCPConnection(config);

        // Checking if XMPP server is reachable, if not, waiting.
        InetAddress address = InetAddress.getByName(WorkingValues.getJabberDomain());
        boolean reachable = address.isReachable(10000);

        System.out.println("Is host reachable?: " + reachable);

        // Waiting for XMPP server reachable.
        if (!reachable) {
            do {
                Thread.sleep(6000);
                System.out.println("Waiting for host reachable");
                reachable = address.isReachable(10000);
            } while (!reachable);
            System.out.println("Is host reachable?: " + true);
        }

        // Connecting to server.
        System.out.println("Connecting...");
        connection.setReplyTimeout(100000);
        boolean conn;
       do {
           try {
               connection.connect();
           } catch (Exception e) {
               System.out.println("Connection unreachable.");
           }
           conn = connection.isConnected();
           if (!conn) {
               System.out.println("Waiting for connection reachable.");
               Thread.sleep(300000);
           }
       } while (!conn);

        // Login on XMPP server.
        System.out.println("Is connected?: " + true + " to "
                + connection.getXMPPServiceDomain());
        try {
            connection.login();
        } catch (Exception e) {
            System.out.println("Error while login on XMPP server: " + e.getMessage());
            LOGGER.fatal("Error while login on XMPP server: ", e.fillInStackTrace());
            System.exit(1);
        }

        // Calling instance of SmackXmppChattable.
        SmackXmppChattable chatter;
        if (WorkingValues.isServer()) {
            chatter = SmackXmppServerChatter.getChatterInstance(connection);
        } else {
            chatter = SmackXmppClientChatter.getChatterInstance(connection);
        }
        chatter.beginChat();
    }

    /**
     * Returns a string representation of the SmackXmppConnector.
     * @return a string representation of the SmackXmppConnector.
     */
    @Override
    public String toString() {
        return "SmackXmppConnector";
    }
}
