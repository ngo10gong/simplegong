package com.hk.simplenewsgong.simplegong.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * this class act as the  ContentProvider for this app
 * this app will use the sqlite database to store the data locally.
 * <p></p>
 * Created by simplegong
 */
public class GongProvider extends ContentProvider {
    private final String TAG = GongProvider.class.getSimpleName();


    // each CODE_XXXX represent a URI matcher constant value
    public static final int CODE_CATEGORY = 103;
    public static final int CODE_DOMAIN = 104;
    public static final int CODE_FIRSTSUBDOMAIN = 105;
    public static final int CODE_SIGNAL = 108;
    public static final int CODE_FRAGARTICLE = 123;
    public static final int CODE_FRAGARTICLE_NAME = 124;
    public static final int CODE_FRAGARTICLE_FIRSTSUBDOMAIN = 125;
    public static final int CODE_FRAGARTICLE_CATEGORY = 126;
    public static final int CODE_FRAGARTICLE_ENTITY_NAMELIST = 127;
    public static final int CODE_FRAGARTICLE_BOOKMARK = 128;


    public static final int CODE_CATEGORY_WITH_ID = 203;
    public static final int CODE_DOMAIN_WITH_ID = 204;
    public static final int CODE_FIRSTSUBDOMAIN_WITH_ID = 205;
    public static final int CODE_SIGNAL_WITH_ID = 208;

    public static final int CODE_FRAGARTICLE_FIRSTSUBDOMAIN_ID = 217;
    public static final int CODE_FRAGARTICLE_CATEGORY_ID = 218;
    public static final int CODE_FRAGARTICLE_NAME_NAME = 220;
    public static final int CODE_FRAGARTICLE_ENTITY_NAMEENTRIES = 221;


    //the following is the mask for indicate which table has been initialized
    public static final int INITIAL_MASK_ALL = 0x001e; // mask for all table have been initialized
    public static final int INITIAL_MASK_CATEGORYTABLE = 0x0002; //mask for indicate category table has been initialzed
    public static final int INITIAL_MASK_DOMAINTABLE = 0x0004; //mask for indicate domain table has been initialzed
    public static final int INITIAL_MASK_FIRSTSUBDOMAINTABLE = 0x0008; //mask for indicate firstsubdomain table has been initialzed
    public static final int INITIAL_MASK_FRAGARTICLETABLE = 0x0010; //mask for indicate fragment article table has been initialzed
    private int mCurrent_initial_mask = 0x0000; //initial value for checking initial mask


    //holding the urimatcher for matching uri
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    //sql helper class to get the read/write database reference
    private GongDbHelper mOpenHelper;

    /**
     * bind specific URIs to CODE_XXXX
     *
     * @return a urimatcher for contentprovider function to use
     */
    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        //the default authority for this app
        final String authority = FragArticleTableContract.CONTENT_AUTHORITY;


        //the following will create the URI for matching
        // for example uri content://com.hk.simplenewsgong.simplegong/category
        matcher.addURI(authority, CategoryTableContract.PATH_CATEGORY, CODE_CATEGORY);
        // for example uri content://com.hk.simplenewsgong.simplegong/domain
        matcher.addURI(authority, DomainTableContract.PATH_DOMAIN, CODE_DOMAIN);
        // for example uri content://com.hk.simplenewsgong.simplegong/firstsubdomain
        matcher.addURI(authority, FirstSubdomainTableContract.PATH_FIRSTSUBDOMAIN, CODE_FIRSTSUBDOMAIN);
        // for example uri content://com.hk.simplenewsgong.simplegong/signal
        matcher.addURI(authority, SignalContract.PATH_SIGNAL, CODE_SIGNAL);


