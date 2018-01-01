package com.hk.simplenewsgong.simplegong;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.hk.simplenewsgong.simplegong.util.FragInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * pager adapter for showing different category according to tab selection
 * <p></p>
 * Created by simplegong
 */

public class NewsFragmentPagerAdapter
        extends FragmentPagerAdapter {
    private static final String TAG = NewsFragmentPagerAdapter.class.getName();

    public static final String FRAGMENT_NAME = "fragment_name";


    /**
     * an interface for this pager adapter to call individual category fragment
     */
    public interface TalkToIndividualFragment {
        /**
         * report how many has added in remote database
         * for now, it is a fake number
         */
        public int checkHowManyNewItem();

        /**
         * user has selected a list of preferred media source on drawer fragment.
         * update the article list accordingly
         *
         * @param arrayOfFSD : a list of first subdomain id for retrieving data
         */
        public void initPreferredFirstSubDomain(int[] arrayOfFSD);

        /**
         * user has cleared out all the selection from filter fragment or unselected all the options,
         * update the article list accordingly
         */
        public void clearPreferredFirstSubDomain();

        /**
         * user click the update button when there is a snackbar indicate x amount of items have been
         * added remote database. Hosting activity will call this method to update the article list
         */
        public void swiperefreshNow();

    }

    // a map key(position, start from 0), value(fragment tag name)
    private Map<Integer, String> mFragmentIDMap;

    //reference to fragment manager
    private FragmentManager mFragmentManger;

    /**
     * constructor of NewsFragmentPagerAdapter
     *
     * @param fm : fragment manager that created from mainnewsactivity
     */
    public NewsFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragmentIDMap = new HashMap<Integer, String>();
        mFragmentManger = fm;
    }

    /**
     * return the containerfragment with category information according to position
     *
     * @param position position on tab layout
     * @return initialized fragment with name and other setup
     */
    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, " NewsFragmentPagerAdapter getitem=" + position + " end");

        Fragment containerfragment = new ContainerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(FRAGMENT_NAME, FragInfo.getInstance().get_tablistFragTableName(position));
        containerfragment.setArguments(bundle);
        return containerfragment;


    }

    /**
     * get the tab title according to position
     *
     * @param position position on tab layout
     * @return a string that contains the title of tab at position
     */
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        Log.d(TAG, " NewsFragmentPagerAdapter getpagetitle=" + position + " end");
        return FragInfo.getInstance().get_tablistname(position);
    }

    /**
     * return how many tab
     *
     * @return how many category should show
     */
    @Override
    public int getCount() {
        Log.d(TAG, " NewsFragmentPagerAdapter getcount end= length=" + FragInfo.getInstance().get_tablistSize());
        return FragInfo.getInstance().get_tablistSize();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment createdFragment = (Fragment) super.instantiateItem(container, position);

        mFragmentIDMap.put(position, createdFragment.getTag());

        return createdFragment;
    }

    /**
     * find the fragment by using the fragment manager with position
     *
     * @param position position in tablayout
     * @return fragment that match the position
     */
    public Fragment getFragment(int position) {
        String fragmenttag = mFragmentIDMap.get(position);
        return mFragmentManger.findFragmentByTag(fragmenttag);
    }

    /**
     * main activity will invoke this function.  This function will find the fragment that is showing
     * in the screen and invoke checkHowManyNewItem method on it
     *
     * @param position position in tablayout
     * @return total of new items
     */
    public int shouldShowMoreItemSnackBar(int position) {

        String fragmenttag = mFragmentIDMap.get(position);
        Fragment fragment = mFragmentManger.findFragmentByTag(fragmenttag);
        Log.d(TAG, " shouldShowMoreItemSnackBar 1 position=" + position);
        return ((TalkToIndividualFragment) fragment).checkHowManyNewItem();

    }


    /**
     * get the category id that is correlated with position in tablayout
     *
     * @param position position in tablayout
     * @return category id
     */
    public int getCurrentFragmentCategory(int position) {
        Log.d(TAG, " getCurrentFragmentCategory 1");

        return FragInfo.getInstance().get_category(position);
    }


    /**
     * this function is invoked by main activity.  Pass a array of first subdomain id to the
     * appropiate fragment
     *
     * @param position   : position in tablayout
     * @param arrayOfFSD : array of first subdomain id
     */
    public void passInPreferredFSD(int position, int[] arrayOfFSD) {
        String fragmenttag = mFragmentIDMap.get(position);
        Fragment fragment = mFragmentManger.findFragmentByTag(fragmenttag);
        Log.d(TAG, " passInPreferredFSD 1 length=" + arrayOfFSD.length);
        if (arrayOfFSD.length > 0) {
            Log.d(TAG, " passInPreferredFSD 2");
            ((TalkToIndividualFragment) fragment).initPreferredFirstSubDomain(arrayOfFSD);
        } else {
            Log.d(TAG, " passInPreferredFSD 3");
            ((TalkToIndividualFragment) fragment).clearPreferredFirstSubDomain();
        }
    }


    /**
     * reset the article list in current fragment without any preferred first subdomain id
     *
     * @param position : position in tablayout
     */
    public void clearInPreferredFSD(int position) {
        String fragmenttag = mFragmentIDMap.get(position);
        Fragment fragment = mFragmentManger.findFragmentByTag(fragmenttag);
        Log.d(TAG, " clearInPreferredFSD 1 position=" + position);
        ((TalkToIndividualFragment) fragment).clearPreferredFirstSubDomain();
        Log.d(TAG, " clearInPreferredFSD 2");

    }

    /**
     * when user click the update on snackbar, this function will be called by main activity.
     * In turn, this will call corresponding fragment to update the list of articles
     *
     * @param position
     */
    public void goupdateNow(int position) {
        String fragmenttag = mFragmentIDMap.get(position);
        Fragment fragment = mFragmentManger.findFragmentByTag(fragmenttag);
        Log.d(TAG, " goupdateNow 1 position=" + position);
        ((TalkToIndividualFragment) fragment).swiperefreshNow();
        Log.d(TAG, " goupdateNow 2 position=" + position);

    }

}
