package com.hk.simplenewsgong.simplegong;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuItemImpl;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.view.menu.MenuView;

import com.hk.simplenewsgong.simplegong.data.SourceInfo;
import com.hk.simplenewsgong.simplegong.sync.Gongdispatch;
import com.hk.simplenewsgong.simplegong.util.BottomNavigationViewHelper;
import com.hk.simplenewsgong.simplegong.util.FragInfo;

import java.util.List;
import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * this is the main activity hosts different categroy fragments
 */
public class MainNewsActivity extends AppCompatActivity implements
        FilterFragment.CallBackMainActivity {
    private final String TAG = MainNewsActivity.class.getSimpleName();

    // reference to viewpager for showing different fragment
    private ViewPager mViewPager;

    // pager adapter for different category fragment
    private NewsFragmentPagerAdapter mPageradapter;

    // reference for drawer layout
    private DrawerLayout mDrawerLayout;

    //reference for filter fragment
    private FilterFragment mFilterFragment;

    // reference for bottom navigation view
    private BottomNavigationView mBottomNavigationView;

    // reference to the coordinator layout of hosting this activity
    private CoordinatorLayout mCoordinatorlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "oncreate 1 ");
        setContentView(R.layout.activity_main_news);

        // get the configuration
        Configuration conf = getResources().getConfiguration();
        //en_US
        Log.d(TAG, " configuration.locale = " + conf.locale);

        //init fraginfo class
        FragInfo.init(this);

        // configure drawerlayout
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //Log.d(TAG, "mDrawerLayout onDrawerSlide 1");
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                Log.d(TAG, "mDrawerLayout onDrawerOpened 1");
                // get the correct category id  and pass it to filter fragment to update the
                // information accordingly
                int currentItemPos = mViewPager.getCurrentItem();
                Log.d(TAG, "mDrawerLayout onDrawerOpened 2 =" + currentItemPos);
                mFilterFragment.drawerOpenedWithCategory(mPageradapter.getCurrentFragmentCategory(currentItemPos));
                Log.d(TAG, "mDrawerLayout onDrawerOpened 3");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                //Log.d(TAG, "mDrawerLayout onDrawerClosed 1");
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                //Log.d(TAG, "mDrawerLayout onDrawerStateChanged 1");
            }
        });


        // Find the view pager that will allow the user to swipe between fragments
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        //init sourceinfo to store the source information
        //this need to executed in the activity before anything else
        Log.d(TAG, "oncreate 1.3");
        SourceInfo.init(getApplicationContext(), getSupportLoaderManager());

        // initialize gongdispatch's fragment
        Gongdispatch.fraginitialize(this);
        Log.d(TAG, "oncreate 1.4");

        //setting up the actionbar
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_tmp_icon);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Create an adapter that knows which fragment should be shown on each page
        mPageradapter = new NewsFragmentPagerAdapter(getSupportFragmentManager());

        // Set the adapter onto the view pager
        mViewPager.setAdapter(mPageradapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);
        Log.d(TAG, "oncreate 2 ");


        //configure the bottom navigation view
        mBottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_mainnews_navigation);
        BottomNavigationViewHelper.selection(this, mBottomNavigationView,R.id.nav_mainnews);

        mCoordinatorlayout = findViewById(R.id.main_news_coordinatorlayout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.top_menu, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_feedback) {

            //send the intent to appropiate app to handle mail
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, "simplegonggongngo.0000001@gmail.com");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Priceless feedback for you about simplegong!!!");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }

            return true;
        }
        if (id == R.id.menu_preferred_list) {
            // open the drawer for user to choose preferred media source
            mDrawerLayout.openDrawer(Gravity.LEFT, true);
            return true;
        }
        if (id == R.id.menu_settings) {
            Log.d(TAG, " onOptionsItemSelected 1 ");
            //start setting activity for user to choose language
            Intent settingIntent = new Intent(this, SettingActivity.class);
            startActivity(settingIntent);
            finish();
            Log.d(TAG, " onOptionsItemSelected 2 ");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        int currentItemPos = mViewPager.getCurrentItem();
        // check how many new items of current category since last retrieve
        int numOfNewItem = mPageradapter.shouldShowMoreItemSnackBar(currentItemPos);

        Log.d(TAG, " onrestart numofnewitem = " + numOfNewItem);
        if (numOfNewItem > 0) {
            // has new items

            //construct display string
            String displayString = getString(R.string.more_news_on_remote) +
                    String.valueOf(numOfNewItem) +
                    getString(R.string.number_of_news);
            Snackbar snack = Snackbar.make(mCoordinatorlayout, displayString, Snackbar.LENGTH_LONG);
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)
                    snack.getView().getLayoutParams();
            DisplayMetrics metrics = getResources().getDisplayMetrics();

            //show the snackbar in appropiate height
            int px = (int) ((int) 56 * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
            Log.d(TAG, " left=" + params.leftMargin
                    + ",top=" + params.topMargin
                    + ",right=" + params.rightMargin
                    + ",height=" + px);
            params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, px);
            snack.getView().setLayoutParams(params);
            snack.setAction(R.string.update_action, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPageradapter.goupdateNow(currentItemPos);
                }
            });
            snack.show();
        }
    }


    /**
     * find which category is showing in current screen
     *
     * @return
     */
    @Override
    public int getCurrentDisplayCategory() {
        int currentItemPos = mViewPager.getCurrentItem();
        Log.d(TAG, "getcurrentdisplaycategoy " + currentItemPos);
        return 0;
    }

    /**
     * store the filter fragment reference
     *
     * @param filterFragment filter fragment reference
     */
    @Override
    public void passInFilterFragment(Fragment filterFragment) {
        Log.d(TAG, "passInFilterfragment 1 ");
        mFilterFragment = (FilterFragment) filterFragment;
    }

    /**
     * filter fragment has called this function to invoke refreshing on  current fragment (category)
     * with a list of first subdomain id
     *
     * @param preferredList a list of preferred first subdomain id
     */
    @Override
    public void setCurrentPreferredSourceList(List<Integer> preferredList) {
        int currentItemPos = mViewPager.getCurrentItem();
        int[] passIntArray = new int[preferredList.size()];
        int index = 0;
        for (int x : preferredList) {
            passIntArray[index++] = x;
        }
        for (int y = 0; y < passIntArray.length; y++) {
            Log.d(TAG, "setCurrentPreferredSourceList 1=" + passIntArray[y]);
        }
        //ask pager to refresh the article list on fragment
        mPageradapter.passInPreferredFSD(currentItemPos, passIntArray);

    }

    /**
     * filter fragment has received clear command from user, then invoke appropiate fragment
     * to refresh
     */
    @Override
    public void clearCurrentPreferredSourceList() {
        int currentItemPos = mViewPager.getCurrentItem();
        Log.d(TAG, " clearCurrentPreferredSourceList 1 currentitempos=" + currentItemPos);
        mPageradapter.clearInPreferredFSD(currentItemPos);
        Log.d(TAG, " clearCurrentPreferredSourceList 2 ");
    }


}
