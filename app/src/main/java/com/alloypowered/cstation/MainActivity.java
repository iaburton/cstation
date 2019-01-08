package com.alloypowered.cstation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import cstation.Cstation;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.i(TAG, "In onCreate, hardwareBacked: " + CryptoHelper.SecureHardware);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String url = prefs.getString(getString(R.string.servAddressKey), null);
        String usr = prefs.getString(getString(R.string.credUserKey), null);
        String pass = prefs.getString(getString(R.string.credPassKey), null);

        if (url == null || usr == null || pass == null) {
            Toast.makeText(this, "Please ensure all settings are correct", Toast.LENGTH_LONG).show();
            startSettingsActivity();
            return;
        }

        Log.i(TAG, "After perfs");
        try {
            Cstation.initLibrary(getFilesDir().getAbsolutePath(), url, prefs.getBoolean(getString(R.string.servCertSwitchKey), false));
        } catch (Exception e) {
            Log.e(TAG, "InitLibrary failed", e);
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            return;
        }
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
                startSettingsActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startSettingsActivity() {
        Intent displaySettings = new Intent(this, SettingsActivity.class);
        Log.i(TAG, "Before result");
        startActivityForResult(displaySettings, SettingsActivity.FOR_RESULT);
        Log.i(TAG, "After result");
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

    public void login(View view) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String usr = prefs.getString(getString(R.string.credUserKey), null);
        String pass = prefs.getString(getString(R.string.credPassKey), null);
        try {
            Cstation.login(CryptoHelper.decryptText(usr), CryptoHelper.decryptText(pass));
            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Login failed", e);
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void logout(View view) {
        try {
            Cstation.logout();
            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Logout failed", e);
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
