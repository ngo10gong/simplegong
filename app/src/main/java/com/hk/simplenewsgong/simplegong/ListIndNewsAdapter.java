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

import com.hk.simplenewsgong.simplegong.data.FragArticleTableContract;
import com.hk.simplenewsgong.simplegong.data.GongPreference;
import com.hk.simplenewsgong.simplegong.data.GongProvider;
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
 * this adapter help to display a list of related article to the previous selected article
 * (called from containerfragment)
 * <p></p>
 * Created by simplegong
 */
public class ListIndNewsAdapter extends
        RecyclerView.Adapter<ListIndNewsAdapter.ListIndNewsViewHolder> {

    private final String TAG = ListIndNewsAdapter.class.getSimpleName();

    //context for resolving resources
    private final Context mContext;

    //onclicklistner reference to assign it to different view after initialize
    // handle calling detail activity
    private View.OnClickListener mcallDetailActivity;
    // handle share icon is clicked
    private View.OnClickListener mshareClickListener;
    // handle bookmark icon is clicked
    private View.OnClickListener mbookmarkClickListener;

    // a map for storing signal table information
    private Map<Integer, Integer> mSignalMap;

    //mask for checking current value
    private final static int BOOKMARKALREADY_MASK = 0x0001;
    private final static int READALREADY_MASK = 0x0002;

    //storing the json string for further processing
    private String mArticleListJSONStr;
    private String msignalBitJSONStr;

    //length of the related articles
    private int mArticlelistlength;

    //reference to the hosting activity
    private ListIndNewsAdapter.ListIndNewsAdapterOnClickHandler mClickHandler;

    /**
     * the interface for adapter to call the hosting fragment/activity when user click on
     * 1, to display detail news activity
     * 2, handle bookmark icon is clicked
     */
    public interface ListIndNewsAdapterOnClickHandler {
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
     * Creates a ListindNewsAdapter.
     *
     * @param context         : used for resolving resources
     * @param clickHandler    : handle when user click on individual view
     * @param articleListJSON : a string contains json formatted article list
     * @param signalBitJSON   : a string contains json formatted signal information
     */

    public ListIndNewsAdapter(@NonNull Context context,
                              ListIndNewsAdapter.ListIndNewsAdapterOnClickHandler clickHandler,
                              String articleListJSON,
                              String signalBitJSON) {

        //initialize reference
        mContext = context;
        mClickHandler = clickHandler;
        mSignalMap = new HashMap<Integer, Integer>();
        mArticleListJSONStr = articleListJSON;
        msignalBitJSONStr = signalBitJSON;

        try {
            //decode json formatted string of article list
            JSONTokener tokener = new JSONTokener(mArticleListJSONStr);
            Log.d(TAG, " ListIndNewsAdapter constructor 1 ");

            JSONArray jsonarray = (JSONArray) tokener.nextValue();
            mArticlelistlength = jsonarray.length();
            /*
            for (int index=0; index < jsonarray.length() ; index++){
                JSONObject jsonObject = (JSONObject) jsonarray.get(index);
                jsonObject.get(FragArticleTableContract.FragArticleEntry.COLUMN_ARTICLEID);
                jsonObject.get(FragArticleTableContract.FragArticleEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID);
                jsonObject.get(FragArticleTableContract.FragArticleEntry.COLUMN_TITLE);
                jsonObject.get(FragArticleTableContract.FragArticleEntry.COLUMN_FINALURL);
                jsonObject.get(FragArticleTableContract.FragArticleEntry.COLUMN_IMAGEURL);
                jsonObject.get(FragArticleTableContract.FragArticleEntry.COLUMN_TIMESTAMPONDOC);
            }
            */
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // create onclicklistener for handling action to display detail activity
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


                //set the background color
                ViewGroup oneupLevelLayout = (ViewGroup) view.getParent();
                if (view.getTag(R.string.VIEWTAG_BACKGROUND_LAYOUT_ID) != null) {
                    oneupLevelLayout.findViewById(
                            (int) view.getTag(R.string.VIEWTAG_BACKGROUND_LAYOUT_ID))
                            .setBackgroundResource(R.color.after_reading_color);
                }
                if (view.getTag(R.string.VIEWTAG_BACKGROUND_BOOKMARK_ID) != null) {
                    oneupLevelLayout.findViewById(
                            (int) view.getTag(R.string.VIEWTAG_BACKGROUND_BOOKMARK_ID))
                            .setBackgroundResource(R.color.after_reading_color);
                }
                if (view.getTag(R.string.VIEWTAG_BACKGROUND_SHARE_ID) != null) {
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

                    //let the hosting activity to handle
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

                    //let the hosting activity to handle
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
        Log.d(TAG, " ListIndNewsAdapter constructor ");
    }


    /**
     * create viewholder to display recyclerview item
     *
     * @param viewGroup viewgroup that viewholder resides on
     * @param viewType  which viewtype of this viewholder should be created
     * @return viewholder holds each recycler item
     */
    @Override
    public ListIndNewsAdapter.ListIndNewsViewHolder
    onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        int layoutId;
        View view;

        layoutId = R.layout.article_expand_list_item;
        view = LayoutInflater.from(mContext).inflate(layoutId, viewGroup, false);

        view.setFocusable(true);
        Log.d(TAG, "ListIndNewsAdapter.ListIndNewsViewHolder onCreateViewHolder");

        return new ListIndNewsAdapter.ListIndNewsViewHolder(view);

    }

    /**
     * bind the necessary information to display for specific recycler item
     *
     * @param listIndNewsAdapterViewHolder viewholder to hold the information for display
     * @param position                     The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ListIndNewsAdapter.ListIndNewsViewHolder listIndNewsAdapterViewHolder,
                                 int position) {
        final int articlePrimaryID;
        final String finalurl;

        try {
            //decode the json string
            JSONTokener articlelisttokener = new JSONTokener(mArticleListJSONStr);
            Log.d(TAG, " ListIndNewsAdapter onBindViewHolder 1 ");

            JSONArray jsonarray = (JSONArray) articlelisttokener.nextValue();
            mArticlelistlength = jsonarray.length();
            JSONObject jsonObject = (JSONObject) jsonarray.get(position);
            articlePrimaryID = (int) jsonObject.get(FragArticleTableContract.FragArticleEntry.COLUMN_ARTICLEID);
            finalurl = (String) jsonObject.get(FragArticleTableContract.FragArticleEntry.COLUMN_FINALURL);

            //configure the thumbnail view
            if (((String) jsonObject.get(FragArticleTableContract.FragArticleEntry.COLUMN_IMAGEURL))
                    .compareTo("EMPTYSTRINGVALUE") == 0) {
                listIndNewsAdapterViewHolder.itemthumbnailView.setImageResource(R.drawable.ic_logo_hourglass_question);
            } else {
                GlideApp.with(mContext)
                        .load((String) jsonObject.get(FragArticleTableContract.FragArticleEntry.COLUMN_IMAGEURL))
                        .placeholder(R.drawable.ic_tmp_icon)
                        .fitCenter()
                        .into(listIndNewsAdapterViewHolder.itemthumbnailView);
            }
            listIndNewsAdapterViewHolder.itemthumbnailView.setOnClickListener(mcallDetailActivity);
            FirstLevelNewsAdapter.setupTAGInfo((View) listIndNewsAdapterViewHolder.itemthumbnailView, articlePrimaryID, finalurl,
                    R.id.constraintlayout_item_primary, R.id.ib_bookmark,
                    R.id.ib_share, R.id.ib_expandless);


            int pagination_firstsubdomainid = (int) jsonObject.get(FragArticleTableContract.FragArticleEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID);

            // configure/assign source icon
            ArrayList<String> sourceIconURLChiName = SourceInfo.getInstance()
                    .getSourceIconURLChiName(pagination_firstsubdomainid);
            GlideApp.with(mContext)
                    .load(sourceIconURLChiName.get(SourceInfo.ARRAY_SOURCEICONURL_POS))
                    .placeholder(R.drawable.ic_tmp_icon)
                    .fitCenter()
                    .into(listIndNewsAdapterViewHolder.newsourceiconView);
            listIndNewsAdapterViewHolder.newsourceiconView.setOnClickListener(mcallDetailActivity);
            FirstLevelNewsAdapter.setupTAGInfo((View) listIndNewsAdapterViewHolder.newsourceiconView, articlePrimaryID, finalurl,
                    R.id.constraintlayout_item_primary, R.id.ib_bookmark,
                    R.id.ib_share, R.id.ib_expandless);


            Log.d(TAG, " onBindViewHolder 2 ");


            // configure/assign domain source textview
            listIndNewsAdapterViewHolder.tv_domainsourceView.setText(sourceIconURLChiName.get(SourceInfo.ARRAY_NAME_POS));
            listIndNewsAdapterViewHolder.tv_domainsourceView.setOnClickListener(mcallDetailActivity);
            FirstLevelNewsAdapter.setupTAGInfo((View) listIndNewsAdapterViewHolder.tv_domainsourceView, articlePrimaryID, finalurl,
                    R.id.constraintlayout_item_primary, R.id.ib_bookmark,
                    R.id.ib_share, R.id.ib_expandless);


            //extract the signal map from json string
            int bitfromjson = 0;
            JSONTokener signalmaptokener = new JSONTokener(msignalBitJSONStr);
            JSONArray signalmapJSONArray = (JSONArray) signalmaptokener.nextValue();
            for (int index = 0; index < signalmapJSONArray.length(); index++) {
                JSONObject indSignalMapObj = (JSONObject) signalmapJSONArray.get(index);
                if (indSignalMapObj.has(String.valueOf(articlePrimaryID))) {
                    bitfromjson = (int) indSignalMapObj.get(String.valueOf(articlePrimaryID));
                    break;
                }
            }


            int bookmarkalready = bitfromjson & FirstLevelNewsAdapter.BOOKMARKALREADY_MASK;
            int readalready = bitfromjson & FirstLevelNewsAdapter.READALREADY_MASK;
            Log.d(TAG, " ListIndNewsAdapter onBindViewHolder 3 articlePrimaryID=" + articlePrimaryID + ", bitfromjson=" + bitfromjson);
            int bookmarkalready_inmap = 0;
            int readalready_inmap = 0;
            int bitvalBookRead = 0;

            // configure whether to show the background of bookmark/share button accordingly
            if (mSignalMap.containsKey(articlePrimaryID)) {
                bitvalBookRead = mSignalMap.get(articlePrimaryID);
                bookmarkalready_inmap = bitvalBookRead & BOOKMARKALREADY_MASK;
                readalready_inmap = bitvalBookRead & READALREADY_MASK;
            } else {
                mSignalMap.put(articlePrimaryID, bitfromjson);
            }

            if ((bookmarkalready != 0)
                    || (bookmarkalready_inmap != 0)) {
                listIndNewsAdapterViewHolder.ib_bookmarkButton.setSelected(true);
            } else {
                //  (bookmarkalready == 0) && (bookmarkalready_inmap == 0) or else
                listIndNewsAdapterViewHolder.ib_bookmarkButton.setSelected(false);
            }
            if ((readalready != 0)
                    || (readalready_inmap != 0)) {
                listIndNewsAdapterViewHolder.ib_bookmarkButton.setBackgroundResource(R.color.after_reading_color);
                listIndNewsAdapterViewHolder.ib_shareButton.setBackgroundResource(R.color.after_reading_color);
                ((ViewGroup) listIndNewsAdapterViewHolder.ib_shareButton.getParent()).setBackgroundResource(R.color.after_reading_color);
            } else {
                //use default background
            }


            //assign onclicklistener for bookmark button
            listIndNewsAdapterViewHolder.ib_bookmarkButton.setOnClickListener(mbookmarkClickListener);
            listIndNewsAdapterViewHolder.ib_bookmarkButton.setTag(R.string.VIEWTAG_ARTICLEID, articlePrimaryID);


            final String primarytitle = (String) jsonObject.get(FragArticleTableContract.FragArticleEntry.COLUMN_TITLE);

            //configure share button
            listIndNewsAdapterViewHolder.ib_shareButton.setOnClickListener(mshareClickListener);
            listIndNewsAdapterViewHolder.ib_shareButton.setTag(R.string.VIEWTAG_ARTICLEID, articlePrimaryID);
            listIndNewsAdapterViewHolder.ib_shareButton.setTag(R.string.VIEWTAG_FINALURL, finalurl);
            listIndNewsAdapterViewHolder.ib_shareButton.setTag(R.string.VIEWTAG_TITLE, primarytitle);

            //disable expandless button
            listIndNewsAdapterViewHolder.ib_expandlessButton.setVisibility(View.INVISIBLE);

            //configure title view
            listIndNewsAdapterViewHolder.primarytitleView.setText(primarytitle);
            listIndNewsAdapterViewHolder.primarytitleView.setOnClickListener(mcallDetailActivity);
            FirstLevelNewsAdapter.setupTAGInfo((View) listIndNewsAdapterViewHolder.primarytitleView, articlePrimaryID, finalurl,
                    R.id.constraintlayout_item_primary, R.id.ib_bookmark,
                    R.id.ib_share, R.id.ib_expandless);


            //assign and configure timestamp
            int timestampondoc = (int) jsonObject.get(FragArticleTableContract.FragArticleEntry.COLUMN_TIMESTAMPONDOC);
            String timestampondocTranslatedString = GongTimeUtil.getDisplayTimeStringFromData((long) timestampondoc);
            listIndNewsAdapterViewHolder.tv_dateView.setText(timestampondocTranslatedString);
            listIndNewsAdapterViewHolder.tv_dateView.setTag(R.string.VIEWTAG_ARTICLEID, articlePrimaryID);
            listIndNewsAdapterViewHolder.tv_dateView.setTag(R.string.VIEWTAG_FINALURL, finalurl);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, " ListIndNewsAdapter.ListIndNewsViewHolder onBindViewHolder 1 " + position);

    }


    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of article list items
     */
    @Override
    public int getItemCount() {
        Log.d(TAG, " ListIndNewsAdapter getitemcount 1 ");
        return mArticlelistlength;
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
     * hosting fragment/activity has new cursor of newly signal map information
     * , update local information accordingly
     *
     * @param newCursor cursor with latest signal map information
     */
    void updateSignalMapFromCursor(Cursor newCursor) {
        Log.d(TAG, " ListIndNewsAdapter  updateSignalMapFromCursor  ");

        if (newCursor != null) {
            Log.d(TAG, " ListIndNewsAdapter  updateSignalMapFromCursor  getcount=" + newCursor.getCount());
            //go thur the new cursor and update local information
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


        }
    }


    /**
     * viewholder for recyclerview item
     */
    class ListIndNewsViewHolder extends RecyclerView.ViewHolder {
        private final String TAG = ListIndNewsAdapter.ListIndNewsViewHolder.class.getSimpleName();

        //the whole view of item
        //final View articleindlistitem;

        //article_list_item_primary.xml
        final ImageView itemthumbnailView;
        final ImageView newsourceiconView;
        final ImageButton ib_bookmarkButton;
        final ImageButton ib_expandlessButton;
        final ImageButton ib_shareButton;
        final TextView tv_domainsourceView;
        final TextView tv_dateView;
        final TextView primarytitleView;


        ListIndNewsViewHolder(View view) {
            super(view);


            itemthumbnailView = (ImageView) view.findViewById(R.id.itemthumbnail);
            newsourceiconView = (ImageView) view.findViewById(R.id.newsourceicon);
            ib_bookmarkButton = (ImageButton) view.findViewById(R.id.ib_bookmark);
            ib_expandlessButton = (ImageButton) view.findViewById(R.id.ib_expandless);
            ib_shareButton = (ImageButton) view.findViewById(R.id.ib_share);
            tv_domainsourceView = (TextView) view.findViewById(R.id.tv_domainsource);
            tv_dateView = (TextView) view.findViewById(R.id.tv_date);
            primarytitleView = (TextView) view.findViewById(R.id.primarytitle);


            Log.d(TAG, " ListIndNewsViewHolder constructor 1");


        }


    }

}

