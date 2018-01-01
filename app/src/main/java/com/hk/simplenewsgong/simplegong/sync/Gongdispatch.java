package com.hk.simplenewsgong.simplegong.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.AbstractWindowedCursor;
import android.database.Cursor;
import android.database.CursorWindow;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.hk.simplenewsgong.simplegong.FilterFragment;
import com.hk.simplenewsgong.simplegong.FilterFragmentAdapter;
import com.hk.simplenewsgong.simplegong.GlideApp;
import com.hk.simplenewsgong.simplegong.data.CategoryTableContract;
import com.hk.simplenewsgong.simplegong.data.DomainTableContract;
import com.hk.simplenewsgong.simplegong.data.FirstSubdomainTableContract;
import com.hk.simplenewsgong.simplegong.data.FragArticleTableContract;
import com.hk.simplenewsgong.simplegong.data.SourceInfo;
import com.hk.simplenewsgong.simplegong.util.FragInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * this class has the functions for
 * 1, dispatch the thread to insert the initial fake data to the database
 * 2, dispatch the thread to insert more data to the database after initial
 * 3, check whether the device is online or not
 * <p>
 * <p>
 * Created by simplegong
 */
public class Gongdispatch {
    private final static String TAG = Gongdispatch.class.getSimpleName();

    private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS + SYNC_INTERVAL_SECONDS / 2;
    private static final int SYNC_INTERVAL_SECONDS_INITIAL = 20;
    private static final int SYNC_FLEXTIME_SECONDS_INITIAL = SYNC_INTERVAL_SECONDS_INITIAL + SYNC_INTERVAL_SECONDS_INITIAL / 2;

    private static boolean fragInitialized; // indicator whether the app have been initialized or not

    /**
     * check the database has been initialized with fake data or not
     * if it does not have, bulk insert the fake data by creating a thread to finish the job
     * if it does have data, do nothing
     *
     * @param context Context to use for communicate with content provider
     */
    synchronized public static void fraginitialize(@NonNull final Context context) {
        Log.d(TAG, "in fraginitialize initialize 1");

        if (fragInitialized) {
            //do nothing if it has been initilized
            return;
        }


        fragInitialized = true;
        Log.d(TAG, "in fraginitialize initialize 3");

        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "in fraginitialize thread 1 ");

                // check any data in the database
                Uri fragQueryUri = FragArticleTableContract.FragArticleEntry.CONTENT_URI;
                String[] projectionColumns = {FragArticleTableContract.FragArticleEntry._ID};
                Cursor cursor = context.getContentResolver().query(
                        FragArticleTableContract.FragArticleEntry.CONTENT_URI,
                        projectionColumns,
                        null,
                        null,
                        null);
                Log.d(TAG, "in  fraginitialize thread 2 ");


