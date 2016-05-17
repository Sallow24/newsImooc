package com.example.newsimooc;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by siqi on 2016/5/16.
 */
public class ImageLoader {
    private ImageView mImageView;
    private String mUrl;
    //创建缓存Cache
    private LruCache<String , Bitmap> mCache;
    private ListView mListView;
    private Set<NewsAsynTask> mTask;

    public ImageLoader(ListView listView){
        mListView = listView;
        mTask = new HashSet<>();
        //获取最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        //将图片的缓存大小设置为可用内存的1/4；
        int cacheSize = maxMemory/2;
        mCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {

                return value.getByteCount();
            }
        };
    }

    public void addBitmapToCache(String url , Bitmap bitmap){
        if(getBitmapFromCache(url)== null){
            mCache.put(url , bitmap);
        }
    }

    public Bitmap getBitmapFromCache(String url){
        return mCache.get(url);
    }


    //创建内部类 继承AsyncTask 实现异步加载
    private class NewsAsynTask extends AsyncTask<String , Void , Bitmap>{
        // private  ImageView mImageView;
        private  String mUrl;
        public NewsAsynTask( String url){
            //  this.mImageView = imageView;
            this.mUrl = url;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            Bitmap bitmap = getBitmapFormUrl(url);
            if(bitmap != null){
                addBitmapToCache(url , bitmap);
           }
            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap){
            super.onPostExecute(bitmap);
            ImageView imageView = (ImageView) mListView.findViewWithTag(mUrl);
            if(imageView != null && bitmap != null){
                imageView .setImageBitmap(bitmap);
                mTask.remove(this);
            }
        }
    }


    public void showImageByAsyncTask(ImageView imageView , String url){
                Bitmap bitmap = getBitmapFromCache(url);
                if(bitmap == null) {
                    imageView.setImageResource(R.mipmap.ic_launcher);
                }else{
                    imageView.setImageBitmap(bitmap);
                }

    }

    public void loadImages(int start , int end){
        for(int i = start ; i < end ; i++){
            String url = NewsAdapter.URLS[i];
            Bitmap bitmap = getBitmapFromCache(url);
            if(bitmap == null) {
                NewsAsynTask task = new NewsAsynTask(url);
                task.execute(url);
                mTask.add(task);
            }else{
                ImageView imageView = (ImageView) mListView.findViewWithTag(url);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    public void cancelAllTasks(){
        if(mTask != null){
            for(NewsAsynTask task : mTask){
                task.cancel(true);
            }
        }
    }


    public Bitmap getBitmapFormUrl(String urlString) {
        Bitmap bitmap;
        InputStream is = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            is = new BufferedInputStream(connection.getInputStream());
            bitmap = BitmapFactory.decodeStream(is);
            connection.disconnect();
            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    //<--通过多线程方式异步加载图片 -->

//    private Handler mHandler = new Handler(){
//    public void handleMessage(Message msg){
//            super.handleMessage(msg);
//            if(mImageView.getTag().equals(mUrl)) {
//                mImageView.setImageBitmap((Bitmap) msg.obj);
//            }
//        }
//
//    };

//    public void showImage(ImageView imageView, final String url) {
//        mUrl = url;
//        mImageView = imageView;
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                Bitmap bitmap = getBitmapFormUrl(url);
//                Message message = Message.obtain();
//                message.obj = bitmap;
//                mHandler.sendMessage(message);
//            }
//        }.start();
//    }


}