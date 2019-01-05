package com.fxn.pix;

import com.fxn.utility.ImageQuality;

import java.io.Serializable;

public class Options implements Serializable {
    private int count = 1;
    private int requestCode = 0;
    private String path = "/DCIM/Camera";
    private int imageQuality = 40;
    private int height = 0, width = 0;
    private boolean frontfacing = false;

    private Options() {
    }

    public static Options init() {
        return new Options();
    }

    public int getImageQuality() {
        return imageQuality;
    }

    public Options setImageQuality(final ImageQuality imageQuality) {
        if (imageQuality == ImageQuality.LOW) {
            this.imageQuality = 20;
        } else if (imageQuality == ImageQuality.HIGH) {
            this.imageQuality = 80;
        } else {
            this.imageQuality = 40;
        }
        return this;
    }

    public void setImageResolution(int height, int width) {
        if (height == 0 || width == 0) {
            throw new NullPointerException("width or height can not be 0");
        }
        this.height = height;
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public boolean isFrontfacing() {
        return this.frontfacing;
    }

    public Options setFrontfacing(boolean frontfacing) {
        this.frontfacing = frontfacing;
        return this;
    }

    private void check() {
        if (this == null) {
            throw new NullPointerException("call init() method to initialise Options class");
        }
    }

    public int getCount() {
        return count;
    }

    public Options setCount(int count) {
        check();
        this.count = count;
        return this;
    }

    public int getRequestCode() {
        if (this.requestCode == 0) {
            throw new NullPointerException("requestCode in Options class is null");
        }
        return requestCode;
    }

    public Options setRequestCode(int requestcode) {
        check();
        this.requestCode = requestcode;
        return this;
    }

    public String getPath() {
        return this.path;
    }

    public Options setPath(String path) {
        check();
        this.path = path;
        return this;
    }

}
