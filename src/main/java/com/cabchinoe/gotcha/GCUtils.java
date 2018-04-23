package com.cabchinoe.gotcha;

/**
 * Created by n3212 on 2017/9/19.
 */
public class GCUtils {
    public static void log(String s, Object...args) {
        GotChaForge.logger.info(String.format(s, args));
    }
}
