package cc.zsakvo.yuedusync;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import cc.zsakvo.yuedusync.listener.ChangePath;
import moe.shizuku.preference.Preference;
import moe.shizuku.preference.PreferenceFragment;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener,ChangePath {

    Preference cache_path;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setDefaultPackages(new String[]{BuildConfig.APPLICATION_ID + "."});
        getPreferenceManager().setSharedPreferencesName("settings");
        getPreferenceManager().setSharedPreferencesMode(MODE_PRIVATE);
        setPreferencesFromResource(R.xml.app_stettings, null);

        Preference ab_id = findPreference("ab_id");
        ab_id.setOnPreferenceClickListener(this);

        Preference ab_code = findPreference("ab_code");
        ab_code.setOnPreferenceClickListener(this);

        cache_path = findPreference("cache_path");
        cache_path.setOnPreferenceClickListener(this);



    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()){
            case "cache_path":
                SettingsActivity sa = (SettingsActivity)getActivity();
                assert sa != null;
                sa.fileChoose("cache_path",this);
                break;
            case "ab_id":
                Intent intent_id = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.coolapk.com/u/522069"));
                startActivity(intent_id);
                break;
            case "ab_code":
                Intent intent_code = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/zsakvo/YuduSync"));
                startActivity(intent_code);
                break;
                default:
                    break;
        }
        return false;
    }

    @Override
    public void changePath(String str, String path) {
        switch (str) {
            case "cache_path":
                cache_path.setSummary(path);
                break;
        }
    }
}
