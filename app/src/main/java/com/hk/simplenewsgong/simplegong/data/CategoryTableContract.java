package com.hk.simplenewsgong.simplegong.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * contains the category table information.
 * Category table has 3 columns
 * categorytable_id : reference id from server side database
 * chi_name : chinese name
 * eng_name : english name
 * <p>
 * Created by simplegong
 */

public class CategoryTableContract {
    public static final String CONTENT_AUTHORITY = "com.hk.simplenewsgong.simplegong";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_CATEGORY = "categorytable";

    public static final class CategoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_CATEGORY)
                .build();

        public static final String TABLE_NAME = "category";

        public static final String COLUMN_CATEGORYTABLE_ID = "categorytable_id";
        public static final String COLUMN_CHI_NAME = "chi_name";
        public static final String COLUMN_ENG_NAME = "eng_name";

    }

}
