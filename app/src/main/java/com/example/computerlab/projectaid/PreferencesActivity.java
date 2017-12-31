package com.example.computerlab.projectaid;

import android.app.Fragment;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

/**
 * Created by computerlab on 4/5/17.
 */

public class PreferencesActivity extends PreferenceActivity {
    //new resource directory -> xml -> new xml resource file -> preferences.xml


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(
                android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment {

    }
}
