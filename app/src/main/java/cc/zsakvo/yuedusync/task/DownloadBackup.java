package cc.zsakvo.yuedusync.task;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.thegrizzlylabs.sardineandroid.Sardine;
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine;

import org.zeroturnaround.zip.ZipUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

@SuppressWarnings("ALL")
public class DownloadBackup extends AsyncTask<String,Void,Boolean> {

    cc.zsakvo.yuedusync.listener.DownloadBackup downloadBackup;

    public DownloadBackup(cc.zsakvo.yuedusync.listener.DownloadBackup downloadBackup){
        this.downloadBackup = downloadBackup;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        String backupPath = strings[0];
        String backupZipPath = strings[1];
        String account = strings[2];
        String password = strings[3];
        File backupFolder = new File(backupPath);
        File backupZip = new File(backupZipPath);
        if (!backupFolder.exists()) backupFolder.mkdir();
        Sardine sardine = new OkHttpSardine();
        sardine.setCredentials(account, password);
        try {
            InputStream is = sardine.get("https://dav.jianguoyun.com/dav/YueDu/YueDuBackup.zip");
            new File(backupZipPath).createNewFile();
            FileOutputStream fos = new FileOutputStream(backupZipPath);
            byte[] getData = readInputStream(is);
            fos.write(getData);
            if (fos!=null){
                fos.close();
            }
            if (is!=null){
                is.close();
            }
            ZipUtil.unpack(backupZip,backupFolder);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean b) {
        super.onPostExecute(b);
        downloadBackup.download(b);
    }

    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

}
