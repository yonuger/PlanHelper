package com.young.planhelper.mvp.schedule.view.backlogview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.young.planhelper.R;
import com.young.planhelper.mvp.schedule.model.bean.BacklogInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: young
 * email:1160415122@qq.com
 * date:16/10/5  21:33
 */


public class BacklogAdapter extends BaseAdapter{

    private List<BacklogInfo> mDatas;
    private Context mContext;
    private OnItemClickListener onItemClickListener;


    public BacklogAdapter(Context context, List<BacklogInfo> datas) {
        this.mContext = context;
        if( datas == null )
            mDatas = new ArrayList<>();
        else
            this.mDatas = datas;
    }


    public void setDatas(List<BacklogInfo> data) {
        this.mDatas = data;
    }

    @Override
    public int getCount() {
        if( mDatas != null )
            return mDatas.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        if( mDatas != null && mDatas.size() > 0 )
            return mDatas.get(position);
        else
            return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if( convertView == null ){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.view_backlog_item, parent, false);
        }
        BacklogItemView backlogItemView = (BacklogItemView)convertView;
        backlogItemView.setData(mDatas.get(position));

        backlogItemView.setOnClickListener(v -> {
            onItemClickListener.onItemClick(position);
        });

        return backlogItemView;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
