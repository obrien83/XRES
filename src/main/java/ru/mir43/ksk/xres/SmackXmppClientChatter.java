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
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static org.jivesoftware.smack.packet.Presence.Type.available;

/**
 * SmackXmppClientChatter - implementation of interface SmackXmppChattable,
 * which start chat with server mode of program,
 * and sends "start" message to start the server mode finding out the inner IP address,
 * validates messages from server mode for IP address presence, after that sends "quit"
 * message to the server mode, and than, receiving "quit" message execute ClientPerformer.
 * @see SmackXmppChattable
 * @see SmackXmppServerChatter
 */
public class SmackXmppClientChatter implements SmackXmppChattable {
    //Log added
    private static final Logger LOGGER = LogManager.getLogger(SmackXmppClientChatter.class.getName());
    /**
     * Regexp pattern for IP address.
     */
    private static final String IP_ADDRESS_PATTERN
            = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    /**
     * The single exemplar of SmackXmppChatter class.
     */
    private static SmackXmppClientChatter instance = null;
    /**
     * An exemplar of Smack's XMPPConnection.
     */
    private final AbstractXMPPConnection connection;

    /**
     * @param connection an exemplar of Smack's XMPPConnection.
     */
    private SmackXmppClientChatter(AbstractXMPPConnection connection){
        this.connection = connection;
    }

    /**
     * The single exemplar of SmackXmppChatter class getter.
     * @param connection an exemplar of Smack's XMPPConnection.
     * @return hhe single exemplar of SmackXmppConnector class getter.
     */
    static synchronized SmackXmppClientChatter getChatterInstance(AbstractXMPPConnection connection) {
        if (instance == null) {
            instance = new SmackXmppClientChatter(connection);
            return instance;
        }else return instance;
    }

    /**
     * Calls chat() method. Realization of SmackXmppChattable beginChat().
     */
    @Override
    public void beginChat() {
        try {
            chat();
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            System.out.println("Error starting chat: " + e.getMessage());
            LOGGER.error("Error starting chat: " + e.fillInStackTrace());
        }
    }

    /**
     * Chutting in client mode.
     * @throws SmackException.NotConnectedException - If when chat begins, connection with
     * XMPP server is not established or interrupted.
     * @throws InterruptedException - If while sending message, connection is interrupted.
     */
    private void chat() throws SmackException.NotConnectedException, InterruptedException {
        boolean isJidOnline = false;
        // Create Chat Manager
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        // Incoming message listener.
        chatManager.addIncomingListener((from, message, chat) -> {
            System.out.println("New message from " + from + ": " + message.getBody());
            if (!WorkingValues.isPlain()) {
                if (validate(message.getBody())) try {
                    chat.send("run_server");
                    new ClientPerformer(message.getBody()).performOperations();
                } catch (SmackException.NotConnectedException | InterruptedException e) {
                    System.out.println("Error sending message \"run_server\": " + e.getMessage());
                    LOGGER.error("Error sending message \"run_server\": " + e.fillInStackTrace());
                }
            } else if (message.getBody().equals("ready")) try {
                chat.send("run_server");
            } catch (SmackException.NotConnectedException | InterruptedException e) {
                System.out.println("Error sending message \"run_server\": " + e.getMessage());
                LOGGER.error("Error sending message \"run_server\": " + e.fillInStackTrace());
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
        //Waiting message from jid
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> {
            try {
                chat.wait();
            } catch (InterruptedException e) {
                System.out.println("Waiting error, thread was interrupted: " + e.getMessage());
                LOGGER.error("Waiting error, thread was interrupted: " + e.fillInStackTrace());
            }
        }, 60, 60, TimeUnit.SECONDS);

        Roster roster = Roster.getInstanceFor(connection);
        if (!roster.isLoaded()) {
            try {
                roster.reloadAndWait();
            } catch (SmackException.NotLoggedInException e) {
                System.out.println("Roster error: " + e.getMessage());
                LOGGER.error("Roster error: " + e.fillInStackTrace());
            }
        }

        Presence presence = new Presence(jid, available);
        do {
            if (presence.isAvailable()) {
                isJidOnline = true;
            }
        } while (!isJidOnline);

        chat.send("start");
    }

    /**
     * Validates message from server mode.
     * @param messageBody message body from server.
     * @return true if message contains IP address, false if message not contains IP address.
     */
    private boolean validate(String messageBody) {
        Pattern pattern = Pattern.compile(IP_ADDRESS_PATTERN);
        return pattern.matcher(messageBody).matches();
    }

    /**
     * Returns a string representation of the SmackXmppClientChatter.
     * @return a string representation of the SmackXmppClientChatter.
     */
    @Override
    public String toString() {
        return "SmackXmppClientChatter{" +
                "connection=" + connection +
                '}';
    }
}
