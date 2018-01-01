package com.hk.simplenewsgong.simplegong.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * this class responsible interact with Preference key/value pair
 * <p>
 * Created by simplegong
 */
public class GongPreference {


    //each number represent the first subdomain id , leftmost is the most prefer , rightmost is the least prefer
    public static final String DEFAULT_PREFERRED_LIST_STRING = "12 15 9 10 7 6 1 2 4 11 8 13 16 17 18 14 19 20 21 22 3 5";
    public static final String PREFERRED_LIST = "preferred_list";
    public static final String DEFAULT_PREFERRED_LIST_DELIMITER = " ";

    public static final long DEFAULT_LASTUPDATETIME_VALUE = 0;
    public static final int DEFAULT_NOOFENTRY_VALUE = 0;


    /**
     * get the preferred first subdomain list from preference
     *
     * @param context : context for accessing preference
     * @return a list of preferred first subdomain id
     */
    public static List<Integer> getPreferredlist(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        //String defaultLocation = context.getString(R.string.pref_location_default);
        String result = sp.getString(PREFERRED_LIST, DEFAULT_PREFERRED_LIST_STRING);

        List<Integer> resultlist = new ArrayList<Integer>();

        //split the string into a list with the default delimiter
        for (String s : result.split(DEFAULT_PREFERRED_LIST_DELIMITER)) {
            resultlist.add(Integer.valueOf(s));
        }

        return resultlist;
    }

    /**
     * set the preferred first subdomain id string to preference
     *
     * @param context : context for accessing preference
     * @param intlist : a list of integer (first subdomain id)
     */
    public static void setPreferredlist(Context context, List<Integer> intlist) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        StringBuilder sb = new StringBuilder();

        //construct the string with the passed in preferred first subdomain id list
        for (int x : intlist) {
            sb.append(String.valueOf(x)).append(DEFAULT_PREFERRED_LIST_DELIMITER);
        }
        sb.deleteCharAt(sb.length() - 1);

        //store the string to preference
        editor.putString(PREFERRED_LIST, sb.toString());
        editor.apply();
    }


}
