package update.lb.cn.updateapk.bean;

/**
 * Created by lb on 2018/12/23.
 */

public class UpgradeRequestInfo {

    private String requestApkName;
    private String nativeTitle;

    public UpgradeRequestInfo() {
    }

    public UpgradeRequestInfo(String requestApkName, String nativeTitle) {
        this.requestApkName = requestApkName;
        this.nativeTitle = nativeTitle;
    }

    public String getRequestApkName() {
        return requestApkName;
    }

    public void setRequestApkName(String requestApkName) {
        this.requestApkName = requestApkName;
    }

    public String getNativeTitle() {
        return nativeTitle;
    }

    public void setNativeTitle(String nativeTitle) {
        this.nativeTitle = nativeTitle;
    }
}
