package cn.icheny.download;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author Cheny
 */
public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 001;
    TextView tv_file_name1, tv_progress1, tv_time_cacl1, tv_file_name2, tv_progress2, tv_time_cacl2;
    Button btn_download1, btn_download2, btn_download_all;
    ProgressBar pb_progress1, pb_progress2;

    DownloadManager mDownloadManager;
    String xesUrl = "https://acj4.pc6.com/pc6_soure/2020-3/GRKrJosWjXLlC4BQze53DdAx.apk";
    String yfdUrl = "https://www.yuanfudao.com/download?userType=student&vendor=PCOfficialWebsite01&keyfrom=yfd-mkt-xiaoxue-xt782-BDPZ-boya-pcss-title-yw-x&client=android";

    long startTime = 0;
    long middleTime = 0;
    int endTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initDownloads();
    }

    private void initDownloads() {
        mDownloadManager = DownloadManager.getInstance();
        mDownloadManager.add(xesUrl, new DownloadListner() {
            @Override
            public void onFinished() {
                Toast.makeText(MainActivity.this, "下载完成!", Toast.LENGTH_SHORT).show();
                caclElapsedTime("xes");
            }

            @Override
            public void onProgress(float progress) {
                pb_progress1.setProgress((int) (progress * 100));
                tv_progress1.setText(String.format("%.2f", progress * 100) + "%");
            }

            @Override
            public void onPause() {
                Toast.makeText(MainActivity.this, "暂停了!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancel() {
                tv_progress1.setText("0%");
                tv_time_cacl1.setText("待计时");
                pb_progress1.setProgress(0);
                btn_download1.setText("下载");
                Toast.makeText(MainActivity.this, "下载已取消!", Toast.LENGTH_SHORT).show();
            }
        });

        mDownloadManager.add(yfdUrl, new DownloadListner() {
            @Override
            public void onFinished() {
                Toast.makeText(MainActivity.this, "下载完成!", Toast.LENGTH_SHORT).show();
                caclElapsedTime("yfd");
            }

            @Override
            public void onProgress(float progress) {
                pb_progress2.setProgress((int) (progress * 100));
                tv_progress2.setText(String.format("%.2f", progress * 100) + "%");
            }

            @Override
            public void onPause() {
                Toast.makeText(MainActivity.this, "暂停了!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                tv_progress2.setText("0%");
                tv_time_cacl2.setText("待计时");
                pb_progress2.setProgress(0);
                btn_download2.setText("下载");
                Toast.makeText(MainActivity.this, "下载已取消!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 初始化View控件
     */
    private void initViews() {
        tv_file_name1 = findViewById(R.id.tv_file_name1);
        tv_progress1 = findViewById(R.id.tv_progress1);
        tv_time_cacl1 = findViewById(R.id.tv_time_cacl1);
        pb_progress1 = findViewById(R.id.pb_progress1);
        btn_download1 = findViewById(R.id.btn_download1);
        tv_file_name1.setText("学而思");

        tv_file_name2 = findViewById(R.id.tv_file_name2);
        tv_progress2 = findViewById(R.id.tv_progress2);
        tv_time_cacl2 = findViewById(R.id.tv_time_cacl2);
        pb_progress2 = findViewById(R.id.pb_progress2);
        btn_download2 = findViewById(R.id.btn_download2);
        tv_file_name2.setText("猿辅导");

        btn_download_all = findViewById(R.id.btn_download_all);

    }

    /**
     * 下载或暂停下载
     *
     * @param view
     */
    public void downloadOrPause(View view) {
        switch (view.getId()) {
            case R.id.btn_download1:
                if (!mDownloadManager.isDownloading(xesUrl)) {
                    mDownloadManager.download(xesUrl);
                    btn_download1.setText("暂停");
                    tv_time_cacl1.setText("计时中");
                    startTime = System.currentTimeMillis();
                    Log.d("耗时", "下载开始时间" + startTime);

                } else {
                    btn_download1.setText("下载");
                    mDownloadManager.pause(xesUrl);

                }
                break;
            case R.id.btn_download2:
                if (!mDownloadManager.isDownloading(yfdUrl)) {
                    mDownloadManager.download(yfdUrl);
                    btn_download2.setText("暂停");
                    tv_time_cacl2.setText("计时中");
                    startTime = System.currentTimeMillis();
                    Log.d("耗时", "下载开始时间" + startTime);
                } else {
                    btn_download2.setText("下载");
                    mDownloadManager.pause(yfdUrl);
                }
                break;
        }
    }

    public void downloadOrPauseAll(View view) {
        if (!mDownloadManager.isDownloading(xesUrl, yfdUrl)) {
            btn_download1.setText("暂停");
            btn_download2.setText("暂停");
            btn_download_all.setText("全部暂停");
            mDownloadManager.download(xesUrl, yfdUrl);//最好传入个String[]数组进去
        } else {
            mDownloadManager.pause(xesUrl, yfdUrl);
            btn_download1.setText("下载");
            btn_download2.setText("下载");
            btn_download_all.setText("全部下载");
        }
    }

    /**
     * 取消下载
     *
     * @param view
     */
    public void cancel(View view) {

        switch (view.getId()) {
            case R.id.btn_cancel1:
                mDownloadManager.cancel(xesUrl);
                break;
            case R.id.btn_cancel2:
                mDownloadManager.cancel(yfdUrl);
                break;
        }
    }

    public void cancelAll(View view) {
        mDownloadManager.cancel(xesUrl, yfdUrl);
        btn_download1.setText("下载");
        btn_download2.setText("下载");
        btn_download_all.setText("全部下载");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        if (!checkPermission(permission)) {//针对android6.0动态检测申请权限
            if (shouldShowRationale(permission)) {
                showMessage("需要权限跑demo哦...");
            }
            ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelAll(null);
    }

    /**
     * 显示提示消息
     *
     * @param msg
     */
    private void showMessage(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 检测用户权限
     *
     * @param permission
     * @return
     */
    protected boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 是否需要显示请求权限的理由
     *
     * @param permission
     * @return
     */
    protected boolean shouldShowRationale(String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
    }

    public void caclElapsedTime(String str) {
        middleTime = System.currentTimeMillis();
        Log.d("耗时", "下载完成时间" + middleTime );
        endTime = (int)((middleTime - startTime)/1000);
        Log.d("耗时", "下载耗时" + endTime );
        if ("xes" == str)
            tv_time_cacl1.setText("下载耗时(秒):" + Integer.toString(endTime));
        else
            tv_time_cacl2.setText("下载耗时(秒):" + Integer.toString(endTime));
    }
}