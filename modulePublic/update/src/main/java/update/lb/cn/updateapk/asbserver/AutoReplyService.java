package update.lb.cn.updateapk.asbserver;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.dragondevl.clog.CLog;

import java.util.List;

/**
 * Created by lb on 2018/12/22.
 */

public class AutoReplyService extends AccessibilityService {

    public static final int TYPE_KILL_APP = 1;
    public static final int TYPE_INSTALL_APP = 2;
    public static final int TYPE_UNINSTALL_APP = 3;

    public static int INVOKE_TYPE = TYPE_INSTALL_APP;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        this.processAccessibilityEvent(event);
    }

    @Override
    public void onInterrupt() {

    }

    public static void reset() {
        INVOKE_TYPE = TYPE_INSTALL_APP;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void processAccessibilityEvent(AccessibilityEvent event) {
        if (event.getSource() == null) {
            CLog.i("the source = null！");
        } else {
            CLog.i("event = " + event.toString());
            switch (INVOKE_TYPE) {
                case TYPE_KILL_APP:
                    processKillApp(event);
                    break;
                case TYPE_INSTALL_APP:
                    processInstallApp(event);
                    break;
                case TYPE_UNINSTALL_APP:
                    processUninstallApp(event);
                    break;
            }
        }
    }

    private void processUninstallApp(AccessibilityEvent event) {
        if (event.getSource() != null) {
            if ("com.android.packageinstaller".equals(event.getPackageName())) {
                List<AccessibilityNodeInfo> okNodes = event.getSource().findAccessibilityNodeInfosByText("确定");
                if (okNodes != null && !okNodes.isEmpty()) {
                    AccessibilityNodeInfo nodeInfo;
                    for (int i = 0; i < okNodes.size(); i++) {
                        nodeInfo = okNodes.get(i);
                        if ("android.widget.Button".equals(nodeInfo.getClassName()) && nodeInfo.isEnabled()) {
                            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }
            }
        }
    }

    private void processInstallApp(AccessibilityEvent event) {
        if (event.getSource() != null) {
            if ("com.android.packageinstaller".equals(event.getPackageName())) {
                List<AccessibilityNodeInfo> uninstallNodes = event.getSource().findAccessibilityNodeInfosByText("安装");
                if (uninstallNodes != null && !uninstallNodes.isEmpty()) {
                    CLog.e("安装 != null!");
                    AccessibilityNodeInfo nodeInfo;
                    for (int i = 0; i < uninstallNodes.size(); i++) {
                        nodeInfo = uninstallNodes.get(i);
                        if ("android.widget.Button".equals(nodeInfo.getClassName()) && nodeInfo.isEnabled()) {
                            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                } else {
                    CLog.e("安装 == null!");
                }
                List<AccessibilityNodeInfo> nextNodes = event.getSource().findAccessibilityNodeInfosByText("下一步");
                if (nextNodes != null && !nextNodes.isEmpty()) {
                    CLog.e("下一步 != null!");
                    AccessibilityNodeInfo nodeInfo;
                    for (int i = 0; i < nextNodes.size(); i++) {
                        nodeInfo = nextNodes.get(i);
                        if ("android.widget.Button".equals(nodeInfo.getClassName()) && nodeInfo.isEnabled()) {
                            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                } else {
                    CLog.e("下一步 == null!");
                }
                List<AccessibilityNodeInfo> okNodes = event.getSource().findAccessibilityNodeInfosByText("打开");
                if (okNodes != null && !okNodes.isEmpty()) {
                    CLog.e("打开 != null!");
                    AccessibilityNodeInfo nodeInfo;
                    for (int i = 0; i < okNodes.size(); i++) {
                        nodeInfo = okNodes.get(i);
                        if ("android.widget.Button".equals(nodeInfo.getClassName()) && nodeInfo.isEnabled()) {
                            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                } else {
                    CLog.e("打开 == null!");
                }
            }
        }
    }

    private void processKillApp(AccessibilityEvent event) {
        if (event.getSource() != null) {
            if ("com.android.settings".equals(event.getPackageName())) {
                List<AccessibilityNodeInfo> stopNodes = event.getSource().findAccessibilityNodeInfosByText("强行停止");
                if (stopNodes != null && !stopNodes.isEmpty()) {
                    AccessibilityNodeInfo node;
                    for (int i = 0; i < stopNodes.size(); i++) {
                        node = stopNodes.get(i);
                        if (node.getClassName().equals("android.widget.Button")) {
                            if (node.isEnabled()) {
                                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            }
                        }
                    }
                }

                List<AccessibilityNodeInfo> okNodes = event.getSource().findAccessibilityNodeInfosByText("确定");
                if (okNodes != null && !okNodes.isEmpty()) {
                    AccessibilityNodeInfo node;
                    for (int i = 0; i < okNodes.size(); i++) {
                        node = okNodes.get(i);
                        if (node.getClassName().equals("android.widget.Button")) {
                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            CLog.i("click ok");
                        }
                    }

                }
            }
        }
    }

}
