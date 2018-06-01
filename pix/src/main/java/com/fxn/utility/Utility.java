package com.fxn.utility;

import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;

import com.fxn.pix.R;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by akshay on 21/01/18.
 */

public class Utility {

    public static int HEIGHT, WIDTH;
    private String pathDir;

    public static void SetupStatusBarHiden(AppCompatActivity appCompatActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = appCompatActivity.getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    public static void showStatusBar(AppCompatActivity appCompatActivity) {
        synchronized (appCompatActivity) {
            Window w = appCompatActivity.getWindow();
            if (Build.VERSION.SDK_INT < 16) {

                w.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                View decorView = w.getDecorView();
                // Show Status Bar.
                int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
                decorView.setSystemUiVisibility(uiOptions);
            }
        }
    }

    public static void hideStatusBar(AppCompatActivity appCompatActivity) {
        synchronized (appCompatActivity) {
            Window w = appCompatActivity.getWindow();
            if (Build.VERSION.SDK_INT < 16) {
                w.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                w.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                View decorView = w.getDecorView();
                // Hide Status Bar.
                int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
            }
        }
    }

    public static void showTranslucentNavigation(AppCompatActivity appCompatActivity) {
        synchronized (appCompatActivity) {
            Window w = appCompatActivity.getWindow();
            if (Build.VERSION.SDK_INT < 16) {

                w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
        }
    }

    public static void hideTranslucentNavigation(AppCompatActivity appCompatActivity) {
        synchronized (appCompatActivity) {
            Window w = appCompatActivity.getWindow();
            if (Build.VERSION.SDK_INT < 16) {
                w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
        }
    }

    public static int getSoftButtonsBarSizePort(Activity activity) {
        // getRealMetrics is only available with API 17 and +
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }

    public static void getScreensize(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        HEIGHT = displayMetrics.heightPixels;
        WIDTH = displayMetrics.widthPixels;
        //  Log.e("WIDTH", "- " + WIDTH);
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        //   Log.e("-----", "px " + px + "         dp " + dp);
        return dp;
    }

    public static String getDateDifference(Calendar calendar) {
        Date d = calendar.getTime();
        Calendar lastmonth = Calendar.getInstance();
        Calendar lastweek = Calendar.getInstance();
        Calendar recent = Calendar.getInstance();
        lastmonth.add(Calendar.DAY_OF_MONTH, -(Calendar.DAY_OF_MONTH));
        lastweek.add(Calendar.DAY_OF_MONTH, -7);
        recent.add(Calendar.DAY_OF_MONTH, -2);
        if (calendar.before(lastmonth)) {
            return new SimpleDateFormat("MMMM").format(d);
        } else if (calendar.after(lastmonth) && calendar.before(lastweek)) {
            return "last month";
        } else if (calendar.after(lastweek) && calendar.before(recent)) {
            return "last week";
        } else {
            return "recent";
        }
    }

    public static boolean isNull(View topChild) {
        return topChild == null;
    }

    public static Cursor getCursor(Context context) {
        return context.getContentResolver().query(Constants.URI, Constants.PROJECTION,
                null, null, Constants.ORDERBY);
    }

    public static boolean isViewVisible(View view) {
        return view != null && view.getVisibility() == View.VISIBLE;
    }

    public static ViewPropertyAnimator showScrollbar(View mScrollbar, Context context) {
        float transX = context.getResources().getDimensionPixelSize(R.dimen.fastscroll_scrollbar_padding_end);
        mScrollbar.setTranslationX(transX);
        mScrollbar.setVisibility(View.VISIBLE);
        return mScrollbar.animate().translationX(0f).alpha(1f)
                .setDuration(Constants.sScrollbarAnimDuration)
                .setListener(new AnimatorListenerAdapter() {
                    // adapter required for new alpha value to stick
                });
    }

    public static void cancelAnimation(ViewPropertyAnimator animator) {
        if (animator != null) {
            animator.cancel();
        }
    }

    public static void manupulateVisibility(AppCompatActivity activity, float slideOffset,
                                            RecyclerView instantRecyclerView, RecyclerView recyclerView,
                                            View status_bar_bg, View topbar, View clickme, View sendButton, boolean longSelection) {
        instantRecyclerView.setAlpha(1 - slideOffset);
        clickme.setAlpha(1 - slideOffset);
        if (longSelection) {
            sendButton.setAlpha(1 - slideOffset);
        }
        topbar.setAlpha(slideOffset);
        recyclerView.setAlpha(slideOffset);
        if ((1 - slideOffset) == 0 && instantRecyclerView.getVisibility() == View.VISIBLE) {
            instantRecyclerView.setVisibility(View.GONE);
            clickme.setVisibility(View.GONE);
        } else if (instantRecyclerView.getVisibility() == View.GONE && (1 - slideOffset) > 0) {
            instantRecyclerView.setVisibility(View.VISIBLE);
            clickme.setVisibility(View.VISIBLE);
            if (longSelection) {
                sendButton.clearAnimation();
                sendButton.setVisibility(View.VISIBLE);
            }

        }
        if ((slideOffset) > 0 && recyclerView.getVisibility() == View.INVISIBLE) {
            recyclerView.setVisibility(View.VISIBLE);
            status_bar_bg.animate().translationY(0).setDuration(300).start();
            topbar.setVisibility(View.VISIBLE);
            Utility.showStatusBar(activity);
            Utility.hideTranslucentNavigation(activity);
        } else if (recyclerView.getVisibility() == View.VISIBLE && (slideOffset) == 0) {
            Utility.hideStatusBar(activity);
            Utility.showTranslucentNavigation(activity);
            recyclerView.setVisibility(View.INVISIBLE);
            topbar.setVisibility(View.GONE);
            status_bar_bg.animate().translationY(-(status_bar_bg.getHeight())).setDuration(300).start();

        }
    }

    @SuppressWarnings("SameParameterValue")
    public static int getValueInRange(int min, int max, int value) {
        int minimum = Math.max(min, value);
        return Math.min(minimum, max);
    }

    public static void vibe(Context c, long l) {
        ((Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(l);
    }

    public static File writeImage(byte[] jpeg) {
        File dir = new File(Environment.getExternalStorageDirectory(), "/DCIM/Camera");
        if (!dir.exists())
            dir.mkdir();
        File photo = new File(dir, "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmSS").format(new Date()) + ".jpg");
        if (photo.exists()) {
            photo.delete();
        }

        try {
            FileOutputStream fos = new FileOutputStream(photo.getPath());
            fos.write(jpeg);
            fos.close();
        } catch (Exception e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }
        return photo;
    }

    public static Bitmap getExcifCorrectedBitmap(File f) {
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(f.getAbsolutePath(), bounds);

        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(f.getAbsolutePath(), opts);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(f.getAbsolutePath().toString());

            String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;

            int rotationAngle = 0;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

            Matrix matrix = new Matrix();
            matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
            return rotatedBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getScaledBitmap(int maxWidth, Bitmap rotatedBitmap) {
        int nh = (int) (rotatedBitmap.getHeight() * (512.0 / rotatedBitmap.getWidth()));
        Bitmap scaled = Bitmap.createScaledBitmap(rotatedBitmap, maxWidth, nh, true);
        return scaled;
    }

    public List<Uri> getImagesFromGallary(Context context) {
        List<Uri> images = new ArrayList<Uri>();
        Cursor imageCursor = null;
        try {
            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.ImageColumns.ORIENTATION};
            final String orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC";
            imageCursor = context.getApplicationContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
            while (imageCursor.moveToNext()) {
                Uri uri = Uri.parse(imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                images.add(uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (imageCursor != null && !imageCursor.isClosed()) {
                imageCursor.close();
            }
        }
        return images;
    }

    @NonNull
    private Uri[] getAllMediaThumbnailsPath(Context context, long id,
                                            Boolean exceptGif) {
        ContentResolver resolver = context.getContentResolver();
        String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
        String bucketId = String.valueOf(id);
        String sort = MediaStore.Images.Media._ID + " DESC";
        String[] selectionArgs = {bucketId};

        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor c;
        if (!bucketId.equals("0")) {
            c = resolver.query(images, null, selection, selectionArgs, sort);
        } else {
            c = resolver.query(images, null, null, null, sort);
        }
        Uri[] imageUris = new Uri[c == null ? 0 : c.getCount()];
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    setPathDir(c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA)),
                            c.getString(c.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
                    int position = -1;
                    RegexUtil regexUtil = new RegexUtil();
                    do {
                        if (exceptGif &&
                                regexUtil.checkGif(c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA))))
                            continue;
                        int imgId = c.getInt(c.getColumnIndex(MediaStore.MediaColumns._ID));
                        Uri path = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + imgId);
                        imageUris[++position] = path;
                    } while (c.moveToNext());
                }
                c.close();
            } catch (Exception e) {
                if (!c.isClosed()) c.close();
            }
        }
        return imageUris;
    }

    private String setPathDir(String path, String fileName) {
        return pathDir = path.replace("/" + fileName, "");
    }

    public String getPathDir(Long bucketId) {
        if (pathDir.equals("") || bucketId == 0)
            pathDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM + "/Camera").getAbsolutePath();
        return pathDir;
    }
}
