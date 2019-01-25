package cc.zsakvo.yuedusync.task;

import android.os.AsyncTask;
import android.os.Environment;
import com.thegrizzlylabs.sardineandroid.Sardine;
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine;

import org.zeroturnaround.zip.ZipUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class UploadBackup extends AsyncTask<String,Void,Boolean> {

    private cc.zsakvo.yuedusync.listener.UploadBackup uploadBackup;

    public UploadBackup(cc.zsakvo.yuedusync.listener.UploadBackup uploadBackup){
        this.uploadBackup = uploadBackup;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        String backupPath = strings[0];
        String backupZipPath = strings[1];
        String account = strings[2];
        String password = strings[3];
        ZipUtil.pack(new File(backupPath),new File(backupZipPath));
        Sardine sardine = new OkHttpSardine();
        sardine.setCredentials(account, password);
        try {
            sardine.createDirectory("https://dav.jianguoyun.com/dav/YueDu");
            sardine.put("https://dav.jianguoyun.com/dav/YueDu/YueDuBackup.zip",new File(backupZipPath),"application/x-www-form-urlencoded");
            //noinspection ResultOfMethodCallIgnored
//            new File(backupZipPath).delete();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean b) {
        super.onPostExecute(b);
        uploadBackup.upload(b);
    }

}
