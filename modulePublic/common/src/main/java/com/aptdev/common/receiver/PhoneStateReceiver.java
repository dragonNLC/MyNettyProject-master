package com.aptdev.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by lb on 2019/1/22.
 */

public class PhoneStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent launcherIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            if (launcherIntent != null) {
                launcherIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(launcherIntent);
            }
        }
    }

}
