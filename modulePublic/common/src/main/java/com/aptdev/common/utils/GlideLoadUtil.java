package com.aptdev.common.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.aptdev.common.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;


/**
 * Created by lb on 2018/10/25.
 */

public class GlideLoadUtil {

    public static void loadImage(Context context, Object path, ImageView imageView) {
        loadImage(context, null, null, path, imageView, false);
    }

    public static void loadImage(Activity activity, Object path, ImageView imageView) {
        loadImage(null, activity, null, path, imageView, false);
    }

    public static void loadImage(Fragment fragment, Object path, ImageView imageView) {
        loadImage(null, null, fragment, path, imageView, false);
    }

    public static void loadImageHasDiskCache(Context context, Object path, ImageView imageView) {
        loadImage(context, null, null, path, imageView, true);
    }

    public static void loadImageHasDiskCache(Activity activity, Object path, ImageView imageView) {
        loadImage(null, activity, null, path, imageView, true);
    }

    public static void loadImageHasDiskCache(Fragment fragment, Object path, ImageView imageView) {
        loadImage(null, null, fragment, path, imageView, true);
    }

    public static void loadRoundImage(Context mContext, Object path, ImageView imageView) {
        loadRoundImage(mContext, null, null, path, imageView);
    }

    public static void loadRoundImage(Activity activity, Object path, ImageView imageView) {
        loadRoundImage(null, activity, null, path, imageView);
    }

    public static void loadRoundImage(Fragment fragment, Object path, ImageView imageView) {
        loadRoundImage(null, null, fragment, path, imageView);
    }

    private static void loadRoundImage(Context context, Activity activity, Fragment fragment, Object object, ImageView imageView) {
        try {
            if (context != null) {
                Glide.with(context).load(object).apply(new RequestOptions().error(R.mipmap.ic_logo).transform(new GlideRoundTransform(context)).dontAnimate().diskCacheStrategy(DiskCacheStrategy.NONE)).into(imageView);
            } else if (activity != null) {
                Glide.with(activity).load(object).apply(new RequestOptions().error(R.mipmap.ic_logo).transform(new GlideRoundTransform(activity)).dontAnimate().diskCacheStrategy(DiskCacheStrategy.NONE)).into(imageView);
            } else {
                Glide.with(fragment).load(object).apply(new RequestOptions().error(R.mipmap.ic_logo).transform(new GlideRoundTransform(null)).dontAnimate().diskCacheStrategy(DiskCacheStrategy.NONE)).into(imageView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadImage(Context context, Activity activity, Fragment fragment, Object object, ImageView imageView, boolean cache) {
        try {
            if (context != null) {
                if (cache) {
                    Glide.with(context).load(object).apply(new RequestOptions().dontAnimate()).into(imageView);
                } else {
                    Glide.with(context).load(object).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).dontAnimate()).into(imageView);
                }
            } else if (activity != null) {
                if (cache) {
                    Glide.with(activity).load(object).apply(new RequestOptions().dontAnimate()).into(imageView);
                } else {
                    Glide.with(activity).load(object).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).dontAnimate()).into(imageView);
                }
            } else {
                if (cache) {
                    Glide.with(fragment).load(object).apply(new RequestOptions().dontAnimate()).into(imageView);
                } else {
                    Glide.with(fragment).load(object).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).dontAnimate()).into(imageView);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
