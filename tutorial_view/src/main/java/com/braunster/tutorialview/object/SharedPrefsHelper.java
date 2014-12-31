package com.braunster.tutorialview.object;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by braunster on 30/12/14.
 *
 * Simple helper class for handling checks if the user has already seen the tutorial.
 *
 */
public abstract class SharedPrefsHelper {

    protected Context mContext;

    protected boolean isFirstTime(String identifier, String key){
        if (validate())
        {
            SharedPreferences preferences = getSharedPrefsForIdentifier(identifier);

            boolean saw = preferences.contains(key);

            if (!saw)
            {
                preferences.edit().putBoolean(key, false).apply();
            }

            return !saw;
        }
        else
            return false;
    }

    protected SharedPreferences getSharedPrefsForIdentifier(String identifier){
        return mContext.getSharedPreferences(identifier, Context.MODE_PRIVATE);
    }

    public abstract boolean validate();
}
