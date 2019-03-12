package com.fxn.utility;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.fxn.modals.Img;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by akshay on 06/04/18.
 */

public class ImageFetcher extends AsyncTask<Cursor, Void, ImageFetcher.ModelList> {


    private ArrayList<Img> selectionList = new ArrayList<>();

    private ArrayList<Img> LIST = new ArrayList<>();
    private ArrayList<String> preSelectedUrls = new ArrayList<>();

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
        if (cursor != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
            int date = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
            int data = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            int contentUrl = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            String header = "";
            int limit = 100;
            if (cursor.getCount() < 100) {
                limit = cursor.getCount();
            }
            cursor.move(limit - 1);
            for (int i = limit; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                Uri curl = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + cursor.getInt(contentUrl));
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(cursor.getLong(date));
                String dateDifference = Utility.getDateDifference(context, calendar);

                if (!header.equalsIgnoreCase(dateDifference)) {
                    header = dateDifference;
                    LIST.add(new Img(dateDifference, "", "", dateFormat.format(calendar.getTime())));
                }
                Img img = new Img("" + header, "" + curl.toString(), cursor.getString(data), "" + dateFormat.format(calendar.getTime()));

                if (getPreSelectedUrls().contains(img.getUrl())) {
                    img.setSelected(true);
                    selectionList.add(img);
                }
                LIST.add(img);
            }
            cursor.close();
        }

        return new ModelList(LIST, selectionList);
    }

    private Context context;

    public ImageFetcher(Context context) {
        this.context = context;
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
