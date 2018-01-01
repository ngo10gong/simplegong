package com.hk.simplenewsgong.simplegong;

import android.content.Context;
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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hk.simplenewsgong.simplegong.data.FragArticleTableContract;
import com.hk.simplenewsgong.simplegong.data.SignalContract;
import com.hk.simplenewsgong.simplegong.sync.Gongdispatch;
import com.hk.simplenewsgong.simplegong.util.FragInfo;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;

/**
 * After User click individual entity in SimpleSimpleGongActivity , this activity responsible
 * for showing a list of articles related to the selected entity
 * <p></p>
 * Created by simplegong
 */

public class SimpleGongDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        FirstLevelNewsAdapter.FirstLevelNewsAdapterOnClickHandler {

    private final String TAG = SimpleGongDetailActivity.class.getSimpleName();

    // loader id
    private static final int ID_SIGNAL_LOADER = 603;
    private static final int ID_GONGINFO_NAME_NAME_LOADER = 604;

    // reference to adapter to display a list of entity's related article
    private FirstLevelNewsAdapter mFirstLevelNewsAdapter;

    // reference to recyclerview
    private SlowdownRecyclerView mRecyclerView;
    //indicator of initial stage
    private int mPosition = RecyclerView.NO_POSITION;

    //reference to loading indicator
    private ProgressBar mLoadingIndicator;

    // indicate the activity is in progress to load for data
    private boolean mLoadingMore = false;

    //reference to the swipe refresh layout
    private SwipeRefreshLayout mSwipeRefreshLayout;
    // indicate whether refreshing is in progress
    private boolean mRefreshingLayout = false;
    //reference to current context and pass to other listener
    private Context mCurrentContext;

    //tag name for storing information
    public final static String ENTITY_NAME = "entity_name";
    public final static String ENTITY_ID = "entity_id";
    private String mEntity_name;
    private int mEntity_id;

