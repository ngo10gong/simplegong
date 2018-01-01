package com.hk.simplenewsgong.simplegong.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.hk.simplenewsgong.simplegong.BookmarkListActivity;
import com.hk.simplenewsgong.simplegong.SimpleSimpleGongActivity;
import com.hk.simplenewsgong.simplegong.MainNewsActivity;
import com.hk.simplenewsgong.simplegong.R;

/**
 * this class do the switch between different features view from the bottom navigation view
 * <p>
 * Created by simplegong
 */
public class BottomNavigationViewHelper {

    private static final String TAG = BottomNavigationViewHelper.class.getSimpleName();

    /**
     * a method to start the choosen feature
     *
     * @param context : context to used for starting activity
     * @param view    : the bottom navigation view that contains different category
     */
    public static void selection(final Context context, BottomNavigationView view, int currentNavid) {

        //setting the listener for navigation when user click on it
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                Log.d(TAG, " onNavigationItemSelected");
                switch (item.getItemId()) {

                    case R.id.nav_simplegong: //starting simplegong feature activity
                        if (currentNavid == R.id.nav_simplegong){
                            Log.d(TAG, " onNavigationItemSelected 1");
                            return false;
                        }
                        intent = new Intent(context, SimpleSimpleGongActivity.class);//ACTIVITY_NUM = 1
                        Log.d(TAG, " onNavigationItemSelected 2");
                        break;

                    case R.id.nav_bookmark: //starting bookmark feature activity
                        if (currentNavid == R.id.nav_bookmark){
                            Log.d(TAG, " onNavigationItemSelected 3");
                            return false;
                        }
                        intent = new Intent(context, BookmarkListActivity.class);//ACTIVITY_NUM = 2
                        Log.d(TAG, " onNavigationItemSelected 4");
                        break;
                    case R.id.nav_mainnews: //starting mainnews feature activity
                    default:
                        if (currentNavid == R.id.nav_mainnews){
                            Log.d(TAG, " onNavigationItemSelected 5");
                            return false;
                        }
                        Log.d(TAG, " onNavigationItemSelected 6");
                        intent = new Intent(context, MainNewsActivity.class);//ACTIVITY_NUM = 0
                        break;


                }

                context.startActivity(intent);
                ((Activity) context).finish();
                return true;
            }
        });
    }
}
