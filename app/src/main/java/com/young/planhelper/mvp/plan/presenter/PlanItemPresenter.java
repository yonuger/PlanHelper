package com.young.planhelper.mvp.plan.presenter;

import android.content.Context;

import com.young.planhelper.application.AppApplication;
import com.young.planhelper.mvp.base.model.IBiz;
import com.young.planhelper.mvp.base.presenter.Presenter;
import com.young.planhelper.mvp.base.view.IView;
import com.young.planhelper.mvp.plan.model.bean.PlanInfo;
import com.young.planhelper.mvp.plan.model.bean.PlanItemInfo;
import com.young.planhelper.mvp.plan.model.biz.IPlanBiz;
import com.young.planhelper.mvp.plan.model.biz.PlanBiz;
import com.young.planhelper.network.plan.PlanItemListApiService;
import com.young.planhelper.network.plan.PlanListApiService;

import java.util.List;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * @author: young
 * email:1160415122@qq.com
 * date:16/11/23  21:38
 */


public class PlanItemPresenter extends Presenter implements IPlanItemPresenter{

    private IPlanBiz mBiz;

    private Retrofit mRetrofit;

    public PlanItemPresenter(IView view, Context context) {
        super(view, context);
        mBiz = new PlanBiz(context);
        mRetrofit = ((AppApplication)context.getApplicationContext()).getmAppComponent().getRetrofit();
    }

    @Override
    public void initData() {

    }

    @Override
    public void getPlanItemInfo(long planInfoId, IBiz.ICallback callback) {
        mBiz.getPlanItemInfo(planInfoId, callback);
    }

    @Override
    public void getPlanItemInfoByNetWork(long planInfoId, IBiz.ICallback callback) {
        PlanItemListApiService planItemListApiService = mRetrofit.create(PlanItemListApiService.class);

        Observable<List<PlanItemInfo>> data = planItemListApiService.getPlanItemInfoList(planInfoId);

        callback.onResult(data);
    }
}