                //check to see whether it has the initial fake data already or not
                if (null == cursor || cursor.getCount() == 0) {
                    //do the fake data initial
                    Log.d(TAG, "in  fraginitialize thread 3 ");


                    //init category table with fake data
                    ContentValues[] contentValues = new ContentValues[8];
                    contentValues[0] = new ContentValues();
                    contentValues[0].put(CategoryTableContract.CategoryEntry.COLUMN_CATEGORYTABLE_ID, 1);
                    contentValues[0].put(CategoryTableContract.CategoryEntry.COLUMN_CHI_NAME, "總新聞");
                    contentValues[0].put(CategoryTableContract.CategoryEntry.COLUMN_ENG_NAME, "allnews");
                    contentValues[1] = new ContentValues();
                    contentValues[1].put(CategoryTableContract.CategoryEntry.COLUMN_CATEGORYTABLE_ID, 2);
                    contentValues[1].put(CategoryTableContract.CategoryEntry.COLUMN_CHI_NAME, "要聞港聞");
                    contentValues[1].put(CategoryTableContract.CategoryEntry.COLUMN_ENG_NAME, "dailynews");
                    contentValues[2] = new ContentValues();
                    contentValues[2].put(CategoryTableContract.CategoryEntry.COLUMN_CATEGORYTABLE_ID, 3);
                    contentValues[2].put(CategoryTableContract.CategoryEntry.COLUMN_CHI_NAME, "即時新聞");
                    contentValues[2].put(CategoryTableContract.CategoryEntry.COLUMN_ENG_NAME, "latestnews");
                    contentValues[3] = new ContentValues();
                    contentValues[3].put(CategoryTableContract.CategoryEntry.COLUMN_CATEGORYTABLE_ID, 4);
                    contentValues[3].put(CategoryTableContract.CategoryEntry.COLUMN_CHI_NAME, "立法會");
                    contentValues[3].put(CategoryTableContract.CategoryEntry.COLUMN_ENG_NAME, "legistration");
                    contentValues[4] = new ContentValues();
                    contentValues[4].put(CategoryTableContract.CategoryEntry.COLUMN_CATEGORYTABLE_ID, 5);
                    contentValues[4].put(CategoryTableContract.CategoryEntry.COLUMN_CHI_NAME, "即時財經");
                    contentValues[4].put(CategoryTableContract.CategoryEntry.COLUMN_ENG_NAME, "latestfinnews");
                    contentValues[5] = new ContentValues();
                    contentValues[5].put(CategoryTableContract.CategoryEntry.COLUMN_CATEGORYTABLE_ID, 6);
                    contentValues[5].put(CategoryTableContract.CategoryEntry.COLUMN_CHI_NAME, "財經");
                    contentValues[5].put(CategoryTableContract.CategoryEntry.COLUMN_ENG_NAME, "financialnews");
                    contentValues[6] = new ContentValues();
                    contentValues[6].put(CategoryTableContract.CategoryEntry.COLUMN_CATEGORYTABLE_ID, 7);
                    contentValues[6].put(CategoryTableContract.CategoryEntry.COLUMN_CHI_NAME, "體育");
                    contentValues[6].put(CategoryTableContract.CategoryEntry.COLUMN_ENG_NAME, "sportsnews");
                    contentValues[7] = new ContentValues();
                    contentValues[7].put(CategoryTableContract.CategoryEntry.COLUMN_CATEGORYTABLE_ID, 8);
                    contentValues[7].put(CategoryTableContract.CategoryEntry.COLUMN_CHI_NAME, "娛樂");
                    contentValues[7].put(CategoryTableContract.CategoryEntry.COLUMN_ENG_NAME, "entertainmentnews");

                    Log.d(TAG, " fraginitialize 4 ");
                    ContentResolver gongContentResolver = context.getContentResolver();
                    Log.d(TAG, " fraginitialize  5 ");

                    int result = 0;
                    result = gongContentResolver.delete(
                            CategoryTableContract.CategoryEntry.CONTENT_URI,
                            null,
                            null);
                    Log.d(TAG, " fraginitialize 6  done delete result=" + result);
                    result = gongContentResolver.bulkInsert(
                            CategoryTableContract.CategoryEntry.CONTENT_URI,
                            contentValues);
                    Log.d(TAG, " fraginitialize 7  DONE INSERT result =" + result);


                    //initialize the domaintable with fake data
                    contentValues = new ContentValues[22];
                    for (int x = 0; x < 22; x++) {
                        contentValues[x] = new ContentValues();
                        contentValues[x].put(DomainTableContract.DomainEntry.COLUMN_DOMAINTABLE_ID, x + 1);
                        contentValues[x].put(DomainTableContract.DomainEntry.COLUMN_BASEURL, FragArticleTableContract.FAKE_FINALURL);
                        contentValues[x].put(DomainTableContract.DomainEntry.COLUMN_CHI_NAME, "媒體" + String.valueOf(x + 1));
                        contentValues[x].put(DomainTableContract.DomainEntry.COLUMN_ENG_NAME, "Me" + String.valueOf(x + 1));
                    }
                    result = gongContentResolver.delete(
                            DomainTableContract.DomainEntry.CONTENT_URI,
                            null,
                            null);
                    Log.d(TAG, " fraginitialize 8  done delete result=" + result);
                    result = gongContentResolver.bulkInsert(
                            DomainTableContract.DomainEntry.CONTENT_URI,
                            contentValues);
                    Log.d(TAG, " fraginitialize 9  DONE INSERT result =" + result);


                    //initialize the firstsubdomaintable with fake data
                    contentValues = new ContentValues[33];
                    contentValues[0] = new ContentValues();
                    contentValues[0].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 1);
                    contentValues[0].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 1);
                    contentValues[0].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 3);
                    contentValues[0].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh3.googleusercontent.com/nYhPnY2I-e9rpqnid9u9aAODz4C04OycEGxqHG5vxFnA35OGmLMrrUmhM9eaHKJ7liB-=w300-rw");
                    contentValues[1] = new ContentValues();
                    contentValues[1].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 2);
                    contentValues[1].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 2);
                    contentValues[1].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 2);
                    contentValues[1].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh3.googleusercontent.com/UFJL7ni5i_F8V9Em0yymU4_x8uWhpKqDiA13Zo3ybgPJa48ujJjNfHLbvKr-3_MXzjLa=w300-rw");
                    contentValues[2] = new ContentValues();
                    contentValues[2].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 3);
                    contentValues[2].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 4);
                    contentValues[2].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 3);
                    contentValues[2].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh5.ggpht.com/tq3WqEUxtRyBn-d_0t3j6WKNHuJDrmLq-FE3GAYrsAMQFIaS7FIgRLfzzql2SvfvLqto=w300-rw");
                    contentValues[3] = new ContentValues();
                    contentValues[3].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 4);
                    contentValues[3].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 5);
                    contentValues[3].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 1);
                    contentValues[3].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh3.googleusercontent.com/N-AY2XwXafWq4TQWfua6VyjPVQvTGRdz9CKOHaBl2nu2GVg7zxS886X5giZ9yY2qIjPh=w300-rw");
                    contentValues[4] = new ContentValues();
                    contentValues[4].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 5);
                    contentValues[4].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 5);
                    contentValues[4].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 1);
                    contentValues[4].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh3.googleusercontent.com/ZrNeuKthBirZN7rrXPN1JmUbaG8ICy3kZSHt-WgSnREsJzo2txzCzjIoChlevMIQEA=w300-rw");
                    contentValues[5] = new ContentValues();
                    contentValues[5].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 6);
                    contentValues[5].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 6);
                    contentValues[5].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 2);
                    contentValues[5].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh6.ggpht.com/k7Z4J1IIXXJnC2NRnFfJNlkn7kZge4Zx-Yv5uqYf4222tx74wXDzW24OvOxlcpw0KcQ=w300-rw");
                    contentValues[6] = new ContentValues();
                    contentValues[6].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 7);
                    contentValues[6].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 6);
                    contentValues[6].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 3);
                    contentValues[6].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh3.googleusercontent.com/8gaEOU2p30N4Up-KMUl4MQBtnn0F5DyH5bqKKr0QqptnQgPk4lxXaWLJhi8Dcu9i8qE=w300-rw");
                    contentValues[7] = new ContentValues();
                    contentValues[7].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 8);
                    contentValues[7].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 8);
                    contentValues[7].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 2);
                    contentValues[7].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh5.ggpht.com/DY9VpGNRSHoICKJVpIy9dMjgm7TOb3lTveLtsv4got8aqbLGpSS4w5xXiR7VBe-mr_M=w300-rw");
                    contentValues[8] = new ContentValues();
                    contentValues[8].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 9);
                    contentValues[8].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 8);
                    contentValues[8].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 3);
                    contentValues[8].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh4.ggpht.com/-wROmWQVYTcjs3G6H0lYkBK2nPGYsY75Ik2IXTmOO2Oo0SMgbDtnF0eqz-BRR1hRQg=w300-rw");
                    contentValues[9] = new ContentValues();
                    contentValues[9].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 10);
                    contentValues[9].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 9);
                    contentValues[9].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 3);
                    contentValues[9].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh3.ggpht.com/oGR9I1X9No3SfFEXrq655tETtVVzI3jIphhmEVPGPEVuM5gfwh8lOGWHQFf6gjSTvw=w300-rw");
                    contentValues[10] = new ContentValues();
                    contentValues[10].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 11);
                    contentValues[10].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 10);
                    contentValues[10].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 3);
                    contentValues[10].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh3.googleusercontent.com/X64En0aW6jkvDnd5kr16u-YuUsoJ1W2cBzJab3CQ5lObLeQ3T61DpB7AwIoZ7uqgCn4=w300-rw");
                    contentValues[11] = new ContentValues();
                    contentValues[11].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 12);
                    contentValues[11].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 10);
                    contentValues[11].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 4);
                    contentValues[11].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh3.googleusercontent.com/gdBHEk-u3YRDtuCU3iDTQ52nZd1t4GPmldYaT26Jh6EhXgp1mlhQiuLFl4eXDAXzDig5=w300-rw");
                    contentValues[12] = new ContentValues();
                    contentValues[12].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 13);
                    contentValues[12].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 11);
                    contentValues[12].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 3);
                    contentValues[12].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh3.googleusercontent.com/AEUm_nsufnYaRVkZ4opiyGVWqbcbo7PF1fWxQvxGSaA5G4kN5s80oxf16pwkP6aOHV8=w300-rw");
                    contentValues[13] = new ContentValues();
                    contentValues[13].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 14);
                    contentValues[13].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 12);
                    contentValues[13].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 3);
                    contentValues[13].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh3.ggpht.com/yrkQ_bDsgS0eFnJRsqxzPDdRNNHE9-dK1eZYNoN3ryDl9V9foQwZgZaTZwiYUfv2jnGI=w300-rw");
                    contentValues[14] = new ContentValues();
                    contentValues[14].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 15);
                    contentValues[14].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 13);
                    contentValues[14].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 3);
                    contentValues[14].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh3.googleusercontent.com/Ned_Tu_ge6GgJZ_lIO_5mieIEmjDpq9kfgD05wapmvzcInvT4qQMxhxq_hEazf8ZsqA=w300-rw");
                    contentValues[15] = new ContentValues();
                    contentValues[15].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 16);
                    contentValues[15].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 14);
                    contentValues[15].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 3);
                    contentValues[15].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh3.googleusercontent.com/EIX5pscg5l1Quvsz17LKk6W5oCSIxOfukBAUmbGR4Uvs1uKDYvbNr0XYE7hqpI3KHQ=w300-rw");
                    contentValues[16] = new ContentValues();
                    contentValues[16].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 17);
                    contentValues[16].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 4);
                    contentValues[16].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 2);
                    contentValues[16].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh3.googleusercontent.com/EIX5pscg5l1Quvsz17LKk6W5oCSIxOfukBAUmbGR4Uvs1uKDYvbNr0XYE7hqpI3KHQ=w300-rw");
                    contentValues[17] = new ContentValues();
                    contentValues[17].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 18);
                    contentValues[17].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 5);
                    contentValues[17].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 3);
                    contentValues[17].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh3.googleusercontent.com/76AS8vaLwxcJMD5Yd8xW5Dy8AW8lloWa2AkP2cgUdCY6rlzeqcjIvrcxOvq3nNwxmofL=w300-rw");
                    contentValues[18] = new ContentValues();
                    contentValues[18].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 19);
                    contentValues[18].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 16);
                    contentValues[18].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 2);
                    contentValues[18].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh3.googleusercontent.com/EamensCSpActl8Y0uOxXOUSVHUBJOc4N3Lsp0OU0oMNBa9GU2JVmp1HWU9fyTWvMQQ=w300-rw");
                    contentValues[19] = new ContentValues();
                    contentValues[19].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 20);
                    contentValues[19].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 17);
                    contentValues[19].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 5);
                    contentValues[19].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh6.ggpht.com/8-N_qLXgV-eNDQINqTR-Pzu5Y8DuH0Xjz53zoWq_IcBNpcxDL_gK4uS_MvXH00yN6nd4=w300-rw");
                    contentValues[20] = new ContentValues();
                    contentValues[20].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 21);
                    contentValues[20].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 18);
                    contentValues[20].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 6);
                    contentValues[20].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh3.ggpht.com/9rwhkrvgiLhXVBeKtScn1jlenYk-4k3Wyqt1PsbUr9jhGew0Gt1w9xbwO4oePPd5yOM=w300-rw");
                    contentValues[21] = new ContentValues();
                    contentValues[21].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 22);
                    contentValues[21].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 19);
                    contentValues[21].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 5);
                    contentValues[21].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh3.ggpht.com/e3oZddUHSC6EcnxC80rl_6HbY94sM63dn6KrEXJ-C4GIUN-t1XM0uYA_WUwyhbIHmVMH=w300-rw");
                    contentValues[22] = new ContentValues();
                    contentValues[22].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 23);
                    contentValues[22].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 20);
                    contentValues[22].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 6);
                    contentValues[22].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh3.googleusercontent.com/N0DZfR5dMsTDvbci4TWWfcl8djUWpf-sE8s7efbMP6eUYMeHSXcGerlguqRK0j4S7sY=w300-rw");
                    contentValues[23] = new ContentValues();
                    contentValues[23].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 24);
                    contentValues[23].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 21);
                    contentValues[23].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 5);
                    contentValues[23].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh3.googleusercontent.com/N0DZfR5dMsTDvbci4TWWfcl8djUWpf-sE8s7efbMP6eUYMeHSXcGerlguqRK0j4S7sY=w300-rw");
                    contentValues[24] = new ContentValues();
                    contentValues[24].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 25);
                    contentValues[24].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 22);
                    contentValues[24].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 6);
                    contentValues[24].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh3.googleusercontent.com/DglqS-eYHQYXnj8M8tmzh3JcKDXcidSo3IzgyCZzci8ZTV9Pmuk8vvIFh9XHOztC3Q=w300-rw");
                    contentValues[25] = new ContentValues();
                    contentValues[25].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 26);
                    contentValues[25].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 2);
                    contentValues[25].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 6);
                    contentValues[25].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh3.googleusercontent.com/fnqDFUD0zN_T1rR-4fyiCGsn6-MVE3azzA6fgMZN5xmsNIvpNQ7NbG0sXNGovftaQhb6=w300-rw");
                    contentValues[26] = new ContentValues();
                    contentValues[26].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 27);
                    contentValues[26].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 2);
                    contentValues[26].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 7);
                    contentValues[26].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh3.googleusercontent.com/MOf9Kxxkj7GvyZlTZOnUzuYv0JAweEhlxJX6gslQvbvlhLK5_bSTK6duxY2xfbBsj43H=w300-rw");
                    contentValues[27] = new ContentValues();
                    contentValues[27].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 28);
                    contentValues[27].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 8);
                    contentValues[27].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 7);
                    contentValues[27].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh3.googleusercontent.com/r0JaW_cBZRoYqdStT3eP6tEI85Gu4ByXFfD1w9AZUcCcZe5aYE8TLIM36alYdiFs7w=w300-rw");
                    contentValues[28] = new ContentValues();
                    contentValues[28].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 29);
                    contentValues[28].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 4);
                    contentValues[28].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 7);
                    contentValues[28].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh3.googleusercontent.com/NiL007f9XI2_cre6oy5QpDNIueTbemZR7TuEMUTPSJDeBG56OrB2XKlj8e3V30h7Mg=w300-rw");
                    contentValues[29] = new ContentValues();
                    contentValues[29].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 30);
                    contentValues[29].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 2);
                    contentValues[29].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 8);
                    contentValues[29].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh3.googleusercontent.com/50-i3khy6z44n6xQsiJKx6WqLWK4zeb6IyXJYW2qZJGBE_2QvWSI5an09m-H7WgMlRqQ=w300-rw");
                    contentValues[30] = new ContentValues();
                    contentValues[30].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 31);
                    contentValues[30].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 8);
                    contentValues[30].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 8);
                    contentValues[30].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh3.googleusercontent.com/IZWJNgJjVHD0ekADSo1Q43LbucxOALHxGk6kogKtrCFJl_oWwzjQFSf7ZJYJoKBDtDY=w300-rw");
                    contentValues[31] = new ContentValues();
                    contentValues[31].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 32);
                    contentValues[31].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 4);
                    contentValues[31].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 8);
                    contentValues[31].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh3.googleusercontent.com/v0dDT-SYr4Zxyq1V6Ke9oQbxqMQEzNyR_ksL14pajGlDUgC9JTqzBuswJQ8gpgmB5lrn=w300-rw");
                    contentValues[32] = new ContentValues();
                    contentValues[32].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID, 33);
                    contentValues[32].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_DOMAINTABLE_ID, 4);
                    contentValues[32].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_CATEGORYTABLE_ID, 6);
                    contentValues[32].put(FirstSubdomainTableContract.FirstSubdomainEntry.COLUMN_SOURCEICONURL,
                            "https://lh3.googleusercontent.com/HPc5gptPzRw3wFhJE1ZCnTqlvEvuVFBAsV9etfouOhdRbkp-zNtYTzKUmUVPERSZ_lAL=w300-rw");
                    result = gongContentResolver.delete(
                            FirstSubdomainTableContract.FirstSubdomainEntry.CONTENT_URI,
                            null,
                            null);
                    Log.d(TAG, " fraginitialize 10  done delete result=" + result);
                    result = gongContentResolver.bulkInsert(
                            FirstSubdomainTableContract.FirstSubdomainEntry.CONTENT_URI,
                            contentValues);
                    Log.d(TAG, " fraginitialize 11  DONE INSERT result =" + result);


                    //initial fake article info
                    List<ContentValues> results = new ArrayList<ContentValues>();
                    for (Map.Entry<Integer, List<Integer>> indrelatedata : FragArticleTableContract.CATEGORY_FSD_MAP.entrySet()) {

                        Log.d(TAG, " fraginitialize 11.1=" + indrelatedata.getKey());
                        for (int fsdid : indrelatedata.getValue()) {
                            Log.d(TAG, " fraginitialize 11.2=" + fsdid);

                            //insert 20 fake data for each firstsubdomain(source) entry
                            for (int index = 0; index < 20; index++) {
                                ContentValues contentValue = new ContentValues();
                                fillupContentValuesandResults (contentValue, fsdid,
                                        results,
                                        FragInfo.getInstance().get_EngNameFromCategory(indrelatedata.getKey()),
                                        indrelatedata.getKey());


                                results.add(contentValue);
                            }
                        }
                    }
                    contentValues = new ContentValues[results.size()];
                    contentValues = results.toArray(contentValues);

                    result = context.getContentResolver().bulkInsert(
                            FragArticleTableContract.FragArticleEntry.CONTENT_URI,
                            contentValues);
                    Log.d(TAG, "in fraginitialize thread 12  result="
                            + result
                            + ",resultlist.size()="
                            + results.size());

                } else {
                    //there are some data, do not need to add fake data
                    Log.d(TAG, "in  fraginitialize thread 4 ");
                }
                cursor.close();
            }
        });

        //start the checking with a thread
        checkForEmpty.start();
        Log.d(TAG, "in  fraginitialize 4");
    }


    /**
     * this method is supposed to get one article information.
     * For now, it will do nothing.
     *
     * @param context   : the context for accessing database and fetch article information
     * @param sheet_id  : the id of the sheet to get information
     * @param rowid     : row number of the article
     * @param articleid : id of the article
     */
    synchronized public static void gsheetfetchOneEntry(@NonNull final Context context,
                                                        final String sheet_id,
                                                        final long rowid,
                                                        final long articleid) {
        Log.d(TAG, "in gsheetfetch initialize 1");

        new Thread(() -> {
            //suppose to get a row, however, print statement for fake
            Log.d(TAG, "in gsheetfetch thread 1 sheet_id=" + sheet_id + ",rowid=" + rowid);
        }).start();
        Log.d(TAG, "in gsheetfetch  4");
    }


    /**
     * this method will insert a block of fake data to the database
     *
     * @param uri        : uri for inserting fake data
     * @param context    Context to use for communicate with content provider
     * @param name       : category name for the block of data to insert
     * @param categoryid : category id for the block of data to insert
     * @return indicate whether it is sucess or not
     */
    public static boolean retrieveSheetAndNameCat(Uri uri, Context context, String name, int categoryid) {

        new Thread(() -> {
            Log.d(TAG, "in retrieveSheetAndNameCat thread 1 uri="
                    + uri
                    + ", name=" + name
                    + ", categoryid=" + categoryid);

            //store the list of first subdomain ids that related to the categoryid
            List<FilterFragmentAdapter.FSDCatgeoryInfo> listFSD = SourceInfo.getInstance().getFSDListWithCategory(categoryid);

            //store the generated results before inserting to database
            List<ContentValues> results = new ArrayList<ContentValues>();

            for (FilterFragmentAdapter.FSDCatgeoryInfo entry : listFSD) {

                //insert 20 fake data for each firstsubdomain(source) entry
                for (int index = 0; index < 20; index++) {
                    ContentValues contentValue = new ContentValues();
                    fillupContentValuesandResults (contentValue, entry.getFirstSubDomainID(),
                            results,
                            name,
                            categoryid);

                    results.add(contentValue);


                }

            }

            //converting the list of fake data to contentvalues array and insert it to database
            ContentValues[] contentValues = new ContentValues[results.size()];
            contentValues = results.toArray(contentValues);

            int result = context.getContentResolver().bulkInsert(
                    uri,
                    contentValues);
            Log.d(TAG, "in gsheetfetchOneBlock thread 3 result="
                    + result
                    + ",resultlist.size()="
                    + results.size());


        }).start();

        Log.d(TAG, "in gsheetfetchOneBlock  4");

        return true;

    }

    /**
     * this function check whether the device is online or not
     *
     * @param context Context to use for checking network connectivity
     * @return true if is connect to network or else
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


    /**
     * fill up the content values and update the results
     * @param contentValue : is used to store the fake data for inserting
     * @param fsdid  : first subdomain id of this fake article
     * @param results  : an array of ContentValues
     * @param name : name of the category
     * @param categoryid : category id
     */
    synchronized public static void fillupContentValuesandResults (ContentValues contentValue,
                                                int fsdid,
                                                List<ContentValues> results,
                                                String name,
                                                int categoryid){
        int articleid = FragArticleTableContract.fake_getarticleid();
        contentValue.put(FragArticleTableContract.FragArticleEntry.COLUMN_ARTICLEID,
                articleid);
        contentValue.put(FragArticleTableContract.FragArticleEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID,
                fsdid);
        contentValue.put(FragArticleTableContract.FragArticleEntry.COLUMN_FINALURL,
                FragArticleTableContract.FAKE_FINALURL);
        contentValue.put(FragArticleTableContract.FragArticleEntry.COLUMN_IMAGEURL,
                FragArticleTableContract.FAKE_IMAGEURL);
        int fake_simCount = FragArticleTableContract.fake_getsimCount();
        contentValue.put(FragArticleTableContract.FragArticleEntry.COLUMN_SIMILARITIESCOUNT,
                fake_simCount);
        long timestampondoc = FragArticleTableContract.fake_gettimestamp();
        contentValue.put(FragArticleTableContract.FragArticleEntry.COLUMN_TIMESTAMPONDOC,
                timestampondoc);
        contentValue.put(FragArticleTableContract.FragArticleEntry.COLUMN_TITLE,
                FragArticleTableContract.fake_gettitle());

        //create fake json entry
        JSONObject resultJSONObject = new JSONObject();
        if (fake_simCount > 0) {
            for (int count = 0; count < fake_simCount; count++) {
                ContentValues contentJSONValue = new ContentValues();
                try {
                    JSONObject indjsonObject = new JSONObject();
                    int indarticleid = FragArticleTableContract.fake_getarticleid();
                    long indtimestampondoc = FragArticleTableContract.fake_gettimestamp();
                    indjsonObject.put(FragArticleTableContract.FragArticleEntry.COLUMN_ARTICLEID,
                            indarticleid);
                    contentJSONValue.put(FragArticleTableContract.FragArticleEntry.COLUMN_ARTICLEID,
                            indarticleid);

                    int jfsdid = FragArticleTableContract.fake_getfirstsubdomainid();
                    indjsonObject.put(FragArticleTableContract.FragArticleEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID,
                            jfsdid);
                    contentJSONValue.put(FragArticleTableContract.FragArticleEntry.COLUMN_FIRSTSUBDOMAINTABLE_ID,
                            jfsdid);

                    String jtitle = FragArticleTableContract.fake_gettitle();
                    indjsonObject.put(FragArticleTableContract.FragArticleEntry.COLUMN_TITLE,
                            jtitle);
                    contentJSONValue.put(FragArticleTableContract.FragArticleEntry.COLUMN_TITLE,
                            jtitle);

                    indjsonObject.put(FragArticleTableContract.FragArticleEntry.COLUMN_FINALURL,
                            FragArticleTableContract.FAKE_FINALURL);
                    contentJSONValue.put(FragArticleTableContract.FragArticleEntry.COLUMN_FINALURL,
                            FragArticleTableContract.FAKE_FINALURL);

                    indjsonObject.put(FragArticleTableContract.FragArticleEntry.COLUMN_IMAGEURL,
                            FragArticleTableContract.FAKE_IMAGEURL);
                    contentJSONValue.put(FragArticleTableContract.FragArticleEntry.COLUMN_IMAGEURL,
                            FragArticleTableContract.FAKE_IMAGEURL);

                    indjsonObject.put(FragArticleTableContract.FragArticleEntry.COLUMN_TIMESTAMPONDOC,
                            indtimestampondoc);
                    contentJSONValue.put(FragArticleTableContract.FragArticleEntry.COLUMN_TIMESTAMPONDOC,
                            indtimestampondoc);

                    String fake_timestampondocandid = (("0000000000" +
                            String.valueOf(indtimestampondoc)).substring(String.valueOf(indtimestampondoc).length())
                            + "Z"
                            + ("0000000000" + String.valueOf(indarticleid)).substring(String.valueOf(indarticleid).length()));
                    contentJSONValue.put(FragArticleTableContract.FragArticleEntry.COLUMN_TIMESTAMPONDOC_AND_ID,
                            fake_timestampondocandid);

                    contentJSONValue.put(FragArticleTableContract.FragArticleEntry.COLUMN_ENTRY,
                            "EMPTYSTRINGVALUE");
                    contentJSONValue.put(FragArticleTableContract.FragArticleEntry.COLUMN_SIMILARITIESCOUNT,
                            0);
                    contentJSONValue.put(FragArticleTableContract.FragArticleEntry.COLUMN_NAME,
                            name);
                    contentJSONValue.put(FragArticleTableContract.FragArticleEntry.COLUMN_CATEGORYTABLE_ID,
                            categoryid);
                    for (Map.Entry<String, String> mapentry : FragArticleTableContract.fake_getEntityInfo().entrySet()) {
                        contentJSONValue.put(FragArticleTableContract.FragArticleEntry.COLUMN_ENTITY_NAME,
                                mapentry.getKey());
                        contentJSONValue.put(FragArticleTableContract.FragArticleEntry.COLUMN_ENTITY_ICONURL,
                                mapentry.getValue());
                    }
                    results.add(contentJSONValue);



                    resultJSONObject.put(fake_timestampondocandid, indjsonObject);

                } catch (Exception e) {
                    Log.d(TAG, "creating moredetailist ");
                }
            }
        }
        if (fake_simCount > 0) {
            Log.d(TAG, "resultJSONObject = " + resultJSONObject.toString());
        }
        String tmp_string = resultJSONObject.toString();
        if (fake_simCount > 0) {
            contentValue.put(FragArticleTableContract.FragArticleEntry.COLUMN_ENTRY,
                    tmp_string);

        } else {
            contentValue.put(FragArticleTableContract.FragArticleEntry.COLUMN_ENTRY,
                    "EMPTYSTRINGVALUE");
        }

        contentValue.put(FragArticleTableContract.FragArticleEntry.COLUMN_TIMESTAMPONDOC_AND_ID,
                ("0000000000" + String.valueOf(timestampondoc)).substring(String.valueOf(timestampondoc).length())
                        + "Z"
                        + ("0000000000" + String.valueOf(articleid)).substring(String.valueOf(articleid).length()));
        contentValue.put(FragArticleTableContract.FragArticleEntry.COLUMN_NAME,
                name);
        contentValue.put(FragArticleTableContract.FragArticleEntry.COLUMN_CATEGORYTABLE_ID,
                categoryid);
        for (Map.Entry<String, String> mapentry : FragArticleTableContract.fake_getEntityInfo().entrySet()) {
            contentValue.put(FragArticleTableContract.FragArticleEntry.COLUMN_ENTITY_NAME,
                    mapentry.getKey());
            contentValue.put(FragArticleTableContract.FragArticleEntry.COLUMN_ENTITY_ICONURL,
                    mapentry.getValue());
        }


    }

}
