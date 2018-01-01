package com.hk.simplenewsgong.simplegong;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hk.simplenewsgong.simplegong.data.FragArticleTableContract;
import com.hk.simplenewsgong.simplegong.data.SignalContract;
import com.hk.simplenewsgong.simplegong.data.SourceInfo;
import com.hk.simplenewsgong.simplegong.util.ArticleInfo;
import com.hk.simplenewsgong.simplegong.util.GongTimeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * this is a list adapter to display bookmark list which contains all the article that user has
 * marked with bookmark icon
 * <p>
 * Created by simplegong
 */
public class BookmarkListAdapter extends
        RecyclerView.Adapter<BookmarkListAdapter.BookmarkIndNewsViewHolder> {

    private final String TAG = BookmarkListAdapter.class.getSimpleName();

    /* The context we use to utility methods, app resources and layout inflaters */
    private final Context mContext;

    //call the activity which use this adapter
    private View.OnClickListener mBookmarkListActivity;
    //when share icon is clicked, this reference will be used for start the necessary steps
    private View.OnClickListener mshareClickListener;
    //store the signal information for articles
    private Map<Integer, Integer> mSignalMap;
    //store a list of articleinfo for display
    private List<ArticleInfo> mArticleInfoList;


    //reference cursor to a list of bookmarked articles
    private Cursor mCursor;


    //how long should a article to be waited before real removal
    private static final int PENDING_REMOVAL_TIMEOUT = 5000; // 3sec

    //a list of article is pending to remove
    List<ArticleInfo> mArticlePendingRemoval;

    // hanlder for running delayed runnables
    private Handler handler = new Handler();


    // a callback to the object that implement BookmarkIndNewsAdapterOnClickHandler interface.
    // most likely, it is a activity
    private BookmarkListAdapter.BookmarkIndNewsAdapterOnClickHandler mClickHandler;

    // map of items to pending runnables, so we can cancel a removal if need be
    Map<ArticleInfo, Runnable> pendingRunnables = new HashMap<>();

    /**
     * the callback interface to the activity
     */
    public interface BookmarkIndNewsAdapterOnClickHandler {
        /**
         * this method will start the detail news activity when the user click on individual article
         * view item
         *
         * @param entryID  : detail news activity retrieve the row from database (not used at this moment)
         * @param finalurl : let the webview in detail news activity to load the specific url
         */
        void onClickListIndNews(long entryID, String finalurl);

        /**
         * when the user click the bookmark icon, retrieve the necessary data ahead or not
         *
         * @param entryID : gongdispatch will use this entryID to find out whether there is one in local
         *                database.  If "save" is true, go get it. Otherwise, remove it
         * @param save    : if it is true, store the information.  Otherwise, remove it
         */
        void onClickBookmarkArticleStoreOrRemove(long entryID, boolean save);
    }


    /**
     * Creates the adapter. Initialize the necessary data structures.  Also,
     * 1, setting up the onclicklistener when individual article is clicked.  Like mark it as read already
     * in signal map , change background color...
     * 2, setting up the onclicklistener when share icon is clicked. Send the title, url..etc to
     * the Intent that handle send message
     *
     * @param context      use for resolving the app resources
     * @param clickHandler callback object that will handle BookmarkIndNewsAdapterOnClickHandler
     *                     interface
     */

    public BookmarkListAdapter(@NonNull Context context,
                               BookmarkListAdapter.BookmarkIndNewsAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
        mSignalMap = new HashMap<Integer, Integer>();
        mArticleInfoList = new ArrayList<>();
        mArticlePendingRemoval = new ArrayList<>();


        //set up the onclicklistener
        mBookmarkListActivity = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //update the signal table
                int articleID = (int) view.getTag(R.string.VIEWTAG_ARTICLEID);
                String finalurl = (String) view.getTag(R.string.VIEWTAG_FINALURL);
                int readalready_inmap = 0;
                int bitvalBookRead = 0;
                Uri uri = SignalContract.SignalEntry.CONTENT_URI;
                ContentValues contentValues = new ContentValues();
                contentValues.put(SignalContract.SignalEntry.COLUMN_ARTICLE_ID, articleID);
                if (mSignalMap.containsKey(articleID)) {
                    bitvalBookRead = mSignalMap.get(articleID);
                }
                readalready_inmap = bitvalBookRead | FirstLevelNewsAdapter.READALREADY_MASK;
                mSignalMap.put(articleID, readalready_inmap);
                contentValues.put(SignalContract.SignalEntry.COLUMN_READALREADY, 1);
                mContext.getContentResolver().insert(
                        uri,
                        contentValues);


                //set the background color
                ViewGroup oneupLevelLayout = (ViewGroup) view.getParent();
                if (view.getTag(R.string.VIEWTAG_BACKGROUND_LAYOUT_ID) != null) {
                    Log.d(TAG, "VIEWTAG_BACKGROUND_LAYOUT_ID R.color.after_reading_color");
                    oneupLevelLayout.findViewById(
                            (int) view.getTag(R.string.VIEWTAG_BACKGROUND_LAYOUT_ID))
                            .setBackgroundResource(R.color.after_reading_color);
                }
                if (view.getTag(R.string.VIEWTAG_BACKGROUND_SHARE_ID) != null) {
                    Log.d(TAG, "VIEWTAG_BACKGROUND_SHARE_ID R.color.after_reading_color");
                    oneupLevelLayout.findViewById(
                            (int) view.getTag(R.string.VIEWTAG_BACKGROUND_SHARE_ID))
                            .setBackgroundResource(R.color.after_reading_color);
                }

                //should start detailactivity if available
                Log.d(TAG, "---------------------> starting detail activity articleid="
                        + articleID
                        + ",finalurl="
                        + finalurl);
                mClickHandler.onClickListIndNews(articleID, finalurl);
            }
        };


        //setup share icon clicklistener
        mshareClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, " ib_sharebutton 1");
                //gather the neccessary information and send it out
                String finalurl = (String) view.getTag(R.string.VIEWTAG_FINALURL);
                String title = (String) view.getTag(R.string.VIEWTAG_TITLE);
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = title + "\n" + finalurl;
                String shareSub = title;
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                mContext.startActivity(Intent.createChooser(sharingIntent, "Share using"));
                Log.d(TAG, " ib_sharebutton 2");

            }
        };
        Log.d(TAG, " BookmarkListAdapter constructor ");
    }


    /**
     * create viewholder to display recyclerview item
     *
     * @param viewGroup viewgroup that viewholder resides on
     * @param viewType  which viewtype of this viewholder should be created
     * @return viewholder holds each recycler item
     */
    @Override
    public BookmarkListAdapter.BookmarkIndNewsViewHolder
    onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        int layoutId;
        View view;

        layoutId = R.layout.bookmark_list_item;
        view = LayoutInflater.from(mContext).inflate(layoutId, viewGroup, false);

        view.setFocusable(true);
        Log.d(TAG, "BookmarkListAdapter onCreateViewHolder");

        return new BookmarkListAdapter.BookmarkIndNewsViewHolder(view);

    }


    /**
     * bind the neccessary information to display for specific recycler item
     *
     * @param bookmarkIndNewsAdapterViewHolder viewholder to hold the information for display
     * @param position                         The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(BookmarkListAdapter.BookmarkIndNewsViewHolder bookmarkIndNewsAdapterViewHolder,
                                 int position) {

        //get the information from pass-in position value of articleinfo list
        final int articlePrimaryID = mArticleInfoList.get(position).getID();
        final ArticleInfo articleEntry = mArticleInfoList.get(position);
        final String finalurl = mArticleInfoList.get(position).getFinalurl();

        Log.d(TAG, " onBindViewHolder 1 ");

        //check whether the item is in pending removal queue or not
        if (mArticlePendingRemoval.contains(articleEntry)) {
            Log.d(TAG, " onBindViewHolder 2 ");

            //if the item is in the pending removal queue
            //turn off the view that are not used
            bookmarkIndNewsAdapterViewHolder.itemView.setBackgroundColor(Color.BLACK);
            bookmarkIndNewsAdapterViewHolder.itemthumbnailView.setVisibility(View.GONE);
            bookmarkIndNewsAdapterViewHolder.newsourceiconView.setVisibility(View.GONE);
            bookmarkIndNewsAdapterViewHolder.ib_bookmarkButton.setVisibility(View.GONE);
            bookmarkIndNewsAdapterViewHolder.ib_expandlessButton.setVisibility(View.GONE);
            bookmarkIndNewsAdapterViewHolder.ib_shareButton.setVisibility(View.GONE);
            bookmarkIndNewsAdapterViewHolder.tv_domainsourceView.setVisibility(View.GONE);
            bookmarkIndNewsAdapterViewHolder.tv_dateView.setVisibility(View.GONE);
            bookmarkIndNewsAdapterViewHolder.primarytitleView.setVisibility(View.GONE);
            bookmarkIndNewsAdapterViewHolder.b_undoButton.setVisibility(View.VISIBLE);
            ((ViewGroup) bookmarkIndNewsAdapterViewHolder.ib_shareButton.getParent()).setBackground(null);
            int bottombound = ((View) bookmarkIndNewsAdapterViewHolder.b_undoButton.getParent()).getHeight();
            Log.d(TAG, " onBindViewHolder getwidth=" + bottombound);

            //setting up the undo button to display at right location
            if (bookmarkIndNewsAdapterViewHolder.leftToright) {
                Log.d(TAG, " onBindViewHolder undoButton lefttoright = true");
                bookmarkIndNewsAdapterViewHolder.b_undoButton.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.START));
            } else {
                Log.d(TAG, " onBindViewHolder undoButton lefttoright = false");
                bookmarkIndNewsAdapterViewHolder.b_undoButton.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.END));
            }

            //setup the onclicklistener for undobutton
            bookmarkIndNewsAdapterViewHolder.b_undoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // user wants to undo the removal, let's cancel the pending task
                    Runnable pendingRemovalRunnable = pendingRunnables.get(articleEntry);
                    pendingRunnables.remove(articleEntry);
                    if (pendingRemovalRunnable != null)
                        handler.removeCallbacks(pendingRemovalRunnable);
                    mArticlePendingRemoval.remove(articleEntry);
                    // this will rebind the row in "normal" state
                    notifyItemChanged(mArticleInfoList.indexOf(articleEntry));
                }
            });


        } else {
            Log.d(TAG, " onBindViewHolder 3 ");
            //show the necessary view for non pending removal item
            bookmarkIndNewsAdapterViewHolder.itemthumbnailView.setVisibility(View.VISIBLE);
            bookmarkIndNewsAdapterViewHolder.newsourceiconView.setVisibility(View.VISIBLE);
            bookmarkIndNewsAdapterViewHolder.ib_shareButton.setVisibility(View.VISIBLE);
            bookmarkIndNewsAdapterViewHolder.tv_domainsourceView.setVisibility(View.VISIBLE);
            bookmarkIndNewsAdapterViewHolder.tv_dateView.setVisibility(View.VISIBLE);
            bookmarkIndNewsAdapterViewHolder.primarytitleView.setVisibility(View.VISIBLE);

            Drawable background = bookmarkIndNewsAdapterViewHolder.itemView.getBackground();
            int color = 0;
            if (background instanceof ColorDrawable) {
                color = ((ColorDrawable) background).getColor();
                Log.d(TAG, " onBindViewHolder 3.1 color=" + color);
                bookmarkIndNewsAdapterViewHolder.itemView.setBackground(null);
                Log.d(TAG, " onBindViewHolder 3.2 color=" + color);

            }

            bookmarkIndNewsAdapterViewHolder.b_undoButton.setVisibility(View.GONE);

            //setup the thumbnail pic
            if (mArticleInfoList.get(position).getImageurl()
                    .compareTo("EMPTYSTRINGVALUE") == 0) {
                bookmarkIndNewsAdapterViewHolder.itemthumbnailView.setImageResource(R.drawable.ic_logo_hourglass_question);
            } else {
                GlideApp.with(mContext)
                        .load(mArticleInfoList.get(position).getImageurl())
                        .placeholder(R.drawable.ic_tmp_icon)
                        .fitCenter()
                        .into(bookmarkIndNewsAdapterViewHolder.itemthumbnailView);
            }
            //attach information to this view
            bookmarkIndNewsAdapterViewHolder.itemthumbnailView.setOnClickListener(mBookmarkListActivity);
            setupTAGInformation((View) bookmarkIndNewsAdapterViewHolder.itemthumbnailView, articlePrimaryID, finalurl);

            int pagination_firstsubdomainid = mArticleInfoList.get(position).getFirstsubdomaintable_id();

            //setup the source icon
            ArrayList<String> sourceIconURLChiName = SourceInfo.getInstance()
                    .getSourceIconURLChiName(pagination_firstsubdomainid);
            GlideApp.with(mContext)
                    .load(sourceIconURLChiName.get(SourceInfo.ARRAY_SOURCEICONURL_POS))
                    .placeholder(R.drawable.ic_tmp_icon)
                    .fitCenter()
                    .into(bookmarkIndNewsAdapterViewHolder.newsourceiconView);
            //attach information to this view
            bookmarkIndNewsAdapterViewHolder.newsourceiconView.setOnClickListener(mBookmarkListActivity);
            setupTAGInformation((View) bookmarkIndNewsAdapterViewHolder.newsourceiconView, articlePrimaryID, finalurl);


            Log.d(TAG, " onBindViewHolder 4 ");


            //setup the source text view and attach information to this view
            bookmarkIndNewsAdapterViewHolder.tv_domainsourceView.setText(sourceIconURLChiName.get(SourceInfo.ARRAY_NAME_POS));
            bookmarkIndNewsAdapterViewHolder.tv_domainsourceView.setOnClickListener(mBookmarkListActivity);
            setupTAGInformation((View) bookmarkIndNewsAdapterViewHolder.tv_domainsourceView, articlePrimaryID, finalurl);


            int bitfromjson = mArticleInfoList.get(position).getSignalBitmap();
            int bookmarkalready = bitfromjson & FirstLevelNewsAdapter.BOOKMARKALREADY_MASK;
            int readalready = bitfromjson & FirstLevelNewsAdapter.READALREADY_MASK;


            Log.d(TAG, " BookmarkListAdapter.BookmarkIndNewsViewHolder  1 articlePrimaryID="
                    + articlePrimaryID
                    + ", bitfromjson=" + bitfromjson
                    + ",bookmarkalready=" + bookmarkalready
                    + ",readalready=" + readalready);


            int readalready_inmap = 0;
            int bitvalBookRead = 0;

            //setup the readalready information
            if (mSignalMap.containsKey(articlePrimaryID)) {
                bitvalBookRead = mSignalMap.get(articlePrimaryID);
                readalready_inmap = bitvalBookRead & FirstLevelNewsAdapter.READALREADY_MASK;
            } else {
                mSignalMap.put(articlePrimaryID, bitfromjson);
            }
            Log.d(TAG, " BookmarkListAdapter.BookmarkIndNewsViewHolder  2 articlePrimaryID="
                    + articlePrimaryID
                    + ",bitvalBookRead=" + bitvalBookRead
                    + ",readalready_inmap=" + readalready_inmap
                    + ",title=" + mArticleInfoList.get(position).getTitle());

            if ((readalready != 0)
                    || (readalready_inmap != 0)) {
                bookmarkIndNewsAdapterViewHolder.ib_shareButton.setBackgroundResource(R.color.after_reading_color);
                ((ViewGroup) bookmarkIndNewsAdapterViewHolder.ib_shareButton.getParent()).setBackgroundResource(R.color.after_reading_color);
            } else {
                //use default background
                bookmarkIndNewsAdapterViewHolder.ib_shareButton.setBackgroundResource(0);
                ((ViewGroup) bookmarkIndNewsAdapterViewHolder.ib_shareButton.getParent()).setBackgroundResource(0);
            }


            bookmarkIndNewsAdapterViewHolder.ib_bookmarkButton.setVisibility(View.INVISIBLE);


            final String primarytitle = mArticleInfoList.get(position).getTitle();

            //setup sharebutton
            bookmarkIndNewsAdapterViewHolder.ib_shareButton.setOnClickListener(mshareClickListener);
            bookmarkIndNewsAdapterViewHolder.ib_shareButton.setTag(R.string.VIEWTAG_ARTICLEID, articlePrimaryID);
            bookmarkIndNewsAdapterViewHolder.ib_shareButton.setTag(R.string.VIEWTAG_FINALURL, finalurl);
            bookmarkIndNewsAdapterViewHolder.ib_shareButton.setTag(R.string.VIEWTAG_TITLE, primarytitle);

            //disable expandless button
            bookmarkIndNewsAdapterViewHolder.ib_expandlessButton.setVisibility(View.INVISIBLE);

            //setup the primary title view
            bookmarkIndNewsAdapterViewHolder.primarytitleView.setText(primarytitle);
            bookmarkIndNewsAdapterViewHolder.primarytitleView.setOnClickListener(mBookmarkListActivity);
            setupTAGInformation((View) bookmarkIndNewsAdapterViewHolder.primarytitleView, articlePrimaryID, finalurl);


            //setup time stamp information
            int timestampondoc = mArticleInfoList.get(position).getTimestampondoc();
            String timestampondocTranslatedString = GongTimeUtil.getDisplayTimeStringFromData((long) timestampondoc);
            bookmarkIndNewsAdapterViewHolder.tv_dateView.setText(timestampondocTranslatedString);
            bookmarkIndNewsAdapterViewHolder.tv_dateView.setTag(R.string.VIEWTAG_ARTICLEID, articlePrimaryID);
            bookmarkIndNewsAdapterViewHolder.tv_dateView.setTag(R.string.VIEWTAG_FINALURL, finalurl);
            bookmarkIndNewsAdapterViewHolder.tv_dateView.setOnClickListener(mBookmarkListActivity);


            Log.d(TAG, " BookmarkListAdapter.BookmarkIndNewsViewHolder onBindViewHolder 5 " + position);
        }

    }

    /**
     * attach the information to the view and onclicklistner will use the information accordingly
     *
     * @param view             : information will be attached to
     * @param articlePrimaryID : article id
     * @param finalurl         : url associated with this article
     */
    private void setupTAGInformation(View view,
                                     int articlePrimaryID,
                                     String finalurl) {
        view.setTag(R.string.VIEWTAG_ARTICLEID, articlePrimaryID);
        view.setTag(R.string.VIEWTAG_FINALURL, finalurl);
        view.setTag(R.string.VIEWTAG_BACKGROUND_LAYOUT_ID, R.id.constraintlayout_item_primary);
        view.setTag(R.string.VIEWTAG_BACKGROUND_BOOKMARK_ID, R.id.ib_bookmark);
        view.setTag(R.string.VIEWTAG_BACKGROUND_SHARE_ID, R.id.ib_share);
        view.setTag(R.string.VIEWTAG_BACKGROUND_EXPANDLESS_ID, R.id.ib_expandless);

    }

    /**
     * assign the articleinfo to the pending removal queue
     * create a delayed runnable for removing articleinfo from the actual queue
     *
     * @param position : position in the articleinfolist
     */
    public void pendingRemoval(int position) {
        final ArticleInfo item = mArticleInfoList.get(position);
        if (!mArticlePendingRemoval.contains(item)) {
            mArticlePendingRemoval.add(item);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the item
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    remove(mArticleInfoList.indexOf(item));
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(item, pendingRemovalRunnable);
        }
    }


    /**
     * update the article's bookmark information from signal map
     * remove the articleinfo from pending queue
     * remove from the articleinfolist
     *
     * @param position : position in the recycler view
     */
    public void remove(int position) {
        final ArticleInfo item = mArticleInfoList.get(position);

        //let the host activity to handle the bookmark item
        mClickHandler.onClickBookmarkArticleStoreOrRemove(item.getID(), false);

        //update signal datatable
        Uri uri = SignalContract.SignalEntry.CONTENT_URI;
        ContentValues contentValues = new ContentValues();
        contentValues.put(SignalContract.SignalEntry.COLUMN_ARTICLE_ID, item.getID());
        mSignalMap.remove(item.getID());
        contentValues.put(SignalContract.SignalEntry.COLUMN_BOOKMARKALREADY, 0);
        mContext.getContentResolver().insert(
                uri,
                contentValues);

        //remove from pending queue
        if (mArticlePendingRemoval.contains(item)) {
            Log.d(TAG, " remove mArticlePendingRemoval =" + mArticlePendingRemoval.remove(item));
        }

        //remove from display list
        if (mArticleInfoList.contains(item)) {
            Log.d(TAG, " remove mArticleInfoList =" + mArticleInfoList.remove(position).getTitle());
            notifyItemRemoved(position);
        }
    }

    /**
     * check whether articleinfolist with the supplied position is in pending removal queue
     *
     * @param position : position in the articleinfolist
     * @return indicate the position is in the pending removal queue
     */
    public boolean isPendingRemoval(int position) {
        final ArticleInfo item = mArticleInfoList.get(position);
        return mArticlePendingRemoval.contains(item);
    }


    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available to display
     */
    @Override
    public int getItemCount() {
        Log.d(TAG, " BookmarkListAdapter getitemcount 1 =" + mArticleInfoList.size());
        return mArticleInfoList.size();
    }

    /**
     * this app only have one type
     *
     * @param position index within our RecyclerView and Cursor
     * @return the view type
     */
    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    /**
     * refresh/setup the articleinfolist with new cursor information
     *
     * @param newCursor the new cursor contains bookmark items list
     */
    void swapCursor(Cursor newCursor) {

        Log.d(TAG, " BookmarkListAdapter swapcursor  ");

        mCursor = newCursor;
        if (mCursor != null) {
            Log.d(TAG, " BookmarkListAdapter swapcursor  getcount=" + mCursor.getCount());
        }

        //refresh/setup the articleinfolist
        if ((newCursor != null) && (newCursor.getCount() > 0)) {
            mArticleInfoList.clear();
            for (int x = 0; x < newCursor.getCount(); x++) {
                newCursor.moveToPosition(x);
                int bitmask = newCursor.getInt(FragArticleTableContract.INDEX_BOOKMARK_BOOKMARKALREADY) == 1 ?
                        FirstLevelNewsAdapter.BOOKMARKALREADY_MASK : 0;
                bitmask |= newCursor.getInt(FragArticleTableContract.INDEX_BOOKMARK_READALREADY) == 1 ?
                        FirstLevelNewsAdapter.READALREADY_MASK : 0;

                //adding articleinfo to the list for displaying
                ArticleInfo newEntry = new ArticleInfo(
                        newCursor.getInt(FragArticleTableContract.INDEX_BOOKMARK_FIRSTSUBDOMAINTABLE_ID),
                        newCursor.getString(FragArticleTableContract.INDEX_BOOKMARK_TITLE),
                        newCursor.getString(FragArticleTableContract.INDEX_BOOKMARK_IMAGEURL),
                        newCursor.getInt(FragArticleTableContract.INDEX_BOOKMARK_ARTICLETABLE_ID),
                        newCursor.getString(FragArticleTableContract.INDEX_BOOKMARK_FINALURL),
                        newCursor.getInt(FragArticleTableContract.INDEX_BOOKMARK_TIMESTAMPONDOC),
                        bitmask);
                mArticleInfoList.add(newEntry);
            }
        }
        notifyDataSetChanged();
    }


    /**
     * when the signal table has been updated from other activity/fragment,
     * update this locally
     *
     * @param newCursor new cursor with new information
     */
    void updateSignalMapFromCursor(Cursor newCursor) {
        Log.d(TAG, " BookmarkListAdapter  updateSignalMapFromCursor  ");

        if (newCursor != null) {
            mSignalMap.clear();
            Log.d(TAG, " BookmarkListAdapter  updateSignalMapFromCursor  getcount=" + newCursor.getCount());
            //update local signal information
            for (int index = 0; index < newCursor.getCount(); index++) {
                newCursor.moveToPosition(index);
                int articleID = newCursor.getInt(SignalContract.INDEX_ARTICLE_ID);
                int readalreadyFromCursor = newCursor.getInt(SignalContract.INDEX_READALREADY);
                int bitvalBookRead = 0;
                if (mSignalMap.containsKey(articleID)) {
                    bitvalBookRead = mSignalMap.get(articleID);
                }
                if (readalreadyFromCursor == 0) {
                    bitvalBookRead = bitvalBookRead & ~FirstLevelNewsAdapter.READALREADY_MASK;
                } else {
                    bitvalBookRead = bitvalBookRead | FirstLevelNewsAdapter.READALREADY_MASK;

                }
                mSignalMap.put(articleID, bitvalBookRead);
            }
            notifyDataSetChanged();
        }
    }


    /**
     * viewholder to hold view information for displaying in each recycler item
     */
    class BookmarkIndNewsViewHolder extends RecyclerView.ViewHolder {
        private final String TAG = BookmarkListAdapter.BookmarkIndNewsViewHolder.class.getSimpleName();

        final ImageView itemthumbnailView;
        final ImageView newsourceiconView;
        final ImageButton ib_bookmarkButton;
        final ImageButton ib_expandlessButton;
        final ImageButton ib_shareButton;
        final TextView tv_domainsourceView;
        final TextView tv_dateView;
        final TextView primarytitleView;
        final Button b_undoButton;
        boolean leftToright = true;


        BookmarkIndNewsViewHolder(View view) {
            super(view);


            itemthumbnailView = (ImageView) view.findViewById(R.id.itemthumbnail);
            newsourceiconView = (ImageView) view.findViewById(R.id.newsourceicon);
            ib_bookmarkButton = (ImageButton) view.findViewById(R.id.ib_bookmark);
            ib_expandlessButton = (ImageButton) view.findViewById(R.id.ib_expandless);
            ib_shareButton = (ImageButton) view.findViewById(R.id.ib_share);
            tv_domainsourceView = (TextView) view.findViewById(R.id.tv_domainsource);
            tv_dateView = (TextView) view.findViewById(R.id.tv_date);
            primarytitleView = (TextView) view.findViewById(R.id.primarytitle);
            b_undoButton = (Button) view.findViewById(R.id.undo_button);


            Log.d(TAG, " BookmarkIndNewsViewHolder constructor 1");


        }


    }

}


