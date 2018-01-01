package com.hk.simplenewsgong.simplegong;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hk.simplenewsgong.simplegong.data.FragArticleTableContract;
import com.hk.simplenewsgong.simplegong.data.GongPreference;
import com.hk.simplenewsgong.simplegong.data.SignalContract;
import com.hk.simplenewsgong.simplegong.sync.Gongdispatch;
import com.hk.simplenewsgong.simplegong.util.FragInfo;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;
import static com.hk.simplenewsgong.simplegong.NewsFragmentPagerAdapter.FRAGMENT_NAME;

/**
 * this fragment class will host different categories news article list
 * <p>
 * Created by simplegong
 */
public class ContainerFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        FirstLevelNewsAdapter.FirstLevelNewsAdapterOnClickHandler,
        NewsFragmentPagerAdapter.TalkToIndividualFragment {


    private final String TAG = ContainerFragment.class.getSimpleName();

    private int ID_SIGNAL_LOADER; //load article signal information
    private int ID_FRAGARTICLE_TABLE_LOADER; //load all of domains in this category name/id
    private int ID_FRAGARTICLE_TABLE_CATEGORY_LOADER; //load specific domains in this category/id

    //adapter to put the right information to the corresponding view
    private FirstLevelNewsAdapter mFirstLevelNewsAdapter;

    //reference to the recyclerview
    private SlowdownRecyclerView mRecyclerView;

    //NO_POSITION = initial state ,
    // =0 after first check localdatabase, "going" to get remote data
    private int mPosition = RecyclerView.NO_POSITION;
    //NO_POSITION = initial state ,
    // =0 after first check localdatabase, "going" to get remote data
    private int mPosition_archive = RecyclerView.NO_POSITION;

    //reference to loading indicator
    private ProgressBar mLoadingIndicator;

    //indicate the fragment is in loading more state
    private boolean mLoadingMore = false;

    //reference to swiperefreshlayout for refreshing
    private SwipeRefreshLayout mSwipeRefreshLayout;

    //indicator whether recyclerview is refreshing or not
    private boolean mRefreshingLayout = false;

    //indicate how many page has been loaded
    private int mFragPageNumber = 0;

    //key name of preferred list
    public final String PREFERREDLISTINTARRAY = "preferredlistarray";

    //reference to the hosting activity
    private MainNewsActivity mMainNewsActivity;

    //fragment name which use to identify category
    private String mFragment_name;


    /**
     * empty constructor
     */
    public ContainerFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //get the root view to display the article list
        View rootView = inflater.inflate(R.layout.article_list, container, false);

        //get the fragment name information from the passedin bundle
        Bundle passInBundle = getArguments();
        mFragment_name = passInBundle.getString(FRAGMENT_NAME);

        //initial the loader id value
        int startloaderid = FragInfo.getInstance().get_startingloaderid(mFragment_name);
        ID_SIGNAL_LOADER = startloaderid + 2;
        ID_FRAGARTICLE_TABLE_LOADER = startloaderid + 3;
        ID_FRAGARTICLE_TABLE_CATEGORY_LOADER = startloaderid + 4;

        Log.d(TAG, "oncreateview 1 " + mFragment_name);

        //setup the reference
        mRecyclerView = (SlowdownRecyclerView) rootView.findViewById(R.id.recyclerview_slowdown);
        mLoadingIndicator = (ProgressBar) rootView.findViewById(R.id.pb_loading_indicator);


        //setup the recyclerview
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mFirstLevelNewsAdapter = new FirstLevelNewsAdapter(this.getActivity(), this);
        mRecyclerView.setAdapter(mFirstLevelNewsAdapter);

        Log.d(TAG, "oncreateview 2 " + mFragment_name);

        //add onscrolllistener to see whether it should retrieve more information/article list or not
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d(TAG, "-->  " + mFragment_name + " onScrollStateChanged idle="
                        + SCROLL_STATE_IDLE + ",settling=" + SCROLL_STATE_SETTLING + ",dragging=" +
                        SCROLL_STATE_DRAGGING + ",newState=" + newState);

                //total item for displaying
                int totalItemCount = recyclerView.getLayoutManager().getItemCount();

                //position value of last visible item on screen
                int findLastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                        .findLastVisibleItemPosition();
                Log.d(TAG, "---->  " + mFragment_name + " onscrollstatechanged " +
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
                    if (Gongdispatch.isOnline(getActivity())) {

                        //get more data
                        mLoadingMore = Gongdispatch.retrieveSheetAndNameCat(
                                FragArticleTableContract.buildUriWithNamePathAndName(mFragment_name),
                                mMainNewsActivity,
                                mFragment_name,
                                FragInfo.getInstance().get_category(mFragment_name)
                        );
                        Log.d(TAG, "  " + mFragment_name + " onscrollstatechanged 8 mFragPageNumber=" + mFragPageNumber);
                        mFragPageNumber++;

                        Log.d(TAG, "  " + mFragment_name + " onscrollstatechanged 9 mFragPageNumber=" + mFragPageNumber);
                    } else {
                        //it's not online
                        Toast.makeText(getActivity(), R.string.check_network_setting, Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });


        Log.d(TAG, "  " + mFragment_name + " oncreateview 3 ");


        //set up the swipe refresh layout
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setDistanceToTriggerSync(32);
        mSwipeRefreshLayout.setNestedScrollingEnabled(true);
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.d(TAG, "  " + mFragment_name + " onRefresh called from SwipeRefreshLayout 1 ");
                        if (Gongdispatch.isOnline(getActivity())) {
                            //if it is online, get more data
                            mLoadingMore = Gongdispatch.retrieveSheetAndNameCat(
                                    FragArticleTableContract.buildUriWithNamePathAndName(mFragment_name),
                                    mMainNewsActivity,
                                    mFragment_name,
                                    FragInfo.getInstance().get_category(mFragment_name)
                            );
                            mFragPageNumber = 0;
                            mRefreshingLayout = true;
                        } else {
                            // it is offline
                            Log.d(TAG, "  " + mFragment_name + " onRefresh called from SwipeRefreshLayout 3");
                            Toast.makeText(mMainNewsActivity, R.string.check_network_setting, Toast.LENGTH_LONG).show();
                            mSwipeRefreshLayout.setRefreshing(false);
                            mRefreshingLayout = false;

                        }
                    }
                }
        );
        Log.d(TAG, "  " + mFragment_name + " oncreateview 4 ");

        //start loader
        getActivity().getSupportLoaderManager().initLoader(ID_SIGNAL_LOADER, null, this);
        getActivity().getSupportLoaderManager().initLoader(ID_FRAGARTICLE_TABLE_LOADER, null, this);

        Log.d(TAG, "  " + mFragment_name + " oncreateview 5 ");


        mFragPageNumber = 0;

        return rootView;

    }


    /**
     * show the loading indicator and hide recyclerview
     */
    private void showLoading() {
        Log.d(TAG, "  " + mFragment_name + " showloading ");
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Finally, show the loading indicator */
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    /**
     * hide the loading indicator and show recyclerview
     */
    private void showDataView() {
        /* First, hide the loading indicator */
        Log.d(TAG, "  " + mFragment_name + " showdatashow ");
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        /* make data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "---->  " + mFragment_name + "  oncreateloader " + ",id=" + id);
        if (id == ID_FRAGARTICLE_TABLE_LOADER) {
            //create the loader for loading specific category(mFragment_name) article with
            Log.d(TAG, "---->  " + mFragment_name + " oncreateloader 3");
            return new CursorLoader(getActivity(),
                    FragArticleTableContract.buildUriWithNamePathAndName(mFragment_name),
                    null,
                    null,
                    null,
                    null);
        } else if (id == ID_FRAGARTICLE_TABLE_CATEGORY_LOADER) {
            //create the loader for a specific list of first subdomain id
            Log.d(TAG, "---->  " + mFragment_name + " oncreateloader 4");

            //construct the selection string for query
            StringBuilder selectionBuilder = new StringBuilder();

            //get the specific list of first subdomain id from passed in bundle
            int[] arrayOfFSD = args.getIntArray(PREFERREDLISTINTARRAY);

            //hold the selection argument value
            String[] selectionArgs = new String[arrayOfFSD.length + 1];
            Log.d(TAG, "---->  " + mFragment_name + " oncreateloader 4.01=" + arrayOfFSD.length);
            selectionArgs[0] = mFragment_name;
            selectionBuilder.append(FragArticleTableContract.RAWQUERY_FRAGARTICLE_WHERENAMESTRING + " and ");
            for (int y = 0; y < arrayOfFSD.length; y++) {
                Log.d(TAG, "---->  " + mFragment_name + " oncreateloader 4.1 = " + arrayOfFSD[y]);
            }

            //construct the selection string
            selectionBuilder.append(" ( ");
            for (int index = 0; index < arrayOfFSD.length; index++) {
                selectionArgs[index + 1] = String.valueOf(arrayOfFSD[index]);
                selectionBuilder.append(FragArticleTableContract.RAWQUERY_FRAGARTICLE_WHEREFIRSTSUBSTRING);
                if ((index + 1) < arrayOfFSD.length) {
                    selectionBuilder.append(" or ");
                }

            }
            selectionBuilder.append(" ) ");


            Log.d(TAG, "---->  " + mFragment_name + " oncreateloader 4.2 = " + selectionBuilder.toString());
            for (int y = 0; y < selectionArgs.length; y++) {
                Log.d(TAG, ",  " + mFragment_name + " selectionargs=" + selectionArgs[y]);
            }

            return new CursorLoader(getActivity(),
                    FragArticleTableContract.buildUriWithCategory(FragInfo.getInstance().get_category(mFragment_name)),
                    null,
                    selectionBuilder.toString(),
                    selectionArgs,
                    null);
        } else if (id == ID_SIGNAL_LOADER) {
            //create the signal loader
            Uri signalQueryUri = SignalContract.SignalEntry.CONTENT_URI;
            String signalsortOrder = SignalContract.SignalEntry.COLUMN_ARTICLE_ID + " ASC";
            Log.d(TAG, "---->  " + mFragment_name + " oncreateloader 6");

            return new CursorLoader(getActivity(),
                    signalQueryUri,
                    SignalContract.PROJECTION,
                    null,
                    null,
                    signalsortOrder);
        } else {
            throw new RuntimeException(" " + mFragment_name + " Loader Not Implemented: " + id);
        }


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "  " + mFragment_name + " containerfragment  onloadfinished 1 loader.getid=" + loader.getId());

        int loaderid = loader.getId();
        if (loaderid == ID_FRAGARTICLE_TABLE_LOADER) {
            //all the firstsubdomain within this category
            Log.d(TAG, "   " + mFragment_name + " ID_FRAGARTICLE_TABLE_LOADER onloadfinished 1.1 ");

            if ((data == null) || ((data != null) && (data.getCount() == 0))) {
                if (((data != null) && (data.getCount() == 0)) && (mPosition == 0)) {
                    //it is really no data to show, even after get it from remote
                    Log.d(TAG, "  " + mFragment_name + " ID_FRAGARTICLE_TABLE_LOADER onloadfinished 1.1.1 ");
                    //show nothing
                    mFirstLevelNewsAdapter.swapCursor(null, false);
                    mRefreshingLayout = false;

                }
                //go get data, nothing in local database, go get it from remote
                showLoading();
                if (Gongdispatch.isOnline(getActivity())) {
                    Log.d(TAG, "   " + mFragment_name + " ID_FRAGARTICLE_TABLE_LOADER onloadfinished 1.3 ");

                    if (mPosition == RecyclerView.NO_POSITION) {
                        //let's try to get some data from remote because this is first time to try loading
                        Gongdispatch.retrieveSheetAndNameCat(
                                FragArticleTableContract.buildUriWithNamePathAndName(mFragment_name),
                                mMainNewsActivity,
                                mFragment_name,
                                FragInfo.getInstance().get_category(mFragment_name)
                        );
                        mRefreshingLayout = true;
                        mFragPageNumber = 1;
                        mPosition = 0;
                    }
                } else {
                    //it is offline
                    Log.d(TAG, "  " + mFragment_name + " onloadfinished ID_FRAGARTICLE_TABLE_LOADER 1.5");
                    Toast.makeText(getActivity(), R.string.check_network_setting, Toast.LENGTH_LONG).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                    mRefreshingLayout = false;
                }

            } else {
                //there is some data, let show it
                if (mRefreshingLayout == true) {
                    //it was in refreshing state, reset it
                    mRefreshingLayout = false;
                    mSwipeRefreshLayout.setRefreshing(false);
                    Log.d(TAG, "   " + mFragment_name + " onloadfinished ID_FRAGARTICLE_TABLE_LOADER 2 ");
                }
                Log.d(TAG, "  " + mFragment_name + " onloadfinished ID_FRAGARTICLE_TABLE_LOADER 3 ");
                //update the adapter with new cursor information
                mFirstLevelNewsAdapter.swapCursor(data, false);
                mLoadingMore = false;
                Log.d(TAG, "   " + mFragment_name + " onloadfinished ID_FRAGARTICLE_TABLE_LOADER 4 ");

                if (mPosition == RecyclerView.NO_POSITION) {
                    //it means there are some data in the database
                    if (Gongdispatch.isOnline(getActivity())) {
                        mFragPageNumber = 1;
                        mPosition = 0;
                    } else {
                        Toast.makeText(getActivity(), R.string.check_network_setting, Toast.LENGTH_LONG).show();
                    }

                } else if (mPosition == 0) {
                    // got data from remote, set the value to indicate it
                    mPosition = data.getCount();
                    mRecyclerView.smoothScrollToPosition(0);
                }
                Log.d(TAG, "   " + mFragment_name + " onloadfinished ID_FRAGARTICLE_TABLE_LOADER 5 data.getcount=" + data.getCount());
                if (data.getCount() != 0) showDataView();
            }
        } else if (loaderid == ID_FRAGARTICLE_TABLE_CATEGORY_LOADER) {
            // a list of article with specific list of first subdomain in this category
            Log.d(TAG, "   " + mFragment_name + " ID_FRAGARTICLE_TABLE_CATEGORY_LOADER onloadfinished 1.1 ");

            if ((data == null) || ((data != null) && (data.getCount() == 0))) {
                if (((data != null) && (data.getCount() == 0)) && (mPosition_archive == 0)) {
                    //it is really no data to show
                    Log.d(TAG, "  " + mFragment_name + " ID_FRAGARTICLE_TABLE_CATEGORY_LOADER onloadfinished 1.1.1 ");
                    mFirstLevelNewsAdapter.swapCursor(null, false);
                    mRefreshingLayout = false;
                }
                //go get data
                showLoading();
                if (Gongdispatch.isOnline(getActivity())) {
                    if (mPosition == RecyclerView.NO_POSITION) {
                        //let's try to get some data from remote because this is first time to try loading
                        Gongdispatch.retrieveSheetAndNameCat(
                                FragArticleTableContract.buildUriWithCategory(FragInfo.getInstance().get_category(mFragment_name)),
                                mMainNewsActivity,
                                mFragment_name,
                                FragInfo.getInstance().get_category(mFragment_name)
                        );
                        mRefreshingLayout = true;
                        mPosition_archive = 0;
                    }
                } else {
                    //it is offline
                    Log.d(TAG, "  " + mFragment_name + " onloadfinished ID_FRAGARTICLE_TABLE_CATEGORY_LOADER 1.5");
                    Toast.makeText(getActivity(), R.string.check_network_setting, Toast.LENGTH_LONG).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                    mRefreshingLayout = false;
                }

            } else {
                //there is some data, let show it
                if (mRefreshingLayout == true) {
                    mRefreshingLayout = false;
                    mSwipeRefreshLayout.setRefreshing(false);
                    Log.d(TAG, "   " + mFragment_name + " onloadfinished ID_FRAGARTICLE_TABLE_CATEGORY_LOADER 2 ");
                }
                Log.d(TAG, "  " + mFragment_name + " onloadfinished ID_FRAGARTICLE_TABLE_CATEGORY_LOADER 3 ");
                //update the adapter with new data
                mFirstLevelNewsAdapter.swapCursor(data, true);
                mLoadingMore = false;
                Log.d(TAG, "   " + mFragment_name + " onloadfinished ID_FRAGARTICLE_TABLE_CATEGORY_LOADER 4 ");

                if (mPosition_archive == RecyclerView.NO_POSITION) {
                    //it means there are some data in the database
                    //check whether there are more new news
                    if (Gongdispatch.isOnline(getActivity())) {
                        mPosition_archive = 0;
                    } else {
                        //offline
                        Toast.makeText(getActivity(), R.string.check_network_setting, Toast.LENGTH_LONG).show();
                    }

                } else if (mPosition_archive == 0) {
                    //got data from remote , update the indicator
                    mPosition_archive = data.getCount();
                    mRecyclerView.smoothScrollToPosition(0);

                }
                Log.d(TAG, "   " + mFragment_name + " onloadfinished ID_FRAGARTICLE_TABLE_CATEGORY_LOADER 5 data.getcount=" + data.getCount());
                if (data.getCount() != 0) showDataView();
            }
        } else if (loaderid == ID_SIGNAL_LOADER) {

            //update the signal information in adapter
            mFirstLevelNewsAdapter.updateSignalMapFromCursor(data);
            Log.d(TAG, "  " + mFragment_name + " ID_SIGNAL_LOADER onloadfinished 9 loaderid=" + loader.getId());

        } else {
            Log.d(TAG, "  " + mFragment_name + " ID_SIGNAL_LOADER onloadfinished 10 loaderid=" + loader.getId());
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "---->  " + mFragment_name + " onloaderreset 1 loader.getid=" + loader.getId());
        if ((loader.getId() == ID_FRAGARTICLE_TABLE_CATEGORY_LOADER)
                || (loader.getId() == ID_FRAGARTICLE_TABLE_LOADER)) {
            Log.d(TAG, "---->  " + mFragment_name + " onloaderreset 2 loader.getid=" + loader.getId());
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

        Log.d(TAG, "  -->  " + mFragment_name + " onClickDetailNews 1 entryID=" + entryID);
        Intent intent = new Intent(getActivity(), DetailNewsActivity.class);
        intent.putExtra(DetailNewsActivity.FINALURL, finalurl);
        startActivity(intent);
        Log.d(TAG, "  -->  " + mFragment_name + " onClickDetailNews 2 entryID=" + entryID);
        Log.d(TAG, " -->  " + mFragment_name + " onClickDetailNews 3 ");


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
        Intent intent = new Intent(getActivity(), ExpandNewsActivity.class);
        intent.putExtra(ExpandNewsActivity.JSONARTICLELISTSTR, jsonArticleList);
        intent.putExtra(ExpandNewsActivity.JSONSIGNALBITSTR, jsonSignalbit);
        startActivity(intent);
        Log.d(TAG, " -->  " + mFragment_name + " onClickExpandNews 1 ");

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
            if (Gongdispatch.isOnline(getActivity())) {
                //go fetch the content

            } else {
                Toast.makeText(getActivity(), R.string.check_network_setting, Toast.LENGTH_LONG).show();
            }
            Log.d(TAG, " -->  " + mFragment_name + " onClickBookmarkArticleStoreOrRemove 3 ");
        } else {
            //remove
            Log.d(TAG, " -->  " + mFragment_name + " onClickBookmarkArticleStoreOrRemove 4 ");

        }


    }


    /**
     * report how many has added in remote database
     * for now, it is a fake number
     */
    @Override
    public int checkHowManyNewItem() {

        Random random = new Random();
        return random.nextInt(100);
    }

    /**
     * user has selected a list of preferred media source on drawer fragment.
     * update the article list accordingly
     *
     * @param arrayOfFSD : a list of first subdomain id for retrieving data
     */
    @Override
    public void initPreferredFirstSubDomain(int[] arrayOfFSD) {

        ContentResolver contentResolver = getActivity().getContentResolver();

        //create the bundle to include the first subdomain id list
        Bundle passBundle = new Bundle();
        passBundle.putIntArray(PREFERREDLISTINTARRAY, arrayOfFSD);
        for (int x = 0; x < arrayOfFSD.length; x++) {
            Log.d(TAG, " " + mFragment_name + " initPreferredFirstSubDomain 1 arrayoffsd[" +
                    x + "]=" + arrayOfFSD[x]);
        }
        //restart the loader with new list of first subdomain id
        getActivity().getSupportLoaderManager().restartLoader(ID_FRAGARTICLE_TABLE_CATEGORY_LOADER, passBundle, this);
        Log.d(TAG, "  " + mFragment_name + " initPreferredFirstSubDomain 2 arrayoffsd = " + arrayOfFSD.toString());

    }

    /**
     * user has cleared out all the selection from filter fragment or unselected all the options,
     * update the article list accordingly
     */
    @Override
    public void clearPreferredFirstSubDomain() {
        Log.d(TAG, "  " + mFragment_name + " clearPreferredFirstSubDomain 1");
        getActivity().getSupportLoaderManager().restartLoader(ID_FRAGARTICLE_TABLE_LOADER, null, this);
        Log.d(TAG, "  " + mFragment_name + " clearPreferredFirstSubDomain 2");

    }

    /**
     * get the context reference for callback usage
     *
     * @param context context that is hosting this fragment
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            mMainNewsActivity = (MainNewsActivity) context;
            Log.d(TAG, "  " + mFragment_name + " onAttach 1");
        }

    }

    /**
     * user click the update button when there is a snackbar indicate x amount of items have been
     * added remote database. Hosting activity will call this method to update the article list
     */
    @Override
    public void swiperefreshNow() {
        if (Gongdispatch.isOnline(getActivity())) {
            //do the retrieve only if it is online
            mLoadingMore = Gongdispatch.retrieveSheetAndNameCat(
                    FragArticleTableContract.buildUriWithNamePathAndName(mFragment_name),
                    mMainNewsActivity,
                    mFragment_name,
                    FragInfo.getInstance().get_category(mFragment_name)
            );
            mFragPageNumber = 0;
            mRefreshingLayout = true;
            mSwipeRefreshLayout.setRefreshing(true);
        } else {
            Toast.makeText(getActivity(), R.string.check_network_setting, Toast.LENGTH_LONG).show();
        }

    }
}
