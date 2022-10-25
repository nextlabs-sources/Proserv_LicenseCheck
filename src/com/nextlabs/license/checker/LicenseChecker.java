package com.nextlabs.license.checker;

import com.wald.license.checker.JarChecker;
import com.wald.license.common.LicenseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

/**
 * This class is used to validate the license.dat file. It checks the expiry
 * date property in the license.
 *
 * @author pkalra
 * @since 29-10-2015
 */
public class LicenseChecker {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String EXPIRATION = "expiration";

    /**
     * Run
     *
     * @param args a
     */
    public static void main(String[] args) {

        if (args.length <= 1) {
            System.err.println("Invalid arguments:");
            System.err.println("Please run as java -cp CLASSPATH -jar LicenseChecker.jar path_to_license.jar_file output_file");
            System.exit(1);
        }

        Path path = Paths.get(args[0]);

        if (!Files.exists(path)) {
            LOGGER.error(args[0] + " does not exist");
            System.err.println(args[0] + " does not exists");
            System.exit(1);
        }

        if (!path.getFileName().toString().equals("license.jar")) {
            LOGGER.error(args[0] + " is not a path to license.jar");
            System.err.println(args[0] + " is not a path to license.jar");
            System.exit(1);
        }
        LOGGER.info("------------------------------------------");
        LOGGER.info("------------------------------------------");
        LOGGER.debug("The absolute path to license.jar = " + args[0]);

        LOGGER.info("Trying to get license.properties...");

        JarChecker jarChecker = new JarChecker(args[0]);

        try {
            jarChecker.check();
        } catch (LicenseException e) {
            LOGGER.error("Invalid License " + e.getMessage(), e);
            System.exit(1);
        }

        Properties properties = JarChecker.getProperties();
        if (properties == null) {
            LOGGER.error("Could not get the license properties, found null!");
        }

        LOGGER.info("Got license properties successfully.");

        if (!isLicenseExpired(properties)) {
            writeToFile(properties, args[1]);
            System.exit(0);
        } else {
            System.exit(1);
        }

        System.exit(0);
    }

    private static void writeToFile(Properties properties, String fileName) {
        try {
            File f = new File(fileName);
            OutputStream out = new FileOutputStream(f);
            properties.store(out, "License Properties");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private static boolean isLicenseExpired(Properties properties) {

        if (properties.containsKey(EXPIRATION)) {
            String value = properties.getProperty(EXPIRATION);
            if (value.equals("-1")) {
                LOGGER.info("The license is a perpetual license!");
                return false;
            } else {
                Date expiryDate = JarChecker.license.getExpirationDate();
                if (expiryDate != null) {
                    if (expiryDate.after(Calendar.getInstance().getTime())) {
                        LOGGER.info("The license is valid! Expiry = " + expiryDate);
                        return false;
                    } else {
                        LOGGER.error("The license is expired! Expiry = " + expiryDate);
                        return true;
                    }
                } else {
                    LOGGER.error("The expiryDate found in the license is null");
                    return true;
                }
            }
        } else {
            LOGGER.error("The license does not contain the 'expiration' key");
            return true;
        }
    }
}
