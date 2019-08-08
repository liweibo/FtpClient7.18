//package com.example.administrator.ftpclient;
//
//import android.os.SystemClock;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.BaseAdapter;
//import android.widget.ListView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class TestActivity extends AppCompatActivity {
//
//
//    public List<Data> mTasks = new ArrayList<>();
//    public ListView mListView;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test);
//
//        for (int i = 0; i < 50; i++) {
//            mTasks.add(new Data("data" + i));
//        }
//        mListView = (ListView) findViewById(R.id.list);
//        mListView.setAdapter(new TestAdapter());
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                download(position);
//            }
//        });
//    }
//
//    private void download(final int positionInAdapter) {
//        mTasks.get(positionInAdapter).setDownload(true);
////        if (positionInAdapter >= mListView.getFirstVisiblePosition() &&
////                positionInAdapter <= mListView.getLastVisiblePosition()) {
////            int positionInListView = positionInAdapter - mListView.getFirstVisiblePosition();
////            ProgressBar item = (ProgressBar) mListView.getChildAt(positionInListView)
////                    .findViewById(R.id.item_progress);
////            item.setVisibility(View.VISIBLE);
////        }
////        new Thread(new Runnable() {
////            @Override
////            public void run() {
////                for (int i = 1; i < 101; i++) {
////                    final int progress = i;
////                    runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            publishProgress(positionInAdapter, progress);
////                        }
////                    });
////                    SystemClock.sleep(500);
////                }
////            }
////        }).start();
//    }
//
//    public void publishProgress(final int positionInAdapter, final int progress) {
//        mTasks.get(positionInAdapter).setProgress(progress);
////        if (positionInAdapter >= mListView.getFirstVisiblePosition() &&
////                positionInAdapter <= mListView.getLastVisiblePosition()) {
////
////            //getFirstVisiblePosition是屏幕看得见的第一个item，item的位置计数是从整个listview开始算
////            //positionInAdapter是点击的某个item的在列表中的实际值。
////            int positionInListView = positionInAdapter - mListView.getFirstVisiblePosition();
////            ProgressBar item = (ProgressBar) mListView.getChildAt(positionInListView)
////                    .findViewById(R.id.item_progress);//getChildAt是屏幕看的到范围中的某个item
////            item.setProgress(progress);
////        }
//    }
//
//
//    public class TestAdapter extends BaseAdapter {
//
//        @Override
//        public int getCount() {
//            return mTasks.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return mTasks.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder viewHolder;
//            if (convertView == null) {
//                viewHolder = new ViewHolder();
//                convertView = View.inflate(TestActivity.this, R.layout.progress_item, null);
//                viewHolder.textView = (TextView) convertView.findViewById(R.id.item_name);
//                viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.item_progress);
//                convertView.setTag(viewHolder);
//            } else {
//                viewHolder = (ViewHolder) convertView.getTag();
//            }
//
//            viewHolder.textView.setText(mTasks.get(position).getName());
//
//
//            if (mTasks.get(position).isDownload()) {
//
//                viewHolder.progressBar.setVisibility(View.VISIBLE);
//                new TestAdapter().notifyDataSetChanged();
//
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        for (int i = 1; i < 101; i++) {
//                            final int progress = i;
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//
//                                    viewHolder.progressBar.setProgress(progress);
//                                }
//                            });
//                            SystemClock.sleep(500);
//                        }
//                    }
//                }).start();
//
//
//            } else {
//                viewHolder.progressBar.setVisibility(View.INVISIBLE);
//            }
//
//
//            return convertView;
//        }
//
//        class ViewHolder {
//            TextView textView;
//            ProgressBar progressBar;
//        }
//
//    }
//
//}
