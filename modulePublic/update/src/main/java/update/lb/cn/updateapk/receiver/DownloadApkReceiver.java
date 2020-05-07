package update.lb.cn.updateapk.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;


import com.dragondevl.clog.CLog;

import java.io.File;

import update.lb.cn.updateapk.R;
import update.lb.cn.updateapk.download.DownLoadApk;
import update.lb.cn.updateapk.util.PackageUtils;
import update.lb.cn.updateapk.util.ShellUtils;


/**
 * Created by Administrator on 2018/1/31.
 */

public class DownloadApkReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        CLog.e("DownloadApkReceiver" + intent.getAction());
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(10000);
                    final String path = DownLoadApk.requestAPKUri(context);
                    if (!TextUtils.isEmpty(path)) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                installApk(context, path);
                            }
                        });
                    } else {
                        CLog.e("apk is a bad file with old version or empty!");
                    }
                }
            }).start();
        }/* else if (Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())) {
            Uri data = intent.getData();
            if (data != null && context.getPackageName().equals(data.getEncodedSchemeSpecificPart())) {
                Log.d("TAG", "更新安装成功.....");
                Toast.makeText(context, "更新安装成功", Toast.LENGTH_LONG).show();
                // 重新启动APP
                Intent intentToStart = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                intentToStart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentToStart);
            }
        }*/
    }

    private void installApk(Context context, String apkPath) {
        if (context == null || TextUtils.isEmpty(apkPath)) {
            return;
        }
        File file = new File(apkPath);
        if (file.exists()) {
            boolean b = ShellUtils.checkRootPermission();
            if (b) {
                int resultCode = PackageUtils.installSilent(context, apkPath);
                if (resultCode != PackageUtils.INSTALL_SUCCEEDED) {
                    //Toast.makeText(context, "升级失败！",     Toast.LENGTH_SHORT).show();
                    CLog.e("resultCode = " + resultCode);
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.str_update_success), Toast.LENGTH_SHORT).show();
                }

            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                } else {
                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                Toast.makeText(context, context.getResources().getString(R.string.str_use_accessibility_install), Toast.LENGTH_SHORT).show();
            }
        } else {
            CLog.e("file not exists!");
        }
    }

}
