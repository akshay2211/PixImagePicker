package com.fxn.pix;

import com.fxn.utility.ImageQuality;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

public class Options implements Serializable {
    private int count = 1;
    private int requestCode = 0;
    private String path = "/DCIM/Camera";
    private int imageQuality = 40;
    private int height = 0, width = 0;
    private boolean frontfacing = false;
    public static final int SCREEN_ORIENTATION_UNSET = -2;
    public static final int SCREEN_ORIENTATION_UNSPECIFIED = -1;
    public static final int SCREEN_ORIENTATION_LANDSCAPE = 0;
    public static final int SCREEN_ORIENTATION_PORTRAIT = 1;
    public static final int SCREEN_ORIENTATION_USER = 2;
    public static final int SCREEN_ORIENTATION_BEHIND = 3;
    public static final int SCREEN_ORIENTATION_SENSOR = 4;
    public static final int SCREEN_ORIENTATION_NOSENSOR = 5;
    public static final int SCREEN_ORIENTATION_SENSOR_LANDSCAPE = 6;
    public static final int SCREEN_ORIENTATION_SENSOR_PORTRAIT = 7;
    public static final int SCREEN_ORIENTATION_REVERSE_LANDSCAPE = 8;
    public static final int SCREEN_ORIENTATION_REVERSE_PORTRAIT = 9;
    public static final int SCREEN_ORIENTATION_FULL_SENSOR = 10;
    public static final int SCREEN_ORIENTATION_USER_LANDSCAPE = 11;
    public static final int SCREEN_ORIENTATION_USER_PORTRAIT = 12;
    public static final int SCREEN_ORIENTATION_FULL_USER = 13;
    public static final int SCREEN_ORIENTATION_LOCKED = 14;
    private ArrayList<String> preSelectedUrls = new ArrayList<>();
    private ArrayList<String> previouslySelectedPathList = new ArrayList<>();
    @ScreenOrientation
    private int screenOrientation = SCREEN_ORIENTATION_UNSPECIFIED;

    private Options() {
    }

    public static Options init() {
        return new Options();
    }

    public ArrayList<String> getPreSelectedUrls() {
        return preSelectedUrls;
    }

    public Options setPreSelectedUrls(ArrayList<String> preSelectedUrls) {
        check();
        this.preSelectedUrls = preSelectedUrls;
        return this;
    }

    public ArrayList<String> getPreviouslySelectedPathList() {
        return previouslySelectedPathList;
    }

    public Options setPreviouslySelectedPathList(ArrayList<String> previouslySelectedPathList) {
        check();
        if(previouslySelectedPathList == null){
            this.previouslySelectedPathList = new ArrayList<>();
        }else{
            this.previouslySelectedPathList = previouslySelectedPathList;
        }
        return this;
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

    public Options setImageResolution(int height, int width) {
        check();
        if (height == 0 || width == 0) {
            throw new NullPointerException("width or height can not be 0");
        }
        this.height = height;
        this.width = width;
        return this;
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

    public int getScreenOrientation() {
        return screenOrientation;
    }

    public Options setScreenOrientation(@ScreenOrientation int screenOrientation) {
        check();
        this.screenOrientation = screenOrientation;
        return this;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ScreenOrientation {
    }

}
