package com.hk.simplenewsgong.simplegong.util;

import android.content.Context;

import com.hk.simplenewsgong.simplegong.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * this singleton class contains fragment information (like category name, english name, chinese name, category id)
 * for using in MainNewsActivity
 * <p>
 * Created by simplegong
 */
public class FragInfo {

    //fragment information map , key is the category name of this fragment ,
    // value is a class contain information about this fragment
    private static final Map<String, FragInfoElem> FRAGINFO_MAP = new HashMap<String, FragInfoElem>();

    //fragment archive information map, key is the category name of this fragment,
    // value is the archive name of this category name
    private static final Map<String, String> FRAGINFO_ARCHIVEMAP = new HashMap<String, String>();

    //list contains the fragment information to display in tab section
    private static final List<FragInfoElem> FRAGINFO_TABLIST = new ArrayList<FragInfoElem>();

    /**
     * individual fragment information
     */
    public class FragInfoElem {
        private String displayName;  // displayname on tab
        private int category_id;  //category id associated with this fragment
        private String fragtablename; // fragment table associated with is fragment
        private int startingLoaderID; // offset for assigning loader id

        /**
         * constructor
         *
         * @param dName     : display name ,
         * @param cid       : category id
         * @param fname     : fragment table name
         * @param sloaderid : loader offset starting value
         */
        public FragInfoElem(String dName, int cid, String fname, int sloaderid) {
            displayName = dName;
            category_id = cid;
            fragtablename = fname;
            startingLoaderID = sloaderid;
        }

        /**
         * @return category id of this fragment associated
         */
        public int get_CategoryID() {
            return category_id;
        }

        /**
         * @return display name of this fragment associated
         */
        public String get_name() {
            return displayName;
        }

        /**
         * @return fragment table name of this fragment associated
         */
        public String get_fragtablename() {
            return fragtablename;
        }

        /**
         * @return preference name of this fragment associated number of entry
         */
        public String get_noofentryname() {
            return fragtablename + "_local_noofentry";
        }

        /**
         * @return preference name of this fragment associated last update time
         */
        public String get_lastupdatetimename() {
            return fragtablename + "_local_lastupdatetime";
        }

        /**
         * @return starting loader id value of this fragment associated
         */
        public int getStartingLoaderID() {
            return startingLoaderID;
        }
    }


    //contain instance of this class
    private static FragInfo mInstance = null;

    //context to use getstring
    private static Context mContext = null;

    /**
     * initialize the class with context
     *
     * @param context : context use for getstring
     */
    public static void init(Context context) {
        mContext = context;
        mInstance = new FragInfo(context);
    }

    /**
     * get singleton reference
     *
     * @return FragInfo reference
     */
    public static FragInfo getInstance() {
        if (mInstance == null) {
            mInstance = new FragInfo(mContext);
            return mInstance;
        } else {
            return mInstance;
        }
    }

