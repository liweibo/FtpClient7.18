package com.example.administrator.ftpclient.checkboxList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.administrator.ftpclient.FtpUtils;
import com.example.administrator.ftpclient.R;
import com.example.administrator.ftpclient.RingProgressBar;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dwb on 2018/1/3.
 * describe1:
 * describe2:
 * email:wolfking0608@163.com
 */

public class MyAdapter extends BaseAdapter {
    // 填充数据的list
    private        ArrayList<FtpUtils.wxhFile>         list;
    // 用来控制CheckBox的选中状况
    private static HashMap<Integer, Boolean> isSelected;
    // 上下文
    private        Context                   context;
    // 用来导入布局
    private LayoutInflater inflater = null;

    // 构造器
    public MyAdapter(ArrayList<FtpUtils.wxhFile> list, Context context) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        isSelected = new HashMap<Integer, Boolean>();
        // 初始化数据
        initDate();


    }

    // 初始化isSelected的数据
    private void initDate() {
        for (int i = 0; i < list.size(); i++) {
            getIsSelected().put(i, false);
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            // 获得ViewHolder对象
            holder = new ViewHolder();
            // 导入布局并赋值给convertview
            convertView = inflater.inflate(R.layout.item_list, null);
            holder.tv = (TextView) convertView.findViewById(R.id.item_tv);
            holder.item_tv_sucfail = (TextView) convertView.findViewById(R.id.item_tv_sucfail);
            holder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);
            holder.ringProgressBarAdapter = (RingProgressBar) convertView.findViewById(R.id.ringProgressBarAdapter);
            // 为view设置标签
            convertView.setTag(holder);
        } else {
            // 取出holder
            holder = (ViewHolder) convertView.getTag();
        }

        // 设置list中TextView的显示
        holder.tv.setText((CharSequence) list.get(position));
        // 根据isSelected来设置checkbox的选中状况
        holder.cb.setChecked(getIsSelected().get(position));

        new Thread(new Runnable() {
            @Override
            public void run() {

//                    new ViewHolder().ringProgressBarAdapter.setProgress(mProgress);

            }
        }).start();



        return convertView;
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        MyAdapter.isSelected = isSelected;
    }

    public static class ViewHolder {
        public TextView tv;
        public TextView item_tv_sucfail;
        public CheckBox cb;
        public RingProgressBar ringProgressBarAdapter;
    }
}