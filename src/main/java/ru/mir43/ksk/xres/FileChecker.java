/*
 * Copyright(c) Obrien83 2018
 * All rights reserved
 */
package ru.mir43.ksk.xres;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * FileChecker - checks if necessary files and directories exists.
 */
class FileChecker {
    //Log added
    private static final Logger LOGGER = LogManager.getLogger(FileChecker.class.getName());

    static boolean check() throws IOException {
        // Log directory check, and creating if not exists.
        Path logDirectoryPath = Paths.get(System.getProperty("user.dir")+ "/log");
        if (!Files.exists(logDirectoryPath)){
            Files.createDirectory(logDirectoryPath);
            LOGGER.info("Created log directory");
        }
        // Scripts directory check
        Path scriptDirectoryPath = Paths.get(System.getProperty("user.dir")+ "/scripts");
        if (!Files.exists(scriptDirectoryPath)) {
            System.out.println("Directory \"scripts\" does not exist!");
            LOGGER.info("Directory \"scripts\" does not exist!");
            return false;
        }
        // Config.proprieties file check
        Path configFilePath = Paths.get(System.getProperty("user.dir") + "/config.properties");
        if (!Files.exists(configFilePath)) {
            System.out.println("File \"config.proprieties\" does not exist!");
            LOGGER.info("File \"config.proprieties\" does not exist!");
            return false;
        }
        return true;
    }
}
