package com.example.administrator.ftpclient;

import android.graphics.Color;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FileListActivity extends AppCompatActivity {
    public ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);
        mListView = (ListView) findViewById(R.id.list);
        mListView.setAdapter(new TestAdapter());

    }


    public class TestAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return ItemChooseData.getDownloadSucOrFailNamew().size();
        }

        @Override
        public Object getItem(int position) {
            return ItemChooseData.getDownloadSucOrFailNamew().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(FileListActivity.this, R.layout.progress_item, null);
                viewHolder.fileName = (TextView) convertView.findViewById(R.id.filename);
                viewHolder.dowmSucFail = (TextView) convertView.findViewById(R.id.downsucfail);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.fileName.setText( ItemChooseData.getDownloadSucOrFailNamew().get(position));
            if (!ItemChooseData.getDownloadSucOrFailIsTrueOrFalse().get(position)){
                viewHolder.dowmSucFail.setBackgroundColor(Color.parseColor("#DB7093"));
            }
            viewHolder.dowmSucFail.setText(ItemChooseData.getDownloadSucOrFailIsTrueOrFalse().get(position)?"下载成功":"下载失败");

            return convertView;
        }

        class ViewHolder {
            TextView fileName;
            TextView dowmSucFail;
        }

    }
}
