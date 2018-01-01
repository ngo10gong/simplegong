package com.hk.simplenewsgong.simplegong.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.hk.simplenewsgong.simplegong.FilterFragmentAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class with updated information about category, domain & firstsubdomain information
 * it will provide a easy and fast access to other class (like activity/fragment/adapter).
 * this is a singleton class implementation
 * <p></p>
 * Created by simplegong
 */

public class SourceInfo implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = SourceInfo.class.getSimpleName();

    //resource for loading information from database
    private static SourceInfo mInstance;
    private static Context mContext;
    private static LoaderManager mLoaderManager;

    private static boolean mLoaderReset = false;

    public static final int ARRAY_SOURCEICONURL_POS = 0;
    public static final int ARRAY_NAME_POS = 1;

    //loader id for different database
    private final int LOADER_ID_CATEGORY = 100;
    private final int LOADER_ID_DOMAIN = 101;
    private final int LOADER_ID_FIRSTSUBDOMAIN = 102;

    //reference cursor for storing category, domain or firstsubdomain
    private static Cursor mCategoryCursor;
    private static Cursor mDomainCursor;
    private static Cursor mFirstSubDomainCursor;

    //default constant value
    private static final String DEFAULT_WAITING_URL = "https://goo.gl/images/cwYwRd";
    private static final String DEFAULT_SOURCE_CHI_NAME = "不明";
    private static final String DEFAULT_SOURCE_ENG_NAME = "Unknown";
    private static final int DEFAULT_DOMAIN_ID = 12;


    //the following map are for faster access by other classes
    private static Map<Integer, SourceCategoryEntry> mCategoryMap;
    private static Map<Integer, SourceDomainEntry> mDomainMap;
    private static Map<Integer, SourceFirstSubDomainEntry> mFirstSubDomainMap;


    //storing individual category entry
    public class SourceCategoryEntry {
        private int mCategoryTable_ID;
        private String mChi_name;
        private String mEng_name;

        SourceCategoryEntry(int categoryTable_id, String chi_name, String eng_name) {
            mCategoryTable_ID = categoryTable_id;
            mChi_name = chi_name;
            mEng_name = eng_name;
        }

        public int getCategoryTableID() {
            return mCategoryTable_ID;
        }

        public String getChiName() {
            return mChi_name;
        }

        public String getEngName() {
            return mEng_name;
        }
    }

    //loader's projection
    public static final String[] CATEGORY_PROJECTION = {
            CategoryTableContract.CategoryEntry.COLUMN_CATEGORYTABLE_ID,
            CategoryTableContract.CategoryEntry.COLUMN_CHI_NAME,
            CategoryTableContract.CategoryEntry.COLUMN_ENG_NAME
    };
    //loader's index value
    public static final int INDEX_CATEGORY_CATEGORYTABLE_ID = 0;
    public static final int INDEX_CATEGORY_CHI_NAME = 1;
    public static final int INDEX_CATEGORY_ENG_NAME = 2;

    //storing individual domain entry
    public class SourceDomainEntry {
        private int mDomainTable_ID;
        private String mChi_name;
        private String mEng_name;
        private String mBaseURL;

        SourceDomainEntry(int domainTable_id, String chi_name, String eng_name, String url) {
            mDomainTable_ID = domainTable_id;
            mChi_name = chi_name;
            mEng_name = eng_name;
            mBaseURL = url;
        }

        public int getDomainTableID() {
            return mDomainTable_ID;
        }

        public String getChiName() {
            return mChi_name;
        }

        public String getEngName() {
            return mEng_name;
        }

        public String getBaseURL() {
            return mBaseURL;
        }
    }


    //loader's projection
    public static final String[] DOMAIN_PROJECTION = {
            DomainTableContract.DomainEntry.COLUMN_DOMAINTABLE_ID,
            DomainTableContract.DomainEntry.COLUMN_BASEURL,
            DomainTableContract.DomainEntry.COLUMN_CHI_NAME,
            DomainTableContract.DomainEntry.COLUMN_ENG_NAME
    };
    //loader's index value
    public static final int INDEX_DOMAIN_DOMAINTABLE_ID = 0;
    public static final int INDEX_DOMAIN_BASEURL = 1;
    public static final int INDEX_DOMAIN_CHI_NAME = 2;
    public static final int INDEX_DOMAIN_ENG_NAME = 3;


    //storing individual first subdomain entry
    public class SourceFirstSubDomainEntry {
        private int mFirstSubDomainTable_ID;
        private int mCategoryTableID;
        private int mDomainTableID;
        private String mSourceIconURL;

        SourceFirstSubDomainEntry(int firstsubdomainTable_id, int categoryid, int domainid, String url) {
            mFirstSubDomainTable_ID = firstsubdomainTable_id;
            mCategoryTableID = categoryid;
            mDomainTableID = domainid;
            mSourceIconURL = url;
        }

        public int getFirstSubDomainTableID() {
            return mFirstSubDomainTable_ID;
        }

        public int getCategoryTableID() {
            return mCategoryTableID;
        }

        public int getDomainTableID() {
            return mDomainTableID;
        }

        public String getSourceIconURL() {
            return mSourceIconURL;
        }
    }

    //loader's projection
    public static final String[] FIRSTSUBDOMAIN_PROJECTION = {
            FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID,
            FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID,
            FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID,
            FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL
    };
    //loader's index value
    public static final int INDEX_FIRSTSUBDOMAIN_FIRSTSUBDOMAINTABLE_ID = 0;
    public static final int INDEX_FIRSTSUBDOMAIN_CATEGORYTABLE_ID = 1;
    public static final int INDEX_FIRSTSUBDOMAIN_DOMAINTABLE_ID = 2;
    public static final int INDEX_FIRSTSUBDOMAIN_SOURCEICONURL = 3;


    /*
     *  initial function to setup resource for later user
     */
    public static void init(Context context, LoaderManager loaderManager) {
        mContext = context;
        mLoaderManager = loaderManager;
        mInstance = new SourceInfo(context,
                loaderManager);
    }

    /*
     *  get the instance of this class
     */
    public static SourceInfo getInstance() {
        if (mLoaderReset) {
            mInstance = new SourceInfo(mContext,
                    mLoaderManager);
            mLoaderReset = false;
            //Log.d(TAG, " SourceInfo getInstance 1");

            return mInstance;
        } else {
            //Log.d(TAG, " SourceInfo getInstance 2");
            return mInstance;
        }
    }

    /**
     * a private constructor
     *
     * @param context       : help to resolve resource
     * @param loaderManager : help to start loader
     */
    private SourceInfo(Context context, LoaderManager loaderManager) {
        mContext = context;
        mLoaderManager = loaderManager;

        Log.d(TAG, " SourceInfo constructor 1");
        //restart loader
        mLoaderManager.restartLoader(LOADER_ID_CATEGORY, null, this);
        mLoaderManager.restartLoader(LOADER_ID_DOMAIN, null, this);
        mLoaderManager.restartLoader(LOADER_ID_FIRSTSUBDOMAIN, null, this);
        mCategoryMap = new HashMap<>();
        mDomainMap = new HashMap<>();
        mFirstSubDomainMap = new HashMap<>();

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        Uri queryUri;
        String order;
        mLoaderReset = false;

        switch (id) {
            case LOADER_ID_CATEGORY:
                //load the category table
                Log.d(TAG, "sourceinfo LOADER_ID_CATEGORY ");
                queryUri = CategoryTableContract.CategoryEntry.CONTENT_URI;
                order = CategoryTableContract.CategoryEntry.COLUMN_CATEGORYTABLE_ID + " ASC";

                return new CursorLoader(mContext,
                        queryUri,
                        CATEGORY_PROJECTION,
                        null,
                        null,
                        order);


            case LOADER_ID_DOMAIN:
                //load the domain table
                Log.d(TAG, "sourceinfo LOADER_ID_DOMAIN");
                queryUri = DomainTableContract.DomainEntry.CONTENT_URI;
                order = DomainTableContract.DomainEntry.COLUMN_DOMAINTABLE_ID + " ASC";

                return new CursorLoader(mContext,
                        queryUri,
                        DOMAIN_PROJECTION,
                        null,
                        null,
                        order);


            case LOADER_ID_FIRSTSUBDOMAIN:
                //load first subdomain table
                Log.d(TAG, "sourceinfo LOADER_ID_FIRSTSUBDOMAIN");
                queryUri = FirstSubdomainTableContract.FirstSubdomainEntry.CONTENT_URI;
                order = FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID + " ASC";

                return new CursorLoader(mContext,
                        queryUri,
                        FIRSTSUBDOMAIN_PROJECTION,
                        null,
                        null,
                        order);


            default:
                throw new RuntimeException("sourceinfo onCreateLoader Loader Not Implemented: " + id);

        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {


        Log.d(TAG, " sourceinfo onLoadFinished=" + loader.getId());
        switch (loader.getId()) {
            case LOADER_ID_CATEGORY:

                if ((data != null) && (data.getCount() > 0)) {
                    mCategoryMap.clear();
                    //construct a map for faster reference
                    for (int i = 0; i < data.getCount(); i++) {
                        data.moveToPosition(i);
                        mCategoryMap.put(data.getInt(INDEX_CATEGORY_CATEGORYTABLE_ID),
                                new SourceCategoryEntry(data.getInt(INDEX_CATEGORY_CATEGORYTABLE_ID),
                                        data.getString(INDEX_CATEGORY_CHI_NAME),
                                        data.getString(INDEX_CATEGORY_ENG_NAME)
                                ));
                    }
                }
                mCategoryCursor = data;

                break;

            case LOADER_ID_DOMAIN:
                if ((data != null) && (data.getCount() > 0)) {
                    mDomainMap.clear();
                    //construct a map for faster reference
                    for (int i = 0; i < data.getCount(); i++) {
                        data.moveToPosition(i);
                        mDomainMap.put(data.getInt(INDEX_CATEGORY_CATEGORYTABLE_ID),
                                new SourceDomainEntry(data.getInt(INDEX_DOMAIN_DOMAINTABLE_ID),
                                        data.getString(INDEX_DOMAIN_CHI_NAME),
                                        data.getString(INDEX_DOMAIN_ENG_NAME),
                                        data.getString(INDEX_DOMAIN_BASEURL)

                                ));
                    }
                }

                mDomainCursor = data;
                break;

            case LOADER_ID_FIRSTSUBDOMAIN:
                if ((data != null) && (data.getCount() > 0)) {
                    mFirstSubDomainMap.clear();
                    //construct a map for faster reference
                    for (int i = 0; i < data.getCount(); i++) {
                        data.moveToPosition(i);
                        mFirstSubDomainMap.put(data.getInt(INDEX_FIRSTSUBDOMAIN_FIRSTSUBDOMAINTABLE_ID),
                                new SourceFirstSubDomainEntry(data.getInt(INDEX_FIRSTSUBDOMAIN_FIRSTSUBDOMAINTABLE_ID),
                                        data.getInt(INDEX_FIRSTSUBDOMAIN_CATEGORYTABLE_ID),
                                        data.getInt(INDEX_FIRSTSUBDOMAIN_DOMAINTABLE_ID),
                                        data.getString(INDEX_FIRSTSUBDOMAIN_SOURCEICONURL)

                                ));
                    }
                }
                mFirstSubDomainCursor = data;
                break;

            default:
                throw new RuntimeException("sourceinfo onLoadFinished Loader Not Implemented: " + loader.getId());
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, " onloaderreset 1=" + loader.getId());
    }


    /**
     * get both source icon url and its english name by using passin firstsubdomainid
     *
     * @param firstsubdomainid : first subdomain id for checking
     * @return an array list (first element will be iconurl, second element will be english name
     */
    public ArrayList<String> getSourceIconURLEngName(int firstsubdomainid) {
        ArrayList<String> result = new ArrayList<>(2);

        String sourceiconurl;
        String engname;

        if (mFirstSubDomainMap.size() > 0) {
            sourceiconurl = mFirstSubDomainMap.get(firstsubdomainid).getSourceIconURL();
        } else {
            sourceiconurl = DEFAULT_WAITING_URL;
        }
        if ((mDomainMap.size() > 0) && (sourceiconurl.compareTo(DEFAULT_WAITING_URL) != 0)) {
            engname = mDomainMap.get(mFirstSubDomainMap.get(firstsubdomainid).getDomainTableID()).getEngName();
        } else {
            engname = DEFAULT_SOURCE_ENG_NAME;
        }

        result.add(sourceiconurl);
        result.add(engname);

        return result;
    }


    /**
     * get both source icon url and its chinese name by using passin firstsubdomainid
     *
     * @param firstsubdomainid : first subdomain id for checking
     * @return an array list (first element will be iconurl, second element will be chinese name
     */
    public ArrayList<String> getSourceIconURLChiName(int firstsubdomainid) {
        ArrayList<String> result = new ArrayList<>(2);

        String sourceiconurl;
        String chiname;


        if (mFirstSubDomainMap.size() > 0) {
            sourceiconurl = mFirstSubDomainMap.get(firstsubdomainid).getSourceIconURL();
        } else {
            sourceiconurl = DEFAULT_WAITING_URL;
        }
        if ((mDomainMap.size() > 0) && (sourceiconurl.compareTo(DEFAULT_WAITING_URL) != 0)) {
            chiname = mDomainMap.get(mFirstSubDomainMap.get(firstsubdomainid).getDomainTableID()).getChiName();
        } else {
            chiname = DEFAULT_SOURCE_ENG_NAME;
        }

        result.add(sourceiconurl);
        result.add(chiname);

        return result;
    }


    /**
     * get category chinese name by using passin firstsubdomainid
     *
     * @param firstsubdomainid : first subdomain id for checking
     * @return chinese name of the category which firstsubdomainid belongs to
     */
    public String getCategoryChiName(int firstsubdomainid) {
        String name = DEFAULT_SOURCE_CHI_NAME;

        if ((mFirstSubDomainMap.size() > 0) && (mCategoryMap.size() > 0)) {
            name = mCategoryMap.get(mFirstSubDomainMap.get(firstsubdomainid).getCategoryTableID()).getChiName();
        }

        return name;
    }

    /**
     * get category english name by using passin firstsubdomainid
     *
     * @param firstsubdomainid : first subdomain id for checking
     * @return english name of the category which firstsubdomainid belongs to
     */
    public String getCategoryEngName(int firstsubdomainid) {
        String name = DEFAULT_SOURCE_ENG_NAME;

        if ((mFirstSubDomainMap.size() > 0) && (mCategoryMap.size() > 0)) {
            name = mCategoryMap.get(mFirstSubDomainMap.get(firstsubdomainid).getCategoryTableID()).getEngName();
        }
        return name;
    }

    /**
     * get domain chinese name by using passin firstsubdomainid
     *
     * @param firstsubdomainid first subdomain id for checking
     * @return chinese name of the domain which firstsubdomainid belongs to
     */
    public String getDomainChiName(int firstsubdomainid) {
        String name = DEFAULT_SOURCE_CHI_NAME;

        if ((mFirstSubDomainMap.size() > 0) && (mCategoryMap.size() > 0)) {
            name = mDomainMap.get(mFirstSubDomainMap.get(firstsubdomainid).getDomainTableID()).getChiName();
        }
        return name;
    }

    /**
     * get domain english name by using passin firstsubdomainid
     *
     * @param firstsubdomainid first subdomain id for checking
     * @return english name of the domain which firstsubdomainid belongs to
     */
    public String getDomainEngName(int firstsubdomainid) {
        String name = DEFAULT_SOURCE_ENG_NAME;
        if ((mFirstSubDomainMap.size() > 0) && (mCategoryMap.size() > 0)) {
            name = mDomainMap.get(mFirstSubDomainMap.get(firstsubdomainid).getDomainTableID()).getEngName();
        }
        return name;
    }


    /**
     * get the domain id by using passin firstsubdomainid
     *
     * @param firstsubdomainid first subdomain id for checking
     * @return domain id
     */
    public int getDomainID(int firstsubdomainid) {

        int domainid = DEFAULT_DOMAIN_ID;
        if (mFirstSubDomainMap.size() > 0) {
            domainid = mFirstSubDomainMap.get(firstsubdomainid).getDomainTableID();
        }
        return domainid;
    }

    /**
     * get a list of first subdomain id which are belongs to pass-in category_num
     *
     * @param category_num category id
     * @return a list of first subdomain id
     */
    public List<FilterFragmentAdapter.FSDCatgeoryInfo> getFSDListWithCategory(int category_num) {
        List<FilterFragmentAdapter.FSDCatgeoryInfo> returnList = new ArrayList<FilterFragmentAdapter.FSDCatgeoryInfo>();

        for (Map.Entry<Integer, SourceFirstSubDomainEntry> fsdentry : mFirstSubDomainMap.entrySet()) {
            if (fsdentry.getValue().getCategoryTableID() == category_num) {
                int firstsubdomain_id = fsdentry.getValue().getFirstSubDomainTableID();
                FilterFragmentAdapter.FSDCatgeoryInfo entry = new FilterFragmentAdapter.FSDCatgeoryInfo(
                        firstsubdomain_id,
                        getDomainChiName(firstsubdomain_id),
                        getDomainEngName(firstsubdomain_id),
                        fsdentry.getValue().getSourceIconURL()
                );
                returnList.add(entry);
            }
        }

        return returnList;
    }


}
