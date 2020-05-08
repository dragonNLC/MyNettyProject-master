package com.aptdev.framework.test.nioworkthread;

/**
 * @ClassName ClientStatusListener
 * @Description TODO
 * @SysUser Administrator
 * @Author dragon
 * @Date 2020-05-08 14:46
 * @Version 1.0
 */
public interface ClientStatusListener {

    void onClose(String ip);

}
