package com.hk.simplenewsgong.simplegong;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hk.simplenewsgong.simplegong.data.SourceInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * this adapter to show a list of media source for a given category id
 * <p>
 * Created by simplegong
 */
public class FilterFragmentAdapter extends RecyclerView.Adapter<FilterFragmentAdapter.FilterRowViewHolder> {
    private final String TAG = FilterFragmentAdapter.class.getSimpleName();


    /**
     * a Inteface for fragment callback.  Method will be used when user select media source
     */
    public interface ListChanged {
        /**
         * after user select any media source, this method will tell the hosting fragment to update
         * accordingly
         *
         * @param preferredList : a list of first subdomain id
         */
        public void setCurrentPreferredSourceList(List<Integer> preferredList);
    }

    //context for resolved resource
    private final Context mContext;
    //store the current user selected media source
    private Map<Integer, Integer> mFSDSelectedMap;
    //the list of first subdomain information of the given category id
    private List<FSDCatgeoryInfo> mCurrentSourceToDisplay;

    //reference back to the calling fragment
    private FilterFragmentAdapter.ListChanged mFragmentListChanged;

    /**
     * constructor
     *
     * @param context     : context to resolve resource
     * @param listchanged : reference of calling fragment
     */
    public FilterFragmentAdapter(Context context,
                                 FilterFragmentAdapter.ListChanged listchanged) {
        mContext = context;
        mFragmentListChanged = listchanged;
        mFSDSelectedMap = new HashMap<>();
        mCurrentSourceToDisplay = new ArrayList<>();
    }


    /**
     * create viewholder to display recyclerview item
     *
     * @param parent   viewgroup that viewholder resides on
     * @param viewType which viewtype of this viewholder should be created
     * @return viewholder holds each recycler item
     */
    @Override
    public FilterRowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId;
        View view;

        //inflate the view
        layoutId = R.layout.list_item_filter_drawer;
        view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);

        view.setFocusable(true);
        Log.d(TAG, "FilterRowViewHolder onCreateViewHolder");

        return new FilterRowViewHolder(view);
    }

    /**
     * bind the neccessary information to display for specific recycler item
     *
     * @param holder   :viewholder to hold the information for display
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(FilterRowViewHolder holder, int position) {

        Log.d(TAG, " onBindViewHolder 1 position=" + position);
        //load the specified image url
        GlideApp.with(mContext)
                .load(mCurrentSourceToDisplay.get(position).getIconURL())
                .placeholder(R.drawable.ic_tmp_icon)
                .fitCenter()
                .into(holder.iv_sourceiconfilter);
        holder.tv_domainsourcefilter.setText(mCurrentSourceToDisplay.get(position).getChiName());

        //configure the checkbox for this media source
        holder.cb_filter_checkbox.setTag(R.string.VIEWTAG_FIRSTSUBDOMAIN_NUM, mCurrentSourceToDisplay.get(position).getFirstSubDomainID());
        holder.cb_filter_checkbox.setOnClickListener(v ->
        {
            boolean checked = ((CheckBox) v).isChecked();
            List<Integer> resultList = new ArrayList<>();
            if (checked) {
                mFSDSelectedMap.put((int) v.getTag(R.string.VIEWTAG_FIRSTSUBDOMAIN_NUM), 1);
            } else {
                mFSDSelectedMap.put((int) v.getTag(R.string.VIEWTAG_FIRSTSUBDOMAIN_NUM), 0);
            }
            for (Map.Entry<Integer, Integer> entry : mFSDSelectedMap.entrySet()) {
                if (entry.getValue() == 1) {
                    resultList.add(entry.getKey());
                }
            }
            mFragmentListChanged.setCurrentPreferredSourceList(resultList);

        });
        holder.cb_filter_checkbox.setChecked(
                mFSDSelectedMap.get(mCurrentSourceToDisplay.get(position).getFirstSubDomainID()) == 1
                        ? true : false);

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available to display
     */
    @Override
    public int getItemCount() {
        Log.d(TAG, "  getitemcount 1 ");
        if (null == mCurrentSourceToDisplay) return 0;
        Log.d(TAG, "  getitemcount 2 getcount=" + mCurrentSourceToDisplay.size());
        return mCurrentSourceToDisplay.size();
    }


    /**
     * filter fragment calls this method to setup the media source list
     *
     * @param categoryNum          : category id
     * @param previousSelectedList :  a list of selected media source
     */
    public void setCurrentCategory(int categoryNum, List<Integer> previousSelectedList) {
        Log.d(TAG, " setCurrentCategory 1 =" + categoryNum);

        //get a list of first subdomain id with specified CategoryNum
        mCurrentSourceToDisplay = SourceInfo.getInstance().getFSDListWithCategory(categoryNum);
        mFSDSelectedMap.clear();
        Log.d(TAG, " setCurrentCategory mCurrentSourceToDisplay.size =" + mCurrentSourceToDisplay.size());

        //assign the map with the list of first subdomain
        for (FSDCatgeoryInfo entry : mCurrentSourceToDisplay) {
            Log.d(TAG, " setCurrentCategory entry.FSDID ="
                    + entry.getFirstSubDomainID()
                    + ", chiname=" + entry.getChiName());
            mFSDSelectedMap.put(entry.getFirstSubDomainID(), 0);
        }

        //put back the previous selected media source
        if (previousSelectedList != null) {
            for (int x : previousSelectedList) {
                mFSDSelectedMap.put(x, 1);
            }
        }

        notifyDataSetChanged();
    }


    /**
     * view holder for each media source item
     */
    public class FilterRowViewHolder extends RecyclerView.ViewHolder {
        private final String TAG = FilterRowViewHolder.class.getSimpleName();

        final LinearLayout listItemRowLayout;
        final ImageView iv_sourceiconfilter;
        final TextView tv_domainsourcefilter;
        final CheckBox cb_filter_checkbox;


        FilterRowViewHolder(View view) {
            super(view);

            listItemRowLayout = (LinearLayout) view.findViewById(R.id.listItemRowLayout);
            iv_sourceiconfilter = (ImageView) view.findViewById(R.id.iv_sourceiconfilter);
            tv_domainsourcefilter = (TextView) view.findViewById(R.id.tv_domainsourcefilter);
            cb_filter_checkbox = (CheckBox) view.findViewById(R.id.filter_checkbox);

            Log.d(TAG, " FilterRowViewHolder constructor 1");


        }


    }

    /**
     * store each first subdomain information like chinese/english name and its source icon url
     */
    public static class FSDCatgeoryInfo {
        private int mFirstSubDomain_id;
        private String mChiName;
        private String mEngName;
        private String mSourceIconURL;

        public FSDCatgeoryInfo(int fsd_id, String chiname, String engname, String url) {
            mFirstSubDomain_id = fsd_id;
            mChiName = chiname;
            mEngName = engname;
            mSourceIconURL = url;
        }

        public int getFirstSubDomainID() {
            return mFirstSubDomain_id;
        }

        public String getChiName() {
            return mChiName;
        }

        public String getEngName() {
            return mEngName;
        }

        public String getIconURL() {
            return mSourceIconURL;
        }


    }

}
