package com.example.newsimooc;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;

    private static String URL ="http://www.imooc.com/api/teacher?type=4&num=30";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.lv_main);
        new NewsAsyncTask().execute(URL);
    }

    class NewsAsyncTask extends AsyncTask<String,Void,List<newsbean>>{
        @Override
        protected void onPostExecute(List<newsbean> newsbeen){
            super.onPostExecute(newsbeen);
            NewsAdapter adapter = new NewsAdapter(MainActivity.this , newsbeen ,mListView);
            mListView.setAdapter(adapter);
        }

        private List<newsbean> getJsonData(String url){
            List<newsbean> newsbeanList = new ArrayList<newsbean>();
            JSONObject jsonObject;
            try {
                //从后台获取json数据
                String jsonString = readStream(new URL(url).openStream());
                //解析json数据，封装成newsbean对象，传入list数组
                jsonObject = new JSONObject(jsonString);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0 ; i < jsonArray.length() ; i++ ){
                    jsonObject = jsonArray.getJSONObject(i);
                    newsbean newsBean = new newsbean();
                    newsBean.newsIconUrl = jsonObject.getString("picSmall");
                    newsBean.newsContent = jsonObject.getString("description");
                    newsBean.newsTitle = jsonObject.getString("name");
                    newsbeanList.add(newsBean);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return newsbeanList;
        }

        private String readStream(InputStream is){
            InputStreamReader isr;
            String result="";
            try {
                isr = new InputStreamReader(is,"utf-8");
                BufferedReader buf = new BufferedReader(isr);
                String line = "";
                while((line = buf.readLine())!=null){
                    result += line;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
        @Override
        protected List<newsbean> doInBackground(String... params) {
            //从传入的参数中获取URL
            //调用getJsonData（String url） 从网络中获取json格式的数据
            //并返回List数据
            return getJsonData(params[0]);

        }
    }

}
