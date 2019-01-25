package cc.zsakvo.yuedusync;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import cc.zsakvo.yuedusync.listener.DownloadBackup;
import cc.zsakvo.yuedusync.listener.LoadConfigs;
import cc.zsakvo.yuedusync.task.UploadBackup;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaosu.lib.permission.OnRequestPermissionsCallBack;
import com.xiaosu.lib.permission.PermissionCompat;


public class MainActivity extends AppCompatActivity implements LoadConfigs, cc.zsakvo.yuedusync.listener.UploadBackup,DownloadBackup {

    MenuItem app_settings;
    TextView textView_load_configs;
    String account;
    String password;
    String backupPath;
    String backupZipPath;
    ProgressDialog waitingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView_load_configs = (TextView)findViewById(R.id.text_load_configs);

        backupZipPath = getExternalCacheDir()+"/YueDuBackup.zip";

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar, menu);
        app_settings = menu.findItem(R.id.app_settings);

        if (account.length()==0||password.length()==0) {
            menu.findItem(R.id.upload_backup).setVisible(false);
            menu.findItem(R.id.download_backup).setVisible(false);
        }else {
            menu.findItem(R.id.upload_backup).setVisible(true);
            menu.findItem(R.id.download_backup).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch(menuItem.getItemId()){
            case R.id.app_settings:
                startActivity(new Intent(MainActivity.this,SettingsActivity.class));
                break;
            case R.id.upload_backup:
                new UploadBackup(this).execute(backupPath,backupZipPath,account,password);
                showWaitingDialog(waitingDialog,"正在备份，请稍后……");
                break;
            case R.id.download_backup:
                new cc.zsakvo.yuedusync.task.DownloadBackup(this).execute(backupPath,backupZipPath,account,password);
                showWaitingDialog(waitingDialog,"正在还原，请稍后……");
                default:
                    break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void loadConfigs(String str) {
        textView_load_configs.setText(str);
        invalidateOptionsMenu();
        waitingDialog = new ProgressDialog(MainActivity.this);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (account.length()==0||password.length()==0) {
            menu.findItem(R.id.upload_backup).setVisible(false);
            menu.findItem(R.id.download_backup).setVisible(false);
        }else {
            menu.findItem(R.id.upload_backup).setVisible(true);
            menu.findItem(R.id.download_backup).setVisible(true);
        }

        if (!textView_load_configs.getText().toString().contains("成功")){
            menu.findItem(R.id.upload_backup).setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onStart() {
        super.onStart();
        account = getSharedPreferences("settings",MODE_PRIVATE).getString("web_dav_account","");
        password = getSharedPreferences("settings",MODE_PRIVATE).getString("web_dav_password","");

        assert password != null;
        if (account.length()==0||password.length()==0) {
            showInitDialog();
        }

        invalidateOptionsMenu();

        PermissionCompat.create(this)
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .explain("需要存储读取权限", "需要存储写入权限")
                .retry(true)
                .callBack(new OnRequestPermissionsCallBack() {
                    @Override
                    public void onGrant() {
                        // todo 权限授权成功回调
                        backupPath = getSharedPreferences("settings",MODE_PRIVATE).getString("cache_path",Environment.getExternalStorageDirectory().getAbsolutePath()+"/YueDu");
                        new cc.zsakvo.yuedusync.task.LoadConfigs(MainActivity.this).execute(backupPath);
                    }

                    @Override
                    public void onDenied(String permission, boolean retry) {
                        // todo 权限授权失败回调
                        Toast.makeText(MainActivity.this,"没有权限，程序无法工作！",Toast.LENGTH_LONG).show();
                        finish();
                    }
                })
                .build()
                .request();
    }

    @Override
    public void upload(boolean b) {
        if (b){
            Toast.makeText(this,"备份成功！",Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this,"备份失败！",Toast.LENGTH_LONG).show();
        }
        waitingDialog.dismiss();
    }

    @Override
    public void download(boolean b) {
        if (b){
            Toast.makeText(this,"还原成功！",Toast.LENGTH_LONG).show();
            new cc.zsakvo.yuedusync.task.LoadConfigs(MainActivity.this).execute(backupPath);
        }else {
            Toast.makeText(this,"还原失败！",Toast.LENGTH_LONG).show();
        }
        waitingDialog.dismiss();
    }

    private void showInitDialog() {
        final AlertDialog.Builder initDialog =
                new AlertDialog.Builder(MainActivity.this);
        initDialog.setTitle("提示");
        initDialog.setMessage("使用之前，你需要先设置坚果云 WebDav 的账号与应用密码。是否设置？");
        initDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(MainActivity.this,SettingsActivity.class));
                    }
                });
        initDialog.setNegativeButton("退出",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        initDialog.show();
    }


    private void showWaitingDialog(ProgressDialog waitingDialog,String str) {
        waitingDialog.setTitle("提示");
        waitingDialog.setMessage(str);
        waitingDialog.setIndeterminate(true);
        waitingDialog.setCancelable(false);
        waitingDialog.show();
    }
}
