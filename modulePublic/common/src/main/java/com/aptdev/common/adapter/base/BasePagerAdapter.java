package com.aptdev.common.adapter.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.dragondevl.clog.CLog;

import java.util.List;


/**
 * Created by Administrator on 2017/4/18.
 */

public abstract class BasePagerAdapter<T> extends PagerAdapter {

    protected Context mContext;
    protected List<T> mList;

    private OnPageItemClickListener onPageItemClickListener;

    public BasePagerAdapter(Context mContext, List<T> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    public void add(List<T> dataList) {
        mList.addAll(dataList);
        CLog.i(mList.size() + "");
        notifyDataSetChanged();
    }

    public void add(T file) {
        mList.add(file);
        notifyDataSetChanged();
    }

    public void refresh(List<T> list) {
        mList.clear();
        mList.addAll(list);
        CLog.i(mList.size() + "");
        notifyDataSetChanged();
    }

    public List<T> getDataSet() {
        return mList;
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    public OnPageItemClickListener getOnPageItemClickListener() {
        return onPageItemClickListener;
    }

    public void setOnPageItemClickListener(OnPageItemClickListener onPageItemClickListener) {
        this.onPageItemClickListener = onPageItemClickListener;
    }

    public interface OnPageItemClickListener<T> {
        void onItemClick(int position, T date);
    }

}
