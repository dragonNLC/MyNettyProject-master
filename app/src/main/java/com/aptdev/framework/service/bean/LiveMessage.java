package com.aptdev.framework.service.bean;

/**
 * @ClassName LiveMessage
 * @Description TODO
 * @SysUser Administrator
 * @Author dragon
 * @Date 2020-04-30 16:17
 * @Version 1.0
 */
public class LiveMessage {

    public static final byte TYPE_HEART = 0x01;
    public static final int TYPE_MESSAGE = 0x02;

    //消息类型
    private byte type;
    //消息长度
    private int length;
    //消息体
    private String content;

    public LiveMessage() {
    }

    public LiveMessage(byte type, int length, String content) {
        this.type = type;
        this.length = length;
        this.content = content;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
