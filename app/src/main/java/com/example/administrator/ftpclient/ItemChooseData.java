package com.example.administrator.ftpclient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemChooseData {
    private static List<Integer> listindex = new ArrayList<>();
    private static List<String> listFilePath = new ArrayList<>();


    public static void removeIndex(int i) {
        if (listindex.size() > 0 && listindex.size() > i) {
            listindex.remove(i);
        }
    }

    public static void addIndex(int i) {
        listindex.add(i);
    }

    public static List<Integer> getIndexArr() {
        return listindex;
    }

    public static void addFilePath(String filepath) {
        listFilePath.add(filepath);

    }

    public static List<String> getFilePath() {
        return listFilePath;
    }

    public static void clearFilePath() {
        listFilePath.clear();
    }

    public static void removeFilePath(int i) {
        if (getFilePath().size() > 0 ) {
            //不能按序号直接删，应根据序号先找出currentFiles中对应的文件的路径，再删除该路径
            String s1 = selectFileActivity.currentFiles.get(i).filePath;
            System.out.println("对应："+s1);
            int x1 = getFilePath().indexOf(s1);
            System.out.println("搜索序号："+x1);
            getFilePath().remove(x1);
        }
    }
}
