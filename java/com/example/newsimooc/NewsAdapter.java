package com.example.newsimooc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by siqi on 2016/5/15.
 */
public class NewsAdapter extends BaseAdapter implements AbsListView.OnScrollListener{

    private List<newsbean> mList;
    private LayoutInflater mInflter;
    private ImageLoader mImageLoader;
    private int mStart , mEnd;
    //用来保存当前可见项的url地址
    public static String[] URLS;
    private Boolean firstIn = true;

    public NewsAdapter(Context context , List<newsbean> data ,ListView llistView){
        this.mList = data;
        this.mImageLoader = new ImageLoader(llistView);
        mInflter = LayoutInflater.from(context);
        URLS = new String[data.size()];
        for (int i = 0 ; i < data.size() ; i++){
            URLS [i] = data.get(i).newsIconUrl;
        }
        llistView.setOnScrollListener(this);
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState == SCROLL_STATE_IDLE){
            //当滚动停止，加载可见项
            mImageLoader.loadImages(mStart , mEnd);
        }else {
            //停止加载
            mImageLoader.cancelAllTasks();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mStart = firstVisibleItem;
        mEnd = firstVisibleItem + visibleItemCount;
        if(firstIn && visibleItemCount > 0){
            mImageLoader.loadImages(mStart , mEnd);
            firstIn = false;
        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = mInflter.inflate(R.layout.item_layout ,null);
            viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvContent.setText(mList.get(position).newsContent);
        viewHolder.tvTitle.setText(mList.get(position).newsTitle);
        viewHolder.ivIcon.setImageResource(R.mipmap.ic_launcher);
        String url = mList.get(position).newsIconUrl;
        viewHolder.ivIcon.setTag(url);
//        使用多线程方式加载图片
     //   mImageLoader.showImage(viewHolder.ivIcon , mList.get(position).newsIconUrl);

        //使用AsyncTask加载
       mImageLoader.showImageByAsyncTask(viewHolder.ivIcon , mList.get(position).newsIconUrl);
        return convertView;
    }




    class ViewHolder{
        public TextView tvTitle , tvContent;
        public ImageView ivIcon;
    }
}
