package com.hk.simplenewsgong.simplegong;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hk.simplenewsgong.simplegong.data.GongPreference;
import com.hk.simplenewsgong.simplegong.data.FragArticleTableContract;
import com.hk.simplenewsgong.simplegong.data.SignalContract;
import com.hk.simplenewsgong.simplegong.data.SourceInfo;
import com.hk.simplenewsgong.simplegong.util.ArticleInfo;
import com.hk.simplenewsgong.simplegong.util.GongTimeUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * the adapter for containerfragment to show the article list with specified category
 * <p></p>
 * Created by simplegong
 */

public class FirstLevelNewsAdapter extends
        RecyclerView.Adapter<FirstLevelNewsAdapter.FirstLevelNewsAdapterViewHolder> {

    private final String TAG = FirstLevelNewsAdapter.class.getSimpleName();

    /* The context we use to utility methods, app resources and layout inflaters */
    private final Context mContext;

    // reference of onclicklistener that handle detail activity
    private View.OnClickListener mcallDetailActivity;
    // reference of onclicklistener that handle share action
    private View.OnClickListener mshareClickListener;
    // reference of onclicklistener that handle bookmark action
    private View.OnClickListener mbookmarkClickListener;
    // map structure store article's signal information
    private Map<Integer, Integer> mSignalMap;

    //mask bit for getting neccessary signal information
    public final static int BOOKMARKALREADY_MASK = 0x0001;
    public final static int READALREADY_MASK = 0x0002;
    public final static int EXPANDALREADY_MASK = 0x0004;

    //indicator of whether signalmap information has been init or not
    private boolean mInitupdateSignalMapFromCursor;
    //cursor reference to the list of article
    private Cursor mCursor;
    //indicator of whether archive information is displaying or not
    private boolean mUsingSelectedArchivetoDisplay;


    // reference to call the fragment that implement FirstLevelNewsAdapterOnClickHandler
    private FirstLevelNewsAdapter.FirstLevelNewsAdapterOnClickHandler mClickHandler;

    /**
     * the interface for adapter to call the hosting fragment when user click on
     * 1, to display detail news activity
     * 2, to display a list of related news article
     * 3, handle bookmark icon is clicked
     */
    public interface FirstLevelNewsAdapterOnClickHandler {
        /**
         * this method will start the detail news activity when the user click on individual article
         * view item
         *
         * @param entryID  : detail news activity retrieve the row from database (not used at this moment)
         * @param finalurl : let the webview in detail news activity to load the specific url
         */
        void onClickDetailNews(long entryID, String finalurl);

        /**
         * user has clicked the morenews icon, start the expandnewsactivity
         *
         * @param jsonArticleList : json string that contain a list of related articles to
         *                        the current selected article
         * @param jsonSignalbit   : json string that includes related signal information
         */
        void onClickExpandNews(String jsonArticleList, String jsonSignalbit);

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
     * constructor for creating adapter
     *
     * @param context      context to use for resolving resource
     * @param clickHandler when user clicks on the icon or related view and take appropriate action
     */

    public FirstLevelNewsAdapter(@NonNull Context context,
                                 FirstLevelNewsAdapter.FirstLevelNewsAdapterOnClickHandler clickHandler) {
        //initialize member's reference
        mContext = context;
        mClickHandler = clickHandler;
        mSignalMap = new HashMap<Integer, Integer>();

        //create the onclicklistener to launch detail news activity
        // for any views that want this capability
        mcallDetailActivity = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int articleID = (int) view.getTag(R.string.VIEWTAG_ARTICLEID);
                String finalurl = (String) view.getTag(R.string.VIEWTAG_FINALURL);
                int readalready_inmap = 0;
                int bitvalBookRead = 0;

                //update the signal map table in database
                Uri uri = SignalContract.SignalEntry.CONTENT_URI;
                ContentValues contentValues = new ContentValues();
                contentValues.put(SignalContract.SignalEntry.COLUMN_ARTICLE_ID, articleID);
                if (mSignalMap.containsKey(articleID)) {
                    bitvalBookRead = mSignalMap.get(articleID);
                }
                readalready_inmap = bitvalBookRead | READALREADY_MASK;
                mSignalMap.put(articleID, readalready_inmap);
                contentValues.put(SignalContract.SignalEntry.COLUMN_READALREADY, 1);
                mContext.getContentResolver().insert(
                        uri,
                        contentValues);


                //set the background color accordingly
                ViewGroup twoupLevelLayout = ((ViewGroup) ((ViewGroup) view.getParent()).getParent());
                if (view.getTag(R.string.VIEWTAG_BACKGROUND_LAYOUT_ID) != null) {
                    twoupLevelLayout.findViewById(
                            (int) view.getTag(R.string.VIEWTAG_BACKGROUND_LAYOUT_ID))
                            .setBackgroundResource(R.color.after_reading_color);
                }
                if (view.getTag(R.string.VIEWTAG_BACKGROUND_BOOKMARK_ID) != null) {
                    twoupLevelLayout.findViewById(
                            (int) view.getTag(R.string.VIEWTAG_BACKGROUND_BOOKMARK_ID))
                            .setBackgroundResource(R.color.after_reading_color);
                }
                if (view.getTag(R.string.VIEWTAG_BACKGROUND_SHARE_ID) != null) {
                    twoupLevelLayout.findViewById(
                            (int) view.getTag(R.string.VIEWTAG_BACKGROUND_SHARE_ID))
                            .setBackgroundResource(R.color.after_reading_color);
                }
                if (view.getTag(R.string.VIEWTAG_BACKGROUND_EXPANDLESS_ID) != null) {
                    twoupLevelLayout.findViewById(
                            (int) view.getTag(R.string.VIEWTAG_BACKGROUND_EXPANDLESS_ID))
                            .setBackgroundResource(R.color.after_reading_color);
                }
                if (view.getTag(R.string.VIEWTAG_BACKGROUND_EXPAND_XXX_SB_ID) != null) {
                    twoupLevelLayout.findViewById(
                            (int) view.getTag(R.string.VIEWTAG_BACKGROUND_EXPAND_XXX_SB_ID))
                            .setBackgroundResource(R.color.after_reading_color);
                }

                //should start detailactivity if available
                Log.d(TAG, "---------------------> starting detail activity articleid="
                        + articleID
                        + ",finalurl="
                        + finalurl);
                mClickHandler.onClickDetailNews(articleID, finalurl);
            }
        };

        //create the onclicklistener to handle the action  when user click on bookmark icon
        mbookmarkClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int bookmarkalready_inmap = 0;
                int bitvalBookRead = 0;
                Uri uri = SignalContract.SignalEntry.CONTENT_URI;
                int articleID = (int) view.getTag(R.string.VIEWTAG_ARTICLEID);

                Log.d(TAG, " ib_bookmarkButton onclick 1");
                ContentValues contentValues = new ContentValues();
                contentValues.put(SignalContract.SignalEntry.COLUMN_ARTICLE_ID, articleID);

                if (view.isSelected() == false) {
                    //set it as bookmarked
                    view.setSelected(true);

                    if (mSignalMap.containsKey(articleID)) {
                        bitvalBookRead = mSignalMap.get(articleID);
                    }
                    bookmarkalready_inmap = bitvalBookRead | BOOKMARKALREADY_MASK;
                    mSignalMap.put(articleID, bookmarkalready_inmap);
                    contentValues.put(SignalContract.SignalEntry.COLUMN_BOOKMARKALREADY, 1);
                    Log.d(TAG, " ib_bookmarkButton onclick 2 = bitvalBookRead"
                            + bitvalBookRead
                            + ", bookmarkalready_inmap="
                            + bookmarkalready_inmap);
                    //let the hosting fragment to handle
                    mClickHandler.onClickBookmarkArticleStoreOrRemove(articleID, true);


                } else {
                    //set it off
                    view.setSelected(false);

                    if (mSignalMap.containsKey(articleID)) {
                        bitvalBookRead = mSignalMap.get(articleID);
                    }
                    bookmarkalready_inmap = bitvalBookRead & ~BOOKMARKALREADY_MASK;
                    mSignalMap.put(articleID, bookmarkalready_inmap);
                    contentValues.put(SignalContract.SignalEntry.COLUMN_BOOKMARKALREADY, 0);
                    Log.d(TAG, " ib_bookmarkButton onclick 3 = bitvalBookRead"
                            + bitvalBookRead
                            + ", bookmarkalready_inmap="
                            + bookmarkalready_inmap);
                    //let the hosting fragment to handle
                    mClickHandler.onClickBookmarkArticleStoreOrRemove(articleID, false);

                }
                //update the database either on/off
                mContext.getContentResolver().insert(
                        uri,
                        contentValues);
            }
        };

        //create the onclicklistener to handle the action  when user click on share icon
        mshareClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, " ib_sharebutton 1");
                String finalurl = (String) view.getTag(R.string.VIEWTAG_FINALURL);
                String title = (String) view.getTag(R.string.VIEWTAG_TITLE);

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = title + "\n" + finalurl;
                String shareSub = title;
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

                //start the activity for choosing app to handle sharing
                mContext.startActivity(Intent.createChooser(sharingIntent, "Share using"));
                Log.d(TAG, " ib_sharebutton 2");

            }
        };
        Log.d(TAG, " FirstLevelNewsAdapter constructor ");
    }


    /**
     * create viewholder to display recyclerview item
     *
     * @param viewGroup viewgroup that viewholder resides on
     * @param viewType  which viewtype of this viewholder should be created
     * @return viewholder holds each recycler item
     */
    @Override
    public FirstLevelNewsAdapter.FirstLevelNewsAdapterViewHolder
    onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        int layoutId;
        View view;

        layoutId = R.layout.article_list_item;
        view = LayoutInflater.from(mContext).inflate(layoutId, viewGroup, false);

        view.setFocusable(true);
        Log.d(TAG, "FirstLevelNewsAdapter.FirstLevelNewsAdapterViewHolder  onCreateViewHolder");

        return new FirstLevelNewsAdapter.FirstLevelNewsAdapterViewHolder(view);

    }

    /**
     * bind the necessary information to display for specific recycler item
     *
     * @param FirstLevelNewsAdapterViewHolder viewholder to hold the information for display
     * @param position                        The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(FirstLevelNewsAdapter.FirstLevelNewsAdapterViewHolder FirstLevelNewsAdapterViewHolder,
                                 int position) {
        Log.d(TAG, " FirstLevelNewsAdapter.FirstLevelNewsAdapterViewHolder onBindViewHolder 1 " + position);
        Log.d(TAG, " FirstLevelNewsAdapter.FirstLevelNewsAdapterViewHolder onBindViewHolder 1.1 cursor.uri=" + mCursor.getNotificationUri());
        Log.d(TAG, " FirstLevelNewsAdapter.FirstLevelNewsAdapterViewHolder onBindViewHolder 1.2 cursor.count=" + mCursor.getCount());
        Log.d(TAG, " FirstLevelNewsAdapter.FirstLevelNewsAdapterViewHolder onBindViewHolder 1.3 cursor=" + mCursor);

        mCursor.moveToPosition(position);


        final int articlePrimaryID = mCursor.getInt(FragArticleTableContract.INDEX_RAWQUERY_PAGINATION_ARTICLE_ID);
        final String finalurl = mCursor.getString(FragArticleTableContract.INDEX_RAWQUERY_PAGINATION_FINALURL);


        //configure the thumbnail
        if (mCursor.getString(FragArticleTableContract.INDEX_RAWQUERY_PAGINATION_IMAGEURL)
                .compareTo("EMPTYSTRINGVALUE") == 0) {
            FirstLevelNewsAdapterViewHolder.itemthumbnailView.setImageResource(R.drawable.ic_logo_hourglass_question);
        } else {
            //load the image
            GlideApp.with(mContext)
                    .load(mCursor.getString(FragArticleTableContract.INDEX_RAWQUERY_PAGINATION_IMAGEURL))
                    .placeholder(R.drawable.ic_tmp_icon)
                    .fitCenter()
                    .into(FirstLevelNewsAdapterViewHolder.itemthumbnailView);
        }
        FirstLevelNewsAdapterViewHolder.itemthumbnailView.setOnClickListener(mcallDetailActivity);
        //attach more information for further processing when user click on this view
        setupTAGInfo((View) FirstLevelNewsAdapterViewHolder.itemthumbnailView, articlePrimaryID,
                finalurl, R.id.constraintlayout_item_primary,
                R.id.ib_bookmark, R.id.ib_share, R.id.ib_expandless);


        //configure the source icon
        int pagination_firstsubdomainid = mCursor.getInt(FragArticleTableContract.INDEX_RAWQUERY_PAGINATION_FIRSTSUBDOMAINTABLE_ID);
        ArrayList<String> sourceIconURLChiName = SourceInfo.getInstance()
                .getSourceIconURLChiName(pagination_firstsubdomainid);

        //load the image
        GlideApp.with(mContext)
                .load(sourceIconURLChiName.get(SourceInfo.ARRAY_SOURCEICONURL_POS))
                .placeholder(R.drawable.ic_tmp_icon)
                .fitCenter()
                .into(FirstLevelNewsAdapterViewHolder.newsourceiconView);
        FirstLevelNewsAdapterViewHolder.newsourceiconView.setOnClickListener(mcallDetailActivity);
        //attach more information for further processing when user click on this view
        setupTAGInfo((View) FirstLevelNewsAdapterViewHolder.itemthumbnailView, articlePrimaryID,
                finalurl, R.id.constraintlayout_item_primary,
                R.id.ib_bookmark, R.id.ib_share, R.id.ib_expandless);


        Log.d(TAG, " FirstLevelNewsAdapter.FirstLevelNewsAdapterViewHolder onBindViewHolder 2 ");


        //configure the source text view
        FirstLevelNewsAdapterViewHolder.tv_domainsourceView.setText(sourceIconURLChiName.get(SourceInfo.ARRAY_NAME_POS));
        FirstLevelNewsAdapterViewHolder.tv_domainsourceView.setOnClickListener(mcallDetailActivity);
        //attach more information for further processing when user click on this view
        setupTAGInfo((View) FirstLevelNewsAdapterViewHolder.itemthumbnailView, articlePrimaryID,
                finalurl, R.id.constraintlayout_item_primary,
                R.id.ib_bookmark, R.id.ib_share, R.id.ib_expandless);


        //find out this article has been read/bookmark already?
        int bookmarkalready = 0;
        if (mCursor.isNull(FragArticleTableContract.INDEX_RAWQUERY_SIGNAL_BOOKMARKALREADY) != true) {
            bookmarkalready = mCursor.getInt(FragArticleTableContract.INDEX_RAWQUERY_SIGNAL_BOOKMARKALREADY);
        }
        int readalready = 0;
        if (mCursor.isNull(FragArticleTableContract.INDEX_RAWQUERY_SIGNAL_READALREADY) != true) {
            readalready = mCursor.getInt(FragArticleTableContract.INDEX_RAWQUERY_SIGNAL_READALREADY);
        }

        int bookmarkalready_inmap = 0;
        int readalready_inmap = 0;
        int bitvalBookRead = 0;

        //check the signal map
        if (mSignalMap.containsKey(articlePrimaryID)) {
            bitvalBookRead = mSignalMap.get(articlePrimaryID);
            bookmarkalready_inmap = bitvalBookRead & BOOKMARKALREADY_MASK;
            readalready_inmap = bitvalBookRead & READALREADY_MASK;
        } else {
            mSignalMap.put(articlePrimaryID, bookmarkalready | readalready);
        }

        if ((bookmarkalready != 0)
                || (bookmarkalready_inmap != 0)) {
            FirstLevelNewsAdapterViewHolder.ib_bookmarkButton.setSelected(true);
        } else {
            FirstLevelNewsAdapterViewHolder.ib_bookmarkButton.setSelected(false);
        }
        if ((readalready != 0)
                || (readalready_inmap != 0)) {
            FirstLevelNewsAdapterViewHolder.ib_bookmarkButton.setBackgroundResource(R.color.after_reading_color);
            FirstLevelNewsAdapterViewHolder.ib_shareButton.setBackgroundResource(R.color.after_reading_color);
            FirstLevelNewsAdapterViewHolder.ib_expandlessButton.setBackgroundResource(R.color.after_reading_color);
            ((ViewGroup) FirstLevelNewsAdapterViewHolder.ib_expandlessButton.getParent()).setBackgroundResource(R.color.after_reading_color);
        } else {
            ((ViewGroup) FirstLevelNewsAdapterViewHolder.ib_expandlessButton.getParent()).setBackgroundResource(0);
            //use default background
        }
        Log.d(TAG, " FirstLevelNewsAdapter.FirstLevelNewsAdapterViewHolder onBindViewHolder 3 ");
        //configure bookmark button
        FirstLevelNewsAdapterViewHolder.ib_bookmarkButton.setOnClickListener(mbookmarkClickListener);
        FirstLevelNewsAdapterViewHolder.ib_bookmarkButton.setTag(R.string.VIEWTAG_ARTICLEID, articlePrimaryID);


        final String primarytitle = mCursor.getString(FragArticleTableContract.INDEX_RAWQUERY_PAGINATION_TITLE);

        //configure the sharebutton
        FirstLevelNewsAdapterViewHolder.ib_shareButton.setOnClickListener(mshareClickListener);
        FirstLevelNewsAdapterViewHolder.ib_shareButton.setTag(R.string.VIEWTAG_ARTICLEID, articlePrimaryID);
        FirstLevelNewsAdapterViewHolder.ib_shareButton.setTag(R.string.VIEWTAG_FINALURL, finalurl);
        FirstLevelNewsAdapterViewHolder.ib_shareButton.setTag(R.string.VIEWTAG_TITLE, primarytitle);


        Log.d(TAG, " FirstLevelNewsAdapter.FirstLevelNewsAdapterViewHolder onBindViewHolder 4 ");

        //configure the title textview
        FirstLevelNewsAdapterViewHolder.primarytitleView.setText(primarytitle);
        FirstLevelNewsAdapterViewHolder.primarytitleView.setOnClickListener(mcallDetailActivity);
        //attach more information for further processing when user click on this view
        setupTAGInfo((View) FirstLevelNewsAdapterViewHolder.primarytitleView, articlePrimaryID,
                finalurl, R.id.constraintlayout_item_primary,
                R.id.ib_bookmark, R.id.ib_share, R.id.ib_expandless);


        //configure the timestamp textview
        int timestampondoc = mCursor.getInt(FragArticleTableContract.INDEX_RAWQUERY_PAGINATION_TIMESTAMPONDOC);
        String timestampondocTranslatedString = GongTimeUtil.getDisplayTimeStringFromData((long) timestampondoc);
        FirstLevelNewsAdapterViewHolder.tv_dateView.setText(timestampondocTranslatedString);
        FirstLevelNewsAdapterViewHolder.tv_dateView.setOnClickListener(mcallDetailActivity);
        FirstLevelNewsAdapterViewHolder.tv_dateView.setTag(R.string.VIEWTAG_ARTICLEID, articlePrimaryID);
        FirstLevelNewsAdapterViewHolder.tv_dateView.setTag(R.string.VIEWTAG_FINALURL, finalurl);


        int similiaritiescount = mCursor.getInt(FragArticleTableContract.INDEX_RAWQUERY_PAGINATION_SIMILARITIESCOUNT);


        int expandalready_inmap = 0;
        int checkbitval = 0;


        //check whether this article has one or two or more related articles and
        // set the layout display or not accordingly
        if (mSignalMap.containsKey(articlePrimaryID)) {
            checkbitval = mSignalMap.get(articlePrimaryID);
            expandalready_inmap = checkbitval & EXPANDALREADY_MASK;
        }
        if (expandalready_inmap != 0) {
            FirstLevelNewsAdapterViewHolder.ll_expandlistitem.setVisibility(View.VISIBLE);
            FirstLevelNewsAdapterViewHolder.ib_expandlessButton.animate().rotation(180).setDuration(300).start();

        } else {
            FirstLevelNewsAdapterViewHolder.ll_expandlistitem.setVisibility(View.GONE);
            FirstLevelNewsAdapterViewHolder.ib_expandlessButton.animate().rotation(0).setDuration(300).start();

        }


        Log.d(TAG, " FirstLevelNewsAdapter.FirstLevelNewsAdapterViewHolder onBindViewHolder 5 similiaritiescount= " + similiaritiescount);
        if ((!mUsingSelectedArchivetoDisplay) && (similiaritiescount > 0)) {

            //setup the onclick listener of expandlessbutton
            FirstLevelNewsAdapterViewHolder.ib_expandlessButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LinearLayout expandView = ((LinearLayout) ((ConstraintLayout) view.getParent()).getParent())
                            .findViewById(R.id.expandlistitem);

                    if (expandView.getVisibility() == View.VISIBLE) {
                        //currently the expand layout is visible, need to roll back up
                        int expandalready_inmap = 0;
                        int checkbitval = 0;
                        if (mSignalMap.containsKey(articlePrimaryID)) {
                            checkbitval = mSignalMap.get(articlePrimaryID);
                        }
                        expandalready_inmap = checkbitval & ~EXPANDALREADY_MASK;
                        mSignalMap.put(articlePrimaryID, expandalready_inmap);

                        expandView.setVisibility(View.GONE);
                        view.animate().rotation(0).setDuration(300).start();
                    } else {
                        //currently the expand layout is invisible, need to expand
                        int expandalready_inmap = 0;
                        int checkbitval = 0;
                        if (mSignalMap.containsKey(articlePrimaryID)) {
                            checkbitval = mSignalMap.get(articlePrimaryID);
                        }
                        expandalready_inmap = checkbitval | EXPANDALREADY_MASK;
                        mSignalMap.put(articlePrimaryID, expandalready_inmap);

                        expandView.setVisibility(View.VISIBLE);
                        view.animate().rotation(180).setDuration(300).start();
                    }
                }
            });

            //configure expandless button
            FirstLevelNewsAdapterViewHolder.ib_expandlessButton.setVisibility(View.VISIBLE);
            if (FirstLevelNewsAdapterViewHolder.ll_expandlistitem.getVisibility() == View.VISIBLE) {
                Log.d(TAG, "ll_expandlistitem --- > view.visible articleprimaryid=" + articlePrimaryID);
                FirstLevelNewsAdapterViewHolder.ib_expandlessButton.animate().rotation(180).setDuration(300).start();

            } else if (FirstLevelNewsAdapterViewHolder.ll_expandlistitem.getVisibility() == View.GONE) {
                Log.d(TAG, "ll_expandlistitem --- > view.gone articleprimaryid=" + articlePrimaryID);
                FirstLevelNewsAdapterViewHolder.ib_expandlessButton.animate().rotation(0).setDuration(300).start();
            }
            FirstLevelNewsAdapterViewHolder.v_dividerview1.setVisibility(View.VISIBLE);
            FirstLevelNewsAdapterViewHolder.v_dividerview3.setVisibility(View.VISIBLE);


            //configure the lessbutton
            FirstLevelNewsAdapterViewHolder.ib_lessbuttonButton.setVisibility(View.VISIBLE);
            FirstLevelNewsAdapterViewHolder.ib_lessbuttonButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //this button is for roll back up the view
                    LinearLayout expandView = ((LinearLayout) ((ConstraintLayout) view.getParent()).getParent())
                            .findViewById(R.id.expandlistitem);

                    expandView.setVisibility(View.GONE);
                    ImageButton imageButton = ((LinearLayout) ((LinearLayout) ((ConstraintLayout) view.getParent()).getParent()).getParent())
                            .findViewById(R.id.ib_expandless);
                    imageButton.animate().rotation(0).setDuration(300).start();
                }
            });


            // a json string contains related article information,
            // decode the json string and get the information out
            String entryJSONString = mCursor.getString(FragArticleTableContract.INDEX_RAWQUERY_PAGINATION_ENTRY);
            Log.d(TAG, " FirstLevelNewsAdapter.FirstLevelNewsAdapterViewHolder onBindViewHolder 6 ");

            int count_similiaritiescount = 0;
            //related article list
            List<ArticleInfo> articlelists = new ArrayList<ArticleInfo>();
            //store the rearranged article list after sort it with preferred domain list
            List<ArticleInfo> resultList = new ArrayList<ArticleInfo>();
            try {
                JSONTokener tokener = new JSONTokener(entryJSONString);
                Log.d(TAG, " FirstLevelNewsAdapter.FirstLevelNewsAdapterViewHolder onBindViewHolder 7 ");

                //extract information from json string
                while (tokener.more()) {
                    Log.d(TAG, " FirstLevelNewsAdapter.FirstLevelNewsAdapterViewHolder onBindViewHolder 8 ");

                    JSONObject entryobj = (JSONObject) tokener.nextValue();
                    String timenid = "";

                    Iterator<String> iterator = entryobj.keys();
                    if (entryobj.names().length() > 0) {
                        while (iterator.hasNext()) {
                            // it should have only one?
                            timenid = iterator.next();
                            Log.d(TAG, " timenid = " + timenid);

                            int timestampondoc_timenid = Integer.valueOf(FragArticleTableContract
                                    .decodeGetTimestampondoc(timenid));
                            int articleid_timenid = Integer.valueOf(FragArticleTableContract
                                    .decodeGetId(timenid));

                            JSONObject entryobj_underneath = (JSONObject) entryobj.get(timenid);

                            int firstsubdomaintable_id = (int) entryobj_underneath.getLong(
                                    FragArticleTableContract.FragArticleEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID);
                            String title = entryobj_underneath.getString(
                                    FragArticleTableContract.FragArticleEntry.COLUMN_TITLE);

                            String imageurl = entryobj_underneath.getString(
                                    FragArticleTableContract.FragArticleEntry.COLUMN_IMAGEURL);

                            String finalurl_underneath = entryobj_underneath.getString(
                                    FragArticleTableContract.FragArticleEntry.COLUMN_FINALURL);
                            Log.d(TAG, " FirstLevelNewsAdapter.FirstLevelNewsAdapterViewHolder onBindViewHolder 9 timestampondoc_timenid= " +
                                    " articleid_timenid=" + articleid_timenid + " firstsubdomaintable_id=" + firstsubdomaintable_id + " title=" + title +
                                    " imageurl=" + imageurl + " finalurl=" + finalurl);

                            articlelists.add(new ArticleInfo(firstsubdomaintable_id,
                                    title,
                                    imageurl,
                                    articleid_timenid,
                                    finalurl_underneath,
                                    timestampondoc_timenid));

                            count_similiaritiescount++;

                        }
                    }
                }
                Log.d(TAG, " FirstLevelNewsAdapter.FirstLevelNewsAdapterViewHolder onBindViewHolder 10 =" + articlelists.size());

                //sort the article list with preferred domain information
                resultList = ArticleInfo.sortByPreferredDomain(articlelists,
                        GongPreference.getPreferredlist(mContext));
                Log.d(TAG, " FirstLevelNewsAdapter.FirstLevelNewsAdapterViewHolder onBindViewHolder 10.1=" + resultList.size());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //assign the first related article
            ArticleInfo artInfo = resultList.get(0);
            Log.d(TAG, " FirstLevelNewsAdapter.FirstLevelNewsAdapterViewHolder onBindViewHolder 11 ");

            FirstLevelNewsAdapterViewHolder.tv_titleexpandoneView.setVisibility(View.VISIBLE);
            FirstLevelNewsAdapterViewHolder.tv_titleexpandoneView.setText(artInfo.getTitle());


            ArrayList<String> oneSourceIconURLChiName = SourceInfo.getInstance()
                    .getSourceIconURLChiName(artInfo.getFirstsubdomaintable_id());

            //load the source icon for expand one layout
            FirstLevelNewsAdapterViewHolder.iv_sourceiconexpandoneView.setVisibility(View.VISIBLE);
            GlideApp.with(mContext)
                    .load(oneSourceIconURLChiName.get(SourceInfo.ARRAY_SOURCEICONURL_POS))
                    .placeholder(R.drawable.ic_tmp_icon)
                    .fitCenter()
                    .into(FirstLevelNewsAdapterViewHolder.iv_sourceiconexpandoneView);
            FirstLevelNewsAdapterViewHolder.iv_sourceiconexpandoneView.setVisibility(View.VISIBLE);

            Log.d(TAG, " FirstLevelNewsAdapter.FirstLevelNewsAdapterViewHolder onBindViewHolder 12 ");

            //configure the domain source text view
            FirstLevelNewsAdapterViewHolder.tv_domainsourceexpandoneView.setVisibility(View.VISIBLE);
            FirstLevelNewsAdapterViewHolder.tv_domainsourceexpandoneView.setText(
                    oneSourceIconURLChiName.get(SourceInfo.ARRAY_NAME_POS));


            //configure the timestamp textview
            String timeString = GongTimeUtil.getDisplayTimeStringFromData((long) artInfo.getTimestampondoc());
            FirstLevelNewsAdapterViewHolder.tv_dateexpandoneView.setVisibility(View.VISIBLE);
            FirstLevelNewsAdapterViewHolder.tv_dateexpandoneView.setText(timeString);
            FirstLevelNewsAdapterViewHolder.tv_dateexpandoneView.setOnClickListener(mcallDetailActivity);


            //configure the expand one layout and attach information for further processing when user click it
            FirstLevelNewsAdapterViewHolder.article_list_item_one_expand_layout.setVisibility(View.VISIBLE);
            FirstLevelNewsAdapterViewHolder.article_list_item_one_expand_layout.setOnClickListener(mcallDetailActivity);
            FirstLevelNewsAdapterViewHolder.article_list_item_one_expand_layout.setTag(R.string.VIEWTAG_ARTICLEID, artInfo.getID());
            FirstLevelNewsAdapterViewHolder.article_list_item_one_expand_layout.setTag(R.string.VIEWTAG_FINALURL, artInfo.getFinalurl());
            FirstLevelNewsAdapterViewHolder.article_list_item_one_expand_layout.setTag(R.string.VIEWTAG_BACKGROUND_LAYOUT_ID, R.id.article_list_item_one_expand_layout);
            FirstLevelNewsAdapterViewHolder.article_list_item_one_expand_layout.setTag(R.string.VIEWTAG_BACKGROUND_BOOKMARK_ID, R.id.ib_bookmark1);
            FirstLevelNewsAdapterViewHolder.article_list_item_one_expand_layout.setTag(R.string.VIEWTAG_BACKGROUND_SHARE_ID, R.id.ib_share1);
            FirstLevelNewsAdapterViewHolder.article_list_item_one_expand_layout.setTag(R.string.VIEWTAG_BACKGROUND_EXPAND_XXX_SB_ID, R.id.article_list_item_one_sb_expand_layout);


            FirstLevelNewsAdapterViewHolder.ll_article_one_sb_expand.setVisibility(View.VISIBLE);
            FirstLevelNewsAdapterViewHolder.ib_bookmarkbutton1.setOnClickListener(mbookmarkClickListener);
            FirstLevelNewsAdapterViewHolder.ib_bookmarkbutton1.setTag(R.string.VIEWTAG_ARTICLEID, artInfo.getID());

            bookmarkalready_inmap = 0;
            readalready_inmap = 0;
            bitvalBookRead = 0;

            //find out whether it need to change background color
            if (mInitupdateSignalMapFromCursor) {
                if (mSignalMap.containsKey(artInfo.getID())) {
                    bitvalBookRead = mSignalMap.get(artInfo.getID());
                    bookmarkalready_inmap = bitvalBookRead & BOOKMARKALREADY_MASK;
                    readalready_inmap = bitvalBookRead & READALREADY_MASK;
                }

                if (bookmarkalready_inmap == 0) {
                    FirstLevelNewsAdapterViewHolder.ib_bookmarkbutton1.setSelected(false);
                } else {
                    FirstLevelNewsAdapterViewHolder.ib_bookmarkbutton1.setSelected(true);
                }
                if (readalready_inmap != 0) {
                    FirstLevelNewsAdapterViewHolder.ib_bookmarkbutton1.setBackgroundResource(R.color.after_reading_color);
                    FirstLevelNewsAdapterViewHolder.ib_sharebutton1.setBackgroundResource(R.color.after_reading_color);
                    FirstLevelNewsAdapterViewHolder.ll_article_one_sb_expand.setBackgroundResource(R.color.after_reading_color);
                    FirstLevelNewsAdapterViewHolder.article_list_item_one_expand_layout.setBackgroundResource(R.color.after_reading_color);
                }

            }

            FirstLevelNewsAdapterViewHolder.ib_sharebutton1.setOnClickListener(mshareClickListener);
            FirstLevelNewsAdapterViewHolder.ib_sharebutton1.setTag(R.string.VIEWTAG_FINALURL, artInfo.getFinalurl());
            FirstLevelNewsAdapterViewHolder.ib_sharebutton1.setTag(R.string.VIEWTAG_TITLE, artInfo.getTitle());


            if (similiaritiescount == 1) {
                //only one related article
                FirstLevelNewsAdapterViewHolder.article_list_item_two_expand_layout.setVisibility(View.GONE);
                FirstLevelNewsAdapterViewHolder.ll_article_two_sb_expand.setVisibility(View.GONE);
                FirstLevelNewsAdapterViewHolder.tv_expandtextView.setVisibility(View.GONE);

                Log.d(TAG, " FirstLevelNewsAdapter.FirstLevelNewsAdapterViewHolder onBindViewHolder 13 ");

            } else {
                //more than one related articles
                artInfo = resultList.get(1);

                //configure the two expand layout
                FirstLevelNewsAdapterViewHolder.article_list_item_two_expand_layout.setVisibility(View.VISIBLE);
                FirstLevelNewsAdapterViewHolder.article_list_item_two_expand_layout.setOnClickListener(mcallDetailActivity);
                FirstLevelNewsAdapterViewHolder.article_list_item_two_expand_layout.setTag(R.string.VIEWTAG_ARTICLEID, artInfo.getID());
                FirstLevelNewsAdapterViewHolder.article_list_item_two_expand_layout.setTag(R.string.VIEWTAG_FINALURL, artInfo.getFinalurl());
                FirstLevelNewsAdapterViewHolder.article_list_item_two_expand_layout.setTag(R.string.VIEWTAG_BACKGROUND_LAYOUT_ID, R.id.article_list_item_two_expand_layout);
                FirstLevelNewsAdapterViewHolder.article_list_item_two_expand_layout.setTag(R.string.VIEWTAG_BACKGROUND_BOOKMARK_ID, R.id.ib_bookmark2);
                FirstLevelNewsAdapterViewHolder.article_list_item_two_expand_layout.setTag(R.string.VIEWTAG_BACKGROUND_SHARE_ID, R.id.ib_share2);
                FirstLevelNewsAdapterViewHolder.article_list_item_two_expand_layout.setTag(R.string.VIEWTAG_BACKGROUND_EXPAND_XXX_SB_ID, R.id.article_list_item_two_sb_expand_layout);

                FirstLevelNewsAdapterViewHolder.ll_article_two_sb_expand.setVisibility(View.VISIBLE);


                FirstLevelNewsAdapterViewHolder.v_dividerview2.setVisibility(View.VISIBLE);


                //configure the title textview
                FirstLevelNewsAdapterViewHolder.tv_titleexpandtwoView.setVisibility(View.VISIBLE);
                FirstLevelNewsAdapterViewHolder.tv_titleexpandtwoView.setText(artInfo.getTitle());

                Log.d(TAG, " FirstLevelNewsAdapter.FirstLevelNewsAdapterViewHolder onBindViewHolder 14 ");

                ArrayList<String> twoSourceIconURLChiName = SourceInfo.getInstance()
                        .getSourceIconURLChiName(artInfo.getFirstsubdomaintable_id());

                //configure the source icon in expand two layout
                FirstLevelNewsAdapterViewHolder.iv_sourceiconexpandtwoView.setVisibility(View.VISIBLE);
                GlideApp.with(mContext)
                        .load(twoSourceIconURLChiName.get(SourceInfo.ARRAY_SOURCEICONURL_POS))
                        .placeholder(R.drawable.ic_tmp_icon)
                        .fitCenter()
                        .into(FirstLevelNewsAdapterViewHolder.iv_sourceiconexpandtwoView);
                FirstLevelNewsAdapterViewHolder.iv_sourceiconexpandtwoView.setVisibility(View.VISIBLE);

                //configure domain source textview in expand two layout
                FirstLevelNewsAdapterViewHolder.tv_domainsourceexpandtwoView.setVisibility(View.VISIBLE);
                FirstLevelNewsAdapterViewHolder.tv_domainsourceexpandtwoView.setText(
                        twoSourceIconURLChiName.get(SourceInfo.ARRAY_NAME_POS));

                //configure timestamp textview in expand two layout
                timeString = GongTimeUtil.getDisplayTimeStringFromData((long) artInfo.getTimestampondoc());
                FirstLevelNewsAdapterViewHolder.tv_dateexpandtwoView.setVisibility(View.VISIBLE);
                FirstLevelNewsAdapterViewHolder.tv_dateexpandtwoView.setText(timeString);

                //configure bookmark button in expand two layout
                FirstLevelNewsAdapterViewHolder.ib_bookmarkbutton2.setOnClickListener(mbookmarkClickListener);
                FirstLevelNewsAdapterViewHolder.ib_bookmarkbutton2.setTag(R.string.VIEWTAG_ARTICLEID, artInfo.getID());
                bookmarkalready_inmap = 0;
                readalready_inmap = 0;
                bitvalBookRead = 0;

                //check whether it need to change background color
                if (mInitupdateSignalMapFromCursor) {
                    if (mSignalMap.containsKey(artInfo.getID())) {
                        bitvalBookRead = mSignalMap.get(artInfo.getID());
                        bookmarkalready_inmap = bitvalBookRead & BOOKMARKALREADY_MASK;
                        readalready_inmap = bitvalBookRead & READALREADY_MASK;
                    }

                    if (bookmarkalready_inmap == 0) {
                        FirstLevelNewsAdapterViewHolder.ib_bookmarkbutton2.setSelected(false);
                    } else {
                        FirstLevelNewsAdapterViewHolder.ib_bookmarkbutton2.setSelected(true);
                    }
                    if (readalready_inmap != 0) {
                        FirstLevelNewsAdapterViewHolder.ib_bookmarkbutton2.setBackgroundResource(R.color.after_reading_color);
                        FirstLevelNewsAdapterViewHolder.ib_sharebutton2.setBackgroundResource(R.color.after_reading_color);
                        FirstLevelNewsAdapterViewHolder.ll_article_two_sb_expand.setBackgroundResource(R.color.after_reading_color);
                        FirstLevelNewsAdapterViewHolder.article_list_item_two_expand_layout.setBackgroundResource(R.color.after_reading_color);
                    }

                }

                //configure share button on expand two layout
                FirstLevelNewsAdapterViewHolder.ib_sharebutton2.setOnClickListener(mshareClickListener);
                FirstLevelNewsAdapterViewHolder.ib_sharebutton2.setTag(R.string.VIEWTAG_FINALURL, artInfo.getFinalurl());
                FirstLevelNewsAdapterViewHolder.ib_sharebutton2.setTag(R.string.VIEWTAG_TITLE, artInfo.getTitle());


                if (similiaritiescount <= 2) {
                    //only two or less related articles
                    FirstLevelNewsAdapterViewHolder.tv_expandtextView.setVisibility(View.GONE);

                } else {
                    //more than two related articles
                    FirstLevelNewsAdapterViewHolder.tv_expandtextView.setVisibility(View.VISIBLE);
                    ArticleInfo currentArticleInfo = new ArticleInfo(
                            mCursor.getInt(FragArticleTableContract.INDEX_RAWQUERY_PAGINATION_FIRSTSUBDOMAINTABLE_ID),
                            mCursor.getString(FragArticleTableContract.INDEX_RAWQUERY_PAGINATION_TITLE),
                            mCursor.getString(FragArticleTableContract.INDEX_RAWQUERY_PAGINATION_IMAGEURL),
                            mCursor.getInt(FragArticleTableContract.INDEX_RAWQUERY_PAGINATION_ARTICLE_ID),
                            mCursor.getString(FragArticleTableContract.INDEX_RAWQUERY_PAGINATION_FINALURL),
                            mCursor.getInt(FragArticleTableContract.INDEX_RAWQUERY_PAGINATION_TIMESTAMPONDOC)
                    );
                    //add the current article information to the list
                    resultList.add(0, currentArticleInfo);


                    //create JSON string and tag it
                    JSONArray resultJSONArray = new JSONArray();
                    int[] bookmarkreadarray = new int[resultList.size()];
                    int index = 0;

                    //create JSON string with related articles information
                    for (ArticleInfo article : resultList) {
                        try {

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put(FragArticleTableContract.FragArticleEntry.COLUMN_ARTICLEID,
                                    article.getID());
                            jsonObject.put(FragArticleTableContract.FragArticleEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID,
                                    article.getFirstsubdomaintable_id());
                            jsonObject.put(FragArticleTableContract.FragArticleEntry.COLUMN_TITLE,
                                    article.getTitle());
                            jsonObject.put(FragArticleTableContract.FragArticleEntry.COLUMN_FINALURL,
                                    article.getFinalurl());
                            jsonObject.put(FragArticleTableContract.FragArticleEntry.COLUMN_IMAGEURL,
                                    article.getImageurl());
                            jsonObject.put(FragArticleTableContract.FragArticleEntry.COLUMN_TIMESTAMPONDOC,
                                    article.getTimestampondoc());
                            jsonObject.put(SignalContract.SignalEntry.COLUMN_BOOKMARKALREADY,
                                    (long) (mSignalMap.containsKey(article.getID()) ? mSignalMap.get(articlePrimaryID) : 0));

                            resultJSONArray.put(jsonObject);

                            bookmarkreadarray[index++] = article.getID();
                            Log.d(TAG, " currentarticleinfo.id = " + currentArticleInfo.getID() + ",article.getID=" + article.getID());

                        } catch (Exception e) {
                            Log.d(TAG, "creating moredetailist ");
                        }
                    }
                    String resultStringresultJSONArray = resultJSONArray.toString();
                    //attach information when user click on the expand textview
                    FirstLevelNewsAdapterViewHolder.tv_expandtextView.
                            setTag(R.string.VIEWTAG_EXPANDTEXT_JSON_STRING, resultStringresultJSONArray);
                    FirstLevelNewsAdapterViewHolder.tv_expandtextView.
                            setTag(R.string.VIEWTAG_EXPANDTEXT_JSON_BOOKMARK_STRING, bookmarkreadarray);
                    FirstLevelNewsAdapterViewHolder.tv_expandtextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String jsonArticleString = (String) view.getTag(R.string.VIEWTAG_EXPANDTEXT_JSON_STRING);
                            int[] signalmaparray = (int[]) view.getTag(R.string.VIEWTAG_EXPANDTEXT_JSON_BOOKMARK_STRING);
                            JSONArray bookmarkJSONArray = new JSONArray();

                            if (signalmaparray.length > 0) {
                                //pass the latest signal map
                                for (int x : signalmaparray) {
                                    try {
                                        if ((mSignalMap.containsKey(x))
                                                && (mSignalMap.get(x) != 0)) {
                                            JSONObject jsonObject = new JSONObject();
                                            jsonObject.put(String.valueOf(x), mSignalMap.get(x));
                                            bookmarkJSONArray.put(jsonObject);

                                        }
                                    } catch (Exception e) {
                                        Log.d(TAG, " onclick  tv_expandtextView ");
                                    }
                                }
                                Log.d(TAG, " onclick  tv_expandtextView jsonarticlestring=" + jsonArticleString
                                        + ",       bookarmkjsonarray.tostring=" + bookmarkJSONArray.toString());
                                mClickHandler.onClickExpandNews(jsonArticleString, bookmarkJSONArray.toString());
                            } else {
                                Log.d(TAG, " onclick  tv_expandtextView ERROR bookmarkarray.length=" + signalmaparray.length);
                            }

                        }
                    });

                }

                Log.d(TAG, " FirstLevelNewsAdapter.FirstLevelNewsAdapterViewHolder onBindViewHolder 15 ");


            }

            Log.d(TAG, " FirstLevelNewsAdapter.FirstLevelNewsAdapterViewHolder onBindViewHolder 16 ");

        } else {

            //there are no related articles
            FirstLevelNewsAdapterViewHolder.ib_expandlessButton.setVisibility(View.INVISIBLE);

            FirstLevelNewsAdapterViewHolder.tv_titleexpandoneView.setVisibility(View.GONE);
            FirstLevelNewsAdapterViewHolder.iv_sourceiconexpandoneView.setVisibility(View.GONE);
            FirstLevelNewsAdapterViewHolder.tv_domainsourceexpandoneView.setVisibility(View.GONE);
            FirstLevelNewsAdapterViewHolder.tv_dateexpandoneView.setVisibility(View.GONE);

            FirstLevelNewsAdapterViewHolder.tv_titleexpandtwoView.setVisibility(View.GONE);
            FirstLevelNewsAdapterViewHolder.iv_sourceiconexpandtwoView.setVisibility(View.GONE);
            FirstLevelNewsAdapterViewHolder.tv_domainsourceexpandtwoView.setVisibility(View.GONE);
            FirstLevelNewsAdapterViewHolder.tv_dateexpandtwoView.setVisibility(View.GONE);

            FirstLevelNewsAdapterViewHolder.ib_lessbuttonButton.setVisibility(View.GONE);
            FirstLevelNewsAdapterViewHolder.tv_expandtextView.setVisibility(View.GONE);

            FirstLevelNewsAdapterViewHolder.v_dividerview1.setVisibility(View.GONE);
            FirstLevelNewsAdapterViewHolder.v_dividerview2.setVisibility(View.GONE);
            FirstLevelNewsAdapterViewHolder.v_dividerview3.setVisibility(View.GONE);
            Log.d(TAG, " FirstLevelNewsAdapter.FirstLevelNewsAdapterViewHolder onBindViewHolder 17 ");

        }

        Log.d(TAG, " FirstLevelNewsAdapter.FirstLevelNewsAdapterViewHolder onBindViewHolder 18 ");

    }

    /**
     * setup the tag information to the view for further processing when user click on it
     *
     * @param view         : view that will be set with tag information
     * @param id           : id associated with "view"
     * @param finalurl     : url associated with this "view"
     * @param layoutid     : id of a layout that this "view" reside on
     * @param bookmarkid   : id of bookmark icon on the same layout as the "view" on
     * @param shareid      : id of share icon on the same layout as the "view" on
     * @param expandlessid : id of expandless icon on the same layout as the "view" on
     */
    public static void setupTAGInfo(View view, int id, String finalurl,
                              int layoutid,
                              int bookmarkid,
                              int shareid,
                              int expandlessid) {
        view.setTag(R.string.VIEWTAG_ARTICLEID, id);
        view.setTag(R.string.VIEWTAG_FINALURL, finalurl);
        view.setTag(R.string.VIEWTAG_BACKGROUND_LAYOUT_ID, layoutid);
        view.setTag(R.string.VIEWTAG_BACKGROUND_BOOKMARK_ID, bookmarkid);
        view.setTag(R.string.VIEWTAG_BACKGROUND_SHARE_ID, shareid);
        view.setTag(R.string.VIEWTAG_BACKGROUND_EXPANDLESS_ID, expandlessid);
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available
     */
    @Override
    public int getItemCount() {
        Log.d(TAG, " FirstLevelNewsAdapter getitemcount 1 ");

        if (null == mCursor) return 0;
        Log.d(TAG, " FirstLevelNewsAdapter getitemcount 2 getcount=" + mCursor.getCount()
                + ", getnotificationuri=" + mCursor.getNotificationUri().toString());
        return mCursor.getCount();
    }

    /**
     * this app only have one type
     *
     * @param position index within our RecyclerView and Cursor
     * @return the view type
     */
    @Override
    public int getItemViewType(int position) {
        return -1;
    }

    /**
     * update the local cursor and refresh the display
     *
     * @param newCursor                     cursor with new information
     * @param usingSelectedArchivetoDisplay indicate whether the newCursor is from archive database or not
     */
    void swapCursor(Cursor newCursor, boolean usingSelectedArchivetoDisplay) {
        Log.d(TAG, " FirstLevelNewsAdapter swapcursor  ");

        mUsingSelectedArchivetoDisplay = usingSelectedArchivetoDisplay;
        mCursor = newCursor;
        if (mCursor != null) {
            Log.d(TAG, " FirstLevelNewsAdapter swapcursor  getcount=" + mCursor.getCount());
            Log.d(TAG, " FirstLevelNewsAdapter swapcursor  uri=" + mCursor.getNotificationUri());
        }
        notifyDataSetChanged();
    }


    /**
     * update the local cursor with new signal map informaiton
     *
     * @param newCursor cursor with new information
     */
    void updateSignalMapFromCursor(Cursor newCursor) {
        Log.d(TAG, " FirstLevelNewsAdapter  updateSignalMapFromCursor  ");

        if (newCursor != null) {
            Log.d(TAG, " FirstLevelNewsAdapter  updateSignalMapFromCursor  getcount=" + newCursor.getCount());
            //update local map information
            for (int index = 0; index < newCursor.getCount(); index++) {
                newCursor.moveToPosition(index);
                int articleID = newCursor.getInt(SignalContract.INDEX_ARTICLE_ID);
                int bookmarkalreadyFromCursor = newCursor.getInt(SignalContract.INDEX_BOOKMARKALREADY);
                int readalreadyFromCursor = newCursor.getInt(SignalContract.INDEX_READALREADY);
                int bitvalBookRead = 0;
                if (mSignalMap.containsKey(articleID)) {
                    bitvalBookRead = mSignalMap.get(articleID);
                }
                if (bookmarkalreadyFromCursor == 0) {
                    bitvalBookRead = bitvalBookRead & ~BOOKMARKALREADY_MASK;
                } else {
                    bitvalBookRead = bitvalBookRead | BOOKMARKALREADY_MASK;
                }
                if (readalreadyFromCursor == 0) {
                    bitvalBookRead = bitvalBookRead & ~READALREADY_MASK;
                } else {
                    bitvalBookRead = bitvalBookRead | READALREADY_MASK;

                }
                mSignalMap.put(articleID, bitvalBookRead);

            }

            mInitupdateSignalMapFromCursor = true;

        }
    }


    /**
     * viewholder for recyclerview item
     */
    class FirstLevelNewsAdapterViewHolder extends RecyclerView.ViewHolder {
        private final String TAG = FirstLevelNewsAdapter.FirstLevelNewsAdapterViewHolder.class.getSimpleName();

        final ImageView itemthumbnailView;
        final ImageView newsourceiconView;
        final ImageButton ib_bookmarkButton;
        final ImageButton ib_expandlessButton;
        final ImageButton ib_shareButton;
        final TextView tv_domainsourceView;
        final TextView tv_dateView;
        final TextView primarytitleView;


        final LinearLayout ll_expandlistitem;
        //article_list_item_expand.xml
        final ConstraintLayout article_list_item_one_expand_layout;
        final TextView tv_titleexpandoneView;
        final ImageView iv_sourceiconexpandoneView;
        final TextView tv_domainsourceexpandoneView;
        final TextView tv_dateexpandoneView;
        final LinearLayout ll_article_one_sb_expand;
        final ImageButton ib_sharebutton1;
        final ImageButton ib_bookmarkbutton1;


        final ConstraintLayout article_list_item_two_expand_layout;
        final TextView tv_titleexpandtwoView;
        final ImageView iv_sourceiconexpandtwoView;
        final TextView tv_domainsourceexpandtwoView;
        final TextView tv_dateexpandtwoView;
        final LinearLayout ll_article_two_sb_expand;
        final ImageButton ib_sharebutton2;
        final ImageButton ib_bookmarkbutton2;


        final ConstraintLayout article_list_item_more_expand_layout;
        final ImageButton ib_lessbuttonButton;
        final TextView tv_expandtextView;
        final View v_dividerview1;
        final View v_dividerview2;
        final View v_dividerview3;


        //initialize all the view reference with correct R.id.xxx
        FirstLevelNewsAdapterViewHolder(View view) {
            super(view);


            itemthumbnailView = (ImageView) view.findViewById(R.id.itemthumbnail);
            newsourceiconView = (ImageView) view.findViewById(R.id.newsourceicon);
            ib_bookmarkButton = (ImageButton) view.findViewById(R.id.ib_bookmark);
            ib_expandlessButton = (ImageButton) view.findViewById(R.id.ib_expandless);
            ib_shareButton = (ImageButton) view.findViewById(R.id.ib_share);
            tv_domainsourceView = (TextView) view.findViewById(R.id.tv_domainsource);
            tv_dateView = (TextView) view.findViewById(R.id.tv_date);
            primarytitleView = (TextView) view.findViewById(R.id.primarytitle);

            ll_expandlistitem = (LinearLayout) view.findViewById(R.id.expandlistitem);
            article_list_item_one_expand_layout = (ConstraintLayout) view.findViewById(R.id.article_list_item_one_expand_layout);
            tv_titleexpandoneView = (TextView) view.findViewById(R.id.tv_titleexpandone);
            iv_sourceiconexpandoneView = (ImageView) view.findViewById(R.id.iv_sourceiconexpandone);
            tv_domainsourceexpandoneView = (TextView) view.findViewById(R.id.tv_domainsourceexpandone);
            tv_dateexpandoneView = (TextView) view.findViewById(R.id.tv_dateexpandone);
            ll_article_one_sb_expand = (LinearLayout) view.findViewById(R.id.article_list_item_one_sb_expand_layout);
            ib_bookmarkbutton1 = (ImageButton) view.findViewById(R.id.ib_bookmark1);
            ib_sharebutton1 = (ImageButton) view.findViewById(R.id.ib_share1);


            article_list_item_two_expand_layout = (ConstraintLayout) view.findViewById(R.id.article_list_item_two_expand_layout);
            tv_titleexpandtwoView = (TextView) view.findViewById(R.id.tv_titleexpandtwo);
            iv_sourceiconexpandtwoView = (ImageView) view.findViewById(R.id.iv_sourceiconexpandtwo);
            tv_domainsourceexpandtwoView = (TextView) view.findViewById(R.id.tv_domainsourceexpandtwo);
            tv_dateexpandtwoView = (TextView) view.findViewById(R.id.tv_dateexpandtwo);
            ll_article_two_sb_expand = (LinearLayout) view.findViewById(R.id.article_list_item_two_sb_expand_layout);
            ib_bookmarkbutton2 = (ImageButton) view.findViewById(R.id.ib_bookmark2);
            ib_sharebutton2 = (ImageButton) view.findViewById(R.id.ib_share2);


            article_list_item_more_expand_layout = (ConstraintLayout) view.findViewById(R.id.article_list_item_more_expand_layout);
            ib_lessbuttonButton = (ImageButton) view.findViewById(R.id.ib_lessbutton);
            tv_expandtextView = (TextView) view.findViewById(R.id.tv_expandtext);

            v_dividerview1 = (View) view.findViewById(R.id.dividerview1);
            v_dividerview2 = (View) view.findViewById(R.id.dividerview2);
            v_dividerview3 = (View) view.findViewById(R.id.dividerview3);

            Log.d(TAG, " FirstLevelNewsAdapterViewHolder constructor 1");


        }


    }

}




