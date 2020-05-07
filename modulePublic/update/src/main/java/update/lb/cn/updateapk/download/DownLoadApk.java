package update.lb.cn.updateapk.download;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.dragondevl.clog.CLog;

/**
 * Created by Administrator on 2018/1/31.
 */

public class DownLoadApk {

    public static final String TAG = DownLoadApk.class.getSimpleName();
    public static final String DOWNLOAD_URL = "http://qiaoqiao.aptdev.cn/APP2/test_upgrade.apk";
    public static final String DOWNLOAD_APK_NAME = "test_upgrade.apk";
    //public static final String DOWN_URL = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Environment.DIRECTORY_DOWNLOADS + File.separator + DOWNLOAD_APK_NAME;

    public static void download(Context context, String url, String title) {
        long downloadId = requestDownloadId(context);
        if (downloadId != -1L) {
            FileDownloadManager fdm = FileDownloadManager.getInstance(context);
            int status = fdm.getDownloadStatus(downloadId);
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                String path = fdm.getDownloadPath(downloadId);
                if (!TextUtils.isEmpty(path)) {
                    if (compareApkInfo(context, path)) {
                        startInstall(context);
                        CLog.d("开始安装APK.....");
                        return;
                    } else {
                        fdm.getDownloadManager().remove(downloadId);
                    }
                }
                CLog.d("开始下载2.....");
                start(context, url, title);
            } else if (status == DownloadManager.STATUS_FAILED) {
                CLog.d("开始下载3.....");
                start(context, url, title);
            } else {
                CLog.d("其他下载情况.....");
            }
        } else {
            start(context, url, title);
        }
    }

    public static void startInstall(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        context.sendBroadcast(intent);
    }

    public static String requestAPKUri(Context context) {
        long downloadId = requestDownloadId(context);
        if (downloadId != -1L) {
            FileDownloadManager fdm = FileDownloadManager.getInstance(context);
            int status = fdm.getDownloadStatus(downloadId);
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                String path = fdm.getDownloadPath(downloadId);
                if (!TextUtils.isEmpty(path)) {
                    if (compareApkInfo(context, path)) {
                        return path;
                    }
                    CLog.e("compareApkInfo fail!");
                } else {
                    CLog.e("getDownloadUri fail!");
                }
            } else {
                CLog.e("download fail!");
            }
        }
        return null;
    }

    private static long requestDownloadId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getLong(DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
    }

    public static boolean compareApkInfo(Context context, String apkPath) {
        return compare(getApkInfo(context, apkPath), context);
    }

    private static boolean start(Context context, String url, String title) {
        long id = FileDownloadManager.getInstance(context).startDownload(url, title, "下载完成后点击打开");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Log.e(TAG, "apk start download" + id);
        return sharedPreferences.edit().putLong(DownloadManager.EXTRA_DOWNLOAD_ID, id).commit();
    }

    public static PackageInfo getApkInfo(Context context, String path) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            CLog.e("info = " + info.versionCode);
            return info;
        } else {
            CLog.e("path = " + path);
            CLog.e("info fail!");
        }
        return null;
    }

    private static boolean compare(PackageInfo apkInfo, Context context) {
        if (apkInfo == null) {
            return false;
        }
        String localPackage = context.getPackageName();
        if (apkInfo.packageName.equals(localPackage)) {
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(localPackage, 0);
                CLog.e(apkInfo.versionCode + " -- " + packageInfo.versionCode);
                if (apkInfo.versionCode > packageInfo.versionCode) {
                    return true;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
