package com.fxn.pix;

import java.io.Serializable;

public class Options implements Serializable {
    private static Options options;
    private int count = 1;
    private int requestCode = 0;
    private String path = "/DCIM/Camera";

    private Options() {
    }

    public static Options init() {
        Options.options = new Options();
        return Options.options;
    }

    private static void check() {
        if (Options.options == null) {
            throw new NullPointerException("call init() method to initialise Options class");
        }
    }

    public int getCount() {
        return count;
    }

    public Options setCount(int count) {
        check();
        Options.options.count = count;
        return Options.options;
    }

    public int getRequestCode() {
        if (requestCode == 0) {
            throw new NullPointerException("requestCode in Options class is null");
        }
        return requestCode;
    }

    public Options setRequestCode(int requestcode) {
        check();
        Options.options.requestCode = requestcode;
        return Options.options;
    }

    public String getPath() {
        return path;
    }

    public static Options setPath(String path) {
        check();
        Options.options.path = path;
        return Options.options;
    }

}
