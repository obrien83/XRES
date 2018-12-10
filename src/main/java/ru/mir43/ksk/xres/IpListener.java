/*
 * Copyright(c) Obrien83 2018
 * All rights reserved
 */

package ru.mir43.ksk.xres;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * IpListener - checks for inner IP address using http://checkip.amazonaws.com service.
 */
class IpListener {
    /**
     * Calls http://checkip.amazonaws.com for inner IP address.
     * @throws IOException If malformed url or stream error.
     */
    void listenIp() throws IOException {

        URL whatIsMyIp = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                whatIsMyIp.openStream()));
        // Get the IP as a String
        String thisIp = in.readLine();
        WorkingValues.setIp(thisIp);
    }

    /**
     * Returns a string representation of the IpListener.
     * @return a string representation of the IpListener.
     */
    @Override
    public String toString() {
        return "IpListener";
    }
}
