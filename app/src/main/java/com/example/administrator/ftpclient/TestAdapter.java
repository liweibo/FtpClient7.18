//package com.example.administrator.ftpclient;
//
//import android.widget.BaseAdapter;
//
//public class TestAdapter extends BaseAdapter {
//    @Override
//    public int getCount() {
//        return mTasks.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return mTasks.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder viewHolder;
//        if (convertView == null) {
//            viewHolder = new ViewHolder();
//            convertView = View.inflate(ProgressActivity.this, R.layout.progress_item, null);
//            viewHolder.textView = (TextView) convertView.findViewById(R.id.item_name);
//            viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.item_progress);
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//
//        viewHolder.textView.setText(mTasks.get(position).getName());
//
//        if (mTasks.get(position).isDownload()) {
//            viewHolder.progressBar.setVisibility(View.VISIBLE);
//            viewHolder.progressBar.setProgress(mTasks.get(position).getProgress());
//        } else {
//            viewHolder.progressBar.setVisibility(View.INVISIBLE);
//        }
//
//        return convertView;
//    }
//
//    class ViewHolder {
//        TextView textView;
//        ProgressBar progressBar;
//    }
//
//}
