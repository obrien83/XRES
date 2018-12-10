/*
 * Copyright(c) Obrien83 2018
 * All rights reserved
 */


package ru.mir43.ksk.xres;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * WorkingValues public class with static fields, used by another classes.
 * Fields "JABBER_USERNAME", "JABBER_PASSWORD" , "JABBER_DOMAIN", "JABBER_RESOURCE", "JABBER_PORT", "JABBER_JID_NAME",
 * "OPVPN_CONFIG"
 * are got from file "config.proprieties".
 */
class WorkingValues {
    // Log added.
    private static final Logger LOGGER = LogManager.getLogger(WorkingValues.class.getName());
    // Fields from external config file.
    private static String JABBER_USERNAME;
    private static String JABBER_PASSWORD;
    private static String JABBER_DOMAIN;
    private static String JABBER_RESOURCE;
    private static String JABBER_PORT;
    private static String JABBER_JID_NAME;
    private static String OPVPN_CONFIG;
    private static String OPVPN_PORT;
    private static String OPVPN_EXEC;

    // Constant path to config file
    private static final String CONF_PATH
            = System.getProperty("user.dir") + "/config.properties" ;

    // Fields for ip address and check is server or client
    private static String _ip;
    private static boolean _isServer;
    private static boolean _isPlain;

    // Getters and setters.
    /**
     * Package-private getter for JABBER_USERNAME
     * @return JABBER_USERNAME
     */
    static String getJabberUsername() {
        return JABBER_USERNAME;
    }

    /**
     * Private setter for JABBER_USERNAME
     * @param jabberUsername jabber_username from config.proprieties.
     */
    private static void setJabberUsername(String jabberUsername) {
        WorkingValues.JABBER_USERNAME = jabberUsername;
    }

    /**
     * Package-private getter for JABBER_PASSWORD
     * @return JABBER_PASSWORD
     */
    static String getJabberPassword() {
        return JABBER_PASSWORD;
    }

    /**
     * Private setter for JABBER_PASSWORD
     * @param jabberPassword jabber_password from config.proprieties.
     */
    private static void setJabberPassword(String jabberPassword) {
        WorkingValues.JABBER_PASSWORD = jabberPassword;
    }

    /**
     * Package-private getter for JABBER_DOMAIN
     * @return Domain
     */
    static String getJabberDomain() {
        return JABBER_DOMAIN;
    }

    /**
     * Private setter for JABBER_DOMAIN
     * @param jabberDomain jabber_domain from config.proprieties.
     */
    private static void setJabberDomain(String jabberDomain) {
        WorkingValues.JABBER_DOMAIN = jabberDomain;
    }

    /**
     * Package-private getter for JABBER_RESOURCE
     * @return JABBER_RESOURCE
     */
    static String getJabberResource() {
        return JABBER_RESOURCE;
    }

    /**
     * Private setter for JABBER_RESOURCE
     * @param jabberResource jabber_resource from config.proprieties.
     */
    private static void setJabberResource(String jabberResource) {
        WorkingValues.JABBER_RESOURCE = jabberResource;
    }

    /**
     * Package-private getter for JABBER_PORT
     * @return JABBER_PORT
     */
    static String getJabberPort() {
        return JABBER_PORT;
    }
    /**
     * Private setter for JABBER_PORT
     * @param jabberPort jabber_port from config.proprieties.
     */
    private static void setJabberPort(String jabberPort) {
        WorkingValues.JABBER_PORT = jabberPort;
    }

    /**
     * Package-private getter for JABBER_JID_NAME
     * @return JABBER_JID_NAME
     */
    static String getJabberJidName() {
        return JABBER_JID_NAME;
    }

    /**
     * Private setter for JABBER_JID_NAME
     * @param jabberJidName jabber_jid_name from config.proprieties.
     */
    private static void setJabberJidName(String jabberJidName) {
        JABBER_JID_NAME = jabberJidName;
    }

    /**
     * Package-private getter for OPVPN_CONFIG
     * @return filename of openvpn config.
     */
    static String getOpvpnConfig() {
        return OPVPN_CONFIG;
    }

    /**
     * Private setter for OPVPN_CONFIG
     * @param opvpnConfig openvpn_config from config.proprieties.
     */
    private static void setOpvpnConfig(String opvpnConfig) {
        OPVPN_CONFIG = opvpnConfig;
    }

    /**
     * Package-private getter for OPVPN_PORT.
     * @return OPVPN_PORT
     */
    static String getOpvpnPort() {
        return OPVPN_PORT;
    }

    /**
     * Private setter for OPVPN_PORT.
     * @param opvpnPort openvpn_port from config.proprieties.
     */
    private static void setOpvpnPort(String opvpnPort) {
        OPVPN_PORT = opvpnPort;
    }

    /**
     * Package-private getter for OPVPN_EXEC.
     * @return OPVPN_PORT
     */
    static String getOpvpnExec() {
        return OPVPN_EXEC;
    }

    /**
     * Private setter for OPVPN_PORT.
     * @param opvpnExec openvpn_executable_string from config.proprieties.
     */
    private static void setOpvpnExec(String opvpnExec) {
        OPVPN_EXEC = opvpnExec;
    }


    /**
     * Getter fot ip address
     * @return ip address
     */
    static String getIp(){
        return _ip;
    }

    /**
     * Setter for ip address
     * @param ip IP address from IpListener
     * @see IpListener
     */
    static void setIp(String ip){
        _ip = ip;
    }

    /**
     * Getter for boolean value is server
     * @return isServer
     * @see Main
     */
    static boolean isServer() {
        return _isServer;
    }

    /**
     * Setter for boolean value isServer
     * @param isServer true if command line argument is "-s", false if no argument.
     * @see Main
     */
    static void setServer(boolean isServer) {
        _isServer = isServer;
    }

    /**
     * Getter for boolean value is plain.
     * @return isPlain
     * @see Main
     */
    static boolean isPlain() {
        return _isPlain;
    }

    /**
     * Setter for boolean value isPlain
     * @param isPlain true if command line argument is "-s", false if no argument.
     * @see Main
     */
    static void setPlain(boolean isPlain) {
        WorkingValues._isPlain = isPlain;
    }

    //Static initializer. Reads values from config file and set them to public values.
    static {
        File confFile = new File(CONF_PATH);
        try {
            FileReader reader = new FileReader(confFile);
            Properties properties = new Properties();
            properties.load(reader);

            setJabberUsername(properties.getProperty("jabber_username"));
            setJabberPassword(properties.getProperty("jabber_password"));
            setJabberDomain(properties.getProperty("jabber_domain"));
            setJabberResource(properties.getProperty("jabber_resource"));
            setJabberPort(properties.getProperty("jabber_port"));
            setJabberJidName(properties.getProperty("jabber_jid_name"));
            setOpvpnConfig(properties.getProperty("openvpn_config"));
            setOpvpnPort(properties.getProperty("openvpn_port"));
            setOpvpnExec(properties.getProperty("openvpn_executable_string"));

            setIp(null);
            setServer(false);
            setPlain(false);
        } catch (IOException e) {
            System.out.printf("Fatal error, cannot read values from file %s\n check if file is " +
                    "available and contains all values: ", CONF_PATH + e.getMessage());
            LOGGER.fatal("Fatal. Cannot read config file: ", e.fillInStackTrace());
        }
    }
}
