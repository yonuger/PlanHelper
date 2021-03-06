package com.young.planhelper.mvp.plan.view.planitem.seconditem;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.young.planhelper.R;
import com.young.planhelper.mvp.plan.model.bean.PlanOperationInfo;
import com.young.planhelper.mvp.plan.model.bean.PlanThirdItemInfo;
import com.young.planhelper.util.TimeUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author: young
 * email:1160415122@qq.com
 * date:16/11/27  20:49
 */


public class PlanRecordItemView extends LinearLayout{

    @BindView(R.id.tv_plan_record_title)
    TextView mTitleTv;

    @BindView(R.id.iv_plan_record)
    ImageView mIv;

    private PlanOperationInfo mData;

    public PlanRecordItemView(Context context) {
        super(context);
    }

    public PlanRecordItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlanRecordItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void setData(PlanOperationInfo data) {
        this.mData = data;
        mTitleTv.setText(TimeUtil.getTime2(data.getPlanOperationInfoId()) + " " + data.getName() + ": "+ data.getContent());
        switch ( data.getType() ){
            case PlanOperationInfo.CREATE:
                mIv.setImageResource(R.mipmap.ic_record_create);
                break;
            case PlanOperationInfo.MODIFY_TEXT:
                mIv.setImageResource(R.mipmap.ic_record_text);
                break;
            case PlanOperationInfo.MODIFY_TIME:
                mIv.setImageResource(R.mipmap.ic_record_date);
                break;
            case PlanOperationInfo.FINISH:
                mIv.setImageResource(R.mipmap.ic_record_finish);
                break;
            case PlanOperationInfo.RESTART:
                mIv.setImageResource(R.mipmap.ic_record_restart);
                break;
        }
    }

    public long getPlanSecondItenInfoId(){
        return mData.getPlanSecondItemInfoId();
    }

}
