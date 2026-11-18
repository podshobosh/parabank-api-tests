package com.podsho.parabank.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log {

    // Logs under the caller's class name. The index is tied to the call depth
    // (getStackTrace -> getLogger -> info/debug/... -> caller), so adding a
    // wrapper layer here would log under the wrong class.
    private static Logger getLogger() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String className = stackTrace[3].getClassName();
        return LogManager.getLogger(className);
    }

    public static void info(String message) {
        getLogger().info(message);
    }

    public static void debug(String message) {
        getLogger().debug(message);
    }

    public static void error(String message) {
        getLogger().error(message);
    }

    public static void error(String message, Throwable throwable) {
        getLogger().error(message, throwable);
    }

    public static void warn(String message) {
        getLogger().warn(message);
    }
}
