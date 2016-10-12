package com.example.xyzreader.utils;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

/**
 * Author: Kartik Sharma
 * Created on: 10/9/2016 , 12:02 PM
 * Project: xyzreader
 */

public class AnimUtil {
    public static void fadeOutFadeInSimultaneously(ViewGroup viewGroup,
                                                   Runnable runnable,
                                                   int fadeOutDurationInMillis,
                                                   int fadeInDurationInMillis) {
        //fade out
        viewGroup.setVisibility(View.VISIBLE);
        AlphaAnimation alphaAnimationFadeOut = new AlphaAnimation(1.0f, 0f);
        alphaAnimationFadeOut.setDuration(fadeOutDurationInMillis);
        viewGroup.startAnimation(alphaAnimationFadeOut);
        viewGroup.setVisibility(View.INVISIBLE);

        runnable.run();

        // fade in
        viewGroup.setVisibility(View.INVISIBLE);
        AlphaAnimation alphaAnimationFadeIn = new AlphaAnimation(0f, 1.0f);
        alphaAnimationFadeIn.setDuration(fadeInDurationInMillis);
        viewGroup.startAnimation(alphaAnimationFadeIn);
        viewGroup.setVisibility(View.VISIBLE);
    }
}
