package update.lb.cn.updateapk.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import rx.Observable;
import update.lb.cn.updateapk.bean.UpgradeResultInfo;

/**
 * Created by Administrator on 2018/1/30.
 */

public interface UpdateAppService {
    //http://qiaoqiao.aptdev.cn/APP/aihome.apk
    //http://qiaoqiao.aptdev.cn/handler/version.ashx?type=qiaoqiao
    @Headers({"Connection:close"})
    @GET("/handler/appVersion.ashx")
    Observable<UpgradeResultInfo> getUpdateInfo(@Query("type") String name);


    @Headers({"Connection:close"})
    @GET("/CustomWeb01/UpdateApkInfo.jsp")
    Call<ResponseBody> getUpdateInfo2(@Query("appname") String name, @Query("serverVersion") String appVersion);
}
