package com.fxn.utility;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.fxn.modals.Img;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by akshay on 06/04/18.
 */

public class ImageFetcher extends AsyncTask<Cursor, Void, ImageFetcher.ModelList> {


    public int startingCount = 0;
    public String header = "";
    private ArrayList<Img> selectionList = new ArrayList<>();
    private ArrayList<Img> LIST = new ArrayList<>();
    private ArrayList<String> preSelectedUrls = new ArrayList<>();
    private Context context;

    public ImageFetcher(Context context) {
        this.context = context;
    }

    public int getStartingCount() {
        return startingCount;
    }

    public void setStartingCount(int startingCount) {
        this.startingCount = startingCount;
    }

    public ArrayList<String> getPreSelectedUrls() {
        return preSelectedUrls;
    }

    public ImageFetcher setPreSelectedUrls(ArrayList<String> preSelectedUrls) {
        this.preSelectedUrls = preSelectedUrls;
        return this;
    }

    @Override
    protected ModelList doInBackground(Cursor... cursors) {
        Cursor cursor = cursors[0];
        try {
            if (cursor != null) {
                int date = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
                int data = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                int contentUrl = cursor.getColumnIndex(MediaStore.Images.Media._ID);

                int limit = 100;
                if (cursor.getCount() < limit) {
                    limit = cursor.getCount() - 1;
                }
                cursor.move(limit);
                synchronized (context) {
                    int pos = getStartingCount();

                    for (int i = limit; i < cursor.getCount(); i++) {
                        cursor.moveToNext();
                        Uri path = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + cursor.getInt(contentUrl));
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(cursor.getLong(date));
                        String dateDifference = Utility.getDateDifference(context, calendar);


                        if (!header.equalsIgnoreCase("" + dateDifference)) {
                            header = "" + dateDifference;
                            pos += 1;
                            LIST.add(new Img("" + dateDifference, "", "", "", 1));
                        }
                        Img img = new Img("" + header, "" + path, cursor.getString(data), "" + pos,
                                1);
                        img.setPosition(pos);
                        if (preSelectedUrls.contains(img.getUrl())) {
                            img.setSelected(true);
                            selectionList.add(img);
                        }
                        pos += 1;
                        LIST.add(img);
                    }
                    cursor.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ModelList(LIST, selectionList);
    }

    public class ModelList {
        ArrayList<Img> LIST = new ArrayList<>();
        ArrayList<Img> selection = new ArrayList<>();

        public ModelList(ArrayList<Img> LIST, ArrayList<Img> selection) {
            this.LIST = LIST;
            this.selection = selection;
        }

        public ArrayList<Img> getLIST() {
            return LIST;
        }

        public ArrayList<Img> getSelection() {
            return selection;
        }
    }

}
