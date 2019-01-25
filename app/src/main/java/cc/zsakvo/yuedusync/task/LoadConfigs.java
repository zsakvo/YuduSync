package cc.zsakvo.yuedusync.task;

import android.os.AsyncTask;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class LoadConfigs extends AsyncTask<String,Void,String> {

    private cc.zsakvo.yuedusync.listener.LoadConfigs loadConfigs;

    public LoadConfigs(cc.zsakvo.yuedusync.listener.LoadConfigs loadConfigs){
        this.loadConfigs = loadConfigs;
    }

    @Override
    protected String doInBackground(String... strings) {
        StringBuilder stringBuilder = new StringBuilder();
        String backupPath = strings[0];

        if (new File(backupPath+"/config.xml").exists()){
            stringBuilder.append("设置信息：检测成功 ✔️\n");
        }else {
            stringBuilder.append("设置信息：检测失败 ✖️️\n");
        }

        File searchHistory = new File(backupPath+"/myBookSearchHistory.json");
        if (searchHistory.exists()){
            stringBuilder.append("搜索记录：").append(readJson(searchHistory)).append("条\n");
        }else {
            stringBuilder.append("搜索记录：0条\n");
        }

        File bookShelf = new File(backupPath+"/myBookShelf.json");
        if (bookShelf.exists()){
            stringBuilder.append("保存书籍：").append(readJson(bookShelf)).append("本\n");
        }else {
            stringBuilder.append("保存书籍：0本\n");
        }

        File bookSource = new File(backupPath+"/myBookSource.json");
        if (bookSource.exists()){
            stringBuilder.append("书源数目：").append(readJson(bookSource)).append("个\n");
        }else {
            stringBuilder.append("书源数目：0个\n");
        }

        return stringBuilder.toString();
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        loadConfigs.loadConfigs(string);
    }

    private int readJson(File jsonFile){
        try {
            FileReader r = new FileReader(jsonFile);
            BufferedReader br = new BufferedReader(r);
            StringBuffer json = new StringBuffer();
            String s;
            while ((s = br.readLine())!= null) {
                json = json.append(s).append("\n");
            }
            br.close();
            JSONArray jsonArray = JSON.parseArray(json.toString());
            return jsonArray.size();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
