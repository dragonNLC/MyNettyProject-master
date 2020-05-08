package com.aptdev.framework.iface;

/**
 * @ClassName SimpleServerCallbackListener
 * @Description TODO
 * @SysUser Administrator
 * @Author dragon
 * @Date 2020-05-08 9:09
 * @Version 1.0
 */
public class SimpleServerCallbackListener<T> implements ServerCallbackListener<T> {

    @Override
    public void onStartSuccess() {

    }

    @Override
    public void onStartFail(String errorInfo) {

    }

    @Override
    public void onAlreadyStart() {

    }

    @Override
    public void onReceiverStop() {

    }

    @Override
    public void onReceiverStopFail(String errorInfo) {

    }

    @Override
    public void onClientConnect(String ip) {

    }

    @Override
    public void onClientDisconnect(String ip) {

    }

    @Override
    public void onClientSend(String content) {

    }

    @Override
    public void onSendToClientSuccess() {

    }

    @Override
    public void onSendToClientFail(String errorInfo) {

    }

    @Override
    public void onSuccess(T data) {

    }

    @Override
    public void onFail(String errorInfo) {

    }

    @Override
    public void onCompleted() {

    }

}
