/*
 * Copyright(c) Obrien83 2018
 * All rights reserved
 */

package ru.mir43.ksk.xres;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * ClientPerformer - on receiving message with server IP address
 * changes address in openvpn config file.
 */
class ClientPerformer {
    // Log added.
    private static final Logger LOGGER = LogManager.getLogger(ClientPerformer.class.getName());
    /**
     *Regexp for IP address in openvpn config file.
     */
    private static final String IP_CONFIG_PATTERN = "^remote\\s.*$";
    /**
     * Openvpn config file.
     */
    private final File openVpnConfig;
    /**
     * IP address from chatter message.
     */
    private final String ip;

    /**
     * Constructor, taking ip address from chatter as argument.
     * @param ip IP address from chatter message.
     */
    public ClientPerformer(String ip) {
        this.ip = ip;
        openVpnConfig = new File(System.getProperty("user.dir")
                + "/" + WorkingValues.getOpvpnConfig());
    }

    /**
     * Calling changeIpConfig method and close program.
     */
    void performOperations() {
        if (!WorkingValues.isPlain()) {
            WorkingValues.setIp(ip);
            try {
                changeIpInOpvpnConfig();
            } catch (IOException e) {
                System.out.println("Error, failed to replace line: " + e.getMessage());
                LOGGER.error("Error, failed to replace line: ", e.fillInStackTrace());
                System.exit(1);
            }
            System.out.println("IP address in openvpn config file is changed. Exit.");
            System.exit(0);
        }

    }

    /**
     * Changing remote ip in openvpn config file to received from server mode.
     * @throws IOException - If failed to replace line in openvpn config file.
     */
    private void changeIpInOpvpnConfig() throws IOException {
        String replace = "remote " + WorkingValues.getIp() + " " + WorkingValues.getOpvpnPort();
        ArrayList<String> lineList = new ArrayList<>();

        if (openVpnConfig.exists()) {
            try (BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(openVpnConfig))
            )){
                String strLine;

                while ((strLine = bufferedReader.readLine()) != null) {
                    lineList.add(strLine);
                }
            } catch (IOException e) {
                System.out.println("Error, failed to replace line: " + e.getMessage());
                LOGGER.error("Error, failed to replace line: ", e.fillInStackTrace());
                System.exit(1);
            }
        } else {
            System.out.printf("File %s does not exists. Please put the file " +
                    "in %s directory\nExit\n", openVpnConfig, System.getProperty("user.dir"));
            System.exit(1);
        }

        for (String s: lineList) {
            if (s.matches(IP_CONFIG_PATTERN)) {
                System.out.println(s);
                replaceInFile(openVpnConfig.getAbsolutePath(), s, replace);
            }
        }
    }

    /**
     * Job for replacing strings in a file.
     * @param fileName - Name of a file.
     * @param search - String to be replaced.
     * @param replace - String to put into.
     * @throws IOException - If failed to replace line in file.
     */
    private void replaceInFile(String fileName, String search, String replace) throws IOException {
        Charset charset = StandardCharsets.UTF_8;
        Path path = Paths.get(fileName);
        Files.write(path, new String(Files.readAllBytes(path), charset).replace(search, replace).getBytes(charset));
    }

    /**
     * Returns a string representation of the ClientPerformer.
     * @return a string representation of the ClientPerformer.
     */
    @Override
    public String toString() {
        return "ClientPerformer{" +
                "openVpnConfig=" + openVpnConfig +
                ", ip='" + ip + '\'' +
                '}';
    }
}