    /**
     * get singleton reference
     *
     * @param context : context use for getstring
     * @return FragInfo reference
     */
    public static FragInfo getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new FragInfo(context);
            return mInstance;
        } else {
            return mInstance;
        }
    }


    /**
     * private constructor
     *
     * @param context : context to use for getstring
     */
    private FragInfo(Context context) {
        mContext = context;
        FRAGINFO_MAP.clear();
        FRAGINFO_MAP.put("hklatestnewstable", new FragInfoElem(mContext.getString(R.string.fraginfo_hklatestnews), 3, "hklatestnewstable", 1000));
        FRAGINFO_MAP.put("hknewstable", new FragInfoElem(mContext.getString(R.string.fraginfo_hknews), 2, "hknewstable", 1100));
        FRAGINFO_MAP.put("finlatestnewstable", new FragInfoElem(mContext.getString(R.string.fraginfo_finlatestnews), 5, "finlatestnewstable", 1200));
        FRAGINFO_MAP.put("finnewstable", new FragInfoElem(mContext.getString(R.string.fraginfo_finnews), 6, "finnewstable", 1300));
        FRAGINFO_MAP.put("entnewstable", new FragInfoElem(mContext.getString(R.string.fraginfo_entnews), 8, "entnewstable", 1400));
        FRAGINFO_MAP.put("sportsnewstable", new FragInfoElem(mContext.getString(R.string.fraginfo_sportsnews), 7, "sportsnewstable", 1500));
        FRAGINFO_MAP.put("sportsnewsarchivetable", new FragInfoElem(mContext.getString(R.string.fraginfo_sportsnewsarchive), 7, "sportsnewsarchivetable", 1500));
        FRAGINFO_MAP.put("entnewsarchivetable", new FragInfoElem(mContext.getString(R.string.fraginfo_entnewsarchive), 8, "entnewsarchivetable", 1400));
        FRAGINFO_MAP.put("finnewsarchivetable", new FragInfoElem(mContext.getString(R.string.fraginfo_finnewsarchive), 6, "finnewsarchivetable", 1300));
        FRAGINFO_MAP.put("hknewsarchivetable", new FragInfoElem(mContext.getString(R.string.fraginfo_hknewsarchive), 2, "hknewsarchivetable", 1100));
        FRAGINFO_MAP.put("finlatestnewsarchivetable", new FragInfoElem(mContext.getString(R.string.fraginfo_finlatestnewsarchive), 5, "finlatestnewsarchivetable", 1200));

        FRAGINFO_ARCHIVEMAP.clear();
        FRAGINFO_ARCHIVEMAP.put("hknewstable", "hknewsarchivetable");
        FRAGINFO_ARCHIVEMAP.put("finlatestnewstable", "finlatestnewsarchivetable");
        FRAGINFO_ARCHIVEMAP.put("finnewstable", "finnewsarchivetable");
        FRAGINFO_ARCHIVEMAP.put("entnewstable", "entnewsarchivetable");
        FRAGINFO_ARCHIVEMAP.put("sportsnewstable", "sportsnewsarchivetable");

        FRAGINFO_TABLIST.clear();
        FRAGINFO_TABLIST.add(FRAGINFO_MAP.get("hklatestnewstable"));
        FRAGINFO_TABLIST.add(FRAGINFO_MAP.get("hknewstable"));
        FRAGINFO_TABLIST.add(FRAGINFO_MAP.get("finlatestnewstable"));
        FRAGINFO_TABLIST.add(FRAGINFO_MAP.get("finnewstable"));
        FRAGINFO_TABLIST.add(FRAGINFO_MAP.get("entnewstable"));
        FRAGINFO_TABLIST.add(FRAGINFO_MAP.get("sportsnewstable"));


    }

    /**
     * get archive category id from its corresponding non archive category name
     *
     * @param indexname : non archive category name
     * @return category id
     */
    public int get_archivecategory(String indexname) {
        return FRAGINFO_MAP.get(FRAGINFO_ARCHIVEMAP.get(indexname)).get_CategoryID();
    }

    /**
     * get archive name from non archive category name
     *
     * @param indexname : non archive category name
     * @return archive name associate with non archive category name
     */
    public String get_archivetablename(String indexname) {
        return FRAGINFO_ARCHIVEMAP.get(indexname);
    }

    /**
     * get preference no of entry name of archive category
     *
     * @param indexname : non archive category name
     * @return preference no of entry name of archive category
     */
    public String get_archivenoofentryname(String indexname) {
        return FRAGINFO_MAP.get(FRAGINFO_ARCHIVEMAP.get(indexname)).get_noofentryname();
    }

    /**
     * get preference no of entry name of non archive category
     *
     * @param indexname : non archive category name
     * @return preference no of entry name of category
     */
    public String get_noofentryname(String indexname) {
        return FRAGINFO_MAP.get(indexname).get_noofentryname();
    }

    /**
     * get loader starting value of category name
     *
     * @param indexname : non archive category name
     * @return loader starting offset
     */
    public int get_startingloaderid(String indexname) {
        return FRAGINFO_MAP.get(indexname).getStartingLoaderID();
    }

    /**
     * get category id with category name in this fragment
     *
     * @param indexname : non archive category name
     * @return category id of the associated fragment
     */
    public int get_category(String indexname) {
        return FRAGINFO_MAP.get(indexname).get_CategoryID();
    }

    /**
     * get category id with specific tab position
     *
     * @param position : tab position in the tablayout
     * @return category id of the associated fragment
     */
    public int get_category(int position) {
        return FRAGINFO_TABLIST.get(position).get_CategoryID();
    }

    /**
     * get display string for a specific tab position
     *
     * @param position : tab position in the tablayout
     * @return display string for specific tab
     */
    public String get_tablistname(int position) {
        return FRAGINFO_TABLIST.get(position).get_name();
    }

    /**
     * get the how many tabs should be display in tablayout
     *
     * @return how many tabs in the tablayout
     */
    public int get_tablistSize() {
        return FRAGINFO_TABLIST.size();
    }


    /**
     * get table name for a specific tab position
     *
     * @param position : tab position in the tablayout
     * @return display table name for specific tab
     */
    public String get_tablistFragTableName(int position) {
        return FRAGINFO_TABLIST.get(position).get_fragtablename();
    }


    /**
     * get category name for the specific category id
     *
     * @param categoryid : category id
     * @return category name
     */
    public String get_EngNameFromCategory(int categoryid) {
        for (Map.Entry<String, FragInfoElem> entry : FRAGINFO_MAP.entrySet()) {
            if ((entry.getValue().get_CategoryID() == categoryid)
                    && (!entry.getKey().contains("archive"))) {
                return entry.getKey();
            }
        }
        return "EMPTYSTRINGVALUE";

    }


}
