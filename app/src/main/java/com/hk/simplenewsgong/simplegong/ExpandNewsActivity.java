package com.hk.simplenewsgong.simplegong;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hk.simplenewsgong.simplegong.data.SignalContract;
import com.hk.simplenewsgong.simplegong.sync.Gongdispatch;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * a activity show a list of related articles
 * <p></p>
 * <p>
 * Created by simplegong
 */

public class ExpandNewsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ListIndNewsAdapter.ListIndNewsAdapterOnClickHandler {
    private final String TAG = ExpandNewsActivity.class.getSimpleName();

    public static final String JSONARTICLELISTSTR = "jsonarticleliststr";
    public static final String JSONSIGNALBITSTR = "jsonsignalbitstr";

    //loader id for signal table
    private static final int ID_SIGNAL_LOADER = 202;

    //adapter for display list of article
    private ListIndNewsAdapter mListIndNewsAdapter;

    //reference for recyclerview
    private SlowdownRecyclerView mRecyclerView;


    //json string of article list from pass-in intent
    private String mJSONArticleliststr;
    //json string of article signal list from pass-in intent
    private String mJSONSignalbitstr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expand_news);

        //getting passed in intent information
        Intent intent = getIntent();
        mJSONArticleliststr = intent.getStringExtra(JSONARTICLELISTSTR);
        mJSONSignalbitstr = intent.getStringExtra(JSONSIGNALBITSTR);

        //setup the recyclerview
        mRecyclerView = (SlowdownRecyclerView) findViewById(R.id.recyclerview_slowdown);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mListIndNewsAdapter = new ListIndNewsAdapter(this,
                this, mJSONArticleliststr, mJSONSignalbitstr);
        mRecyclerView.setAdapter(mListIndNewsAdapter);


        //configure the actionbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.more_detail);

        //start the loader
        getSupportLoaderManager().initLoader(ID_SIGNAL_LOADER, null, this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, " onoptionsitemselected 1 " + item.toString());
        switch (item.getItemId()) {
            case android.R.id.home:
                //go back to previous activity
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "----> oncreateloader ");
        switch (id) {
            case ID_SIGNAL_LOADER:
                //create the signal loader
                Uri signalQueryUri = SignalContract.SignalEntry.CONTENT_URI;
                String signalsortOrder = SignalContract.SignalEntry.COLUMN_ARTICLE_ID + " ASC";
                Log.d(TAG, "----> oncreateloader 3");

                return new CursorLoader(this,
                        signalQueryUri,
                        SignalContract.PROJECTION,
                        null,
                        null,
                        signalsortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "  onloadfinished 1 ");

        switch (loader.getId()) {
            case ID_SIGNAL_LOADER:
                //swap the new cursor information in adapter
                mListIndNewsAdapter.updateSignalMapFromCursor(data);
                Log.d(TAG, "  onloadfinished 9 loaderid=" + loader.getId());
                break;

            default:
                Log.d(TAG, "  onloadfinished 10 loaderid=" + loader.getId());
                break;
        }
        data.close();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "----> onloaderreset ");

    }

    /**
     * this method will start the detail news activity when the user click on individual article
     * view item
     *
     * @param entryID  : detail news activity retrieve the row from database (not used at this moment)
     * @param finalurl : let the webview in detail news activity to load the specific url
     */
    @Override
    public void onClickListIndNews(long entryID, String finalurl) {
        Log.d(TAG, "  --> onClickLatestNewsPagination 1 entryID=" + entryID);
        Intent intent = new Intent(this, DetailNewsActivity.class);
        intent.putExtra(DetailNewsActivity.FINALURL, finalurl);
        startActivity(intent);
        Log.d(TAG, "  --> onClickLatestNewsPagination 2 entryID=" + entryID);

    }

    /**
     * when the user click the bookmark icon, retrieve the necessary data ahead or not
     *
     * @param entryID : gongdispatch will use this entryID to find out whether there is one in local
     *                database.  If "save" is true, go get it. Otherwise, remove it
     * @param save    : if it is true, store the information.  Otherwise, remove it
     */
    @Override
    public void onClickBookmarkArticleStoreOrRemove(long entryID, boolean save) {
        if (save) {
            if (Gongdispatch.isOnline(this)) {
            } else {
                Toast.makeText(this, R.string.check_network_setting, Toast.LENGTH_LONG).show();
            }
            Log.d(TAG, "newsfragment --> onClickBookmarkArticleStoreOrRemove 3 ");

        } else {
            //remove

        }

    }

}
