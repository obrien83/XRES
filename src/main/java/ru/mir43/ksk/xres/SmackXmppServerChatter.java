/*
 * Copyright(c) Obrien83 2018
 * All rights reserved
 */

package ru.mir43.ksk.xres;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * SmackXmppServerChatter - implementation of interface SmackXmppChattable,
 * which start chat with client mode of program,
 * and waits for "start" message to find out the inner IP address, send it
 * to the client mode and then, receiving "quit" message execute ServerPerformer.
 * @see SmackXmppChattable
 * @see SmackXmppClientChatter
 */
public class SmackXmppServerChatter implements SmackXmppChattable {
    //Log added
    private static final Logger LOGGER = LogManager.getLogger(SmackXmppServerChatter.class.getName());
    /**
     * True if message "start" from client mode is received by server mode.
     */
    private static boolean isStartReceived;
    /**
     * The single exemplar of SmackXmppChatter class.
     */
    private static SmackXmppServerChatter instance = null;
    /**
     * An exemplar of Smack's XMPPConnection.
     */
    private final AbstractXMPPConnection connection;
    /**
     * @param connection an exemplar of Smack's XMPPConnection.
     */
    private SmackXmppServerChatter(AbstractXMPPConnection connection){
        this.connection = connection;
    }

    /**
     * The single exemplar of SmackXmppChatter class getter.
     * @param connection an exemplar of Smack's XMPPConnection.
     * @return hhe single exemplar of SmackXmppConnector class getter.
     */
    static synchronized SmackXmppServerChatter getChatterInstance(AbstractXMPPConnection connection) {
        if (instance == null) {
            instance = new SmackXmppServerChatter(connection);
            return instance;
        }else return instance;
    }

    /**
     * Calls chat() method. Realization of SmackXmppChattable beginChat().
     */
    @Override
    public void beginChat() {
        chat();
    }
    /**
     * Chatting in server mode/
     */
    private void chat() {
        // Create Chat Manager
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        // Calling exemplar of IpListener to know inner IP address.
       if (!WorkingValues.isPlain()) {
           IpListener listener = new IpListener();
           try {
               listener.listenIp();
           } catch (IOException e) {
               System.out.println("Error trying find out IP address: ");
               LOGGER.error("Error trying find out IP address: ", e.fillInStackTrace());
           }
       }

        // Outgoing message listener.
        chatManager.addOutgoingListener((entityBareJid, message, chat) -> System.out.println("Sent message to " + entityBareJid + ": " + message.getBody()));

        // Incoming message listener.
        chatManager.addIncomingListener((from, message, chat) -> {
            System.out.println("New message from " + from + ": " + message.getBody());
            switch (message.getBody()) {
                case "start":
                    isStartReceived = true;
                    if (WorkingValues.isPlain()) {
                        try {
                            chat.send("ready");
                        } catch (SmackException.NotConnectedException | InterruptedException e) {
                            System.out.println("Error sending message \"ready\": " + e.getMessage());
                            LOGGER.error("Error sending message \"ready\": " + e.fillInStackTrace());
                        }
                    }
                    break;
                case "run_server":
                    try {
                        new ServerPerformer().performOperations();
                    } catch (IOException | InterruptedException e) {
                        System.out.println("Error trying start openvpn server: " + e.getMessage());
                        LOGGER.error("Error trying start openvpn server: ", e.fillInStackTrace());
                    }
                    break;
            }
        });

        // Starting chat with jid from config file.
        EntityBareJid jid = null;
        try {
            jid = JidCreate.entityBareFrom(WorkingValues.getJabberJidName());
        } catch (XmppStringprepException e) {
            System.out.println("Error: no jid found, check your config file. " + e.getMessage());
            LOGGER.error("Error: no jid found, check your config file. ", e.fillInStackTrace());
        }

        // Chat with chosen in external config JID.
        Chat chat = chatManager.chatWith(jid);
        System.out.println("Chat with " + chat.getXmppAddressOfChatPartner());

        // Sending ip address to the client every 60 seconds.
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> {
            while (!isStartReceived) {
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    System.out.println("Waiting error: " + e.getMessage());
                    LOGGER.error("Waiting error: " + e.fillInStackTrace());
                }
            }
            if (!WorkingValues.isPlain()) {
                String ip = WorkingValues.getIp();
                try {
                    chat.send(ip);
                } catch (SmackException.NotConnectedException | InterruptedException e) {
                    System.out.println("Error: cannot send message. " + e.getMessage());
                    LOGGER.error("Error: cannot send message. ", e.fillInStackTrace());
                }
            } else {
                try {
                    new ServerPerformer().performOperations();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 60, 60, TimeUnit.SECONDS);
    }

    /**
     * Returns a string representation of the SmackXmppServerChatter.
     * @return a string representation of the SmackXmppServerChatter.
     */
    @Override
    public String toString() {
        return "SmackXmppServerChatter{" +
                "connection=" + connection +
                '}';
    }
}
