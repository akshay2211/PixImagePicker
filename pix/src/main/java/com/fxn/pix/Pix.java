package com.fxn.pix;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
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
import com.fxn.utility.ImageVideoFetcher;
import com.fxn.utility.PermUtil;
import com.fxn.utility.Utility;
import com.fxn.utility.ui.FastScrollStateChangeListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.FileCallback;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;
import com.otaliastudios.cameraview.controls.Facing;
import com.otaliastudios.cameraview.controls.Flash;
import com.otaliastudios.cameraview.controls.Mode;
import com.otaliastudios.cameraview.size.AspectRatio;
import com.otaliastudios.cameraview.size.SizeSelector;
import com.otaliastudios.cameraview.size.SizeSelectors;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Pix extends AppCompatActivity implements View.OnTouchListener {

    private static final int sBubbleAnimDuration = 1000;
    private static final int sScrollbarHideDelay = 1000;
    private static final String OPTIONS = "options";
    private static final int sTrackSnapRange = 5;
    public static String IMAGE_RESULTS = "image_results";
    public static float TOPBAR_HEIGHT;
    private static int maxVideoDuration = 40000;
    private static ImageVideoFetcher imageVideoFetcher;
    private CameraView camera;
    private int status_bar_height = 0;
    private int BottomBarHeight = 0;
    private int colorPrimaryDark;
    private float zoom = 0.0f;
    private float dist = 0.0f;
    private Handler handler = new Handler();
    private Handler video_counter_handler = new Handler();
    private Runnable video_counter_runnable = null;
    private FastScrollStateChangeListener mFastScrollStateChangeListener;
    private RecyclerView recyclerView, instantRecyclerView;
    private BottomSheetBehavior mBottomSheetBehavior;
    private InstantImageAdapter initaliseadapter;
    private View status_bar_bg, mScrollbar, topbar, bottomButtons, sendButton;
    private TextView mBubbleView, img_count;
    private ImageView mHandleView, selection_back, selection_check;
    private ProgressBar video_counter_progressbar = null;
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
    private TextView selection_count;
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
                        if (mScrollbar.getVisibility() != View.VISIBLE) {
                            Utility.cancelAnimation(mScrollbarAnimator);
                            if (!Utility.isViewVisible(mScrollbar) && (recyclerView.computeVerticalScrollRange()
                                    - mViewHeight > 0)) {

                                mScrollbarAnimator = Utility.showScrollbar(mScrollbar, Pix.this);
                            }
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

    private FrameLayout flash;
    private ImageView front;
    private ImageView clickme;
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
                            if (zoom < maxZoom) {
                                zoom = zoom + 0.01f;
                            }
                        } else if ((newDist < dist) && (zoom > 0)) {
                            //zoom out
                            zoom = zoom - 0.01f;
                        }
                        dist = newDist;
                        camera.setZoom(zoom);
                        break;
                    default:
                        break;
                }
            }
            return false;
        }
    };
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
                        Toast.makeText(Pix.this,
                                String.format(getResources().getString(R.string.selection_limiter_pix),
                                        selectionList.size()), Toast.LENGTH_SHORT).show();
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
                selection_count.setText(selectionList.size() + " " +
                        getResources().getString(R.string.pix_selected));
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
                LongSelection = true;
                if ((selectionList.size() == 0) && (mBottomSheetBehavior.getState()
                        != BottomSheetBehavior.STATE_EXPANDED)) {
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
                        Toast.makeText(Pix.this,
                                String.format(getResources().getString(R.string.selection_limiter_pix),
                                        selectionList.size()), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    img.setPosition(position);
                    selectionList.add(img);
                    initaliseadapter.select(true, position);
                    mainImageAdapter.select(true, position);
                }
                selection_check.setVisibility(View.GONE);
                topbar.setBackgroundColor(colorPrimaryDark);
                selection_count.setText(selectionList.size() + " " + getResources().getString(R.string.pix_selected));
                img_count.setText(String.valueOf(selectionList.size()));
                DrawableCompat.setTint(selection_back.getDrawable(), Color.parseColor("#ffffff"));
            }
        }
    };
    private int video_counter_progress = 0;

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
        camera.open();
        camera.setMode(Mode.PICTURE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera.open();
        camera.setMode(Mode.PICTURE);
    }

    @Override
    protected void onPause() {
        camera.close();
        super.onPause();
    }

    private void initialize() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            params.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        Utility.getScreenSize(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        try {
            options = (Options) getIntent().getSerializableExtra(OPTIONS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        maxVideoDuration = options.getVideoDurationLimitinSeconds() * 1000; //conversion in  milli seconds

        ((TextView) findViewById(R.id.message_bottom)).setText(options.isExcludeVideos() ? R.string.pix_bottom_message_without_video : R.string.pix_bottom_message_with_video);
        status_bar_height = Utility.getStatusBarSizePort(Pix.this);
        setRequestedOrientation(options.getScreenOrientation());
        colorPrimaryDark =
                ResourcesCompat.getColor(getResources(), R.color.colorPrimaryPix, getTheme());
        camera = findViewById(R.id.camera_view);
        camera.setMode(Mode.PICTURE);
        SizeSelector width = SizeSelectors.minWidth(Utility.WIDTH);
        SizeSelector height = SizeSelectors.minHeight(Utility.HEIGHT);
        SizeSelector dimensions = SizeSelectors.and(width, height); // Matches sizes bigger than 1000x2000.
        SizeSelector ratio = SizeSelectors.aspectRatio(AspectRatio.of(1, 2), 0); // Matches 1:1 sizes.
        SizeSelector ratio3 = SizeSelectors.aspectRatio(AspectRatio.of(2, 3), 0); // Matches 1:1 sizes.
        SizeSelector ratio2 = SizeSelectors.aspectRatio(AspectRatio.of(9, 16), 0); // Matches 1:1 sizes.
        SizeSelector result = SizeSelectors.or(
                SizeSelectors.and(ratio, dimensions),
                SizeSelectors.and(ratio2, dimensions),
                SizeSelectors.and(ratio3, dimensions)
        );
        camera.setPictureSize(result);
        camera.setVideoSize(result);
        camera.setLifecycleOwner(Pix.this);

        if (options.isFrontfacing()) {
            camera.setFacing(Facing.FRONT);
        } else {
            camera.setFacing(Facing.BACK);
        }

        camera.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(PictureResult result) {
                File dir = new File(Environment.getExternalStorageDirectory(), options.getPath());
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File photo = new File(dir, "IMG_"
                        + new SimpleDateFormat("yyyyMMdd_HHmmSS", Locale.ENGLISH).format(new Date())
                        + ".jpg");

                result.toFile(photo, new FileCallback() {
                    @Override
                    public void onFileReady(@Nullable File photo) {
                        Utility.vibe(Pix.this, 50);
                        Img img = new Img("", "", photo.getAbsolutePath(), "", 1);
                        selectionList.add(img);
                        Utility.scanPhoto(Pix.this, photo);
                        returnObjects();
                    }
                });
            }

            @Override
            public void onVideoTaken(VideoResult result) {
                // A Video was taken!
                Utility.vibe(Pix.this, 50);
                Img img = new Img("", "", result.getFile().getAbsolutePath(), "", 3);
                selectionList.add(img);
                Utility.scanPhoto(Pix.this, result.getFile());
                camera.setMode(Mode.PICTURE);
                returnObjects();
            }

            @Override
            public void onVideoRecordingStart() {
                findViewById(R.id.video_counter_layout).setVisibility(View.VISIBLE);
                video_counter_progress = 0;
                video_counter_progressbar.setProgress(0);
                video_counter_runnable = new Runnable() {
                    @Override
                    public void run() {
                        ++video_counter_progress;
                        video_counter_progressbar.setProgress(video_counter_progress);
                        TextView textView = findViewById(R.id.video_counter);
                        String counter = "";
                        int min = 0;
                        String sec = "" + video_counter_progress;
                        if (video_counter_progress > 59) {
                            min = video_counter_progress / 60;
                            sec = "" + (video_counter_progress - (60 * min));
                        }
                        if (sec.length() == 1) {
                            sec = "0" + sec;
                        }
                        counter = min + ":" + sec;
                        textView.setText(counter);
                        video_counter_handler.postDelayed(this, 1000);
                    }
                };
                video_counter_handler.postDelayed(video_counter_runnable, 1000);
                clickme.animate().scaleX(1.2f).scaleY(1.2f).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                flash.animate().alpha(0).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                findViewById(R.id.message_bottom).animate().alpha(0).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                front.animate().alpha(0).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
            }

            @Override
            public void onVideoRecordingEnd() {
                findViewById(R.id.video_counter_layout).setVisibility(View.GONE);
                video_counter_handler.removeCallbacks(video_counter_runnable);
                clickme.animate().scaleX(1f).scaleY(1f).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                findViewById(R.id.message_bottom).animate().scaleX(1f).scaleY(1f).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                flash.animate().alpha(1).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                front.animate().alpha(1).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
            }
            // And much more
        });
        zoom = 0.0f;
        flash = findViewById(R.id.flash);
        clickme = findViewById(R.id.clickme);
        front = findViewById(R.id.front);
        topbar = findViewById(R.id.topbar);
        video_counter_progressbar = findViewById(R.id.video_pbr);
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
        status_bar_bg.getLayoutParams().height = status_bar_height;
        status_bar_bg.setTranslationY(-1 * status_bar_height);
        status_bar_bg.requestLayout();
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
        CoordinatorLayout main_content = findViewById(R.id.main_content);
        BottomBarHeight = Utility.getSoftButtonsBarSizePort(this);
        FrameLayout.LayoutParams lp1 =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT);
        lp1.setMargins(0, status_bar_height, 0, 0);
        main_content.setLayoutParams(lp1);
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
        clickme.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    findViewById(R.id.clickmebg).setVisibility(View.GONE);
                    findViewById(R.id.clickmebg).animate().scaleX(1f).scaleY(1f).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                    clickme.animate().scaleX(1f).scaleY(1f).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    findViewById(R.id.clickmebg).setVisibility(View.VISIBLE);
                    findViewById(R.id.clickmebg).animate().scaleX(1.2f).scaleY(1.2f).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                    clickme.animate().scaleX(1.2f).scaleY(1.2f).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                }
                if (event.getAction() == MotionEvent.ACTION_UP && camera.isTakingVideo()) {
                    camera.stopVideo();
                }

                return false;
            }
        });
        clickme.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (options.isExcludeVideos()) {
                    return false;
                }
                camera.setMode(Mode.VIDEO);
                File dir = new File(Environment.getExternalStorageDirectory(), options.getPath());
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File video = new File(dir, "VID_"
                        + new SimpleDateFormat("yyyyMMdd_HHmmSS", Locale.ENGLISH).format(new Date())
                        + ".mp4");
                video_counter_progressbar.setMax(maxVideoDuration / 1000);
                video_counter_progressbar.invalidate();
                camera.takeVideo(video, maxVideoDuration);
                return true;
            }
        });
        clickme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectionList.size() >= options.getCount()) {
                    Toast.makeText(Pix.this,
                            String.format(getResources().getString(R.string.cannot_click_image_pix),
                                    "" + options.getCount()), Toast.LENGTH_LONG).show();
                    return;
                }
                final ObjectAnimator oj = ObjectAnimator.ofFloat(camera, "alpha", 1f, 0f, 0f, 1f);
                oj.setStartDelay(200l);
                oj.setDuration(600l);
                oj.start();
                camera.takePicture();
                return;
            }
        });
        findViewById(R.id.selection_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnObjects();
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                iv.animate()
                        .translationY(height)
                        .setDuration(100)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                iv.setTranslationY(-(height / 2));
                                if (flashDrawable == R.drawable.ic_flash_auto_black_24dp) {
                                    flashDrawable = R.drawable.ic_flash_off_black_24dp;
                                    iv.setImageResource(flashDrawable);
                                    camera.setFlash(Flash.OFF);
                                } else if (flashDrawable == R.drawable.ic_flash_off_black_24dp) {
                                    flashDrawable = R.drawable.ic_flash_on_black_24dp;
                                    iv.setImageResource(flashDrawable);
                                    camera.setFlash(Flash.ON);
                                } else {
                                    flashDrawable = R.drawable.ic_flash_auto_black_24dp;
                                    iv.setImageResource(flashDrawable);
                                    camera.setFlash(Flash.AUTO);
                                }
                                iv.animate().translationY(0).setDuration(50).setListener(null).start();
                            }
                        })
                        .start();
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
                    camera.setFacing(Facing.BACK);
                } else {
                    camera.setFacing(Facing.FRONT);
                    options.setFrontfacing(true);
                }
            }
        });
    }

    private void updateImages() {
        mainImageAdapter.clearList();
        Cursor cursor = Utility.getImageVideoCursor(Pix.this, options.isExcludeVideos());
        if (cursor == null) {
            return;
        }
        ArrayList<Img> INSTANTLIST = new ArrayList<>();
        String header = "";
        int limit = 100;
        if (cursor.getCount() < limit) {
            limit = cursor.getCount() - 1;
        }
        int data = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
        int mediaType = cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE);
        int contentUrl = cursor.getColumnIndex(MediaStore.Files.FileColumns._ID);
        //int videoDate = cursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN);
        int imageDate = cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED);
        Calendar calendar;
        int pos = 0;
        for (int i = 0; i < limit; i++) {
            cursor.moveToNext();
            Uri path = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "" + cursor.getInt(contentUrl));
            calendar = Calendar.getInstance();
            int finDate = imageDate; // mediaType == 1 ? imageDate : videoDate;
            calendar.setTimeInMillis(cursor.getLong(finDate) * 1000);
            //Log.e("time",i+"->"+new SimpleDateFormat("hh:mm:ss dd/MM/yyyy",Locale.ENGLISH).format(calendar.getTime()));
            String dateDifference = Utility.getDateDifference(Pix.this, calendar);
            if (!header.equalsIgnoreCase("" + dateDifference)) {
                header = "" + dateDifference;
                pos += 1;
                INSTANTLIST.add(new Img("" + dateDifference, "", "", "", cursor.getInt(mediaType)));
            }
            Img img =
                    new Img("" + header, "" + path, cursor.getString(data), "" + pos, cursor.getInt(mediaType));
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
            selection_count.setText(selectionList.size() + " " +
                    getResources().getString(R.string.pix_selected));
            img_count.setText(String.valueOf(selectionList.size()));
            DrawableCompat.setTint(selection_back.getDrawable(), Color.parseColor("#ffffff"));
        }
        mainImageAdapter.addImageList(INSTANTLIST);
        initaliseadapter.addImageList(INSTANTLIST);
        imageVideoFetcher = new ImageVideoFetcher(Pix.this) {
            @Override
            protected void onPostExecute(ModelList modelList) {
                super.onPostExecute(modelList);
                mainImageAdapter.addImageList(modelList.getLIST());
                initaliseadapter.addImageList(modelList.getLIST());
                selectionList.addAll(modelList.getSelection());
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
                    selection_count.setText(selectionList.size() + " " +
                            getResources().getString(R.string.pix_selected));
                    img_count.setText(String.valueOf(selectionList.size()));
                    DrawableCompat.setTint(selection_back.getDrawable(), Color.parseColor("#ffffff"));
                }
            }
        };

        imageVideoFetcher.setStartingCount(pos);
        imageVideoFetcher.header = header;
        imageVideoFetcher.setPreSelectedUrls(options.getPreSelectedUrls());
        imageVideoFetcher.execute(Utility.getImageVideoCursor(Pix.this, options.isExcludeVideos()));
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
                Utility.manipulateVisibility(Pix.this, slideOffset, findViewById(R.id.arrow_up),
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
                    camera.open();
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
        int handleY = Utility.getValueInRange(0, (int) (mViewHeight - mHandleView.getHeight()),
                (int) (y - mHandleView.getHeight() / 2));
        mBubbleView.setY(handleY + Utility.convertDpToPixel(60, this));
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

                if (!Utility.isViewVisible(mScrollbar) && (recyclerView.computeVerticalScrollRange()
                        - mViewHeight > 0)) {
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
            for (Img img : selectionList) {
                options.setPreSelectedUrls(new ArrayList<String>());
                mainImageAdapter.getItemList().get(img.getPosition()).setSelected(false);
                mainImageAdapter.notifyItemChanged(img.getPosition());
                initaliseadapter.getItemList().get(img.getPosition()).setSelected(false);
                initaliseadapter.notifyItemChanged(img.getPosition());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        camera.destroy();
    }
}
