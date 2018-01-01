package com.hk.simplenewsgong.simplegong;

/**
 * this class will make the scrolling slower if user fling too fast
 * <p></p>
 * Created by simplegong
 */

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

public class SlowdownRecyclerView extends RecyclerView {
    private final String TAG = SlowdownRecyclerView.class.getSimpleName();

    public SlowdownRecyclerView(Context context) {
        super(context);
    }

    public SlowdownRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SlowdownRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        // if FLING_SPEED_FACTOR between [0, 1[ => slowdown
        Log.d(TAG, "=========== fling velocityX=" + velocityX + ", velociityY=" + velocityY);
        if (Math.abs(velocityY) > 7000) {
            //it is too fast , slow down the speeed by half
            velocityY *= 0.5;
            Log.d(TAG, "=========== fling slowdown velocityX=" + velocityX + ", velociityY=" + velocityY);
        }
        return super.fling(velocityX, velocityY);
    }
}

