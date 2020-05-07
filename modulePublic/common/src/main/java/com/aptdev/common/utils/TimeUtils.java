package com.aptdev.common.utils;

import com.aptdev.common.bean.DateBean;

import java.util.Calendar;


/**
 * Created by Administrator on 2017/7/19.
 */

public class TimeUtils {

    public static DateBean formatCurData() {
        String mYear;
        String mMonth;
        String mDay;
        String mWay;
        String AM_PM;
        String mHour;
        String mMin;
        final Calendar c = Calendar.getInstance();

        /*System.setProperty("user.timezone","GMT+8");
        c.setTimeZone(TimeZone.getTimeZone("GMT+8"));*///如果当前时区不是东八区的话，一般都是按照系统设置的时区

        mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));//获取当前星期
        AM_PM = String.valueOf(c.get(Calendar.AM_PM));
        mHour = String.valueOf(c.get(Calendar.HOUR));
        mMin = String.valueOf(c.get(Calendar.MINUTE));
        if ("1".equals(mWay)) {
            mWay = "天";
        } else if ("2".equals(mWay)) {
            mWay = "一";
        } else if ("3".equals(mWay)) {
            mWay = "二";
        } else if ("4".equals(mWay)) {
            mWay = "三";
        } else if ("5".equals(mWay)) {
            mWay = "四";
        } else if ("6".equals(mWay)) {
            mWay = "五";
        } else if ("7".equals(mWay)) {
            mWay = "六";
        }

        DateBean dateBean = new DateBean();
        dateBean.setYear(mYear);
        dateBean.setMonth(mMonth);
        dateBean.setDay(mDay);
        dateBean.setWay(mWay);
        dateBean.setHour(mHour);

        if ("0".equals(AM_PM)) {
            dateBean.setAmPm("上午");
        } else {
            dateBean.setAmPm("下午");
        }
        dateBean.setHour(dateBean.getHour().length() <= 1 ? "0" + dateBean.getHour() : dateBean.getHour());
        dateBean.setMin(mMin.length() <= 1 ? "0" + mMin : mMin);
        dateBean.setMonth(dateBean.getMonth().length() <= 1 ? "0" + dateBean.getMonth() : dateBean.getMonth());
        dateBean.setDay(dateBean.getDay().length() <= 1 ? "0" + dateBean.getDay() : dateBean.getDay());
        return dateBean;
    }

}
