package com.hk.simplenewsgong.simplegong.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * contains the domain table information.
 * domain  table has 4 columns
 * domain_id : reference id from server side database
 * chi_name : chinese name of media source
 * eng_name : english name of media source
 * baseurl : media source's domain url
 * <p>
 * Created by simplegong
 */

public class DomainTableContract {
    public static final String CONTENT_AUTHORITY = "com.hk.simplenewsgong.simplegong";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_DOMAIN = "domaintable";

    public static final class DomainEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_DOMAIN)
                .build();

        public static final String TABLE_NAME = "domain";

        public static final String COLUMN_DOMAINTABLE_ID = "domaintable_id";
        public static final String COLUMN_BASEURL = "baseurl";
        public static final String COLUMN_CHI_NAME = "chi_name";
        public static final String COLUMN_ENG_NAME = "eng_name";

    }

}
