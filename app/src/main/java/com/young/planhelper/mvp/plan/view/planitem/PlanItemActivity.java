package com.young.planhelper.mvp.plan.view.planitem;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.young.planhelper.R;
import com.young.planhelper.constant.AppStatic;
import com.young.planhelper.mvp.base.BaseActivity;
import com.young.planhelper.mvp.base.view.IView;
import com.young.planhelper.mvp.plan.model.bean.PlanItemInfo;
import com.young.planhelper.mvp.plan.presenter.IPlanItemPresenter;
import com.young.planhelper.mvp.plan.presenter.PlanItemPresenter;
import com.young.planhelper.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PlanItemActivity extends BaseActivity implements IView{

    @BindView(R.id.vp_plan_detail)
    ViewPager mViewPager;

    private long planInfoId;

    private Adapter adapter;

    private IPlanItemPresenter presenter;

    private boolean isActive;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x001:

                    adapter = null;

                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    List<Fragment> fragments = fm.getFragments();
                    if(fragments != null && fragments.size() >0){
                        int len = fragments.size();
                        for (int i = 0; i < len; i++) {
                            ft.remove(fragments.get(i));
                        }
                    }
                    ft.commit();


                    adapter = new Adapter(fm);

                    mViewPager.setAdapter(adapter);

                    presenter.getPlanItemInfo(planInfoId, data -> setData(data));

                    break;
            }
        }
    };
    private boolean isAfterAdd;

    @Override
    protected void initUI() {

        setStatueBarColor();

        mToolbar.setMode(Toolbar.BACK);

        mToolbar.setOnMenuClickListener( () -> finish() );

        presenter = new PlanItemPresenter(this, this);

        isAfterAdd = getIntent().getBooleanExtra("add", false);

        isActive = getIntent().getBooleanExtra("isActive", false);

        planInfoId = getIntent().getLongExtra("planInfoId", 0);

        mToolbar.setTitle(getIntent().getStringExtra("planInfoTitle"));

    }

    @Override
    protected void onResume() {
        super.onResume();

        adapter = null;

        adapter = new Adapter(getSupportFragmentManager());

        mViewPager.setAdapter(adapter);

        if (mViewPager.getAdapter() != null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            List<Fragment> fragments = fm.getFragments();
            if(fragments != null && fragments.size() >0){
                for (int i = 0; i < fragments.size(); i++) {
                    ft.remove(fragments.get(i));
                }
            }
            ft.commit();
        }

        if( !isActive )
            presenter.getPlanItemInfo(planInfoId, data -> setData(data));
        else {
            showProgress();
            presenter.getPlanItemInfoByNetWork(planInfoId, data -> setData(data));
        }

        mViewPager.setCurrentItem(AppStatic.planItemIndex);
    }

    @Override
    public int getLayout() {
        return R.layout.activity_plan_detail;
    }

    @Override
    public void setData(Object data) {

        if( !isActive )
            setList((List<PlanItemInfo>) data);
        else{

            Observable<List<PlanItemInfo>> planItemInfoList = (Observable<List<PlanItemInfo>>) data;
            planItemInfoList.observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<PlanItemInfo>>() {

                        @Override
                        public void onCompleted() {
                            Log.i("way", "onCompleted");
                            hideProgress();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.i("way", "onError" + e.toString());
                            hideProgress();
                        }

                        @Override
                        public void onNext(List<PlanItemInfo> s) {
                            Log.i("way", "onNext" + s.size());
                            hideProgress();
                            setList(s);
                        }
                    });

        }

    }

    public void setList(List<PlanItemInfo> planInfos) {
        try {

            if( planInfos != null && planInfos.size() != 0 ){

                int size = planInfos.size();
                for(int i=0; i<size; i++){
                    PlanItemFragment fragment = new PlanItemFragment(planInfos.get(i));
                    fragment.setHandler(handler);
                    fragment.setIsActive(isActive);
                    fragment.setOrder(i);
                    adapter.addFragment(fragment, planInfos.get(i).getTitle());
                }

            }

            PlanItemAddFragment fragment = new PlanItemAddFragment();
            fragment.setPlanInfoId(planInfoId);
            fragment.setPlanInfoTitle(getIntent().getStringExtra("planInfoTitle"));
            fragment.setIsActive(isActive);

            adapter.addFragment(fragment, "");

            adapter.notifyDataSetChanged();

            if( isAfterAdd )
                if( adapter.getCount() >= 2 )
                    mViewPager.setCurrentItem(adapter.getCount() - 2);

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    static class Adapter extends FragmentPagerAdapter {


        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        public void removeFragment(String title) {

            int index = 0;
            int size = mFragmentTitles.size();

            for( ; index < size; index++){
                String temp = mFragmentTitles.get(index);
                if( title.equals(temp) )
                    break;
            }

            mFragmentTitles.remove(index);
            mFragments.remove(index);

        }


        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }


        private int mChildCount = 0;

        @Override
        public void notifyDataSetChanged() {
            // 重写这个方法，取到子Fragment的数量，用于下面的判断，以执行多少次刷新
            mChildCount = getCount();
            super.notifyDataSetChanged();
        }
        @Override
        public int getItemPosition(Object object) {
            if ( mChildCount > 0) {
                // 这里利用判断执行若干次不缓存，刷新
                mChildCount --;
                // 返回这个是强制ViewPager不缓存，每次滑动都刷新视图
                return POSITION_NONE;
            }
            // 这个则是缓存不刷新视图
            return super.getItemPosition(object);}

    }

    @Override
    protected void onDestroy() {
        AppStatic.planItemIndex =  0;
        super.onDestroy();
    }
}
