package com.fxn.pix;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fxn.adapters.InstantImageAdapter;
import com.fxn.adapters.MainImageAdapter;
import com.fxn.interfaces.OnSelectionListener;
import com.fxn.interfaces.WorkFinish;
import com.fxn.modals.Img;
import com.fxn.utility.Constants;
import com.fxn.utility.HeaderItemDecoration;
import com.fxn.utility.ImageFetcher;
import com.fxn.utility.PermUtil;
import com.fxn.utility.Utility;
import com.fxn.utility.ui.FastScrollStateChangeListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.configuration.CameraConfiguration;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.result.BitmapPhoto;
import io.fotoapparat.selector.FlashSelectorsKt;
import io.fotoapparat.selector.FocusModeSelectorsKt;
import io.fotoapparat.selector.LensPositionSelectorsKt;
import io.fotoapparat.selector.SelectorsKt;
import io.fotoapparat.view.CameraView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class Pix extends AppCompatActivity implements View.OnTouchListener {

    private static final int sBubbleAnimDuration = 1000;
    private static final int sScrollbarHideDelay = 1000;
    private static final String OPTIONS = "options";
    private static final int sTrackSnapRange = 5;
    public static String IMAGE_RESULTS = "image_results";
    public static float TOPBAR_HEIGHT;
    private int BottomBarHeight = 0;
    private int colorPrimaryDark;
    private Fotoapparat fotoapparat;
    private float zoom = 0.0f;
    private float dist = 0.0f;
    private Handler handler = new Handler();
    private FastScrollStateChangeListener mFastScrollStateChangeListener;
    private RecyclerView recyclerView, instantRecyclerView;
    private BottomSheetBehavior mBottomSheetBehavior;
    private InstantImageAdapter initaliseadapter;
    private View status_bar_bg, mScrollbar, topbar, bottomButtons, sendButton;
    private TextView mBubbleView, img_count;
    private ImageView mHandleView, selection_back, selection_check;
    private ViewPropertyAnimator mScrollbarAnimator;
    private ViewPropertyAnimator mBubbleAnimator;
    private Set<Img> selectionList = new HashSet<>();
    private Runnable mScrollbarHider = new Runnable() {
        @Override
        public void run() {
            hideScrollbar();
        }
    };
    private MainImageAdapter mainImageAdapter;
    private float mViewHeight;
    private boolean mHideScrollbar = true;
    private boolean LongSelection = false;
    private Options options = null;
    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (!mHandleView.isSelected() && recyclerView.isEnabled()) {
                setViewPositions(getScrollProportion(recyclerView));
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (recyclerView.isEnabled()) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        handler.removeCallbacks(mScrollbarHider);
                        Utility.cancelAnimation(mScrollbarAnimator);
                        if (!Utility.isViewVisible(mScrollbar) && (recyclerView.computeVerticalScrollRange() - mViewHeight > 0)) {
                            mScrollbarAnimator = Utility.showScrollbar(mScrollbar, Pix.this);
                        }
                        break;
                    case RecyclerView.SCROLL_STATE_IDLE:
                        if (mHideScrollbar && !mHandleView.isSelected()) {
                            handler.postDelayed(mScrollbarHider, sScrollbarHideDelay);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    };
    private TextView selection_count;
    private OnSelectionListener onSelectionListener = new OnSelectionListener() {
        @Override
        public void onClick(Img img, View view, int position) {
            if (LongSelection) {
                if (selectionList.contains(img)) {
                    selectionList.remove(img);
                    initaliseadapter.select(false, position);
                    mainImageAdapter.select(false, position);
                } else {
                    if (options.getCount() <= selectionList.size()) {
                        Toast.makeText(Pix.this, String.format(getResources().getString(R.string.selection_limiter_pix), selectionList.size()), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    img.setPosition(position);
                    selectionList.add(img);
                    initaliseadapter.select(true, position);
                    mainImageAdapter.select(true, position);
                }
                if (selectionList.size() == 0) {
                    LongSelection = false;
                    selection_check.setVisibility(View.VISIBLE);
                    DrawableCompat.setTint(selection_back.getDrawable(), colorPrimaryDark);
                    topbar.setBackgroundColor(Color.parseColor("#ffffff"));
                    Animation anim = new ScaleAnimation(
                            1f, 0f, // Start and end values for the X axis scaling
                            1f, 0f, // Start and end values for the Y axis scaling
                            Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                            Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
                    anim.setFillAfter(true); // Needed to keep the result of the animation
                    anim.setDuration(300);
                    anim.setAnimationListener(new Animation.AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            sendButton.setVisibility(View.GONE);
                            sendButton.clearAnimation();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    sendButton.startAnimation(anim);

                }
                selection_count.setText(getResources().getString(R.string.pix_selected) + " " + selectionList.size());
                img_count.setText(String.valueOf(selectionList.size()));
            } else {
                img.setPosition(position);
                selectionList.add(img);
                returnObjects();
                DrawableCompat.setTint(selection_back.getDrawable(), colorPrimaryDark);
                topbar.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        }

        @Override
        public void onLongClick(Img img, View view, int position) {
            if (options.getCount() > 1) {
                Utility.vibe(Pix.this, 50);
                //Log.e("onLongClick", "onLongClick");
                LongSelection = true;
                if ((selectionList.size() == 0) && (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)) {
                    sendButton.setVisibility(View.VISIBLE);
                    Animation anim = new ScaleAnimation(
                            0f, 1f, // Start and end values for the X axis scaling
                            0f, 1f, // Start and end values for the Y axis scaling
                            Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                            Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
                    anim.setFillAfter(true); // Needed to keep the result of the animation
                    anim.setDuration(300);
                    sendButton.startAnimation(anim);
                }
                if (selectionList.contains(img)) {
                    selectionList.remove(img);
                    initaliseadapter.select(false, position);
                    mainImageAdapter.select(false, position);
                } else {
                    if (options.getCount() <= selectionList.size()) {
                        Toast.makeText(Pix.this, String.format(getResources().getString(R.string.selection_limiter_pix), selectionList.size()), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    img.setPosition(position);
                    selectionList.add(img);
                    initaliseadapter.select(true, position);
                    mainImageAdapter.select(true, position);
                }
                selection_check.setVisibility(View.GONE);
                topbar.setBackgroundColor(colorPrimaryDark);
                selection_count.setText(getResources().getString(R.string.pix_selected) + " " + selectionList.size());
                img_count.setText(String.valueOf(selectionList.size()));
                DrawableCompat.setTint(selection_back.getDrawable(), Color.parseColor("#ffffff"));
            }

        }
    };
    private FrameLayout flash;
    private ImageView front;
    private int flashDrawable;
    private View.OnTouchListener onCameraTouchListner = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getPointerCount() > 1) {

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_POINTER_DOWN:
                        dist = Utility.getFingerSpacing(event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float maxZoom = 1f;

                        float newDist = Utility.getFingerSpacing(event);
                        if (newDist > dist) {
                            //zoom in
                            if (zoom < maxZoom)
                                zoom = zoom + 0.01f;
                        } else if ((newDist < dist) && (zoom > 0)) {
                            //zoom out
                            zoom = zoom - 0.01f;
                        }
                        dist = newDist;
                        fotoapparat.setZoom(zoom);
                        break;
                    default:
                        break;
                }
            }
            return true;
        }
    };


    public static void start(final Fragment context, final Options options) {
        PermUtil.checkForCamaraWritePermissions(context, new WorkFinish() {
            @Override
            public void onWorkFinish(Boolean check) {
                Intent i = new Intent(context.getActivity(), Pix.class);
                i.putExtra(OPTIONS, options);
                context.startActivityForResult(i, options.getRequestCode());
            }
        });
    }

    public static void start(Fragment context, int requestCode) {
        start(context, Options.init().setRequestCode(requestCode).setCount(1));
    }

    public static void start(final FragmentActivity context, final Options options) {
        PermUtil.checkForCamaraWritePermissions(context, new WorkFinish() {
            @Override
            public void onWorkFinish(Boolean check) {
                Intent i = new Intent(context, Pix.class);
                i.putExtra(OPTIONS, options);
                context.startActivityForResult(i, options.getRequestCode());
            }
        });
    }

    public static void start(final FragmentActivity context, int requestCode) {
        start(context, Options.init().setRequestCode(requestCode).setCount(1));
    }

    private void hideScrollbar() {
        float transX = getResources().getDimensionPixelSize(R.dimen.fastscroll_scrollbar_padding_end);
        mScrollbarAnimator = mScrollbar.animate().translationX(transX).alpha(0f)
                .setDuration(Constants.sScrollbarAnimDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mScrollbar.setVisibility(View.GONE);
                        mScrollbarAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);
                        mScrollbar.setVisibility(View.GONE);
                        mScrollbarAnimator = null;
                    }
                });
    }

    public void returnObjects() {
        ArrayList<String> list = new ArrayList<>();
        for (Img i : selectionList) {
            list.add(i.getUrl());
            // Log.e("Pix images", "img " + i.getUrl());
        }
        Intent resultIntent = new Intent();
        resultIntent.putStringArrayListExtra(IMAGE_RESULTS, list);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setupStatusBarHidden(this);
        Utility.hideStatusBar(this);
        setContentView(R.layout.activity_main_lib);
        initialize();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initialize();
        fotoapparat.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fotoapparat.start();
    }

    @Override
    protected void onPause() {
        fotoapparat.stop();
        super.onPause();
    }

    private void initialize() {
        Utility.getScreenSize(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        try {
            options = (Options) getIntent().getSerializableExtra(OPTIONS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setRequestedOrientation(options.getScreenOrientation());
        colorPrimaryDark = ResourcesCompat.getColor(getResources(), R.color.colorPrimaryPix, getTheme());
        CameraView mCamera = findViewById(R.id.camera_view);
        fotoapparat = Fotoapparat.with(this).into(mCamera)
                .previewScaleType(ScaleType.CenterCrop)  // we want the preview to fill the view
                // .photoResolution(ResolutionSelectorsKt.lowestResolution())   // we want to have the biggest photo possible
                .lensPosition(LensPositionSelectorsKt.back())      // we want back camera
                .focusMode(SelectorsKt.firstAvailable(  // (optional) use the first focus mode which is supported by device
                        FocusModeSelectorsKt.continuousFocusPicture(),
                        FocusModeSelectorsKt.autoFocus(),    // in case if continuous focus is not available on device, auto focus will be used
                        FocusModeSelectorsKt.fixed()             // if even auto focus is not available - fixed focus mode will be used
                ))
                //.flash(FlashSelectorsKt.autoRedEye())
                /*.logger(LoggersKt.loggers(            // (optional) we want to log camera events in 2 places at once
                        LoggersKt.logcat(),           // ... in logcat
                        LoggersKt.fileLogger(this)    // ... and to file
                ))*/
                .build();

        zoom = 0.0f;
        mCamera.setOnTouchListener(onCameraTouchListner);
        fotoapparat.start();
        fotoapparat.updateConfiguration(CameraConfiguration.builder().flash(FlashSelectorsKt.autoRedEye()).build());
        flash = findViewById(R.id.flash);
        front = findViewById(R.id.front);
        topbar = findViewById(R.id.topbar);
        selection_count = findViewById(R.id.selection_count);
        selection_back = findViewById(R.id.selection_back);
        selection_check = findViewById(R.id.selection_check);
        selection_check.setVisibility((options.getCount() > 1) ? View.VISIBLE : View.GONE);
        sendButton = findViewById(R.id.sendButton);
        img_count = findViewById(R.id.img_count);
        mBubbleView = findViewById(R.id.fastscroll_bubble);
        mHandleView = findViewById(R.id.fastscroll_handle);
        mScrollbar = findViewById(R.id.fastscroll_scrollbar);
        mScrollbar.setVisibility(View.GONE);
        mBubbleView.setVisibility(View.GONE);
        bottomButtons = findViewById(R.id.bottomButtons);
        TOPBAR_HEIGHT = Utility.convertDpToPixel(56, Pix.this);
        status_bar_bg = findViewById(R.id.status_bar_bg);
        instantRecyclerView = findViewById(R.id.instantRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        instantRecyclerView.setLayoutManager(linearLayoutManager);
        initaliseadapter = new InstantImageAdapter(this);
        initaliseadapter.addOnSelectionListener(onSelectionListener);
        instantRecyclerView.setAdapter(initaliseadapter);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.addOnScrollListener(mScrollListener);
        FrameLayout mainFrameLayout = findViewById(R.id.mainFrameLayout);
        BottomBarHeight = Utility.getSoftButtonsBarSizePort(this);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, 0, 0, BottomBarHeight);
        mainFrameLayout.setLayoutParams(lp);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) sendButton.getLayoutParams();
        layoutParams.setMargins(0, 0, (int) (Utility.convertDpToPixel(16, this)),
                (int) (Utility.convertDpToPixel(174, this)));
        sendButton.setLayoutParams(layoutParams);
        mainImageAdapter = new MainImageAdapter(this);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, MainImageAdapter.SPAN_COUNT);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mainImageAdapter.getItemViewType(position) == MainImageAdapter.HEADER) {
                    return MainImageAdapter.SPAN_COUNT;
                }
                return 1;
            }
        });
        recyclerView.setLayoutManager(mLayoutManager);
        mainImageAdapter.addOnSelectionListener(onSelectionListener);
        recyclerView.setAdapter(mainImageAdapter);
        recyclerView.addItemDecoration(new HeaderItemDecoration(this, mainImageAdapter));
        mHandleView.setOnTouchListener(this);
        final CameraConfiguration cameraConfiguration = new CameraConfiguration();
        if (options.isFrontfacing()) {
            fotoapparat.switchTo(LensPositionSelectorsKt.front(), cameraConfiguration);
        } else {
            fotoapparat.switchTo(LensPositionSelectorsKt.back(), cameraConfiguration);
        }
        onClickMethods();


        flashDrawable = R.drawable.ic_flash_off_black_24dp;

        if ((options.getPreSelectedUrls().size()) > options.getCount()) {
            int large = options.getPreSelectedUrls().size() - 1;
            int small = options.getCount();
            for (int i = large; i > (small - 1); i--) {
                options.getPreSelectedUrls().remove(i);
            }
        }
        DrawableCompat.setTint(selection_back.getDrawable(), colorPrimaryDark);
        updateImages();

    }

    private void onClickMethods() {
        findViewById(R.id.clickme).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fotoapparat.takePicture().toBitmap().transform(new Function1<BitmapPhoto, Bitmap>() {
                    @Override
                    public Bitmap invoke(BitmapPhoto bitmapPhoto) {
                        fotoapparat.stop();
                        return Utility.rotate(bitmapPhoto.bitmap, -bitmapPhoto.rotationDegrees);
                    }
                }).whenAvailable(new Function1<Bitmap, Unit>() {
                    @Override
                    public Unit invoke(Bitmap bitmap) {
                        if (bitmap != null) {
                            synchronized (bitmap) {
                                File photo = Utility.writeImage(bitmap, options.getPath(), options.getImageQuality(), options.getWidth(), options.getHeight());
                                boolean isPreviouslySelectedPix = options.getPreviouslySelectedPathList().contains(photo.getAbsolutePath());
                                selectionList.clear();
                                selectionList.add(new Img("", "", photo.getAbsolutePath(), "", isPreviouslySelectedPix));
                                returnObjects();

                            }
                        }
                        return null;
                    }
                });
            }
        });
        findViewById(R.id.selection_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(Pix.this, "fin", Toast.LENGTH_SHORT).show();
                //Log.e("Hello", "onclick");
                returnObjects();
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(Pix.this, "fin", Toast.LENGTH_SHORT).show();
                //Log.e("Hello", "onclick");
                returnObjects();
            }
        });
        selection_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        selection_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topbar.setBackgroundColor(colorPrimaryDark);
                selection_count.setText(getResources().getString(R.string.pix_tap_to_select));
                img_count.setText(String.valueOf(selectionList.size()));
                DrawableCompat.setTint(selection_back.getDrawable(), Color.parseColor("#ffffff"));
                LongSelection = true;
                selection_check.setVisibility(View.GONE);
            }
        });
        final ImageView iv = (ImageView) flash.getChildAt(0);
        flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int height = flash.getHeight();
                iv.animate().translationY(height).setDuration(100).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        iv.setTranslationY(-(height / 2));
                        if (flashDrawable == R.drawable.ic_flash_auto_black_24dp) {
                            flashDrawable = R.drawable.ic_flash_off_black_24dp;
                            iv.setImageResource(flashDrawable);
                            fotoapparat.updateConfiguration(CameraConfiguration.builder().flash(FlashSelectorsKt.off()).build());
                        } else if (flashDrawable == R.drawable.ic_flash_off_black_24dp) {
                            flashDrawable = R.drawable.ic_flash_on_black_24dp;
                            iv.setImageResource(flashDrawable);
                            fotoapparat.updateConfiguration(CameraConfiguration.builder().flash(FlashSelectorsKt.on()).build());
                        } else {
                            flashDrawable = R.drawable.ic_flash_auto_black_24dp;
                            iv.setImageResource(flashDrawable);
                            fotoapparat.updateConfiguration(CameraConfiguration.builder().flash(FlashSelectorsKt.autoRedEye()).build());
                        }
                        // fotoapparat.focus();
                        iv.animate().translationY(0).setDuration(50).setListener(null).start();
                    }
                }).start();
            }
        });

        front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ObjectAnimator oa1 = ObjectAnimator.ofFloat(front, "scaleX", 1f, 0f).setDuration(150);
                final ObjectAnimator oa2 = ObjectAnimator.ofFloat(front, "scaleX", 0f, 1f).setDuration(150);
                oa1.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        front.setImageResource(R.drawable.ic_photo_camera);
                        oa2.start();
                    }
                });
                oa1.start();
                if (options.isFrontfacing()) {
                    options.setFrontfacing(false);
                    final CameraConfiguration cameraConfiguration = new CameraConfiguration();
                    fotoapparat.switchTo(LensPositionSelectorsKt.back(), cameraConfiguration);
                } else {
                    final CameraConfiguration cameraConfiguration = new CameraConfiguration();
                    options.setFrontfacing(true);
                    fotoapparat.switchTo(LensPositionSelectorsKt.front(), cameraConfiguration);

                }
            }
        });
    }

    private void updateImages() {
        mainImageAdapter.clearList();
        Cursor cursor = Utility.getCursor(Pix.this);
        if (cursor == null)
            return;
        ArrayList<Img> INSTANTLIST = new ArrayList<>();
        String header = "";
        int limit = 100;
        if (cursor.getCount() < limit) {
            limit = cursor.getCount() - 1;
        }
        int date = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
        int data = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
        int contentUrl = cursor.getColumnIndex(MediaStore.Images.Media._ID);
        Calendar calendar;
        int pos = 0;
        for (int i = 0; i < limit; i++) {
            cursor.moveToNext();
            Uri path = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + cursor.getInt(contentUrl));
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(cursor.getLong(date));
            String dateDifference = Utility.getDateDifference(Pix.this, calendar);
            boolean isPreviouslySelectedPix = options.getPreviouslySelectedPathList().contains(cursor.getString(data));
            if (!header.equalsIgnoreCase("" + dateDifference)) {
                header = "" + dateDifference;
                pos += 1;
                INSTANTLIST.add(new Img("" + dateDifference, "", "", "", isPreviouslySelectedPix));
            }
            Img img = new Img("" + header, "" + path, cursor.getString(data), "" + pos, isPreviouslySelectedPix);
            img.setPosition(pos);
            if (options.getPreSelectedUrls().contains(img.getUrl())) {
                img.setSelected(true);
                selectionList.add(img);
            }
            pos += 1;
            INSTANTLIST.add(img);
        }
        if (selectionList.size() > 0) {
            LongSelection = true;
            sendButton.setVisibility(View.VISIBLE);
            Animation anim = new ScaleAnimation(
                    0f, 1f, // Start and end values for the X axis scaling
                    0f, 1f, // Start and end values for the Y axis scaling
                    Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                    Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
            anim.setFillAfter(true); // Needed to keep the result of the animation
            anim.setDuration(300);
            sendButton.startAnimation(anim);
            selection_check.setVisibility(View.GONE);
            topbar.setBackgroundColor(colorPrimaryDark);
            selection_count.setText(getResources().getString(R.string.pix_selected) + " " + selectionList.size());
            img_count.setText(String.valueOf(selectionList.size()));
            DrawableCompat.setTint(selection_back.getDrawable(), Color.parseColor("#ffffff"));
        }
        mainImageAdapter.addImageList(INSTANTLIST);
        initaliseadapter.addImageList(INSTANTLIST);
        ImageFetcher imageFetcher = new ImageFetcher(Pix.this) {
            @Override
            protected void onPostExecute(ImageFetcher.ModelList imgs) {
                super.onPostExecute(imgs);
                mainImageAdapter.addImageList(imgs.getLIST());
                initaliseadapter.addImageList(imgs.getLIST());
                selectionList.addAll(imgs.getSelection());
                if (selectionList.size() > 0) {
                    LongSelection = true;
                    sendButton.setVisibility(View.VISIBLE);
                    Animation anim = new ScaleAnimation(
                            0f, 1f, // Start and end values for the X axis scaling
                            0f, 1f, // Start and end values for the Y axis scaling
                            Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                            Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
                    anim.setFillAfter(true); // Needed to keep the result of the animation
                    anim.setDuration(300);
                    sendButton.startAnimation(anim);
                    selection_check.setVisibility(View.GONE);
                    topbar.setBackgroundColor(colorPrimaryDark);
                    selection_count.setText(getResources().getString(R.string.pix_selected) + " " + selectionList.size());
                    img_count.setText(String.valueOf(selectionList.size()));
                    DrawableCompat.setTint(selection_back.getDrawable(), Color.parseColor("#ffffff"));
                }
            }
        };
        imageFetcher.setStartingCount(pos);
        imageFetcher.header = header;
        imageFetcher.setPreSelectedUrls(options.getPreSelectedUrls());
        imageFetcher.setPreviouslySelectedPathList(options.getPreviouslySelectedPathList());
        imageFetcher.execute(Utility.getCursor(Pix.this));
        cursor.close();
        setBottomSheetBehavior();
    }

    private void setBottomSheetBehavior() {
        View bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setPeekHeight((int) (Utility.convertDpToPixel(194, this)));
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Utility.manipulateVisibility(Pix.this, slideOffset,
                        instantRecyclerView, recyclerView, status_bar_bg,
                        topbar, bottomButtons, sendButton, LongSelection);
                if (slideOffset == 1) {
                    Utility.showScrollbar(mScrollbar, Pix.this);
                    mainImageAdapter.notifyDataSetChanged();
                    mViewHeight = mScrollbar.getMeasuredHeight();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            setViewPositions(getScrollProportion(recyclerView));
                        }
                    });
                    sendButton.setVisibility(View.GONE);
                    //  fotoapparat.stop();
                } else if (slideOffset == 0) {
                    initaliseadapter.notifyDataSetChanged();
                    hideScrollbar();
                    img_count.setText(String.valueOf(selectionList.size()));
                    fotoapparat.start();
                }
            }
        });
    }

    private float getScrollProportion(RecyclerView recyclerView) {
        final int verticalScrollOffset = recyclerView.computeVerticalScrollOffset();
        final int verticalScrollRange = recyclerView.computeVerticalScrollRange();
        final float rangeDiff = verticalScrollRange - mViewHeight;
        float proportion = (float) verticalScrollOffset / (rangeDiff > 0 ? rangeDiff : 1f);
        return mViewHeight * proportion;
    }

    private void setViewPositions(float y) {
        int handleY = Utility.getValueInRange(0, (int) (mViewHeight - mHandleView.getHeight()), (int) (y - mHandleView.getHeight() / 2));
        mBubbleView.setY(handleY + Utility.convertDpToPixel((56), Pix.this));
        mHandleView.setY(handleY);
    }

    private void setRecyclerViewPosition(float y) {
        if (recyclerView != null && recyclerView.getAdapter() != null) {
            int itemCount = recyclerView.getAdapter().getItemCount();
            float proportion;

            if (mHandleView.getY() == 0) {
                proportion = 0f;
            } else if (mHandleView.getY() + mHandleView.getHeight() >= mViewHeight - sTrackSnapRange) {
                proportion = 1f;
            } else {
                proportion = y / mViewHeight;
            }

            int scrolledItemCount = Math.round(proportion * itemCount);
            int targetPos = Utility.getValueInRange(0, itemCount - 1, scrolledItemCount);
            recyclerView.getLayoutManager().scrollToPosition(targetPos);

            if (mainImageAdapter != null) {
                String text = mainImageAdapter.getSectionMonthYearText(targetPos);
                mBubbleView.setText(text);
                if (text.equalsIgnoreCase("")) {
                    mBubbleView.setVisibility(View.GONE);
                }
            }
        }
    }

    private void showBubble() {
        if (!Utility.isViewVisible(mBubbleView)) {
            mBubbleView.setVisibility(View.VISIBLE);
            mBubbleView.setAlpha(0f);
            mBubbleAnimator = mBubbleView
                    .animate()
                    .alpha(1f)
                    .setDuration(sBubbleAnimDuration)
                    .setListener(new AnimatorListenerAdapter() {
                        // adapter required for new alpha value to stick
                    });
            mBubbleAnimator.start();
        }
    }

    private void hideBubble() {
        if (Utility.isViewVisible(mBubbleView)) {
            mBubbleAnimator = mBubbleView.animate().alpha(0f)
                    .setDuration(sBubbleAnimDuration)
                    .setListener(new AnimatorListenerAdapter() {

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mBubbleView.setVisibility(View.GONE);
                            mBubbleAnimator = null;
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            super.onAnimationCancel(animation);
                            mBubbleView.setVisibility(View.GONE);
                            mBubbleAnimator = null;
                        }
                    });
            mBubbleAnimator.start();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getX() < mHandleView.getX() - ViewCompat.getPaddingStart(mHandleView)) {
                    return false;
                }
                mHandleView.setSelected(true);
                handler.removeCallbacks(mScrollbarHider);
                Utility.cancelAnimation(mScrollbarAnimator);
                Utility.cancelAnimation(mBubbleAnimator);

                if (!Utility.isViewVisible(mScrollbar) && (recyclerView.computeVerticalScrollRange() - mViewHeight > 0)) {
                    mScrollbarAnimator = Utility.showScrollbar(mScrollbar, Pix.this);
                }

                if (mainImageAdapter != null) {
                    showBubble();
                }

                if (mFastScrollStateChangeListener != null) {
                    mFastScrollStateChangeListener.onFastScrollStart(this);
                }
            case MotionEvent.ACTION_MOVE:
                final float y = event.getRawY();
             /*   String text = mainImageAdapter.getSectionText(recyclerView.getVerticalScrollbarPosition()).trim();
                mBubbleView.setText("hello------>"+text+"<--");
                if (text.equalsIgnoreCase("")) {
                    mBubbleView.setVisibility(View.GONE);
                }
                Log.e("hello"," -->> "+ mBubbleView.getText());*/
                setViewPositions(y - TOPBAR_HEIGHT);
                setRecyclerViewPosition(y);
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mHandleView.setSelected(false);
                if (mHideScrollbar) {
                    handler.postDelayed(mScrollbarHider, sScrollbarHideDelay);
                }
                hideBubble();
                if (mFastScrollStateChangeListener != null) {
                    mFastScrollStateChangeListener.onFastScrollStop(this);
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {

        if (selectionList.size() > 0) {
            try {
                for (Img img : selectionList) {
                    options.setPreSelectedUrls(new ArrayList<String>());
                    mainImageAdapter.getItemList().get(img.getPosition()).setSelected(false);
                    mainImageAdapter.notifyItemChanged(img.getPosition());
                    initaliseadapter.getItemList().get(img.getPosition()).setSelected(false);
                    initaliseadapter.notifyItemChanged(img.getPosition());
                }
            }
            //In case of unsynchronized selectionList, we close the activity to avoid complexity and crash
            catch (IndexOutOfBoundsException e){
                e.printStackTrace();
                super.onBackPressed();
                return;
            }
            LongSelection = false;
            if (options.getCount() > 1) {
                selection_check.setVisibility(View.VISIBLE);
            }
            DrawableCompat.setTint(selection_back.getDrawable(), colorPrimaryDark);
            topbar.setBackgroundColor(Color.parseColor("#ffffff"));
            Animation anim = new ScaleAnimation(
                    1f, 0f, // Start and end values for the X axis scaling
                    1f, 0f, // Start and end values for the Y axis scaling
                    Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                    Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
            anim.setFillAfter(true); // Needed to keep the result of the animation
            anim.setDuration(300);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    sendButton.setVisibility(View.GONE);
                    sendButton.clearAnimation();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            sendButton.startAnimation(anim);
            selectionList.clear();
        } else if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }


}
