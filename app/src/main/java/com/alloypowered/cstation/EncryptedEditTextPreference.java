package com.alloypowered.cstation;

import android.content.Context;
import androidx.preference.EditTextPreference;
import android.util.AttributeSet;
//import android.util.Log;

//Test class and methods for the moment
public class EncryptedEditTextPreference extends EditTextPreference {
    private static final String TAG = "CustomETP";

    public EncryptedEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public EncryptedEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public EncryptedEditTextPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.editTextPreferenceStyle);
    }

    public EncryptedEditTextPreference(Context context) {
        this(context, null);
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
            return saved;
        }

        return CryptoHelper.decryptText(saved);
    }

    @Override
    public void setText(String text) {
        if (text == null || text.isEmpty()) {
            super.setText(text);
            return;
        }

        super.setText(CryptoHelper.encryptText(text));
    }
}
