package update.lb.cn.updateapk.retrofit;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;

import com.dragondevl.clog.CLog;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import update.lb.cn.updateapk.bean.UpgradeRequestInfo;
import update.lb.cn.updateapk.bean.UpgradeResultInfo;

/**
 * Created by lb on 2018/12/23.
 */

public class ApiServiceFactory {

    public static final String REQUEST_NEW_VERSION = "request_new_version";
    private static final String BASE_URL = "http://qiaoqiao.aptdev.cn";

    public static void coverNewRequest(Context context, String requestType, UpgradeRequestInfo upgradeRequestInfo, ResultVersionCallBack resultVersionCallBack) {
        switch (requestType) {
            case REQUEST_NEW_VERSION:
                requestNewVersion(context, upgradeRequestInfo, resultVersionCallBack);
                break;
        }
    }

    public static <T> T createServiceFrom(final Class<T> serviceClass) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(serviceClass);
    }


    private static void requestNewVersion(final Context context, final UpgradeRequestInfo upgradeRequestInfo, final ResultVersionCallBack resultVersionCallBack) {
        final Handler handler = new Handler(Looper.getMainLooper());
        UpdateAppService appService = createServiceFrom(UpdateAppService.class);
        appService.getUpdateInfo(upgradeRequestInfo.getRequestApkName())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UpgradeResultInfo>() {
                    @Override
                    public void onCompleted() {
                        CLog.i("Retrofit onCompleted！");
                    }

                    @Override
                    public void onError(Throwable e) {
                        CLog.i("Retrofit onError！");
                        e.printStackTrace();
                        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
                            if (resultVersionCallBack != null) {
                                resultVersionCallBack.normal();
                                resultVersionCallBack.onError();
                            }
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (resultVersionCallBack != null) {
                                        resultVersionCallBack.normal();
                                        resultVersionCallBack.onError();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onNext(UpgradeResultInfo updateAppInfo) {
                        CLog.i("Retrofit onNext！");
                        if (context == null) return;
                        try {
                            if (Integer.valueOf(updateAppInfo.getVersion()) > context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode) {
                                if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
                                    if (resultVersionCallBack != null) {
                                        resultVersionCallBack.newVersion();
                                    }
                                    //DownLoadApk.download(context, DOWNLOAD_URL, upgradeRequestInfo.getNativeTitle() + "下载更新");
                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (resultVersionCallBack != null) {
                                                resultVersionCallBack.newVersion();
                                            }
                                            //DownLoadApk.download(context, DOWNLOAD_URL, upgradeRequestInfo.getNativeTitle() + "下载更新");
                                        }
                                    });
                                }
                            } else {
                                if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
                                    if (resultVersionCallBack != null) {
                                        resultVersionCallBack.normal();
                                    }
                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (resultVersionCallBack != null) {
                                                resultVersionCallBack.normal();
                                            }
                                        }
                                    });
                                }
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                            if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
                                if (resultVersionCallBack != null) {
                                    resultVersionCallBack.normal();
                                    resultVersionCallBack.onError();
                                }
                            } else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (resultVersionCallBack != null) {
                                            resultVersionCallBack.normal();
                                            resultVersionCallBack.onError();
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
    }

    public interface ResultVersionCallBack {
        void newVersion();

        void normal();

        void onError();
    }

}
