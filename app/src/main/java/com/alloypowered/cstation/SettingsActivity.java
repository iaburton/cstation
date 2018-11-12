package com.alloypowered.cstation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

public class SettingsActivity extends AppCompatActivity {

    public final static int FOR_RESULT = 1;
    public final static int NO_CHANGE = 2;
    public final static int CHANGE = 3;

    private static final String TAG = "SettingsAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Log.i(TAG, "In onCreate");
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "Before superBackPressed");
        //try { Thread.sleep(2000); }
        //catch (Exception e) { Log.w(TAG, "Sleep", e); }
        super.onBackPressed();
        Log.i(TAG, "After superBackPressed");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
