package com.fxn.modals;

import java.io.Serializable;

/**
 * Created by akshay on 17/03/18.
 */

public class Img implements Serializable {
    private String headerDate;
    private String contentUrl;
    private String url;
    private Boolean isSelected;
    private Boolean isPreviouslySelected;
    private String scrollerDate;
    private int media_type = 1;
    private int position;

    public Img(String headerDate, String contentUrl, String url, String scrollerDate,
               int type, boolean isPreviouslySelected) {
        this.headerDate = headerDate;
        this.contentUrl = contentUrl;
        this.url = url;
        this.isSelected = false;
        this.isPreviouslySelected = isPreviouslySelected;
        this.scrollerDate = scrollerDate;
        this.media_type = type;
    }


    public int getMedia_type() {
        return media_type;
    }

    public void setMedia_type(int media_type) {
        this.media_type = media_type;
    }

    public Boolean getPreviouslySelected() {
        return isPreviouslySelected;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getHeaderDate() {
        return headerDate;
    }

    public void setHeaderDate(String headerDate) {
        this.headerDate = headerDate;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public String getScrollerDate() {
        return scrollerDate;
    }

    public void setScrollerDate(String scrollerDate) {
        this.scrollerDate = scrollerDate;
    }
}
