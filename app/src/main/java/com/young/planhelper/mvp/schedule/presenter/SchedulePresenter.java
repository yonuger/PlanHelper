package com.young.planhelper.mvp.schedule.presenter;

import android.content.Context;

import com.young.planhelper.application.AppApplication;
import com.young.planhelper.mvp.base.model.IBiz;
import com.young.planhelper.mvp.base.presenter.Presenter;
import com.young.planhelper.mvp.base.view.IView;
import com.young.planhelper.mvp.schedule.model.bean.BacklogInfo;
import com.young.planhelper.mvp.schedule.model.biz.IScheduleBiz;
import com.young.planhelper.mvp.schedule.model.biz.ScheduleBIz;

import java.util.List;

import io.realm.Realm;

/**
 * @author: young
 * email:1160415122@qq.com
 * date:16/11/20  12:30
 */


public class SchedulePresenter extends Presenter implements ISchedulePresenter{

    private IScheduleBiz mBiz;

    public SchedulePresenter(IView view, Context context) {
        super(view, context);
        this.mBiz = new ScheduleBIz(context);
    }

    @Override
    public void initData() {

    }

    @Override
    public void getBacklogInfos(IBiz.ICallback callback) {
        mBiz.getBacklogInfo(callback);
    }

    @Override
    public void getBacklogInfos(String date, IBiz.ICallback callback) {
        mBiz.getBackLogInfoByday(date, callback);
    }

    @Override
    public void getBackLogInfoFuture(IBiz.ICallback callback) {
        mBiz.getBackLogInfoFuture(callback);
    }

    @Override
    public void getBackLogInfoOverdue(IBiz.ICallback callback) {
        mBiz.getBackLogInfoOverdue(callback);
    }

    @Override
    public void queryByMonth(String timeBegin, String timeEnd, IBiz.ICallback callback) {
        mBiz.queryByMonth(timeBegin, timeEnd, callback);
    }

    @Override
    public void getBacklogDetailById(long backlogInfoId, IBiz.ICallback callback) {
        mBiz.getBackLogInfoDetail(backlogInfoId, callback);
    }

    @Override
    public void modifyBacklogInfo(BacklogInfo mBacklogInfo, BacklogInfo backlogInfo, IBiz.ICallback callback) {
        mBiz.modifyBacklogInfo(mBacklogInfo, backlogInfo, callback);
    }

    @Override
    public void deleteBacklog(BacklogInfo mBacklogInfo, IBiz.ICallback callback) {
        mBiz.deleteBacklog(mBacklogInfo, callback);
    }

    @Override
    public void finishBacklogInfo(BacklogInfo mBacklogInfo, IBiz.ICallback callback) {
        mBiz.finishBacklogInfo(mBacklogInfo, callback);
    }

    @Override
    public void initBacklogInfo(IBiz.ICallback callback) {
        mBiz.initBacklogInfo(callback);
    }

    @Override
    public void getBacklogInfoNeed(String date, IBiz.ICallback callback) {
        mBiz.getBacklogInfoNeed(date, callback);
    }
}