        // for example uri content://com.hk.simplenewsgong.simplegong/fragarticle
        matcher.addURI(authority, FragArticleTableContract.PATH_FRAGARTICLE, CODE_FRAGARTICLE);
        // for example uri content://com.hk.simplenewsgong.simplegong/fragarticle/name
        matcher.addURI(authority, FragArticleTableContract.PATH_FRAGARTICLE + "/"
                + FragArticleTableContract.PATH_NAME, CODE_FRAGARTICLE_NAME);
        // for example uri content://com.hk.simplenewsgong.simplegong/fragarticle/name/hklatestnews
        matcher.addURI(authority, FragArticleTableContract.PATH_FRAGARTICLE + "/"
                + FragArticleTableContract.PATH_NAME + "/*", CODE_FRAGARTICLE_NAME_NAME);
        // for example uri content://com.hk.simplenewsgong.simplegong/fragarticle/category
        matcher.addURI(authority, FragArticleTableContract.PATH_FRAGARTICLE + "/"
                + FragArticleTableContract.PATH_CATEGORY, CODE_FRAGARTICLE_CATEGORY);
        // for example uri content://com.hk.simplenewsgong.simplegong/fragarticle/category/10
        matcher.addURI(authority, FragArticleTableContract.PATH_FRAGARTICLE + "/"
                + FragArticleTableContract.PATH_CATEGORY + "/#", CODE_FRAGARTICLE_CATEGORY_ID);
        // for example uri content://com.hk.simplenewsgong.simplegong/fragarticle/firstsubdomain
        matcher.addURI(authority, FragArticleTableContract.PATH_FRAGARTICLE + "/"
                + FragArticleTableContract.PATH_FIRSTSUBDOMAIN, CODE_FRAGARTICLE_FIRSTSUBDOMAIN);
        // for example uri content://com.hk.simplenewsgong.simplegong/fragarticle/firstsubdomain/10
        matcher.addURI(authority, FragArticleTableContract.PATH_FRAGARTICLE + "/"
                + FragArticleTableContract.PATH_FIRSTSUBDOMAIN + "/#", CODE_FRAGARTICLE_FIRSTSUBDOMAIN_ID);

        // for example uri content://com.hk.simplenewsgong.simplegong/fragarticle/entitynamelist
        matcher.addURI(authority, FragArticleTableContract.PATH_FRAGARTICLE + "/"
                + FragArticleTableContract.PATH_ENTITY_NAMELIST, CODE_FRAGARTICLE_ENTITY_NAMELIST);
        // for example uri content://com.hk.simplenewsgong.simplegong/fragarticle/entitynameentries/r2d2
        matcher.addURI(authority, FragArticleTableContract.PATH_FRAGARTICLE + "/"
                + FragArticleTableContract.PATH_ENTITY_NAMEENTRIES + "/*", CODE_FRAGARTICLE_ENTITY_NAMEENTRIES);
        // for example uri content://com.hk.simplenewsgong.simplegong/fragarticle/bookmark
        matcher.addURI(authority, FragArticleTableContract.PATH_FRAGARTICLE + "/"
                + FragArticleTableContract.PATH_BOOKMARK, CODE_FRAGARTICLE_BOOKMARK);


        // for example uri content://com.hk.simplenewsgong.simplegong/category/10
        matcher.addURI(authority, CategoryTableContract.PATH_CATEGORY + "/#", CODE_CATEGORY_WITH_ID);
        // for example uri content://com.hk.simplenewsgong.simplegong/domain/10
        matcher.addURI(authority, DomainTableContract.PATH_DOMAIN + "/#", CODE_DOMAIN_WITH_ID);
        // for example uri content://com.hk.simplenewsgong.simplegong/firstsubdomain/10
        matcher.addURI(authority, FirstSubdomainTableContract.PATH_FIRSTSUBDOMAIN + "/#", CODE_FIRSTSUBDOMAIN_WITH_ID);
        // for example uri content://com.hk.simplenewsgong.simplegong/signal/10
        matcher.addURI(authority, SignalContract.PATH_SIGNAL + "/#", CODE_SIGNAL_WITH_ID);


