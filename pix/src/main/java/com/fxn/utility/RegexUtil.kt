package com.fxn.utility;

/**
 * Created by sangcomz on 09/04/2017.
 */

public class RegexUtil {
    private static final String GIF_PATTERN = "(.+?)\\.gif$";

    public boolean checkGif(String path) {
        return path.matches(GIF_PATTERN);
    }
}
