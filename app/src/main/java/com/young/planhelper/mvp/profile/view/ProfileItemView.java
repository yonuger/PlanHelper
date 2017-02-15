package com.young.planhelper.mvp.profile.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.young.planhelper.R;
import com.young.planhelper.mvp.schedule.model.bean.BacklogInfo;
import com.young.planhelper.util.TimeUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author: young
 * email:1160415122@qq.com
 * date:17/1/27  10:40
 */


public class ProfileItemView extends LinearLayout {

    @BindView(R.id.tv_profile_item_content)
    TextView mContentTv;

    @BindView(R.id.tv_profile_item_time)
    TextView mTimeTv;

    @BindView(R.id.tv_profile_item_location)
    TextView mLocationTv;

    private long backlogInfoId;

    public ProfileItemView(Context context) {
        super(context);
    }

    public ProfileItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProfileItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void setData(BacklogInfo data) {

        this.backlogInfoId = data.getBacklogInfoId();

        mContentTv.setText(data.getContent());
        mLocationTv.setText(data.getLocation());
//        mTimeTv.setText(TimeUtil.getTime2(data.getToTime()));
    }

    public long getBacklogInfoId() {
        return backlogInfoId;
    }
}
