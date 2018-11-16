package com.alloypowered.cstation;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.i(TAG, "In onCreate");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent displaySettings = new Intent(this, SettingsActivity.class);
                Log.i(TAG, "Before result");
                startActivityForResult(displaySettings, SettingsActivity.FOR_RESULT);
                Log.i(TAG, "After result");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, "Got request: "+requestCode+" result: "+resultCode);

        //TODO relaunch settings if user didn't set things up & provide toast notification
        //Or provide a dialog and ask the user to setup or exit
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //String user = prefs.getString(getString(R.string.credUserKey), null);
        //Log.i(TAG, "Got user: " + user);
    }
}
