package com.aptdev.framework.test.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by lb on 2019/2/22.
 */

public class GSonUtils {

    private static volatile GSonUtils sGSonUtils;

    private Gson mGSon;

    private GSonUtils() {
        mGSon = new Gson();
    }

    public static GSonUtils getInstance() {
        if (sGSonUtils == null) {
            synchronized (GSonUtils.class) {
                if (sGSonUtils == null) {
                    return sGSonUtils = new GSonUtils();
                }
            }
        }
        return sGSonUtils;
    }

    public <T> List<T> fromJson(String content) {
        Type type = new TypeToken<List<T>>() {
        }.getType();
        return mGSon.fromJson(content, type);
    }

    public <T> T fromJsonOrPrintException(String content, Class<T> clz) throws JsonSyntaxException {
        try {
            return mGSon.fromJson(content, clz);
        } catch (JsonSyntaxException je) {
            throw new JsonSyntaxException(je);
        }
    }

    public <T> T fromJson(String content, Class<T> clz) {
        try {
            return mGSon.fromJson(content, clz);
        } catch (JsonSyntaxException je) {//json语法错误
            je.printStackTrace();
        }
        return null;
    }

    public String toJson(Object clz) {
        return mGSon.toJson(clz);
    }

}
