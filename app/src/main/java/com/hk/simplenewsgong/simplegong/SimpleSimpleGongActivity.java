package com.hk.simplenewsgong.simplegong;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hk.simplenewsgong.simplegong.data.FragArticleTableContract;
import com.hk.simplenewsgong.simplegong.sync.Gongdispatch;
import com.hk.simplenewsgong.simplegong.util.BottomNavigationViewHelper;

/**
 * this activity show a grid layout of entity name
 * <p></p>
 * Created by simplegong
 */
public class SimpleSimpleGongActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        SimpleGongAdapter.SimpleGongAdapterOnClickHandler {


    private final String TAG = SimpleSimpleGongActivity.class.getSimpleName();

    //loader id
    private static final int ID_GONGINFOLOOKUP_ALLNAME_LOADER = 401;
    //adapter for this activity
    private SimpleGongAdapter mSimpleGongAdapter;

    //reference to recyclerview
    private RecyclerView mRecyclerView;
    //indicator for stage of initialization
    private int mPosition = RecyclerView.NO_POSITION;
    //reference to the bottom nvaigationview
    private BottomNavigationView mBottomNavigationView;

    //reference to loading indicator
    private ProgressBar mLoadingIndicator;

    //reference to image view of news logo
    private ImageView mIVSimpleGongLogo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simplegong);
        Log.d(TAG, " SimpleSimpleGongActivity oncreate 1");

        Intent intent = getIntent();


        //configure the bottom navigation view , set it to appropriate icon
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_mainnews_navigation);
        BottomNavigationViewHelper.selection(this, mBottomNavigationView, R.id.nav_simplegong);
        Menu menu = mBottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);


        //configure recyclerview
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_slowdown);
        GridLayoutManager layoutManager =
                new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mSimpleGongAdapter = new SimpleGongAdapter(this,
                this);
        mRecyclerView.setAdapter(mSimpleGongAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);


        //set up newslogo and do the animation
        mIVSimpleGongLogo = (ImageView) findViewById(R.id.simplegong_logo_image);
        mIVSimpleGongLogo.animate().setStartDelay(500).rotation(360).setDuration(1000);


        //init the loader
        getSupportLoaderManager().initLoader(ID_GONGINFOLOOKUP_ALLNAME_LOADER, null, this);

        Log.d(TAG, " SimpleSimpleGongActivity oncreate 2");
    }


    /**
     * show the loading indicator and hide recyclerview
     */
    private void showLoading() {
        Log.d(TAG, " showloading ");
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Finally, show the loading indicator */
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    /**
     * hide the loading indicator and show recyclerview
     */
    private void showDataView() {
        /* First, hide the loading indicator */
        Log.d(TAG, " showdatashow ");
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        mRecyclerView.setVisibility(View.VISIBLE);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "----> oncreateloader ");
        switch (id) {
            case ID_GONGINFOLOOKUP_ALLNAME_LOADER:
                //load a list of entity
                Log.d(TAG, "----> oncreateloader 2");

                return new CursorLoader(this,
                        FragArticleTableContract.FragArticleEntry.CONTENT_URI_ENTITY_NAMELIST,
                        null,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "  onloadfinished 1 ");

        switch (loader.getId()) {
            case ID_GONGINFOLOOKUP_ALLNAME_LOADER:
                if ((data != null) && (data.getCount() > 0)) {
                    //there are data, pass to adapter and display it
                    Log.d(TAG, " ID_GONGINFOLOOKUP_ALLNAME_LOADER getcount>0 =" + data.getCount());
                    mSimpleGongAdapter.swapCursor(data);
                    showDataView();

                } else {
                    //does not have data
                    showLoading();
                    if (!Gongdispatch.isOnline(this)) {
                        Toast.makeText(this, R.string.check_network_setting, Toast.LENGTH_LONG).show();

                    }
                    Log.d(TAG, "  onloadinfished ID_GONGINFOLOOKUP_ALLNAME_LOADER getcount=0");
                }
                Log.d(TAG, " ID_GONGINFOLOOKUP_ALLNAME_LOADER =" + data.getCount());

                break;

            default:
                Log.d(TAG, "  onloadfinished 4 loaderid=" + loader.getId());
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "----> onloaderreset ");

    }

    /**
     * when user click on individual entity on the grid layout, invokes article list of selected
     * entity
     *
     * @param title : entity name
     * @param id    : entity id
     */
    @Override
    public void onClickIndEntity(String title, int id) {
        Log.d(TAG, " --> onClickIndEntity title= " + title + ",id=" + id);
        Intent intent = new Intent(this, SimpleGongDetailActivity.class);
        intent.putExtra(SimpleGongDetailActivity.ENTITY_NAME, title);
        intent.putExtra(SimpleGongDetailActivity.ENTITY_ID, id);
        startActivity(intent);

    }


}


