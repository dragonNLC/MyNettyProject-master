package com.aptdev.framework.test.bean;

/**
 * @ClassName ResultBean
 * @Description socket回送数据内容
 * @SysUser Administrator
 * @Author dragon
 * @Date 2020-05-09 18:09
 * @Version 1.0
 */
public class ResultBean<T> {

    public static final int RESULT_OK = 200;
    public static final int RESULT_ERROR = 400;

    private int code;
    private String error;
    private T data;

    public ResultBean() {
    }

    public ResultBean(int code, String error, T data) {
        this.code = code;
        this.error = error;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
