package com.example.xyzreader.ui.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class ImageFullScreenActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String ARG_TITLE = "title";
    private static final String ARG_AUTHOR = "author";
    private static final String ARG_DATE = "date";
    private static final String ARG_IMAGE_URL = "image_url";
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;
    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };
    private ImageView mImgViewPhoto;
    private ProgressBar mProgressBarLoading;
    private TextView mTxtError;
    private ImageButton mImgBtnClose;
    private FrameLayout mFrameLayoutFullScreen;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mFrameLayoutFullScreen.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private RelativeLayout mRelativeLayoutControls;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            mRelativeLayoutControls.setVisibility(View.VISIBLE);
        }
    };
    private TextView mTxtTitle, mTxtAuthor, mTxtDate;
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    public static void startActivity(Context context,
                                     String title,
                                     String author,
                                     String date,
                                     String imgUrl) {
        Intent intent = new Intent(context, ImageFullScreenActivity.class);
        intent.putExtra(ARG_TITLE, title);
        intent.putExtra(ARG_AUTHOR, author);
        intent.putExtra(ARG_DATE, date);
        intent.putExtra(ARG_IMAGE_URL, imgUrl);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full_screen);
        bindViews();
        init();
        show();
    }

    private void bindViews() {
        mImgViewPhoto = (ImageView) findViewById(R.id.image_view_photo);
        mProgressBarLoading = (ProgressBar) findViewById(R.id.progress_bar_image_loading);
        mTxtError = (TextView) findViewById(R.id.text_view_error);
        mImgBtnClose = (ImageButton) findViewById(R.id.image_button_close);
        mFrameLayoutFullScreen = (FrameLayout) findViewById(R.id.frame_layout_full_screen);
        mRelativeLayoutControls = (RelativeLayout) findViewById(R.id.relative_layout_controls);
        mTxtTitle = (TextView) findViewById(R.id.text_view_title);
        mTxtAuthor = (TextView) findViewById(R.id.text_view_author);
        mTxtDate = (TextView) findViewById(R.id.text_view_date);
    }

    private void init() {
        mImgBtnClose.setOnClickListener(this);
        mFrameLayoutFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        mImgBtnClose.setOnTouchListener(mDelayHideTouchListener);

        Picasso.with(this)
                .load(getIntent().getExtras().getString(ARG_IMAGE_URL))
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        mImgViewPhoto.setImageBitmap(bitmap);
                        mProgressBarLoading.setVisibility(View.GONE);
                        mTxtError.setVisibility(View.GONE);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        mProgressBarLoading.setVisibility(View.GONE);
                        mTxtError.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

        // set other data
        mTxtTitle.setText(getIntent().getExtras().getString(ARG_TITLE));
        mTxtAuthor.setText(getIntent().getExtras().getString(ARG_AUTHOR));

        mTxtDate.setText(DateUtils.getRelativeTimeSpanString(
                Long.parseLong(getIntent().getExtras().getString(ARG_DATE)),
                System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_ALL).toString());

        // set fonts
        mTxtTitle.setTypeface(Typeface.createFromAsset(getResources().getAssets(),
                "Montserrat-Bold.ttf"));
        mTxtAuthor.setTypeface(Typeface.createFromAsset(getResources().getAssets(),
                "Montserrat-Regular.ttf"));
        mTxtDate.setTypeface(Typeface.createFromAsset(getResources().getAssets(),
                "Montserrat-Regular.ttf"));
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        mRelativeLayoutControls.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mFrameLayoutFullScreen.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        switch (viewId) {
            case R.id.image_button_close:
                finish();
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
