package update.lb.cn.updateapk.model;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import update.lb.cn.updateapk.bean.UpgradeResultInfo;

/**
 * Created by Administrator on 2018/1/30.
 */

public class UpdateAppServiceFactory {

    private static final String BASE_URL = "http://qiaoqiao.aptdev.cn";

    public static <T> T createServiceFrom(final Class<T> serviceClass) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //Log.e("TAG", retrofit.baseUrl().toString());
        return retrofit.create(serviceClass);
    }


    public static <T> T createServiceFrom2(final Class<T> serviceClass) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build();
        //Log.e("TAG", retrofit.baseUrl().toString());
        return retrofit.create(serviceClass);
    }

    public interface ResultVersionCallBack {
        void newVersion();
        void normal();
        void onError();
    }

}
