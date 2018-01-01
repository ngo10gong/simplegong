package com.hk.simplenewsgong.simplegong;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.MultiTransformation;
import com.hk.simplenewsgong.simplegong.data.FragArticleTableContract;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;


/**
 * this adapter is used to display the entity in the grid layout
 * <p>
 * Created by simplegong
 */

public class SimpleGongAdapter extends
        RecyclerView.Adapter<SimpleGongAdapter.SimpleGongViewHolder> {

    private final String TAG = SimpleGongAdapter.class.getSimpleName();

    //context for resolving resources
    private final Context mContext;


    //call the activity for handling start detail activity of entity
    private View.OnClickListener mcallSimpleGongActivity;

    //reference to the entity list
    private Cursor mCursor;

    //reference to the hosting activity
    private SimpleGongAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages when individual entity is clicked
     */
    public interface SimpleGongAdapterOnClickHandler {
        /**
         * when user click on individual entity on the grid layout, invokes article list of selected
         * entity
         *
         * @param title : entity name
         * @param id    : entity id
         */
        void onClickIndEntity(String title, int id);

    }


    /**
     * constructor of SimpleGongAdapter
     *
     * @param context      : context to resolve resource
     * @param clickHandler reference for calling back
     */

    public SimpleGongAdapter(@NonNull Context context,
                             SimpleGongAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;

        //onclicklistener for handling when individual entity is clicked
        mcallSimpleGongActivity = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //pass the necessary information to invoke activity
                String sourcetitle = (String) view.getTag(R.string.VIEWTAG_TITLE);
                int sourceid = (int) view.getTag(R.string.VIEWTAG_TITLE_ID);

                //should start detailactivity if available
                Log.d(TAG, "---------------------> starting mcallSimpleGongActivity sourcetitle="
                        + sourcetitle
                        + ",sourceid="
                        + sourceid);
                mClickHandler.onClickIndEntity(sourcetitle, sourceid);
            }
        };
        Log.d(TAG, " SimpleGongAdapter constructor ");
    }


    /**
     * create viewholder to display recyclerview item
     *
     * @param viewGroup viewgroup that viewholder resides on
     * @param viewType  which viewtype of this viewholder should be created
     * @return viewholder holds each recycler item
     */
    @Override
    public SimpleGongViewHolder
    onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        int layoutId;
        View view;

        layoutId = R.layout.entity_card;
        view = LayoutInflater.from(mContext).inflate(layoutId, viewGroup, false);

        view.setFocusable(true);
        Log.d(TAG, "SimpleGongAdapter.SimpleGongViewHolder onCreateViewHolder");

        return new SimpleGongViewHolder(view);

    }

    /**
     * bind the neccessary information to display for specific recycler item
     *
     * @param simplegongAdapterViewHolder viewholder to hold the information for display
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(SimpleGongViewHolder simplegongAdapterViewHolder,
                                 int position) {
        final int titleID;
        final String title;
        mCursor.moveToPosition(position);

        //load the entity image
        GlideApp.with(mContext)
                .load(mCursor.getString(FragArticleTableContract.INDEX_RAWQUERY_ENTITYICONURL))
                .placeholder(R.drawable.ic_tmp_icon)
                .transform(new MultiTransformation(new BlurTransformation(7), new CropTransformation(180, 135)))
                //.apply(new RequestOptions().transform(new BlurTransformation(50)))
                //.fitCenter()
                .into(simplegongAdapterViewHolder.iv_SourceiconView);

        //load the entity name
        simplegongAdapterViewHolder.tv_SourceTitleView.setText(mCursor.getString(FragArticleTableContract.INDEX_RAWQUERY_ENTITYNAME));

        //configure the grid cell information
        simplegongAdapterViewHolder.fl_entity_flayout.setOnClickListener(mcallSimpleGongActivity);
        simplegongAdapterViewHolder.fl_entity_flayout.setTag(R.string.VIEWTAG_TITLE, mCursor.getString(FragArticleTableContract.INDEX_RAWQUERY_ENTITYNAME));
        simplegongAdapterViewHolder.fl_entity_flayout.setTag(R.string.VIEWTAG_TITLE_ID, FragArticleTableContract.fake_getEntityID());


        Log.d(TAG, " SimpleGongAdapter.SimpleGongViewHolder onBindViewHolder 1 " + position);

    }


    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of entity
     */
    @Override
    public int getItemCount() {
        if (mCursor != null) {
            Log.d(TAG, " SimpleGongAdapter getitemcount 1 =" + mCursor.getCount());
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    /**
     * only one type to display
     *
     * @param position index within our RecyclerView and Cursor
     * @return the view type
     */
    @Override
    public int getItemViewType(int position) {
        Log.d(TAG, "--> should not get into this function getitemviewtype");
        return 0;
    }


    /**
     * update the local cursor and refresh the display
     *
     * @param newCursor cursor with new information
     */
    void swapCursor(Cursor newCursor) {
        Log.d(TAG, " SimpleGongAdapter swapcursor  ");

        mCursor = newCursor;
        if (mCursor != null) {
            Log.d(TAG, " ListIndNewsAdapter swapcursor  getcount=" + mCursor.getCount());
        }
        notifyDataSetChanged();
    }


    /**
     * viewholder for reference to individual entity
     */
    class SimpleGongViewHolder extends RecyclerView.ViewHolder {
        private final String TAG = SimpleGongViewHolder.class.getSimpleName();

        //article_list_item_primary.xml
        final ImageView iv_SourceiconView;
        final TextView tv_SourceTitleView;
        final FrameLayout fl_entity_flayout;


        SimpleGongViewHolder(View view) {
            super(view);


            iv_SourceiconView = (ImageView) view.findViewById(R.id.sourceicon);
            tv_SourceTitleView = (TextView) view.findViewById(R.id.sourcetitle);
            fl_entity_flayout = (FrameLayout) view.findViewById(R.id.entity_flayout);


            Log.d(TAG, " SimpleGongViewHolder constructor 1");


        }


    }

}


