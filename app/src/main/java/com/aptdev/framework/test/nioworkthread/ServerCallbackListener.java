package com.aptdev.framework.test.nioworkthread;


import com.aptdev.common.contracts.CallBackListener;

/**
 * @ClassName ServerCallbackListener
 * @Description TODO
 * @SysUser Administrator
 * @Author dragon
 * @Date 2020-05-08 9:07
 * @Version 1.0
 */
public interface ServerCallbackListener<T> extends CallBackListener<T> {

    void onStartSuccess();

    void onStartFail(String errorInfo);

    void onAlreadyStart();

    void onReceiverStop();

    void onReceiverStopFail(String errorInfo);

    void onClientConnect(String ip);

    void onClientDisconnect(String ip);

    void onClientSend(String content);

    void onSendToClientSuccess();

    void onSendToClientFail(String errorInfo);
}
