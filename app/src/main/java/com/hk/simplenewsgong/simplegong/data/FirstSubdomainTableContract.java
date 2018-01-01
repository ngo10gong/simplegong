package com.hk.simplenewsgong.simplegong.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * contains the first subdomain table information.
 * domain  table has 4 columns
 * firstsubdomaintable_id : reference id from server side database
 * domaintable_id  : reference id from domain table
 * categorytable_id  : reference id from category table
 * sourceiconurl : this subdomain icon url
 * <p>
 * Created by simplegong
 */

public class FirstSubdomainTableContract {
    public static final String CONTENT_AUTHORITY = "com.hk.simplenewsgong.simplegong";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FIRSTSUBDOMAIN = "firstsubdomain";

    public static final class FirstSubdomainEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FIRSTSUBDOMAIN)
                .build();

        public static final String TABLE_NAME = "firstsubdomain";

        public static final String COLUMN_FIRSTSUBDOMAINTABLE_ID = "firstsubdomaintable_id";
        public static final String COLUMN_DOMAINTABLE_ID = "domaintable_id";
        public static final String COLUMN_CATEGORYTABLE_ID = "categorytable_id";
        public static final String COLUMN_SOURCEICONURL = "sourceiconurl";

    }


}
