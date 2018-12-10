/*
 * Copyright(c) Obrien83 2018
 * All rights reserved
 */

package ru.mir43.ksk.xres;

/**
 * SmackXmppChattable - public interface for SmackXmppChatter classes,
 * which start chat with another exemplar of program,
 * and in case of mode (server or client) sends and receives messages
 * and calls instances of Executors.
 * @see SmackXmppServerChatter
 * @see SmackXmppClientChatter
 */
interface SmackXmppChattable {
    /**
     * Begins chat.
     */
    void beginChat();
}
