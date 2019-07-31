package com.example.administrator.ftpclient;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class selectFileActivity extends AppCompatActivity implements OnClickListener {
    String host, user, pass;
    int port;
    String currentParent = "";
    private ListView lv;
    private MyAdapter mAdapter;
    private List<FtpUtils.wxhFile> list;
    public static List<FtpUtils.wxhFile> currentFiles = null;

    private int checkNum; // 记录选中的条目数量
    private TextView tv_show;// 用于显示选中的条目数量
    private TextView tv_file_folder;
    private TextView tv_result;//用来显示具体选择的条目
    private List<Integer> checkList = new ArrayList<>();
    public static CheckBox checkbox_all;
    Button bt;
    public Button bttest;


    public static String dirpath;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    FTPClient ftpClient;
    String portStr;
    String serverPath;
    String mypathname;
    String localPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file);
        verifyStoragePermissions(this);
        Intent intent = getIntent();
        //接收数据
        host = intent.getStringExtra("host");
        user = intent.getStringExtra("user");
        pass = intent.getStringExtra("pass");
        port = intent.getIntExtra("port", 21);
        bt = (Button) findViewById(R.id.parent);
        bt.setOnClickListener(selectFileActivity.this);



        /* 实例化各个控件 */
        lv = (ListView) findViewById(R.id.lv);


        list = new ArrayList<FtpUtils.wxhFile>();
        checkbox_all = (CheckBox) findViewById(R.id.checkbox_all);
        bttest = (Button) findViewById(R.id.bttest);
        // 为Adapter准备数据
        initDate();

        setClick();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ItemChooseData.getFilePath().clear();
        System.out.println("按下了back键   onBackPressed()");
    }

    public void setClick() {
        //全选
        checkbox_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    // 遍历list的长度，将MyAdapter中的map值全部设为true
                    System.out.println(currentFiles.size() + "ddddd");
                    for (int i = 0; i < currentFiles.size(); i++) {
                        MyAdapter.getIsSelected().put(i, true);
                        System.out.println("11111" + MyAdapter.getIsSelected().get(i));
                    }
                    mAdapter.notifyDataSetChanged();

                } else {
                    for (int i = 0; i < currentFiles.size(); i++) {
                        MyAdapter.getIsSelected().put(i, false);
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }

        });

        bttest.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ItemChooseData.getFilePath().size() > 0) {
                    List<Integer> listIndex = ItemChooseData.getIndexArr();
                    for (int i = 0; i < listIndex.size(); i++) {
                        String filename = currentFiles.get(listIndex.get(i)).filename;

                        String filePath = currentFiles.get(listIndex.get(i)).filePath;
//                        ItemChooseData.addFilePath(filePath);
                        System.out.println("测试文件名：" + filename);
                        System.out.println("测试文件路径：" + filePath);
                    }
                    System.out.println("测试文件路径汇总：" + ItemChooseData.getFilePath().toString());

//[tffs/start.txt, tffs/EventFas, tffs/Fault, tffs/mvb_confm.dat, tffs/EDRM_CONF.xml]

                    //下载
                    String portStr = new Integer(port).toString();
                    String can[] = new String[4];
                    can[0] = host;
                    can[1] = portStr;
                    can[2] = user;
                    can[3] = pass;

                    //循环下载
                    List<Boolean> booList = new ArrayList<>();
                    for (int i = 0; i < ItemChooseData.getFilePath().size(); i++) {
                        DownTask task = new DownTask(selectFileActivity.this, ItemChooseData.getFilePath().get(i));
                        System.out.println("下载：：" + ItemChooseData.getFilePath().get(i));
                        task.execute(can);
                    }


                } else {
                    Toast tot = Toast.makeText(
                            selectFileActivity.this,
                            "请勾选需要下载的文件或文件夹！",
                            Toast.LENGTH_LONG);
                    tot.show();
                }

            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (currentFiles.get(arg2).isFile) {
                } else {
                    //是文件夹
                    whileDir(arg2);
                }


            }
        });


    }


    public  void  whileDir(int arg2) {
        String can[] = new String[5];
        can[0] = host;
        can[1] = new Integer(port).toString();
        can[2] = user;
        can[3] = pass;
        can[4] = currentFiles.get(arg2).filePath + "/";
        //在点击进入新文件夹之前记住现在的父亲路径是谁
        GetTask task = new GetTask(selectFileActivity.this);
        task.execute(can);
        checkbox_all.setChecked(false);//跳到下级目录，自动设为全不选
        for (int i = 0; i < currentFiles.size(); i++) {
            if (!MyAdapter.isSelected.get(i)) {
                checkbox_all.setChecked(false);
            }
        }
    }


    @Override
    public void onClick(View view) {
        if (currentParent.equals("") && currentFiles == null) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("这已经是最前面的文件夹了！")
                    .setPositiveButton("确定", null)
                    .show();
        } else {
            String portStr = new Integer(port).toString();
            String can[] = new String[5];
            can[0] = host;
            can[1] = portStr;
            can[2] = user;
            can[3] = pass;
            can[4] = currentParent;
            GetTask task = new GetTask(this);
            task.execute(can);
            ItemChooseData.getFilePath().clear();
        }
    }

    // 初始化数据
    private void initDate() {
        String portStr = new Integer(port).toString();
        String can[] = new String[5];
        can[0] = host;
        can[1] = portStr;
        can[2] = user;
        can[3] = pass;
        can[4] = "";
        GetTask task = new GetTask(this);
        task.execute(can);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    class GetTask extends AsyncTask<String, Void, List<FtpUtils.wxhFile>> {
        Context mContext;

        public GetTask(Context ctx) {
            mContext = ctx;
        }

        protected List<FtpUtils.wxhFile> doInBackground(String... Params) {
            String host = Params[0];
            String portStr = Params[1];
            String user = Params[2];
            String pass = Params[3];
            String path = Params[4];
            FtpUtils util = new FtpUtils();
            List<FtpUtils.wxhFile> list = null;
            int port = 21;
            port = Integer.parseInt(portStr);
            try {
                util.connectServer(host, port, user, pass, "");
                list = util.getFileList(path);
                //附带父亲文件夹的路径进去
                list.add(new FtpUtils.wxhFile(path, "", 2));
                for (int i = 0; i < list.size(); i++) {
                    System.out.println("测试--"+list.get(i).filePath+"---"+list.get(i).filename);

                }
            } catch (Exception e) {
                System.out.println("开启连接出错");
                e.printStackTrace();
            } finally {
                try {
                    util.closeServer();
                } catch (Exception e) {
                    e.printStackTrace();
                    ;
                }
                return list;
            }
        }

        protected void onPostExecute(List<FtpUtils.wxhFile> list) {
            String parentPath = null;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isParent) {
                    parentPath = list.get(i).filePath;
                    list.remove(i);
                }
            }


            SharedPreferences pref = selectFileActivity.this.getSharedPreferences("mypath", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("pathname", parentPath);
            editor.commit();

            System.out.println("这个父亲文件夹为：" + parentPath);
            System.out.println("处理结果函数内部");
            if (list == null || list.size() == 0) {
                System.out.println("空文件夹或者访问出错");
                new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage("这个文件夹是个空文件夹！")
                        .setPositiveButton("确定", null)
                        .show();

            } else {
                System.out.println("找到了！！");
                //更新文件list
                currentFiles = list;
                //调用刷新来显示我们的列表
//                inflateListView(list,parentPath);

                // 实例化自定义的MyAdapter
                mAdapter = new MyAdapter(list, selectFileActivity.this);
                // 绑定Adapter
                lv.setAdapter(mAdapter);
            }

        }
    }


    class DownTask extends AsyncTask<String, Long, Boolean> {
        Context mContext;
        ProgressDialog pdialog;
        String downloadPath;

        public DownTask(Context ctx, String path) {
            mContext = ctx;
            downloadPath = path;
        }

        protected void onPreExecute() {
            pdialog = new ProgressDialog(mContext);
            pdialog.setTitle("任务正在执行中");
            pdialog.setMessage("正在下载中，敬请等待...");
            pdialog.setCancelable(false);
            pdialog.setMax(100);
            pdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pdialog.setIndeterminate(false);
            pdialog.show();
        }

        protected void onProgressUpdate(Long... values) {
            long a = Long.valueOf(values[0].toString());
            int value = (int) a;
            pdialog.setProgress(value);
        }

        @Override
        protected Boolean doInBackground(String... Params) {
            ftpClient = new FTPClient();
            host = Params[0];
            portStr = Params[1];
            user = Params[2];
            pass = Params[3];


            SharedPreferences sharedPreferences = getSharedPreferences("mypath",
                    Activity.MODE_PRIVATE);
            mypathname = sharedPreferences.getString("pathname", "");
            String[] strdir = mypathname.split("/");
            crSDFile(strdir);
            localPath = dirpath;

            boolean textb = false;

            if (ItemChooseData.getFilePath().size() > 0) {
                System.out.println("xxxxx");
                textb = download(downloadPath);

            }
            return textb;


        }

        boolean download(String myserverPath) {
            boolean flag = false;
            int port = Integer.parseInt(portStr);
            try {
                ftpClient.setDataTimeout(6000);//设置连接超时时间
                ftpClient.setControlEncoding("utf-8");
                System.out.println("登录前");

                ftpClient.connect(host, port);

                flag = ftpClient.login(user, pass);
                System.out.println("登录后");
                if (!flag) return flag;
                FTPFile[] files = ftpClient.listFiles(myserverPath);
                if (files.length == 0) {
                    return false;
                }


                String ss = files[0].getName();
                String newstr = ss.replace(mypathname, "");
                newstr = newstr.replace("/", "");
                System.out.println("newstr" + newstr);
                localPath = localPath + newstr;


                System.out.println("我的localPath：" + localPath);
                // 接着判断下载的文件是否能断点下载
                long serverSize = files[0].getSize(); // 获取远程文件的长度
                File localFile = new File(localPath);
                System.out.println("localFile：" + localFile);
                long localSize = 0;

                if (localFile.exists()) {
                    System.out.println("localFile.exists()进来了");
                    File file = new File(localPath);
                    file.delete();

                }
                boolean newFile = localFile.createNewFile();
                System.out.println("newFile：" + newFile);
                // 进度
                long step = serverSize / 100;
                long process = 0;
                long currentSize = 0;
                // 开始准备下载文件
                ftpClient.enterLocalActiveMode();
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                OutputStream out = new FileOutputStream(localFile, true);
                ftpClient.setRestartOffset(localSize);
                InputStream input = ftpClient.retrieveFileStream(myserverPath);
                byte[] b = new byte[1024];
                int length = 0;
                while ((length = input.read(b)) != -1) {
                    out.write(b, 0, length);//写文件
                    currentSize = currentSize + length;
                    if (currentSize / step != process) {
                        process = currentSize / step;//下载的百分比
                        publishProgress(process);
                    }
                }
                out.flush();
                out.close();
                input.close();
                // 此方法是来确保流处理完毕，如果没有此方法，可能会造成现程序死掉
                if (ftpClient.completePendingCommand()) {
                    return true;
                } else {
                    return false;
                }


            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                return flag;
            }
        }

        protected void onPostExecute(Boolean flag) {
            if (flag) {
                Toast tot = Toast.makeText(
                        mContext,
                        "文件下载成功,保存在本地存储目录下的1ftpData文件夹",
                        Toast.LENGTH_LONG);
                tot.show();
            } else {
                new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage("抱歉，下载过程中出现错误，请重新尝试")
                        .setPositiveButton("确定", null)
                        .show();

            }
            pdialog.dismiss();
        }
    }


    public static void crSDFile(String... folder) {

        int length = folder.length;
        String genFolder = Environment.getExternalStorageDirectory().getPath().toString() +
                File.separator + "EDRMdata" + File.separator;

        File file, file2;
        file2 = new File(genFolder);
        if (!file2.exists()) {
            file2.mkdir();
        }

        String str = genFolder;
        for (int i = 0; i < length; i++) {

            str = str + folder[i] + "/";
            file = new File(str);

            if (!file.exists()) {
                file.mkdir();

            }

        }

        System.out.println("路径：" + str);
        dirpath = str;

    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


}
