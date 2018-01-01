package com.hk.simplenewsgong.simplegong;

import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;

import com.hk.simplenewsgong.simplegong.data.GongProvider;
import com.hk.simplenewsgong.simplegong.data.SignalContract;
import com.hk.simplenewsgong.simplegong.data.SourceInfo;
import com.hk.simplenewsgong.simplegong.sync.Gongdispatch;
import com.hk.simplenewsgong.simplegong.util.GongTimeUtil;

//import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

/**
 * Created by simplegong on 9/9/2017.
 */
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
//import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * in this simple app, this activity show webview
 * <p>
 * Created by simplegong
 */
public class DetailNewsActivity extends AppCompatActivity {

    public static final String FINALURL = "finalurl";
    private static final String TAG = DetailNewsActivity.class.getSimpleName();

    //holding the url for loading webview
    private String mFinalurl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_news);

        Intent intent = getIntent();
        mFinalurl = intent.getStringExtra(FINALURL);


        //configure webview setting
        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.getSettings().setBuiltInZoomControls(true);
        myWebView.getSettings().setDisplayZoomControls(true);
        myWebView.getSettings().setAllowFileAccess(false);
        myWebView.getSettings().setDatabaseEnabled(false);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.loadUrl(mFinalurl);

        TextView textView = (TextView) findViewById(R.id.webview_not_connected);
        if (Gongdispatch.isOnline(this)) {
            myWebView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);

        } else {
            myWebView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);

        }
        Log.d(TAG, " mywebview.loadurl = " + mFinalurl);

    }


}





