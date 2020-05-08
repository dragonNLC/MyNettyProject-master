package com.aptdev.framework.test.constants;

/**
 * @ClassName ServerConstant
 * @Description TODO
 * @SysUser Administrator
 * @Author dragon
 * @Date 2020-05-07 8:54
 * @Version 1.0
 */
public class ServerConstant {

    public static final int SERVER_PORT = 6601;
    public static final String NATIVE_SERVER_ADDRESS = "127.0.0.1";

    public static final int HEART_PACK = 0x01;//心跳包
    public static final int SEND_CODE_2 = 0x02;//请求当前核销数据，所有人的
    public static final int SEND_CODE_3 = 0x03;//发送当前人员的核销数据到本地服务器

    public static final int REV_CODE_0 = 0x0;//保留
    public static final int REV_CODE_1 = 0x01;//保留
    public static final int REV_CODE_2 = 0x02;//返回当前所有人的核销数据
    public static final int REV_CODE_2_WAITING = 0x92;//返回所有人的核销数据获取结果，本地服务器还未同步完成，请等待
    public static final int REV_CODE_3 = 3;//返回当前人员的核销结果


}