        Log.d("matching-uri", "-------------> after matcher.adduri");
        return matcher;
    }

    /**
     * initialize the sqlite dbhelper and do a quick check on the initial state of the app
     *
     * @return true if everything looks good
     */
    @Override
    public boolean onCreate() {


        mOpenHelper = new GongDbHelper(getContext());
        Cursor cursor;


        //check whether category table has initialized already?
        String[] projectionColumns = {CategoryTableContract.CategoryEntry._ID};
        cursor = mOpenHelper.getReadableDatabase().query(
                CategoryTableContract.CategoryEntry.TABLE_NAME,
                projectionColumns,
                null,
                null,
                null,
                null,
                null);

        if ((cursor != null) && (cursor.getCount() > 0)) {
            mCurrent_initial_mask |= INITIAL_MASK_CATEGORYTABLE;
        }


        //check whether domain table has initialized already?
        projectionColumns[0] = DomainTableContract.DomainEntry._ID;
        cursor = mOpenHelper.getReadableDatabase().query(
                DomainTableContract.DomainEntry.TABLE_NAME,
                projectionColumns,
                null,
                null,
                null,
                null,
                null);

        if ((cursor != null) && (cursor.getCount() > 0)) {
            mCurrent_initial_mask |= INITIAL_MASK_DOMAINTABLE;
        }

        //check whether firstsubdomain table has initialized already?
        projectionColumns[0] = FirstSubdomainTableContract.FirstSubdomainEntry._ID;
        cursor = mOpenHelper.getReadableDatabase().query(
                FirstSubdomainTableContract.FirstSubdomainEntry.TABLE_NAME,
                projectionColumns,
                null,
                null,
                null,
                null,
                null);

        if ((cursor != null) && (cursor.getCount() > 0)) {
            mCurrent_initial_mask |= INITIAL_MASK_FIRSTSUBDOMAINTABLE;
        }

        //check whether fragarticle table has initialized already?
        projectionColumns[0] = FragArticleTableContract.FragArticleEntry._ID;
        cursor = mOpenHelper.getReadableDatabase().query(
                FragArticleTableContract.FragArticleEntry.TABLE_NAME,
                projectionColumns,
                null,
                null,
                null,
                null,
                null);

        if ((cursor != null) && (cursor.getCount() > 0)) {
            mCurrent_initial_mask |= INITIAL_MASK_FRAGARTICLETABLE;
        }


        Log.d(TAG, " onCreate mcurrent_initial_mask = " + mCurrent_initial_mask);
        return true;
    }

    /**
     * bulk insert multiple row values for specific uri
     *
     * @param uri    The content:// URI of the bulk insert request.
     * @param values An array of sets of column_name/value pairs to add to the database.
     *               it can't be null
     * @return The number of values that were inserted.
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsInserted = 0;
        String tablenameToInsert = "NOTABLE";
        Log.d(TAG, "---> gongprovider  bulkinsert uri=" + uri);
        int mask = 0; //temp storage for testing mask value

        switch (sUriMatcher.match(uri)) {

            case CODE_CATEGORY:
                tablenameToInsert = CategoryTableContract.CategoryEntry.TABLE_NAME;
                mask = INITIAL_MASK_CATEGORYTABLE;
                break;

            case CODE_DOMAIN:
                tablenameToInsert = DomainTableContract.DomainEntry.TABLE_NAME;
                mask = INITIAL_MASK_DOMAINTABLE;
                break;

            case CODE_FIRSTSUBDOMAIN:
                tablenameToInsert = FirstSubdomainTableContract.FirstSubdomainEntry.TABLE_NAME;
                mask = INITIAL_MASK_FIRSTSUBDOMAINTABLE;
                break;

            case CODE_SIGNAL:
                tablenameToInsert = SignalContract.SignalEntry.TABLE_NAME;

                break;

            case CODE_FRAGARTICLE:
            case CODE_FRAGARTICLE_NAME:
            case CODE_FRAGARTICLE_NAME_NAME:
            case CODE_FRAGARTICLE_FIRSTSUBDOMAIN:
            case CODE_FRAGARTICLE_CATEGORY:
            case CODE_FRAGARTICLE_ENTITY_NAMELIST:
            case CODE_FRAGARTICLE_ENTITY_NAMEENTRIES:
                tablenameToInsert = FragArticleTableContract.FragArticleEntry.TABLE_NAME;
                mask = INITIAL_MASK_FRAGARTICLETABLE;
                break;


            default:
                return super.bulkInsert(uri, values);
        }
        db.beginTransaction();
        rowsInserted = 0;
        try {
            Log.d(TAG, " bulkinsert tabletoinsert name =" + tablenameToInsert);
            for (ContentValues value : values) {
                //insert the data and replace if there is a conflict the new data
                long _id = db.insertWithOnConflict(tablenameToInsert, null, value, SQLiteDatabase.CONFLICT_REPLACE);
                if (_id != -1) {
                    rowsInserted++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        //after succesfully inserted the data, notify the listner of fragtable only if category&domain&firstsubdomain table
        //are initialized already.
        if (rowsInserted > 0) {
            if ((mCurrent_initial_mask ^ INITIAL_MASK_ALL) == 0) {
                //after all done the initial state
                Log.d(TAG, " bulkinsert 0 rowsinserted=" + rowsInserted);
                getContext().getContentResolver().notifyChange(uri, null);

            } else {

                mCurrent_initial_mask |= mask;
                Log.d(TAG, " bulkinsert 1 mcurrent_mask = " + mCurrent_initial_mask + ", mask=" + mask);
                if ((mask == INITIAL_MASK_FRAGARTICLETABLE)
                        && ((mCurrent_initial_mask ^ INITIAL_MASK_ALL) == 0)) {
                    Log.d(TAG, " bulkinsert 2");
                    // all table have initialzed, the current URI is FragArticleTable
                    getContext().getContentResolver().notifyChange(uri, null);
                    Log.d(TAG, " bulkinsert 3");
                } else if ((mCurrent_initial_mask ^ INITIAL_MASK_ALL) == 0) {
                    //all done for table initial
                    // all table have initialzed, the current URI is not FragArticleTable
                    Log.d(TAG, " bulkinsert 4");
                    getContext().getContentResolver().notifyChange(FragArticleTableContract.FragArticleEntry.CONTENT_URI, null);
                    Log.d(TAG, " bulkinsert 5");
                    getContext().getContentResolver().notifyChange(uri, null);
                    Log.d(TAG, " bulkinsert 6");
                } else if ((mask != INITIAL_MASK_FRAGARTICLETABLE)
                        ) {
                    Log.d(TAG, " bulkinsert 7");
                    getContext().getContentResolver().notifyChange(uri, null);
                }
            }
        }

        return rowsInserted;

    }

    /**
     * handle query with different uri request.
     *
     * @param uri           uri to query
     * @param projection    desired column to be included
     * @param selection     filtering criteria
     * @param selectionArgs value that corresponding to @param selection
     * @param sortOrder     sorting behavior
     * @return result cursor
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor cursor;

        Log.d(TAG, "---> gongprovider query uri=" + uri);
        String lastPathSegment = uri.getLastPathSegment(); //get the last path segment value
        Log.d(TAG, "---> gongprovider query lastpathsegment=" + lastPathSegment);
        String[] selectionArguments = new String[]{lastPathSegment};
        List<String> uriList = uri.getPathSegments(); //get a list of all the path segment value except authority
        for (String x : uriList) {
            //print it out and see :)
            Log.d(TAG, "---> gongprovider query uripath=" + x);
        }

        //determine which uri corresponding
        switch (sUriMatcher.match(uri)) {

            case CODE_CATEGORY:
                cursor = mOpenHelper.getReadableDatabase().query(
                        CategoryTableContract.CategoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                Log.d(TAG, "query , CODE_CATEGORY");
                if (cursor.getCount() > 0) {
                    //indicate this table has been initialized
                    mCurrent_initial_mask |= INITIAL_MASK_CATEGORYTABLE;
                }
                break;

            case CODE_DOMAIN:
                cursor = mOpenHelper.getReadableDatabase().query(
                        DomainTableContract.DomainEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                Log.d(TAG, "query , CODE_DOMAIN");
                if (cursor.getCount() > 0) {
                    //indicate this table has been initialized
                    mCurrent_initial_mask |= INITIAL_MASK_DOMAINTABLE;
                }
                break;


            case CODE_FIRSTSUBDOMAIN:
                cursor = mOpenHelper.getReadableDatabase().query(
                        FirstSubdomainTableContract.FirstSubdomainEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                Log.d(TAG, "query , CODE_FIRSTSUBDOMAIN");
                if (cursor.getCount() > 0) {
                    //indicate this table has been initialized
                    mCurrent_initial_mask |= INITIAL_MASK_FIRSTSUBDOMAINTABLE;
                }
                break;

            case CODE_SIGNAL:
                cursor = mOpenHelper.getReadableDatabase().query(
                        SignalContract.SignalEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                Log.d(TAG, "query , CODE_SIGNAL");
                break;


            case CODE_CATEGORY_WITH_ID:
                cursor = mOpenHelper.getReadableDatabase().query(
                        CategoryTableContract.CategoryEntry.TABLE_NAME,
                        projection,
                        CategoryTableContract.CategoryEntry.COLUMN_CATEGORYTABLE_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                Log.d(TAG, "query , CODE_CATEGORY_WITH_ID");

                break;

            case CODE_DOMAIN_WITH_ID:
                cursor = mOpenHelper.getReadableDatabase().query(
                        DomainTableContract.DomainEntry.TABLE_NAME,
                        projection,
                        DomainTableContract.DomainEntry.COLUMN_DOMAINTABLE_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                Log.d(TAG, "query , CODE_DOMAIN_WITH_ID");
                break;

            case CODE_FIRSTSUBDOMAIN_WITH_ID:
                cursor = mOpenHelper.getReadableDatabase().query(
                        FirstSubdomainTableContract.FirstSubdomainEntry.TABLE_NAME,
                        projection,
                        FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                Log.d(TAG, "query , CODE_FIRSTSUBDOMAIN_WITH_ID");

                break;

            case CODE_SIGNAL_WITH_ID:
                cursor = mOpenHelper.getReadableDatabase().query(
                        SignalContract.SignalEntry.TABLE_NAME,
                        projection,
                        SignalContract.SignalEntry.COLUMN_ARTICLE_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                Log.d(TAG, "query , CODE_SIGNAL_WITH_ID");

                break;


            case CODE_FRAGARTICLE:
                cursor = mOpenHelper.getReadableDatabase().query(
                        FragArticleTableContract.FragArticleEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                Log.d(TAG, "query , CODE_FRAGARTICLE");
                if (cursor.getCount() > 0) {
                    mCurrent_initial_mask |= INITIAL_MASK_FRAGARTICLETABLE;
                }


                break;

            //use rawquery to join two extra tables(signal, firstsubdomain) for getting information
            case CODE_FRAGARTICLE_NAME_NAME:
                cursor = mOpenHelper.getReadableDatabase().rawQuery(FragArticleTableContract.RAWQUERY_FRAGARTICLE_NAMESTRING,
                        selectionArguments);
                if (cursor.getCount() > 0) {
                    mCurrent_initial_mask |= INITIAL_MASK_FRAGARTICLETABLE;
                }

                Log.d(TAG, "query , CODE_FRAGARTICLE_NAME_NAME");
                break;

            //get all the article with mark as bookmarked
            case CODE_FRAGARTICLE_BOOKMARK:
                cursor = mOpenHelper.getReadableDatabase().rawQuery(FragArticleTableContract.RAWQUERYBOOKMARKSTRING,
                        null);
                if (cursor.getCount() > 0) {
                    mCurrent_initial_mask |= INITIAL_MASK_FRAGARTICLETABLE;
                }

                Log.d(TAG, "query , CODE_FRAGARTICLE_BOOKMARK");
                break;

            case CODE_FRAGARTICLE_CATEGORY:
                cursor = mOpenHelper.getReadableDatabase().rawQuery(FragArticleTableContract.RAWQUERY_FRAGARTICLE_CATEGORYSTRING,
                        selectionArguments);
                if (cursor.getCount() > 0) {
                    mCurrent_initial_mask |= INITIAL_MASK_FRAGARTICLETABLE;
                }

                Log.d(TAG, "query , CODE_FRAGARTICLE_CATEGORY");
                break;

            case CODE_FRAGARTICLE_FIRSTSUBDOMAIN_ID:
                cursor = mOpenHelper.getReadableDatabase().rawQuery(FragArticleTableContract.RAWQUERY_FRAGARTICLE_FIRSTSUBSTRING,
                        selectionArguments);
                if (cursor.getCount() > 0) {
                    mCurrent_initial_mask |= INITIAL_MASK_FRAGARTICLETABLE;
                }

                Log.d(TAG, "query , CODE_FRAGARTICLE_FIRSTSUBDOMAIN_ID");
                break;
            case CODE_FRAGARTICLE_CATEGORY_ID:
                cursor = mOpenHelper.getReadableDatabase().rawQuery(FragArticleTableContract.RAWQUERY_FRAGARTICLE_SELECTIONSTRING
                                + " where "
                                + selection
                                + FragArticleTableContract.RAWQUERY_ORDERSTRING,
                        selectionArgs);

                if (cursor.getCount() > 0) {
                    mCurrent_initial_mask |= INITIAL_MASK_FRAGARTICLETABLE;
                }

                Log.d(TAG, "query , CODE_FRAGARTICLE_CATEGORY_ID");
                break;

            case CODE_FRAGARTICLE_ENTITY_NAMELIST:
                cursor = mOpenHelper.getReadableDatabase().rawQuery(FragArticleTableContract.RAWQUERY_FRAGARTICLE_ENTITY_NAMELISTSTRING,
                        null);

                break;
            case CODE_FRAGARTICLE_ENTITY_NAMEENTRIES:
                cursor = mOpenHelper.getReadableDatabase().rawQuery(FragArticleTableContract.RAWQUERY_FRAGARTICLE_ENTITY_NAMEENTRIESSTRING,
                        selectionArguments);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        Log.d(TAG, " query setnotification=" + uri + " ,cursor.getcount= " + cursor.getCount());

        return cursor;
    }

    /**
     * delete data for speicific uri with selected argument value
     *
     * @param uri           uri to delete
     * @param selection     filter string.
     * @param selectionArgs filter value
     * @return number of deleted rows
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "--->  delete uri=" + uri);
        String lastPathSegment = uri.getLastPathSegment();
        String[] selectionArguments = new String[]{lastPathSegment};
        String[] finalselectionArg = selectionArgs;

        /* Users of the delete method will expect the number of rows deleted to be returned. */
        int numRowsDeleted;
        String tablenameToDelete = "NOTABLE"; //table name to be deleted

        if (null == selection) selection = "1"; // "1" delete all information for specific table
        Log.d(TAG, "--->  delete match-uri=" + sUriMatcher.match(uri));
        switch (sUriMatcher.match(uri)) {
            case CODE_CATEGORY:
                tablenameToDelete = CategoryTableContract.CategoryEntry.TABLE_NAME;
                break;

            case CODE_DOMAIN:
                tablenameToDelete = DomainTableContract.DomainEntry.TABLE_NAME;
                break;

            case CODE_FIRSTSUBDOMAIN:
                tablenameToDelete = FirstSubdomainTableContract.FirstSubdomainEntry.TABLE_NAME;
                break;

            case CODE_SIGNAL:
                tablenameToDelete = SignalContract.SignalEntry.TABLE_NAME;
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                tablenameToDelete,
                selection,
                finalselectionArg);

        return numRowsDeleted;
    }

    /**
     * for this app, we do not process any other mime type
     *
     * @param uri uri to delete.
     * @return return null for null type.
     */
    @Override
    public String getType(@NonNull Uri uri) {
        Log.d(TAG, "--->  gettype uri=" + uri);

        return null;
        //throw new RuntimeException("we do not support other type in this app");
    }

    /**
     * insert one row for specific uri
     *
     * @param uri    uri to insert one row
     * @param values column name/value pair to be inserted
     * @return return back the uri that for insert.
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        String lastPathSegment = uri.getLastPathSegment(); //getting the last path segment information
        String[] selectionArguments = new String[]{lastPathSegment};
        Log.d(TAG, "--->  insert uri=" + uri + ", lastpathsegment=" + lastPathSegment);
        Log.d(TAG, "--->  insert match uri=" + sUriMatcher.match(uri));

        long rowresult = 0; // result of newly inserted row id
        Cursor cursor; //cursor holding the query result
        String sortOrder;  // sort order string
        switch (sUriMatcher.match(uri)) {

            case CODE_FRAGARTICLE:
            case CODE_FRAGARTICLE_NAME_NAME:
            case CODE_FRAGARTICLE_FIRSTSUBDOMAIN:
            case CODE_FRAGARTICLE_CATEGORY:
                rowresult = mOpenHelper.getWritableDatabase().insertWithOnConflict(
                        FragArticleTableContract.FragArticleEntry.TABLE_NAME,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_REPLACE);
                Log.d(TAG, " insert update CODE_FRAGARTICLE rowresult = " + rowresult);
                break;


            case CODE_SIGNAL:
            case CODE_SIGNAL_WITH_ID:
                //find whether there is one ,it can use the above insertwithonconflict method to achieve
                //the same thing
                int articleID = 0;

                if (values.containsKey(SignalContract.SignalEntry.COLUMN_ARTICLE_ID)) {
                    articleID = (int) values.get(SignalContract.SignalEntry.COLUMN_ARTICLE_ID);
                    selectionArguments = new String[]{String.valueOf(articleID)};
                }
                sortOrder = SignalContract.SignalEntry.COLUMN_ARTICLE_ID + " ASC ";
                cursor = mOpenHelper.getReadableDatabase().query(
                        SignalContract.SignalEntry.TABLE_NAME,
                        SignalContract.PROJECTION,
                        SignalContract.SignalEntry.COLUMN_ARTICLE_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);

                if ((cursor == null) || (cursor.getCount() == 0)) {
                    rowresult = mOpenHelper.getWritableDatabase().insert(SignalContract.SignalEntry.TABLE_NAME,
                            null,
                            values);
                    if (rowresult == -1) {
                        throw new RuntimeException(
                                " insert error in gong. code_signal ");

                    }
                    Log.d(TAG, " insert rowresult = " + rowresult);
                    getContext().getContentResolver().notifyChange(SignalContract.SignalEntry.CONTENT_URI, null);
                    return SignalContract.SignalEntry.CONTENT_URI;
                } else {
                    //do update if there is one

                    rowresult = mOpenHelper.getWritableDatabase().update(
                            SignalContract.SignalEntry.TABLE_NAME,
                            values,
                            SignalContract.SignalEntry.COLUMN_ARTICLE_ID + " = ? ",
                            selectionArguments
                    );
                    if (rowresult != 1) {
                        throw new RuntimeException(
                                " insert update error in gong. code_signal ");

                    }
                    Log.d(TAG, " insert update CODE_SIGNAL_WITH_ID rowresult = " + rowresult);
                    getContext().getContentResolver().notifyChange(SignalContract.SignalEntry.CONTENT_URI, null);
                    return uri;
                }

            case CODE_CATEGORY:
            case CODE_DOMAIN:
            case CODE_FIRSTSUBDOMAIN:
            case CODE_CATEGORY_WITH_ID:
            case CODE_DOMAIN_WITH_ID:
            case CODE_FIRSTSUBDOMAIN_WITH_ID:
            default:
                throw new RuntimeException(
                        "We are not implementing insert in gong for " + sUriMatcher.match(uri));

        }
        //notify the change for the specific uri
        getContext().getContentResolver().notifyChange(uri, null);
        return uri;
    }

    /**
     * for this app, it does not need to implement this method
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String lastPathSegment = uri.getLastPathSegment();
        String[] selectionArguments = new String[]{lastPathSegment};
        Log.d(TAG, "--->  update uri=" + uri + ", lastpathsegment=" + lastPathSegment);

        Log.d(TAG, "--->  update match uri=" + sUriMatcher.match(uri));

        getContext().getContentResolver().notifyChange(uri, null);

        return 0;
    }

    /**
     * this is for testing framework
     */
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
