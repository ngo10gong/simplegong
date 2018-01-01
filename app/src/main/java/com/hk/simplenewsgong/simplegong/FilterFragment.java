package com.hk.simplenewsgong.simplegong;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * this fragment show a list of media source associate with the current displayed category
 * <p>
 * Created by simplegong
 */
public class FilterFragment extends Fragment implements
        FilterFragmentAdapter.ListChanged {
    private final String TAG = FilterFragment.class.getSimpleName();


    //reference to the activity hosting this fragment
    private MainNewsActivity mMainNewsActivity;

    //reference to recyclerview
    private RecyclerView mRecyclerView;

    //reference to clear filter button
    private Button mClearFilterButton;

    //integer id of hosting current category
    private int mCurrentCategory;

    //map of category id (key) with list(value) of selected first subdomain id
    private Map<Integer, List<Integer>> mFSDHistoryMap;


    //reference to the adapter to show the filter list
    private FilterFragmentAdapter mFilterFragmentAdapter;

    /**
     * user has selected preferred media source to show in current category
     *
     * @param preferredList : a list of first subdomain id
     */
    @Override
    public void setCurrentPreferredSourceList(List<Integer> preferredList) {
        mFSDHistoryMap.put(mCurrentCategory, preferredList);
        ((CallBackMainActivity) mMainNewsActivity).setCurrentPreferredSourceList(preferredList);
    }

    /**
     * interface that this fragment will call hosting activity to perform some actions
     */
    public interface CallBackMainActivity {

        /**
         * get category id of current fragment that is actively display right now
         *
         * @return current fragment category id
         */
        public int getCurrentDisplayCategory();

        /**
         * for hosting activity to callback this filter fragment
         */
        public void passInFilterFragment(Fragment filterFragment);

        /**
         * user has selected a list of media source(first subdomain id)
         * hosting activity will call the current fragment to reflect the necessary changes
         */
        public void setCurrentPreferredSourceList(List<Integer> preferredList);

        /**
         * user has pressed the clear button
         * hosting activity will call the current fragment to get all the first subdomain in that
         * category
         */
        public void clearCurrentPreferredSourceList();

    }


    /**
     * initial first subdomain map in this default constructor
     */
    public FilterFragment() {
        mFSDHistoryMap = new HashMap<Integer, List<Integer>>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.filter_drawer, container, false);

        Log.d(TAG, " filterfragment oncreateview 1");


        //configure the clearfilterbutton
        mClearFilterButton = (Button) rootView.findViewById(R.id.clear_filters);
        mClearFilterButton.setVisibility(View.GONE);
        mClearFilterButton.setOnClickListener(v -> {
            boolean pressed = ((Button) v).isPressed();

            if (pressed) {
                // clear all the selection in the adapter
                mFilterFragmentAdapter.setCurrentCategory(mCurrentCategory, null);
                v.setVisibility(View.GONE);
                // call hosting activity to invoke the clear up method on current fragment
                ((CallBackMainActivity) mMainNewsActivity).clearCurrentPreferredSourceList();
                mFSDHistoryMap.remove(mCurrentCategory);
            }

        });

        //setting up the recyclerview for displaying
        mFilterFragmentAdapter = new FilterFragmentAdapter(getActivity(), this);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.filters);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mFilterFragmentAdapter);

        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            //get the hosting activity for call back
            mMainNewsActivity = (MainNewsActivity) context;
            Log.d(TAG, " onAttach 1");
            mMainNewsActivity.passInFilterFragment(this);
            Log.d(TAG, " onAttach 2");
        }

    }


    /**
     * pass the category id to the adapter which will initial the media source list with
     * the passedin category id
     *
     * @param category_id category id for passing to adapter
     */
    public void drawerOpenedWithCategory(int category_id) {
        Log.d(TAG, " drawerOpenedWithCategory 1" + category_id);
        mCurrentCategory = category_id;
        if (mFSDHistoryMap.get(category_id) != null) {
            mFilterFragmentAdapter.setCurrentCategory(category_id, mFSDHistoryMap.get(category_id));
            mClearFilterButton.setVisibility(View.VISIBLE);
        } else {
            mFilterFragmentAdapter.setCurrentCategory(category_id, null);
        }

    }


}