    // reference to the textview of showing "no news" content
    private TextView mNoNewsHimHer;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simplegong_detail);

        //get the information from intent
        Intent intent = getIntent();
        mEntity_name = intent.getStringExtra(ENTITY_NAME);
        mEntity_id = intent.getIntExtra(ENTITY_ID, 0);

        Log.d(TAG, " oncreate 0 name=" + mEntity_name + ",id=" + mEntity_id);
        mCurrentContext = this;
        Log.d(TAG, "oncreate 1 ");

        // configure recyclerview
        mRecyclerView = (SlowdownRecyclerView) findViewById(R.id.recyclerview_slowdown);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(false);
        mFirstLevelNewsAdapter = new FirstLevelNewsAdapter(this, this);
        mRecyclerView.setAdapter(mFirstLevelNewsAdapter);

        Log.d(TAG, "oncreateview 2 ");


        //assign onscrolllistener which help to determine whether load more data or not
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d(TAG, "-->  " + " onScrollStateChanged idle=" + SCROLL_STATE_IDLE
                        + ",settling=" + SCROLL_STATE_SETTLING + ",dragging=" +
                        SCROLL_STATE_DRAGGING + ",newState=" + newState);
                //total item for displaying
                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                //position value of last visible item on screen
                int findLastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                        .findLastVisibleItemPosition();
                Log.d(TAG, "---->  " + " onscrollstatechanged " +
                        ",totalItemCount=" + totalItemCount +
                        ",findLastVisibleItemPosition=" + findLastVisibleItemPosition
                );

                //if it is not yet start loading more data and the total items have been displayed
                //larger than a ratio and user still dragging the recycler view , go get new data
                if ((mLoadingMore == false)
                        && (newState == SCROLL_STATE_DRAGGING)
                        && (((float) findLastVisibleItemPosition / totalItemCount) > 0.4)
                        ) {
                    //check device is online or not
                    if (Gongdispatch.isOnline(mCurrentContext)) {

                        //get more data
                        mLoadingMore = Gongdispatch.retrieveSheetAndNameCat(
                                FragArticleTableContract.buildUriWithNameEntries(mEntity_name),
                                mCurrentContext,
                                FragInfo.getInstance().get_tablistFragTableName(0),
                                FragInfo.getInstance().get_category(FragInfo.getInstance().get_tablistFragTableName(0))
                        );
                        Log.d(TAG, "  " + FragInfo.getInstance().get_tablistname(0)
                                + " onscrollstatechanged 8 mFragPageNumber=" + FragInfo.getInstance().get_tablistname(0));

                        Log.d(TAG, "  " + FragInfo.getInstance().get_tablistname(0)
                                + " onscrollstatechanged 9 mFragPageNumber=" + FragInfo.getInstance().get_tablistname(0));
                    } else {
                        //it is offline
                        Toast.makeText(mCurrentContext, R.string.check_network_setting, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });


        //set up the swipe refresh layout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setDistanceToTriggerSync(32);
        mSwipeRefreshLayout.setNestedScrollingEnabled(true);
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.d(TAG, "onRefresh called from SwipeRefreshLayout 1 ");
                        if (Gongdispatch.isOnline(mCurrentContext)) {
                            //if it is online, get more data
                            mLoadingMore = Gongdispatch.retrieveSheetAndNameCat(
                                    FragArticleTableContract.buildUriWithNameEntries(mEntity_name),
                                    mCurrentContext,
                                    FragInfo.getInstance().get_tablistFragTableName(0),
                                    FragInfo.getInstance().get_category(FragInfo.getInstance().get_tablistFragTableName(0))
                            );
                            mRefreshingLayout = true;
                        } else {
                            // it is offline
                            Log.d(TAG, "  " + FragInfo.getInstance().get_tablistname(0) + " onRefresh called from SwipeRefreshLayout 3");
                            Toast.makeText(mCurrentContext, R.string.check_network_setting, Toast.LENGTH_LONG).show();
                            mSwipeRefreshLayout.setRefreshing(false);
                            mRefreshingLayout = false;
                        }

                    }
                }
        );

        //assign the name to the textview for showing
        TextView sourcetitle = (TextView) findViewById(R.id.sourcetitle);
        sourcetitle.setText(mEntity_name);

        mNoNewsHimHer = (TextView) findViewById(R.id.tv_no_news_for_him_her);

        Log.d(TAG, "oncreateview 4 ");
        getSupportLoaderManager().initLoader(ID_GONGINFO_NAME_NAME_LOADER, null, this);
        getSupportLoaderManager().initLoader(ID_SIGNAL_LOADER, null, this);

        Log.d(TAG, "oncreateview 5 ");
    }

    /**
     * show the loading indicator and hide recyclerview
     */
    private void showLoading() {
        Log.d(TAG, " showloading ");
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Finally, show the loading indicator */
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mNoNewsHimHer.setVisibility(View.INVISIBLE);
    }

    /**
     * hide the loading indicator and show recyclerview
     */
    private void showDataView() {
        /* First, hide the loading indicator */
        Log.d(TAG, " showdatashow ");
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mNoNewsHimHer.setVisibility(View.INVISIBLE);
    }

    /**
     * there are no news for this entity
     */
    private void showNoNewsView() {
        /* First, hide the loading indicator */
        Log.d(TAG, " showNoNewsView ");
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mNoNewsHimHer.setVisibility(View.VISIBLE);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "----> oncreateloader ");
        switch (id) {
            case ID_GONGINFO_NAME_NAME_LOADER:
                //load all the current entity related articles
                Uri gonginfoQueryUri = FragArticleTableContract.buildUriWithNameEntries(mEntity_name);
                Log.d(TAG, "----> oncreateloader 1");

                return new CursorLoader(this,
                        gonginfoQueryUri,
                        null,
                        null,
                        null,
                        null);

            case ID_SIGNAL_LOADER:
                //load the signal map which indicate whether the article is read/bookmarked already
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
        Log.d(TAG, " simplegongdetailactivity  onloadfinished 1 ");

        switch (loader.getId()) {
            case ID_GONGINFO_NAME_NAME_LOADER:
                Log.d(TAG, "  ID_GONGINFO_NAME_NAME_LOADER onloadfinished 1.1 ");

                if ((data == null) || ((data != null) && (data.getCount() == 0))) {
                    if (((data != null) && (data.getCount() == 0)) && (mPosition == 0)) {
                        //it is really no data to show
                        Log.d(TAG, " ID_GONGINFO_NAME_NAME_LOADER onloadfinished 1.1.1 ");
                        mFirstLevelNewsAdapter.swapCursor(null, false);
                        showNoNewsView();
                        mRefreshingLayout = false;

                        break;

                    }
                    //go get data
                    showLoading();
                    if (Gongdispatch.isOnline(this)) {
                        //it's online
                        Log.d(TAG, " ID_GONGINFO_NAME_NAME_LOADER onloadfinished 1.3 ");
                        //go retrieve data
                        Gongdispatch.retrieveSheetAndNameCat(
                                FragArticleTableContract.buildUriWithNameEntries(mEntity_name),
                                mCurrentContext,
                                FragInfo.getInstance().get_tablistFragTableName(0),
                                FragInfo.getInstance().get_category(FragInfo.getInstance().get_tablistFragTableName(0))
                        );

                        mRefreshingLayout = true;
                        mPosition = 0;

                    } else {
                        //it's offline
                        Log.d(TAG, " onloadfinished ID_GONGINFO_NAME_NAME_LOADER 1.5");
                        Toast.makeText(mCurrentContext, R.string.check_network_setting, Toast.LENGTH_LONG).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                        mRefreshingLayout = false;
                    }

                } else {
                    if (mRefreshingLayout == true) {
                        //reset the refreshing layout
                        mRefreshingLayout = false;
                        mSwipeRefreshLayout.setRefreshing(false);
                        Log.d(TAG, "  onloadfinished ID_GONGINFO_NAME_NAME_LOADER 2 ");

                    }
                    Log.d(TAG, " onloadfinished ID_GONGINFO_NAME_NAME_LOADER 3 ");

                    //pass data(cursor) to adapter
                    mFirstLevelNewsAdapter.swapCursor(data, false);
                    mLoadingMore = false;
                    data.moveToPosition(0);
                    GlideApp.with(this)
                            .load(data.getString(FragArticleTableContract.INDEX_RAWQUERY_ENTITYICONURL))
                            .placeholder(R.drawable.ic_tmp_icon)
                            .centerCrop()
                            .into((ImageView) findViewById(R.id.entity_image));

                    Log.d(TAG, "  onloadfinished ID_GONGINFO_NAME_NAME_LOADER 4 ");

                    if (mPosition == RecyclerView.NO_POSITION) {
                        //it means there are some data in the database
                        //check whether there are more new news
                        Gongdispatch.retrieveSheetAndNameCat(
                                FragArticleTableContract.buildUriWithNameEntries(mEntity_name),
                                mCurrentContext,
                                FragInfo.getInstance().get_tablistFragTableName(0),
                                FragInfo.getInstance().get_category(FragInfo.getInstance().get_tablistFragTableName(0))
                        );

                        mPosition = 0;
                    } else if (mPosition == 0) {
                        //okay, scroll to the top and display it
                        mPosition = data.getCount();
                        mRecyclerView.smoothScrollToPosition(0);

                    }
                    Log.d(TAG, "  onloadfinished ID_GONGINFO_NAME_NAME_LOADER 5 data.getcount=" + data.getCount());
                    if (data.getCount() != 0) showDataView();
                }

                break;

            case ID_SIGNAL_LOADER:
                //update information in adapter
                mFirstLevelNewsAdapter.updateSignalMapFromCursor(data);
                Log.d(TAG, " simplegongdetailactivity onloadfinished 9 loaderid=" + loader.getId());
                break;

            default:
                Log.d(TAG, " simplegongdetailactivity onloadfinished 10 loaderid=" + loader.getId());
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "----> onloaderreset 1 loader.getid=" + loader.getId());
        if (loader.getId() == ID_GONGINFO_NAME_NAME_LOADER) {
            Log.d(TAG, "----> onloaderreset 2 loader.getid=" + loader.getId());
            mFirstLevelNewsAdapter.swapCursor(null, false);
        }
    }

    /**
     * this method will start the detail news activity when the user click on individual article
     * view item
     *
     * @param entryID  : detail news activity retrieve the row from database (not used at this moment)
     * @param finalurl : let the webview in detail news activity to load the specific url
     */
    @Override
    public void onClickDetailNews(long entryID, String finalurl) {
        Intent intent = new Intent(this, DetailNewsActivity.class);
        intent.putExtra(DetailNewsActivity.FINALURL, finalurl);
        startActivity(intent);
        Log.d(TAG, " --> onClickDetailNews 3 ");


    }

    /**
     * user has clicked the morenews icon, start the expandnewsactivity
     *
     * @param jsonArticleList : json string that contain a list of related articles to
     *                        the current selected article
     * @param jsonSignalbit   : json string that includes related signal information
     */
    @Override
    public void onClickExpandNews(String jsonArticleList, String jsonSignalbit) {
        Intent intent = new Intent(this, ExpandNewsActivity.class);
        intent.putExtra(ExpandNewsActivity.JSONARTICLELISTSTR, jsonArticleList);
        intent.putExtra(ExpandNewsActivity.JSONSIGNALBITSTR, jsonSignalbit);
        startActivity(intent);
        Log.d(TAG, " --> onClickExpandNews 1 ");

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
                //go fetch the content

            } else {
                Toast.makeText(this, R.string.check_network_setting, Toast.LENGTH_LONG).show();
            }
            Log.d(TAG, " -->   onClickBookmarkArticleStoreOrRemove 3 ");
        } else {
            //remove
            Log.d(TAG, " --> onClickBookmarkArticleStoreOrRemove 4 ");
        }
    }

}
