package com.example.administrator.ftpclient;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.cunoraz.gifview.library.GifView;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private int mProgress = 0;
    private RingProgressBar roundProgressBar;
    private Socket socket;
    OutputStream outputStream;
    InputStream is;

    // 输入流读取器对象
    InputStreamReader isr;
    BufferedReader br;
    private Handler mMainHandler;

    // 接收服务器发送过来的消息
    String response;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private Button login;
    private Button cancel;
    public Switch switch1;
    public static EditText host, port, name, pass;
    public static boolean haveCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        roundProgressBar = findViewById(R.id.ringProgressBar1);
        haveCheck = false;

//        GifView gifView1 = (GifView) findViewById(R.id.gif11);
//        gifView1.setVisibility(View.VISIBLE);
//        gifView1.play();




        //申请权限
        //verifyStoragePermissions(this);
        int hasReadPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasReadPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        //初始化组件
        switch1 = (Switch) findViewById(R.id.switch1);
        login = (Button) findViewById(R.id.login_button);
        cancel = (Button) findViewById(R.id.cancel_button);
        host = (EditText) findViewById(R.id.login_host);
        port = (EditText) findViewById(R.id.login_port);
        name = (EditText) findViewById(R.id.login_id);
        pass = (EditText) findViewById(R.id.login_password);
        //添加点击函数
        login.setOnClickListener(this);
        cancel.setOnClickListener(this);
        switch1.setOnClickListener(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mProgress < 100) {
                    mProgress += 1;
                    roundProgressBar.setProgress(mProgress);
                    try {
                        Thread.sleep(200);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
}

    @Override
    protected void onStart() {
        super.onStart();
        if (ItemChooseData.getFilePath().size() > 0) {
            System.out.println("验证：" + ItemChooseData.getFilePath().get(0));
        }
    }

    public void onClick(View v) {
        //登录按钮
        if (v == findViewById(R.id.login_button)) {
            String host = this.host.getText().toString();
            String portStr = this.port.getText().toString();
            String name = this.name.getText().toString();
            String pass = this.pass.getText().toString();
            if (host.equals("") || portStr.equals("") || name.equals("") || pass.equals("")) {
                new AlertDialog.Builder(this)
                        .setTitle("警告")
                        .setMessage("信息未填写完整！")
                        .setPositiveButton("确定", null)
                        .show();
            } else {
                //信息填写完整,那么就测试是否能够登录，如果能就跳转，不能就提示用户
                String can[] = new String[4];
                can[0] = host;
                can[1] = portStr;
                can[2] = name;
                can[3] = pass;
                LogTask task = new LogTask(this);
                task.execute(can);
            }
            //清空按钮
        } else if (v == findViewById(R.id.cancel_button)) {
            this.name.setText("");
            this.pass.setText("");
            this.host.setText("");
            this.port.setText("");
            Toast tot = Toast.makeText(
                    this,
                    "填写信息已清空",
                    Toast.LENGTH_LONG);
            tot.show();
        } else if (v == findViewById(R.id.switch1)) {
            if (switch1.isChecked()) {
                haveCheck = true;
            } else {
                haveCheck = false;
            }
            System.out.println("checkValue:" + haveCheck);
        }

    }

//处理登录的异步函数
class LogTask extends AsyncTask<String, Void, Boolean> {
    Context mContext;

    public LogTask(Context ctx) {
        mContext = ctx;
    }

    protected Boolean doInBackground(String... Params) {

        if (haveCheck) {
            return checkLogin(8001);

        } else {
            return nocheckLogin();
        }

    }

    protected void onPostExecute(Boolean flag) {
        if (flag) {
            Toast tot = Toast.makeText(
                    mContext,
                    "登录成功",
                    Toast.LENGTH_LONG);
            tot.show();
            Intent intent = new Intent(MainActivity.this, selectFileActivity.class);
            //准备进入选择界面并且准备好参数
            intent.putExtra("host", host.getText().toString());
            intent.putExtra("user", name.getText().toString());
            intent.putExtra("pass", pass.getText().toString());
            intent.putExtra("port", Integer.parseInt(port.getText().toString()));
            startActivity(intent);
        } else {
            new AlertDialog.Builder(mContext)
                    .setTitle("无法连接")
                    .setMessage("请检查是否连接无线工装WIFI以及信息填写是否无误！")
                    .setPositiveButton("确定", null)
                    .show();

        }
    }


}

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                System.out.println("申请权限成功");
            } else {
                System.out.println("申请失败");
            }
        }
    }

    //校验 只有edrm模块需要校验  其他模块直接登陆即可。
    public boolean nocheckLogin() {
        String host1 = host.getText().toString();
        String user1 = name.getText().toString();
        String pass1 = pass.getText().toString();
        int port1 = Integer.parseInt(port.getText().toString());
        boolean flag = false;
        FTPManager manager = new FTPManager();
        try {
            flag = manager.connect(host1, port1, user1, pass1);
            if (!flag) return flag;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //最后不管是否连接上了 都关闭一下
            try {
                manager.closeFTP();
            } catch (Exception e1) {

            }
            return flag;
        }

    }


    //校验 只有edrm模块需要校验  其他模块直接登陆即可。
    public boolean checkLogin(int socketPort) {
        String host1 = host.getText().toString();
        String user1 = name.getText().toString();
        String pass1 = pass.getText().toString();
        int port1 = Integer.parseInt(port.getText().toString());

        boolean flag = false;
        FTPManager manager = new FTPManager();
        try {
            // 创建Socket对象 & 指定服务端的IP 及 端口号
//            socket = new Socket("10.0.1.5", 8001);
            socket = new Socket(host1, socketPort);

            // 判断客户端和服务器是否连接成功
            System.out.println("连接" + socket.isConnected());
            if (socket.isConnected()) {
                // 步骤1：从Socket 获得输出流对象OutputStream
                // 该对象作用：发送数据
                outputStream = socket.getOutputStream();

                // 步骤2：写入需要发送的数据到输出流对象中
                outputStream.write(("Request").getBytes("US-ASCII"));
                // 特别注意：数据的结尾加上换行符才可让服务器端的readline()停止阻塞

                // 步骤3：发送数据到服务端
                outputStream.flush();
                System.out.println("发request:" + Arrays.toString(("Request").getBytes("US-ASCII")));

                System.out.println("发");

//                                接收

                // 步骤1：创建输入流对象InputStream
                is = socket.getInputStream();

                // 步骤2：创建输入流读取器对象 并传入输入流对象
                // 该对象作用：获取服务器返回的数据
                isr = new InputStreamReader(is);
                br = new BufferedReader(isr);

//                                List<char> list = new ArrayList<>();
                char[] ch = new char[40];
                String strs = "";//Response
                String strs1 = "";//Response后的字符

                for (int i = 1; i < 41; i++) {
                    ch[i - 1] = (char) br.read();
                    if (i < 9) {
                        strs += ch[i - 1];

                    }
                    if (i > 8) {
                        strs1 += ch[i - 1];

                    }
                    System.out.println("接收br第" + i + ":" + ch[i - 1]);
                }


                System.out.println("数组：" + Arrays.toString(ch));
                System.out.println("str前：" + strs);
                System.out.println("str后：" + strs1);

                //////////////////////////////第二次发//////////////////////////////////////
                char[] ch2 = new char[40];
                String strs2 = "";
                if (strs.equals("Response")) {
                    System.out.println("能收到Response");

                    //对Response后的字符转换成十六进制字节数组
                    if (strs1 == null || strs1.trim().equals("")) {
                        return false;
                    }

                    byte[] bytes16 = new byte[strs1.length() / 2];
                    for (int i = 0; i < strs1.length() / 2; i++) {
                        String subStr = strs1.substring(i * 2, i * 2 + 2);
                        bytes16[i] = (byte) Integer.parseInt(subStr, 16);
                    }

                    System.out.println("bytes16[i]:" + Arrays.toString(bytes16));


                    //加密。。。。。。

                    String strTo = "5531129003a313b176c539d223da4182";
                    byte[] bytess16 = new byte[strTo.length() / 2];
                    for (int i = 0; i < strTo.length() / 2; i++) {
                        String subStr = strTo.substring(i * 2, i * 2 + 2);
                        bytess16[i] = (byte) Integer.parseInt(subStr, 16);
                    }
                    System.out.println("strToEny:" + Arrays.toString(bytess16));
                    //先根据字节数组生成 AES密钥对象
                    // 再使用 AES密钥对象 加密数据。

                    try {
                        byte[] byteEny = MainActivity.encrypt(bytess16, bytes16);
                        System.out.println("byteEny:" + Arrays.toString(byteEny));

                        String strEny = MainActivity.byte2hex(byteEny);
                        System.out.println("把字节数组转换成16进值字符串:" + strEny);

//                                        把字节数组转换成16进值字符串
//                                        String strEny = byteEny.toString();
                        int[] intEny = new int[byteEny.length];

                        for (int i = 0; i < byteEny.length; i++) {
                            if (byteEny[i] < 0) {
                                intEny[i] = 256 + byteEny[i];
                            } else {
                                intEny[i] = byteEny[i];

                            }
                        }
                        System.out.println("intEny:" + Arrays.toString(intEny));
                        String strjia = "";
                        for (int i = 0; i < intEny.length; i++) {
                            strjia += (char) intEny[i];
                        }


                        System.out.println("strjia:" + strjia);

                        String sendstrpsw = "Accredit" + strEny;
                        byte[] byteEnys = sendstrpsw.getBytes("US-ASCII");

                        outputStream = socket.getOutputStream();
                        outputStream.write(byteEnys);
                        outputStream.flush();


                        System.out.println("发jiami");


//                                接收
                        is = socket.getInputStream();
                        isr = new InputStreamReader(is);
                        br = new BufferedReader(isr);
                        System.out.println("收jiami");
                        String strOk = "";
                        for (int i = 1; i < 25; i++) {
                            char jiami = (char) br.read();
                            strOk += jiami;
                            System.out.println("////：" + jiami);
                        }
                        System.out.println("加密后收到数据：" + strOk);

                        if (strOk.indexOf("ResponseAccreditOK") == 0) {
                            System.out.println("接受数据正常.....");


                        }

                    } catch (Exception e) {

                    }

                    System.out.println("字节数组：" + Arrays.toString(bytes16));


                    outputStream = socket.getOutputStream();
                    outputStream.write(("WriteFaultToFile").getBytes("US-ASCII"));
                    outputStream.flush();

                    System.out.println("发WriteFaultToFile");


//                                接收
                    is = socket.getInputStream();
                    isr = new InputStreamReader(is);
                    br = new BufferedReader(isr);
                    String str22 = "";
                    for (int i = 1; i < 41; i++) {
                        ch2[i - 1] = (char) br.read();
                        str22 += ch2[i - 1];

                        System.out.println("接收br第" + i + ":" + ch2[i - 1]);
                    }
                    System.out.println("WaiteForWrite打印：" + str22);
                    if (str22.indexOf("WaiteForWrite") == 0 || str22.indexOf("WaiteForWrite_e") == 0) {
                        System.out.println("str22.indexOf(\"WaiteForWrite\")==0成立.....");


                        try {
//                            flag = manager.connect("10.0.1.5", 21, "CSR", "12345678");
                            flag = manager.connect(host1, port1, user1, pass1);
                            if (!flag) return false;
                            System.out.println("我的了flag：" + flag);


                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            //最后不管是否连接上了 都关闭一下
                            try {
                                manager.closeFTP();
                            } catch (Exception e1) {

                            }
                        }


                    }

                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //最后不管是否连接上了 都关闭一下
            try {
                manager.closeFTP();
            } catch (Exception e1) {

            }
            return flag;
        }
    }


    public static byte[] encrypt(byte[] plainBytes, byte[] key) throws Exception {

        // 获取 AES 密码器
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");


        int blockSize = cipher.getBlockSize();
        int length = plainBytes.length;
        //计算需填充长度
        if (length % blockSize != 0) {
            length = length + (blockSize - (length % blockSize));
        }
        byte[] plaintext = new byte[length];
        //填充
        System.arraycopy(plainBytes, 0, plaintext, 0, plainBytes.length);
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");


        // 初始化密码器（加密模型）
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);

        // 加密数据, 返回密文
        return cipher.doFinal(plaintext);
    }


    // /** 字节数组转成16进制字符串 **/
    public static String byte2hex(byte[] b) { // 一个字节的数，
        StringBuffer sb = new StringBuffer(b.length * 2);
        String tmp = "";
        for (int n = 0; n < b.length; n++) {
            // 整数转成十六进制表示
            tmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (tmp.length() == 1) {
                sb.append("0");
            }
            sb.append(tmp);
        }
        return sb.toString().toUpperCase(); // 转成da写
    }
}


