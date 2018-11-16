package com.alloypowered.cstation;

import android.content.Context;
import androidx.preference.EditTextPreference;
import android.util.AttributeSet;
import android.util.Log;

//Test class and methods for the moment
public class EncryptedEditTextPreference extends EditTextPreference {

    private static final String TAG = "CustomETP";

    public EncryptedEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public EncryptedEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public EncryptedEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EncryptedEditTextPreference(Context context) {
        super(context);
    }

//    @Override
//    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
//        Log.i(TAG, "in onSetInitialValue: " + restoreValue + " : " + defaultValue);
//        super.setText(restoreValue ? getPersistedString(null) : (String) defaultValue);
//    }

    @Override
    protected void onSetInitialValue(Object defaultValue) {
        super.setText(getPersistedString(null));
    }

    @Override
    public String getText() {
        String saved = super.getText();
        if (saved == null || saved.isEmpty()) {
            Log.i(TAG, "in getText: null str");
            return saved;
        }

        String toReturn = new StringBuilder(saved).reverse().toString();
        Log.i(TAG, "in getText: saved (" + saved + ") returning (" + toReturn +")");
        return toReturn;
    }

    @Override
    public void setText(String text) {
        if (text == null || text.isEmpty()) {
            Log.i(TAG, "in setText: null str");
            super.setText(text);
            return;
        }

        String toSave = new StringBuilder(text).reverse().toString();
        Log.i(TAG, "in setText: set (" + text + ") saving (" + toSave +")");
        super.setText(toSave);
    }
}
