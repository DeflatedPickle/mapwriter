package mapwriter.util;

import mapwriter.forge.MwForge;

/**
 * THIS CLASS IS DEPRECATED BECAUSE IT IS POINTLESS OVERHEAD. PLEASE JUST USE THE LOGGER
 * DIRECTLY. USE {} TO REFERENCE AN ARGUMENT INSTEAD OF %_
 */
@Deprecated
public class Logging {

    @Deprecated
    public static void debug (String s, Object... args) {

        MwForge.logger.debug(s, args);
    }

    @Deprecated
    public static void log (String s, Object... args) {

        logInfo(s, args);
    }

    @Deprecated
    public static void logError (String s, Object... args) {

        MwForge.logger.error(s, args);
    }

    @Deprecated
    public static void logInfo (String s, Object... args) {

        MwForge.logger.info(s, args);
    }

    @Deprecated
    public static void logWarning (String s, Object... args) {

        MwForge.logger.warn(s, args);
    }
}
