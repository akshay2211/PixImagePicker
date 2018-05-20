package com.fxn.utility;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.fxn.modals.Img;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by akshay on 06/04/18.
 */

public class ImageFetcher extends AsyncTask<Cursor, Void, ArrayList<Img>> {
    ArrayList<Img> LIST = new ArrayList<>();

    @Override
    protected ArrayList<Img> doInBackground(Cursor... cursors) {
        Cursor cursor = cursors[0];
        if (cursor != null) {

            int date = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
            int data = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            int contenturl = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            String header = "";
            int limit = 100;
            if (cursor.getCount() >= 100) {
                limit = 100;
            } else {
                limit = cursor.getCount();
            }
            cursor.move(limit - 1);
            for (int i = limit; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                Uri curl = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + cursor.getInt(contenturl));
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(cursor.getLong(date));
                String mydate = Utility.getDateDifference(calendar);

                if (!header.equalsIgnoreCase("" + mydate)) {
                    header = "" + mydate;
                    LIST.add(new Img("" + mydate, "", "", new SimpleDateFormat("MMMM yyyy").format(calendar.getTime())));
                }
                LIST.add(new Img("" + header, "" + curl, "" + cursor.getString(data), new SimpleDateFormat("MMMM yyyy").format(calendar.getTime())));
            }
        }
        cursor.close();
        return LIST;
    }

}
