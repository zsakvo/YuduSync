package cc.zsakvo.yuedusync;

import androidx.appcompat.app.AppCompatActivity;
import cc.zsakvo.yuedusync.listener.ChangePath;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;


import java.io.File;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Objects.requireNonNull(getSupportActionBar()).setTitle("设置");
        if (savedInstanceState == null) {
            SettingsFragment fragment = new SettingsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_settings, fragment).commit();
        }
    }

    public void fileChoose(final String str, final ChangePath cpath) {
        SimpleFileChooser sfcDialog = new SimpleFileChooser();
        sfcDialog.setOnChosenListener(new SimpleFileChooser.SimpleFileChooserListener() {
            @Override
            public void onFileChosen(File file) {
                Toast.makeText(SettingsActivity.this," 请选择一个目录 ",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDirectoryChosen(File directory) {
                SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(str, directory.getAbsolutePath());
                cpath.changePath(str, directory.getAbsolutePath());
                editor.apply();
                editor.commit();
            }

            @Override
            public void onCancel() {
                Toast.makeText(SettingsActivity.this,"未选择任何目录",Toast.LENGTH_LONG).show();
            }
        });
        sfcDialog.show(getFragmentManager(), "SimpleFileChooserDialog");
    }
}
