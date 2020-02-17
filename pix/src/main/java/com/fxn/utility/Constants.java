package com.fxn.utility;

import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by akshay on 06/04/18.
 */

public class Constants {

public static final int sScrollbarAnimDuration = 500;
    public static String[] PROJECTION = new String[]{
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.DATE_MODIFIED,
    };
    public static Uri URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    public static String ORDERBY = MediaStore.Images.Media.DATE_TAKEN + " DESC";

public static String[] IMAGE_VIDEO_PROJECTION = new String[] {
		MediaStore.Files.FileColumns.DATA,
		MediaStore.Files.FileColumns._ID,
		MediaStore.Files.FileColumns.PARENT,
		MediaStore.Files.FileColumns.DISPLAY_NAME,
		MediaStore.Files.FileColumns.DATE_ADDED,
		MediaStore.Files.FileColumns.MEDIA_TYPE,
		MediaStore.Files.FileColumns.MIME_TYPE,
		MediaStore.Files.FileColumns.TITLE
};
public static String IMAGE_VIDEO_SELECTION = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
		+ MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
		+ " OR "
		+ MediaStore.Files.FileColumns.MEDIA_TYPE + "="
		+ MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
public static Uri IMAGE_VIDEO_URI = MediaStore.Files.getContentUri("external");
public static String IMAGE_VIDEO_ORDERBY = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";

}
