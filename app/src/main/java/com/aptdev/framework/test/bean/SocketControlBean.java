package com.aptdev.framework.test.bean;

import com.google.gson.annotations.Expose;

/**
 * 发送给服务器的命令
 */
public class SocketControlBean {

    /**
     * dtype : 9
     * content : 192.168.0.111
     */

    public SocketControlBean() {
    }

    public SocketControlBean(int dtype, String content, long serialNumber) {
        this.dtype = dtype;
        this.content = content;
        this.serialNumber = serialNumber;
    }

    @Expose
    private int dtype;

    @Expose
    private String content;

    private long serialNumber;//使用时间戳作为流水号//（默认流水号16位，yyyymmddhhMMss(random2)）//不加入序列化

    public int getDtype() {
        return dtype;
    }

    public void setDtype(int dtype) {
        this.dtype = dtype;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(long serialNumber) {
        this.serialNumber = serialNumber;
    }
}
