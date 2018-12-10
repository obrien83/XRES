/*
 * Copyright(c) Obrien83 2018
 * All rights reserved
 */

package ru.mir43.ksk.xres;

import java.io.IOException;

/**
 * ServerPerformer - stops program.
 * Executes when receive message from client.
 */
class ServerPerformer {

    //TODO: Think about what to do in server mode after receiving message from client

    /**
     * Starts performing operations on server side.
     * @throws IOException
     * @throws InterruptedException
     */
    public void performOperations() throws IOException, InterruptedException {
        startRemote();
    }

    /**
     * Stops program after received message "quit" from client mode.
     * Starts openvpn server.
     */
    private void startRemote() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new
                ProcessBuilder(WorkingValues.getOpvpnExec());
        processBuilder.inheritIO();
        processBuilder.start();
        synchronized (processBuilder) {
            processBuilder.wait();
        }
    }

    /**
     * Returns a string representation of the ServerPerformer.
     * @return a string representation of the ServerPerformer.
     */
    @Override
    public String toString() {
        return "ServerPerformer{}";
    }
}
