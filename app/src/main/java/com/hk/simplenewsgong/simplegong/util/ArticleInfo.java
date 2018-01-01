package com.hk.simplenewsgong.simplegong.util;

import android.support.annotation.NonNull;
import android.util.Log;

import com.hk.simplenewsgong.simplegong.data.SourceInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * this class store each article information
 * <p></p>
 * Created by simplegong
 */
public class ArticleInfo {
    private final static String TAG = ArticleInfo.class.getSimpleName();

    private int mFirstsubdomaintable_id;
    private String mTitle;
    private String mImageurl;
    private int mID;
    private String mFinalurl;
    private int mTimestampondoc;
    private int mSignalBitmap;

    /**
     * constructor for ArticleInfo , this is with signalbitmap info
     *
     * @param firstsub_id    : first subdomain id of the article
     * @param title          : title string of the article
     * @param imageurl       : url of the article
     * @param id             : id of the article
     * @param finalurl       : url of the article
     * @param timestampondoc : timestamp of this article
     * @param signalBitmap   : bitmask of this article
     */
    public ArticleInfo(int firstsub_id,
                       String title,
                       String imageurl,
                       int id,
                       String finalurl,
                       int timestampondoc,
                       int signalBitmap
    ) {
        mFirstsubdomaintable_id = firstsub_id;
        mTitle = title;
        mImageurl = imageurl;
        mID = id;
        mFinalurl = finalurl;
        mTimestampondoc = timestampondoc;
        mSignalBitmap = signalBitmap;

    }

    /**
     * constructor for ArticleInfo , this is without signalbitmap info as input
     *
     * @param firstsub_id    : first subdomain id of the article
     * @param title          : title string of the article
     * @param imageurl       : url of the article
     * @param id             : id of the article
     * @param finalurl       : url of the article
     * @param timestampondoc : timestamp of this article
     */

    public ArticleInfo(int firstsub_id,
                       String title,
                       String imageurl,
                       int id,
                       String finalurl,
                       int timestampondoc
    ) {
        mFirstsubdomaintable_id = firstsub_id;
        mTitle = title;
        mImageurl = imageurl;
        mID = id;
        mFinalurl = finalurl;
        mTimestampondoc = timestampondoc;
        mSignalBitmap = 0;

    }

    /**
     * get the domain id of this article associated with
     *
     * @return domain id
     */
    public int getDomainID() {
        return SourceInfo.getInstance().getDomainID(this.mFirstsubdomaintable_id);
    }

    /**
     * get the first subdomain id of this article associated with
     *
     * @return first subdomain id
     */
    public int getFirstsubdomaintable_id() {
        return mFirstsubdomaintable_id;
    }

    /**
     * get the title of this article associated with
     *
     * @return title string
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * get the imageurl of this article associated with
     *
     * @return url address
     */
    public String getImageurl() {
        return mImageurl;
    }

    /**
     * get the article id this article associated with
     *
     * @return article id
     */
    public int getID() {
        return mID;
    }

    /**
     * get the url of this article associated with
     *
     * @return url string
     */
    public String getFinalurl() {
        return mFinalurl;
    }

    /**
     * get the timestamp of this article associated with
     *
     * @return article id
     */
    public int getTimestampondoc() {
        return mTimestampondoc;
    }

    /**
     * get the signal bitmask of this article associated with
     *
     * @return bitmask of this article
     */
    public int getSignalBitmap() {
        return mSignalBitmap;
    }


    /**
     * rearrange a list of articleInfo according to the preferred domamin priority
     *
     * @param articleInfoList    : a list of articleInfo to be arranged
     * @param preferredlistorder : list of preferred domain order
     * @return a rearranged articleInfo list
     */
    public static List<ArticleInfo> sortByPreferredDomain(List<ArticleInfo> articleInfoList, List<Integer> preferredlistorder) {
        List<ArticleInfo> aList = new ArrayList<ArticleInfo>(); //list for storing rearranged articleInfo

        for (int x : preferredlistorder) {
            boolean found = false;
            //each article only have one associated domain value
            for (ArticleInfo ind_aInfo : articleInfoList) {
                if (ind_aInfo.getDomainID() == x) {
                    aList.add(ind_aInfo);
                    found = true;
                }
            }
        }

        return aList;
    }


}
