package com.hk.simplenewsgong.simplegong;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * this is first activity of the app.
 * set the background accordingly instead of a white background
 * <p></p>
 * Created by simplegong
 */
public class SplashFirstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MainNewsActivity.class);
        startActivity(intent);
        finish();
    }
}
