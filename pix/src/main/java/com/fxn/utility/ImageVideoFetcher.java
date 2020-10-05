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
 * Created by akshay on 02/13/20.
 */

public class ImageVideoFetcher extends AsyncTask<Cursor, Void, ImageVideoFetcher.ModelList> {

    public int startingCount = 0;
    public String header = "";
    private ArrayList<Img> selectionList = new ArrayList<>();
    private ArrayList<Img> LIST = new ArrayList<>();
    private ArrayList<String> preSelectedUrls = new ArrayList<>();
    private Context context;
    private ArrayList<String> previouslySelectedPathList = new ArrayList<>();

    public ImageVideoFetcher(Context context) {
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

    public ImageVideoFetcher setPreSelectedUrls(ArrayList<String> preSelectedUrls) {
        this.preSelectedUrls = preSelectedUrls;
        return this;
    }

    @Override
    protected ModelList doInBackground(Cursor... cursors) {
        Cursor cursor = cursors[0];
        try {
            if (cursor != null) {
                int date = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED);
                int data = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                int mediaType = cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE);
                int contentUrl = cursor.getColumnIndex(MediaStore.Files.FileColumns._ID);
                int displayname = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
                int title = cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE);
                int parent = cursor.getColumnIndex(MediaStore.Files.FileColumns.PARENT);

                //int videoDate = cursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN);
                int imageDate = cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED);

                int limit = 100;
                if (cursor.getCount() < limit) {
                    limit = cursor.getCount() - 1;
                }
                cursor.move(limit);
                synchronized (context) {
                    int pos = getStartingCount();
                    for (int i = limit; i < cursor.getCount(); i++) {
                        cursor.moveToNext();
                        Uri path =
                                Uri.withAppendedPath(Constants.IMAGE_VIDEO_URI, "" + cursor.getInt(contentUrl));
                        Calendar calendar = Calendar.getInstance();
                        int finDate = imageDate; //mediaType == 1 ? imageDate : videoDate;
                        calendar.setTimeInMillis(cursor.getLong(finDate) * 1000);
                        String dateDifference = Utility.getDateDifference(context, calendar);
                        //Log.e("difference "+i,"->  "+dateDifference);
                        int media_type = cursor.getInt(mediaType);
                        String display_name = cursor.getString(displayname);
                        String Title = cursor.getString(title);
                        String Parent = cursor.getString(parent);
					/*Log.e("all data", "->  mediaType "
							+ mediaType
							+ " "
							+ media_type
							+ "  "
							+ i
							+ "  "
							+ display_name
							+ "  "
							+ Title
							+ "  "
							+ Parent);*/
                        int im = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
                        if (!header.equalsIgnoreCase("" + dateDifference)) {
                            header = "" + dateDifference;
                            pos += 1;

                            LIST.add(new Img("" + dateDifference, "", "", "", media_type, previouslySelectedPathList.contains(cursor.getString(data))));
                        }

                        Img img = new Img("" + header, "" + path, cursor.getString(data), "" + pos, media_type, previouslySelectedPathList.contains(cursor.getString(data)));

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

    public ArrayList<String> getPreviouslySelectedPathList() {
        return previouslySelectedPathList;
    }

    public void setPreviouslySelectedPathList(ArrayList<String> previouslySelectedPathList) {
        this.previouslySelectedPathList = previouslySelectedPathList;
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