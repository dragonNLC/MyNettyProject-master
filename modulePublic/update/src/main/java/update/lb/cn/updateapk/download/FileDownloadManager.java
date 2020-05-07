package update.lb.cn.updateapk.download;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import com.aptdev.common.utils.FileUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by Administrator on 2018/1/31.
 */

public class FileDownloadManager {

    private DownloadManager downloadManager;
    private Context context;
    private static volatile FileDownloadManager instance;

    public FileDownloadManager(Context context) {
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        this.context = context.getApplicationContext();
    }

    public static FileDownloadManager getInstance(Context context) {
        if (instance == null) {
            synchronized (FileDownloadManager.class) {
                if (instance == null) {
                    instance = new FileDownloadManager(context);
                }
            }
        }
        return instance;
    }

    public long startDownload(String uri, String title, String description) {
        File file = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        FileUtils.checkFile(file);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(uri));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, DownLoadApk.DOWNLOAD_APK_NAME);
        request.setTitle(title);
        request.setDescription(description);
        return downloadManager.enqueue(request);
    }

    public String getDownloadPath(long downId) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downId);
        Cursor cursor = downloadManager.query(query);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    String fileUrl = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI));
                    try {
                        URL url = new URL(fileUrl);
                        return url.getPath();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    public Uri getDownloadUri(long downloadUri) {
        return downloadManager.getUriForDownloadedFile(downloadUri);
    }

    public DownloadManager getDownloadManager() {
        return downloadManager;
    }

    public int getDownloadStatus(long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor cursor = downloadManager.query(query);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    return cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
                }
            } finally {
                cursor.close();
            }
        }
        return -1;
    }



}
