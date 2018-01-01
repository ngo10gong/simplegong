package com.hk.simplenewsgong.simplegong;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hk.simplenewsgong.simplegong.data.FragArticleTableContract;
import com.hk.simplenewsgong.simplegong.data.SignalContract;
import com.hk.simplenewsgong.simplegong.data.SourceInfo;
import com.hk.simplenewsgong.simplegong.sync.Gongdispatch;
import com.hk.simplenewsgong.simplegong.util.BottomNavigationViewHelper;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * activity to display all the article with mark with bookmark indicator
 * <p>
 * Created by simplegong
 */
public class BookmarkListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        BookmarkListAdapter.BookmarkIndNewsAdapterOnClickHandler {


    private final String TAG = BookmarkListActivity.class.getSimpleName();

    //loader id
    private static final int ID_SIGNAL_LOADER = 302;
    private static final int ID_ARTICLEBOOKMARK_LOADER = 303;

    // display list adapter for this activity
    private BookmarkListAdapter mBookmarkListAdapter;

    //reference to recyclerview
    private SlowdownRecyclerView mRecyclerView;

    //reference to bottom nvaigation view
    private BottomNavigationView mBottomNavigationView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_news);

        Intent intent = getIntent();


        //setting up the bottom navigation view , assign the index value to indicate which
        //feature is displaying
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_mainnews_navigation);
        BottomNavigationViewHelper.selection(this, mBottomNavigationView, R.id.nav_bookmark);
        Menu menu = mBottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);


        //setting up the recyclerview with layout manager and list adapter for displaying
        mRecyclerView = (SlowdownRecyclerView) findViewById(R.id.recyclerview_slowdown);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mBookmarkListAdapter = new BookmarkListAdapter(this,
                this);
        mRecyclerView.setAdapter(mBookmarkListAdapter);


        //setting up the swipe delete feature for each recycler item
        setUpItemTouchHelper();
        setUpAnimationDecoratorHelper();


        //setting up the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(R.string.my_bookmarked);

        //init the loader
        getSupportLoaderManager().initLoader(ID_SIGNAL_LOADER, null, this);
        getSupportLoaderManager().initLoader(ID_ARTICLEBOOKMARK_LOADER, null, this);

    }

    /**
     * this method setup the itemtouchhelper for recyclerview.  When User swipe left or right,
     * it will draw the background for that recyclerview item according to swipe direction
     */
    private void setUpItemTouchHelper() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    Drawable background;  //background reference
                    boolean initiated; //indicate initial or now

                    /**
                     * initialize this item helper
                     *
                     */
                    private void init() {
                        background = new ColorDrawable(Color.BLACK);  //show black background
                        initiated = true;
                    }

                    // this app does not support drag of recyclerview item
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                        int position = viewHolder.getAdapterPosition();
                        BookmarkListAdapter bookmarkListAdapter = (BookmarkListAdapter) recyclerView.getAdapter();
                        if (bookmarkListAdapter.isPendingRemoval(position)) {
                            //if the item has already been for pending removal, do not do anything
                            return 0;
                        }
                        return super.getSwipeDirs(recyclerView, viewHolder);
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        int swipedPosition = viewHolder.getAdapterPosition();
                        //no matter which direction, tell adapter to put an item for pending removal
                        BookmarkListAdapter adapter = (BookmarkListAdapter) mRecyclerView.getAdapter();
                        adapter.pendingRemoval(swipedPosition);
                    }

                    @Override
                    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        View itemView = viewHolder.itemView;

                        if (viewHolder.getAdapterPosition() == -1) {
                            // if the recycler item has been removed, do nothing
                            return;
                        }

                        if (!initiated) {
                            Log.d(TAG, "...................start onchilddraw init");
                            init();
                        }

                        // draw background
                        BookmarkListAdapter.BookmarkIndNewsViewHolder bookmarkIndNewsViewHolder =
                                (BookmarkListAdapter.BookmarkIndNewsViewHolder) viewHolder;
                        Log.d(TAG, " simpleItemTouchCallback onChildDraw viewholder=" + bookmarkIndNewsViewHolder.primarytitleView.getText()
                                + ",getgetRight=" + itemView.getRight() + ",dX=" + dX + ",dY=" + dY + ",getTop=" + itemView.getTop() +
                                ", getBottom=" + itemView.getBottom());
                        if ((int) dX < 0) {
                            bookmarkIndNewsViewHolder.leftToright = false;
                            background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                            Log.d(TAG, "simpleItemTouchCallback lefttoright 1");
                        } else {
                            bookmarkIndNewsViewHolder.leftToright = true;
                            background.setBounds(itemView.getLeft(), itemView.getTop(), (int) dX, itemView.getBottom());
                            Log.d(TAG, "simpleItemTouchCallback lefttoright 2");
                        }
                        background.draw(c);

                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }

                };

        //assign the item touch helper to recyclerview
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }


    /**
     * this method will do drawing the upper/lower items of just removed item
     */
    private void setUpAnimationDecoratorHelper() {
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            Drawable background;  //holding the background reference
            boolean initiated;  //indicate whether it has been initiated

            /**
             * initialize background reference
             */
            private void init() {
                background = new ColorDrawable(Color.BLACK);
                initiated = true;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

                if (!initiated) {
                    Log.d(TAG, "...................start ondraw init");
                    init();
                }

                if (parent.getItemAnimator().isRunning()) {

                    //the following will fill up the recycler item that just removed by moving the upper item downward
                    // or lower item upward
                    View lastViewComingDown = null; //item view coming down to fill the gap
                    View firstViewComingUp = null;  //item view going up to fill the gap

                    int left = 0;  // leftmost location
                    int right = parent.getWidth(); //rightmost location

                    int top = 0;  // top location
                    int bottom = 0; //bottom location

                    // find relevant translating views
                    int childCount = parent.getLayoutManager().getChildCount();
                    Log.d(TAG, " setUpAnimationDecoratorHelper childcount=" + childCount + ",");
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);

                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            if (lastViewComingDown == null) {
                                Log.d(TAG, " setUpAnimationDecoratorHelper lastViewComingDown i=" + i + ",");
                                lastViewComingDown = child;
                            }
                        } else if (child.getTranslationY() > 0) {
                            // view is going up
                            if (firstViewComingUp == null) {
                                Log.d(TAG, " setUpAnimationDecoratorHelper firstViewComingUp i=" + i + ",");
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the gap
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();

                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the gap
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the gap
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }

                    Log.d(TAG, " background.setbounds left=" + left + ",top=" + top + ",right=" + right + ",bottom=" + bottom);
                    background.setBounds(left, top, right, bottom);
                    background.draw(c);
                }
                super.onDraw(c, parent, state);
            }

        });
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "----> oncreateloader ");
        switch (id) {
            case ID_SIGNAL_LOADER:
                //set up the signal loader
                Uri signalQueryUri = SignalContract.SignalEntry.CONTENT_URI;
                String signalsortOrder = SignalContract.SignalEntry.COLUMN_ARTICLE_ID + " ASC";
                Log.d(TAG, "----> oncreateloader 3");

                return new CursorLoader(this,
                        signalQueryUri,
                        SignalContract.PROJECTION,
                        null,
                        null,
                        signalsortOrder);
            case ID_ARTICLEBOOKMARK_LOADER:
                //set up the article bookmark loader
                String bookmarksortOrder = FragArticleTableContract.FragArticleEntry.COLUMN_TIMESTAMPONDOC + " DESC";
                Log.d(TAG, "----> oncreateloader 4");

                return new CursorLoader(this,
                        FragArticleTableContract.FragArticleEntry.CONTENT_URI_BOOKMARK,
                        SignalContract.PROJECTION,
                        null,
                        null,
                        bookmarksortOrder);


            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "  onloadfinished 1 ");

        switch (loader.getId()) {
            case ID_SIGNAL_LOADER:
                //let the adapter to update its signal map
                mBookmarkListAdapter.updateSignalMapFromCursor(data);
                Log.d(TAG, "  onloadfinished 2 loaderid=" + loader.getId());

                break;

            case ID_ARTICLEBOOKMARK_LOADER:
                //display the bookmarked item
                mBookmarkListAdapter.swapCursor(data);
                Log.d(TAG, "  onloadfinished 3 loaderid=" + loader.getId());

                break;

            default:
                Log.d(TAG, "  onloadfinished 4 loaderid=" + loader.getId());
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
        Log.d(TAG, "  --> onClickListIndNews 1 entryID=" + entryID);
        Intent intent = new Intent(this, DetailNewsActivity.class);
        //embedded the url to retrieve
        intent.putExtra(DetailNewsActivity.FINALURL, finalurl);
        startActivity(intent);
        Log.d(TAG, "  --> onClickListIndNews 2 entryID=" + entryID);
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
                //go fetch the information
            } else {
                Toast.makeText(this, R.string.check_network_setting, Toast.LENGTH_LONG).show();
            }

            Log.d(TAG, " newsfragment --> onClickBookmarkArticleStoreOrRemove 2 entryID=" + entryID);
        } else {
            //remove
        }
    }

}

