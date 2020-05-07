package com.aptdev.common.contracts;

public interface CallBackListener<T> {

    void onSuccess(T data);

    void onFail(String errorInfo);

    void onCompleted();
}
