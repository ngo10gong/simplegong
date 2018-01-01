package com.hk.simplenewsgong.simplegong.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * a database helper class for creating/upgrading database table
 * <p></p>
 * Created by simplegong
 */


public class GongDbHelper extends SQLiteOpenHelper {

    //database name
    public static final String DATABASE_NAME = "simplegong.db";

    private static final int DATABASE_VERSION = 1;

    public GongDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * create the database
     *
     * @param sqLiteDatabase The database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        // sql statement to create category table
        final String SQL_CREATE_CATEGORY_TABLE =

                "CREATE TABLE " + CategoryTableContract.CategoryEntry.TABLE_NAME + " (" +

                        CategoryTableContract.CategoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        CategoryTableContract.CategoryEntry.COLUMN_CATEGORYTABLE_ID + " INTEGER NOT NULL, " +
                        CategoryTableContract.CategoryEntry.COLUMN_CHI_NAME + " STRING, " +
                        CategoryTableContract.CategoryEntry.COLUMN_ENG_NAME + " STRING ); ";

        sqLiteDatabase.execSQL(SQL_CREATE_CATEGORY_TABLE);


        // sql statement to create domain  table
        final String SQL_CREATE_DOMAIN_TABLE =

                "CREATE TABLE " + DomainTableContract.DomainEntry.TABLE_NAME + " (" +

                        DomainTableContract.DomainEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DomainTableContract.DomainEntry.COLUMN_DOMAINTABLE_ID + " INTEGER NOT NULL, " +
                        DomainTableContract.DomainEntry.COLUMN_BASEURL + " STRING, " +
                        DomainTableContract.DomainEntry.COLUMN_CHI_NAME + " STRING, " +
                        DomainTableContract.DomainEntry.COLUMN_ENG_NAME + " STRING ); ";

        sqLiteDatabase.execSQL(SQL_CREATE_DOMAIN_TABLE);


        // sql statement to create first subdomain  table
        final String SQL_CREATE_FIRSTSUBDOMAIN_TABLE =

                "CREATE TABLE " + FirstSubdomainTableContract.FirstSubdomainEntry.TABLE_NAME + " (" +

                        FirstSubdomainTableContract.FirstSubdomainEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID + " INTEGER , " +
                        FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID + " INTEGER, " +
                        FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID + " INTEGER, " +
                        FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL + " STRING );  ";

        sqLiteDatabase.execSQL(SQL_CREATE_FIRSTSUBDOMAIN_TABLE);

        // sql statement to create signal table
        final String SQL_CREATE_SIGNAL_TABLE =

                "CREATE TABLE " + SignalContract.SignalEntry.TABLE_NAME + " (" +

                        SignalContract.SignalEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        SignalContract.SignalEntry.COLUMN_ARTICLE_ID + " INTEGER , " +
                        SignalContract.SignalEntry.COLUMN_BOOKMARKALREADY + " INTEGER DEFAULT 0, " +
                        SignalContract.SignalEntry.COLUMN_READALREADY + " INTEGER DEFAULT 0, " +
                        SignalContract.SignalEntry.COLUMN_TIMESTAMPONDOC + " INTEGER ); ";

        sqLiteDatabase.execSQL(SQL_CREATE_SIGNAL_TABLE);


        // sql statement to create fragment article table
        final String SQL_CREATE_FRAGARTICLE_TABLE =

                "CREATE TABLE " + FragArticleTableContract.FragArticleEntry.TABLE_NAME + " (" +

                        FragArticleTableContract.FragArticleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        FragArticleTableContract.FragArticleEntry.COLUMN_TIMESTAMPONDOC + " INTEGER, " +
                        FragArticleTableContract.FragArticleEntry.COLUMN_ARTICLEID + " INTEGER, " +
                        FragArticleTableContract.FragArticleEntry.COLUMN_ENTRY + " STRING, " +
                        FragArticleTableContract.FragArticleEntry.COLUMN_FINALURL + " STRING, " +
                        FragArticleTableContract.FragArticleEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID + " INTEGER , " +
                        FragArticleTableContract.FragArticleEntry.COLUMN_IMAGEURL + " STRING, " +
                        FragArticleTableContract.FragArticleEntry.COLUMN_SIMILARITIESCOUNT + " INTEGER DEFAULT 0, " +
                        FragArticleTableContract.FragArticleEntry.COLUMN_TIMESTAMPONDOC_AND_ID + " STRING, " +
                        FragArticleTableContract.FragArticleEntry.COLUMN_NAME + " STRING, " +
                        FragArticleTableContract.FragArticleEntry.COLUMN_CATEGORYTABLE_ID + " INTEGER , " +
                        FragArticleTableContract.FragArticleEntry.COLUMN_ENTITY_NAME + " STRING, " +
                        FragArticleTableContract.FragArticleEntry.COLUMN_ENTITY_ICONURL + " STRING, " +

                        FragArticleTableContract.FragArticleEntry.COLUMN_TITLE + " STRING , " +
                        " UNIQUE ( " + FragArticleTableContract.FragArticleEntry.COLUMN_NAME +
                        " , " + FragArticleTableContract.FragArticleEntry.COLUMN_ARTICLEID + " ) ON CONFLICT REPLACE "
                        + " ); ";

        sqLiteDatabase.execSQL(SQL_CREATE_FRAGARTICLE_TABLE);


    }

    /**
     * not using at this moment, this is for database upgrade
     *
     * @param sqLiteDatabase Database that is being upgraded
     * @param oldVersion     The old database version
     * @param newVersion     The new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CategoryTableContract.CategoryEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DomainTableContract.DomainEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FirstSubdomainTableContract.FirstSubdomainEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SignalContract.SignalEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FragArticleTableContract.FragArticleEntry.TABLE_NAME);


        onCreate(sqLiteDatabase);
    }
}
