package com.hk.simplenewsgong.simplegong.util;


import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateUtils;

import com.hk.simplenewsgong.simplegong.data.GongPreference;
//10739579165

/**
 * util class to get the formatted hong kong time
 * <p>
 * Created by simplegong
 */
public class GongTimeUtil {
    public static final long HOUR_IN_MILLIS = TimeUnit.HOURS.toMillis(1);
    private static TimeZone hkTimeZone16 = TimeZone.getTimeZone("GMT+16");


    /**
     * get the formatted hong kong time with the unix time input
     *
     * @param timedata : unix epoch time
     * @return formatted string for hong kong time format
     */
    public static String getDisplayTimeStringFromData(long timedata) {

        long hkgmtOffsetMillis16 = hkTimeZone16.getOffset(timedata * 1000);
        long hktimeSinceEpochLocalTimeMillis16 = (timedata * 1000) + hkgmtOffsetMillis16;

        SimpleDateFormat hkdateformat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm 香港 ");
        return  hkdateformat.format(Long.valueOf(hktimeSinceEpochLocalTimeMillis16));

    }


}
