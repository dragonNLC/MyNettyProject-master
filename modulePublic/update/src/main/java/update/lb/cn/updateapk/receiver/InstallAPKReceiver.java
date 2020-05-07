package update.lb.cn.updateapk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;


/**
 * Created by Administrator on 2018/1/31.
 */

public class InstallAPKReceiver extends BroadcastReceiver {

    private static final boolean DEBUG = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction()) && !DEBUG) {
            Uri data = intent.getData();
            if (data != null && context.getPackageName().equals(data.getEncodedSchemeSpecificPart())) {
                Toast.makeText(context, "更新安装成功", Toast.LENGTH_LONG).show();
                // 重新启动APP
                Intent intentToStart = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                if (intentToStart != null) {
                    intentToStart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intentToStart);
                }
            }
        }
    }

}
