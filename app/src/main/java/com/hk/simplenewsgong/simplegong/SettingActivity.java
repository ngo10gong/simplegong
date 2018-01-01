package com.hk.simplenewsgong.simplegong;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;

import java.util.Locale;

/**
 * this activity responsible to display setting of the app
 * <p>
 * Created by simplegong
 */

public class SettingActivity extends AppCompatActivity {

    private final String TAG = SettingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //setup the actionbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.settings);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {

            //when the user exit this activity, it need to refresh the locale setting of the app
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            String choiceOfLang = sp.getString(getString(R.string.gong_lang_pref), getString(R.string.choice_value_lang_eng));
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();

            String lang = "en_US";
            if (choiceOfLang.compareTo(getString(R.string.choice_value_lang_eng)) == 0) {
                //default true zh-rhk
                Log.d(TAG, " 1 choiceoflang=" + choiceOfLang);
                lang = "en_US";
                conf.locale = Locale.ENGLISH;

            } else {
                //english
                lang = "zh_HK";
                Log.d(TAG, " 2 choiceoflang=" + choiceOfLang);
                conf.locale = Locale.CHINESE;
            }
            Locale myLocale = new Locale(lang);

            //configure the locale setting and start mainewsactivity with new locale setting
            res.getConfiguration();
            res.updateConfiguration(conf, dm);
            Intent refresh = new Intent(this, MainNewsActivity.class);
            startActivity(refresh);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
