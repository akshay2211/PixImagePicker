package com.fxn.utility;

import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.fxn.modals.Img;
import com.fxn.pix.R;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by akshay on 21/01/18.
 */

public class Utility {

public static int HEIGHT, WIDTH;

private String pathDir;

public static void setupStatusBarHidden(AppCompatActivity appCompatActivity) {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
		Window w = appCompatActivity.getWindow();
		w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
		w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
				WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			w.setStatusBarColor(Color.TRANSPARENT);
		}
		//w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		//  w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

	}
}

public static void showStatusBar(AppCompatActivity appCompatActivity) {
	synchronized (appCompatActivity) {
	     /* Window w = appCompatActivity.getWindow();
	      View decorView = w.getDecorView();
	      // Show Status Bar.
	      int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
	      decorView.setSystemUiVisibility(uiOptions);*/

		appCompatActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
}

public static void hideStatusBar(AppCompatActivity appCompatActivity) {
	synchronized (appCompatActivity) {
	      /*Window w = appCompatActivity.getWindow();
	      View decorView = w.getDecorView();
	      // Hide Status Bar.
	      int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
	      decorView.setSystemUiVisibility(uiOptions);*/
		appCompatActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
		if (realHeight > usableHeight) {
			return realHeight - usableHeight;
		} else {
			return 0;
		}
	}
	return 0;
}

public static int getStatusBarSizePort(AppCompatActivity check) {
	// getRealMetrics is only available with API 17 and +
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
		int result = 0;
		Log.e("->activity", "----------->  " + check);
		Resources res = check.getBaseContext().getResources();
		int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = check.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}
	return 0;
}

public static void getScreenSize(Activity activity) {
	DisplayMetrics displayMetrics = new DisplayMetrics();
	activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
	HEIGHT = displayMetrics.heightPixels;
	WIDTH = displayMetrics.widthPixels;
}

public static float convertDpToPixel(float dp, Context context) {
	Resources resources = context.getResources();
	DisplayMetrics metrics = resources.getDisplayMetrics();
	return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
}

public static float convertPixelsToDp(float px, Context context) {
	Resources resources = context.getResources();
	DisplayMetrics metrics = resources.getDisplayMetrics();
	return px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
}

public static String getDateDifference(Context context, Calendar calendar) {
	Date d = calendar.getTime();
	Calendar lastMonth = Calendar.getInstance();
	Calendar lastWeek = Calendar.getInstance();
	Calendar recent = Calendar.getInstance();
	lastMonth.add(Calendar.DAY_OF_MONTH, -(Calendar.DAY_OF_MONTH));
	lastWeek.add(Calendar.DAY_OF_MONTH, -7);
	recent.add(Calendar.DAY_OF_MONTH, -2);
	if (calendar.before(lastMonth)) {
		return new SimpleDateFormat("MMMM").format(d);
	} else if (calendar.after(lastMonth) && calendar.before(lastWeek)) {
		return context.getResources().getString(R.string.pix_last_month);
	} else if (calendar.after(lastWeek) && calendar.before(recent)) {
		return context.getResources().getString(R.string.pix_last_week);
	} else {
		return context.getResources().getString(R.string.pix_recent);
	}
}

public static boolean isNull(View topChild) {
	return topChild == null;
}

public static Cursor getCursor(Context context) {
	return context.getContentResolver().query(Constants.URI, Constants.PROJECTION,
			null, null, Constants.ORDERBY);
}

public static Cursor getImageVideoCursor(Context context) {
	return context.getContentResolver()
			.query(Constants.IMAGE_VIDEO_URI, Constants.IMAGE_VIDEO_PROJECTION,
					Constants.IMAGE_VIDEO_SELECTION, null, Constants.IMAGE_VIDEO_ORDERBY);
}

public static boolean isViewVisible(View view) {
	return view != null && view.getVisibility() == View.VISIBLE;
}

public static ViewPropertyAnimator showScrollbar(View mScrollbar, Context context) {
	float transX = context.getResources().getDimensionPixelSize(R.dimen.fastscroll_bubble_size);
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

public static void manipulateVisibility(AppCompatActivity activity, float slideOffset,
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
		status_bar_bg.animate().translationY(0).setDuration(200).start();
		topbar.setVisibility(View.VISIBLE);
		Utility.showStatusBar(activity);
	} else if (recyclerView.getVisibility() == View.VISIBLE && (slideOffset) == 0) {
		Utility.hideStatusBar(activity);
		recyclerView.setVisibility(View.INVISIBLE);
		topbar.setVisibility(View.GONE);
		status_bar_bg.animate().translationY(-(status_bar_bg.getHeight())).setDuration(550).start();
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

public static File writeImage(Bitmap bitmap, String path, int quality, int newWidth,
		int newHeight) {
	File dir = new File(Environment.getExternalStorageDirectory(), path);
	if (!dir.exists()) {
		dir.mkdirs();
	}
	File photo = new File(dir, "IMG_"
			+ new SimpleDateFormat("yyyyMMdd_HHmmSS", Locale.ENGLISH).format(new Date())
			+ ".jpg");
	if (photo.exists()) {
		photo.delete();
	}
	if (newWidth == 0 && newHeight == 0) {
		newWidth = bitmap.getWidth() / 2;
		newHeight = bitmap.getHeight() / 2;
	}
	bitmap = getResizedBitmap(bitmap, newWidth, newHeight);

	try {
		FileOutputStream fos = new FileOutputStream(photo.getPath());
		bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
		// fos.write(jpeg);
		fos.close();
	} catch (Exception e) {
		Log.e("PictureDemo", "Exception in photoCallback", e);
	}
	return photo;
}

public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
	int width = bm.getWidth();
	int height = bm.getHeight();
	float scaleWidth = ((float) newWidth) / width;
	float scaleHeight = ((float) newHeight) / height;
	// CREATE A MATRIX FOR THE MANIPULATION
	Matrix matrix = new Matrix();
	// RESIZE THE BIT MAP
	matrix.postScale(scaleWidth, scaleHeight);

	// "RECREATE" THE NEW BITMAP
	Bitmap resizedBitmap = Bitmap.createBitmap(
			bm, 0, 0, width, height, matrix, false);
	return resizedBitmap.copy(Bitmap.Config.RGB_565, false);
}

public static Bitmap getScaledBitmap(int maxWidth, Bitmap rotatedBitmap) {
	try {

		int nh = (int) (rotatedBitmap.getHeight() * (512.0 / rotatedBitmap.getWidth()));
		return Bitmap.createScaledBitmap(rotatedBitmap, maxWidth, nh, true);
	} catch (Exception e) {
		e.printStackTrace();
		return null;
	}
}

public static Bitmap rotate(Bitmap scaledBitmap, int i) {
	if (i == 0) {
		return scaledBitmap;
	}
	Matrix matrix = new Matrix();
	matrix.preRotate(-i);
	return Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(),
			scaledBitmap.getHeight(), matrix, false);
}

public static float getFingerSpacing(MotionEvent event) {
	try {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	} catch (Exception e) {
		Log.e("exc", "->" + e.getMessage());
		return 0;
	}
}

public static boolean containsName(final ArrayList<Img> list, final String url) {
	for (Img o : list) {
		if (o != null && o.getContentUrl().equals(url)) {
			return true;
		}
	}
	return false;
}

public static void scanPhoto(Context pix, File photo) {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
		final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		final Uri contentUri = Uri.fromFile(photo);
		scanIntent.setData(contentUri);
		pix.sendBroadcast(scanIntent);
	} else {
		pix.sendBroadcast(
				new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse(photo.getAbsolutePath())));
	}
}
}
