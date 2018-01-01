package com.hk.simplenewsgong.simplegong.data;

import android.net.Uri;
import android.provider.BaseColumns;

import static com.hk.simplenewsgong.simplegong.data.SignalContract.SignalEntry.CONTENT_URI;

/**
 * this table contains the information for each article whether it has been read or bookmarked
 * <p></p>
 * Created by simplegong
 */
public class SignalContract {
    public static final String CONTENT_AUTHORITY = "com.hk.simplenewsgong.simplegong";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_SIGNAL = "signal";

    public static final int INDEX_ARTICLE_ID = 0;
    public static final int INDEX_BOOKMARKALREADY = 1;
    public static final int INDEX_READALREADY = 2;
    public static final int INDEX_TIMESTAMPONDOC = 3;
    public static final String[] PROJECTION = {
            SignalContract.SignalEntry.COLUMN_ARTICLE_ID,
            SignalContract.SignalEntry.COLUMN_BOOKMARKALREADY,
            SignalContract.SignalEntry.COLUMN_READALREADY,
            SignalContract.SignalEntry.COLUMN_TIMESTAMPONDOC
    };


    public static final class SignalEntry implements BaseColumns {


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_SIGNAL)
                .build();

        public static final String TABLE_NAME = "signal";

        public static final String COLUMN_ARTICLE_ID = "article_id";
        public static final String COLUMN_BOOKMARKALREADY = "bookmarkalready";
        public static final String COLUMN_READALREADY = "readalready";
        public static final String COLUMN_TIMESTAMPONDOC = "timestampondoc";

    }

    public static Uri buildSignalUriWithID(long id) {
        return CONTENT_URI.buildUpon()
                .appendPath(Long.toString(id))
                .build();
    }

}
