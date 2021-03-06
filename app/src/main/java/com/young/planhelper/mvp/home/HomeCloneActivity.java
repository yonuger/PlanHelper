package com.young.planhelper.mvp.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nineoldandroids.animation.ValueAnimator;
import com.young.planhelper.R;
import com.young.planhelper.application.AppApplication;
import com.young.planhelper.constant.AppConstant;
import com.young.planhelper.mvp.base.BaseFragmentActivity;
import com.young.planhelper.mvp.base.model.IBiz;
import com.young.planhelper.mvp.common.service.TaskTimeService;
import com.young.planhelper.mvp.login.model.bean.User;
import com.young.planhelper.mvp.home.view.monthview.MonthView;
import com.young.planhelper.mvp.home.view.weekview.WeekItemView;
import com.young.planhelper.mvp.schedule.view.ScheduleAddCloneActivity;
import com.young.planhelper.mvp.schedule.model.bean.BacklogInfo;
import com.young.planhelper.mvp.schedule.model.bean.DayInfo;
import com.young.planhelper.mvp.schedule.presenter.ISchedulePresenter;
import com.young.planhelper.mvp.schedule.presenter.SchedulePresenter;
import com.young.planhelper.mvp.schedule.view.ScheduleDetailCloneActivity;
import com.young.planhelper.mvp.schedule.view.backlogview.BacklogAdapter;
import com.young.planhelper.util.CalendarUtil;
import com.young.planhelper.util.DateUtil;
import com.young.planhelper.util.LogUtil;
import com.young.planhelper.util.SharePreferenceUtil;
import com.young.planhelper.util.TimeUtil;
import com.young.planhelper.widget.NestListView;
import com.young.planhelper.widget.Toolbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import butterknife.BindView;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.DatePicker;

public class HomeCloneActivity extends BaseFragmentActivity{



    @BindView(R.id.wv_view_item)
    WeekItemView mWeekItemView;

    @BindView(R.id.monthview)
    MonthView mMonthView;

    @BindView(R.id.iv_home_calendar)
    ImageView mCalendarIv;

    @BindView(R.id.tv_home_calendar)
    TextView mCalendarTv;

    @BindView(R.id.tv_home_task_need)
    TextView mNeedTv;

    @BindView(R.id.lv_home_need)
    NestListView mNeedLv;

    @BindView(R.id.tv_home_task_today)
    TextView mTodayTv;

    @BindView(R.id.lv_home_task)
    NestListView mTaskLv;

    @BindView(R.id.tv_home_task_overdue)
    TextView mOverdueTv;

    @BindView(R.id.lv_home_overdue)
    NestListView mOverdueLv;

    @BindView(R.id.rl_home_task_overdue)
    RelativeLayout mOverdueRl;

    @BindView(R.id.tv_home_task_skip)
    TextView mOverdueSkipTv;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.sv_home_task)
    ScrollView mTaskSv;

    @BindView(R.id.ll_home_calendar_bg)
    LinearLayout mCalendarBgLl;

    ISchedulePresenter presenter;

    private BacklogAdapter mNeedAdapter;

    private BacklogAdapter mTaskAdapter;

    private BacklogAdapter mOverdueAdapter;

//    private BacklogAdapter mTaskAdapter;

    private int calendarHeight = 810;
    private List<String> timeList;

    /**
     * 所选日期
     */
    private String date;

    /**
     * 服务进程
     */
    private Intent serviceIntent;

    /**
     * 借用主题函数来启动服务进程
     */
    @Override
    protected void setTheme() {
        super.setTheme();
        serviceIntent = new Intent(this, TaskTimeService.class);
        startService(serviceIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(serviceIntent);
    }

    @Override
    protected void initUI() {

        presenter = new SchedulePresenter(this, this);

        presenter.initBacklogInfo(data -> {});

        mToolbar.setOnDateClickListener( () -> {
            showMonPicker();
        } );


        mCalendarTv.setText(TimeUtil.getDay());


        String currentTime = TimeUtil.getCurrentDateInString1();
        StringTokenizer tokenizer = new StringTokenizer(currentTime, "-");
        String year = tokenizer.nextToken();
        String month = tokenizer.nextToken();

        mToolbar.setTitle(year + "年" + month + "月");

        initMonthView(year, month);

        String date = mToolbar.getDate();
        String monthTemp = date.substring(5, date.length());
        year = date.substring(0, 4);
        month = monthTemp.substring(0, monthTemp.length()-1);
        int monthValue = Integer.parseInt(month);
        int yearValue = Integer.parseInt(year);
        //上个月23号到下个月的6号就行了

        IBiz.ICallback iCallback = data -> {
            try {
                List<String> time = (List<String>) data;

                this.timeList = time;

                initWeekView(null);

                mMonthView.setTaskDay(time);

            }catch (Exception e){
                Toast.makeText(this, (String) data, Toast.LENGTH_SHORT).show();
            }
        };

        if( monthValue == 12 )
            presenter.queryByMonth( year + "年11月23日 00:00",
                    (yearValue+1) + "年1月6日 23:59", iCallback);
        else if( monthValue == 1 )
            presenter.queryByMonth( (yearValue-1) + "年12月23日 00:00",
                    yearValue + "年2月6日 23:59", iCallback);
        else
            presenter.queryByMonth( year + "年"+ (monthValue-1) + "月23日 00:00",
                    year + "年"+ (monthValue+1) +"月6日 23:59", iCallback);

        mToolbar.setOnRightClickListener( () -> {
            startActivity(new Intent(this, ScheduleAddCloneActivity.class));
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharePreferenceUtil sharePreferenceUtil = new SharePreferenceUtil(this);

        User user = sharePreferenceUtil.getUserInfo();

        AppApplication.get(this).getmAppComponent().getUserInfo().copyWith(user);

        if( !user.getIconUrl().equals("") ){
            Glide.with(this)
                    .load(AppConstant.RECOUSE_IMAGE_URL + user.getIconUrl())
                    .into(itemIcon.getIconView());
        }

        initUI();

        setListData();


    }

    private void showMonPicker() {
        String time = TimeUtil.getCurrentDateInString1();
        StringTokenizer tokenizer = new StringTokenizer(time, "-");
        int year = Integer.parseInt(tokenizer.nextToken());
        int month = Integer.parseInt(tokenizer.nextToken());
        int day = Integer.parseInt(tokenizer.nextToken());

        DatePicker picker = new DatePicker(this, DatePicker.YEAR_MONTH);
        picker.setRange(1910, 2050);
        picker.setSelectedItem(year, month);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthPickListener() {
            @Override
            public void onDatePicked(String year, String month) {
                mToolbar.setTitle(year + "年" + month + "月");
                mTaskAdapter.setDatas(null);
                mTaskAdapter.notifyDataSetChanged();
                mOverdueAdapter.setDatas(null);
                mOverdueAdapter.notifyDataSetChanged();
                initMonthView(year, month);
                initWeekView(new DayInfo(year + "-" + month + "-" + day, day+"", "", ""));
            }
        });
        picker.show();
    }


    /**
     * 初始化月视图
     */
    private void initMonthView(String year, String month) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        int dayOfWeek = 0;
        int lastMonthCount = 0;
        int monthCount = 0;

        try {
            String time = "";

            int yearValue = Integer.parseInt(year);
            int monthValue = Integer.parseInt(month);

            time = year + "-" + (monthValue-1) + "-" + 1;
            String lastMonth = year + "-" + monthValue + "-" + 1;

            monthCount = DateUtil.getDaysOfMonth(sdf.parse(time));

            dayOfWeek = CalendarUtil.dayOfWeek(yearValue, monthValue, 0);

            lastMonthCount = DateUtil.getDaysOfMonth(sdf.parse(lastMonth));


        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<DayInfo> monthInfos = new ArrayList<>();

        //先添加进上个月到这个月的补位
        int j=0;
        for(int i=dayOfWeek; i > 0 ; i--){
            String week = getWeek(j);
            j++;
            int monthValue = Integer.parseInt(month);
            String date = "";
            if( monthValue > 9 )
                date = year + "-" + (monthValue-1) + "-" + (lastMonthCount - i + 1);
            else
                date = year + "-0" + (monthValue-1) + "-" + (lastMonthCount - i + 1);

            DayInfo dayInfo = new DayInfo(date, "", week, "");
            monthInfos.add(dayInfo);
//            monthInfos.add(new DayInfo(date, (lastMonthCount - i + 1) + "", week, ""));
        }

        //添加这个月的
        for(int i=1; i <= monthCount; i++){
            String week = getWeek(j);
            j++;
            String date =year + "-" + month + "-";
            if( i < 10 )
                date += "0" + i;
            else
                date += i;
            monthInfos.add(new DayInfo(date, i + "", week, ""));
        }

        //最后添加进下个月到这个月的补位
        for(int i = j%7, k=1; i<7; i++,k++){
            String week = getWeek(j);
            j++;
            int monthValue = Integer.parseInt(month);
            String date = year + "-";
            if( monthValue < 10 )
                date += "0" + (monthValue+1) + "-";
            else
                date += (monthValue+1) + "-";

            if( k < 10 )
                date += "0" + k;
            else
                date += k;
            monthInfos.add(new DayInfo(date, "", week, ""));
//            monthInfos.add(new DayInfo(date, k + "", week, ""));
        }


        mMonthView.setData(monthInfos);


        mMonthView.setOnClickListener(new MonthView.OnClickListener() {
            @Override
            public void onClick(DayInfo dayInfo) {
                detailCalendar();
                if( !dayInfo.getDay().equals("") ) {
                    initWeekView(dayInfo);
                    String time = mToolbar.getDate();
                    time += dayInfo.getDay() + "日";
                    mTaskAdapter.setDatas(null);
                    mTaskAdapter.notifyDataSetChanged();
                    mOverdueAdapter.setDatas(null);
                    mOverdueAdapter.notifyDataSetChanged();
                    date = time;
                    presenter.getBacklogInfos(time, data -> {
                        setTodayData(date, data);
                    });
                }
            }
        });
    }

    private String getWeek(int dayOfWeek) {
        switch (dayOfWeek % 7){
            case 0:
                return "日";
            case 1:
                return "一";
            case 2:
                return "二";
            case 3:
                return "三";
            case 4:
                return "四";
            case 5:
                return "五";
            default:
                return "六";
        }
    }

    /**
     * 初始化周期视图
     */
    private void initWeekView(DayInfo dayInfo) {

        List<DayInfo> dayInfos = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        int dayOfWeek = 0;

        //一个月的天数
        int lastMonthCount = 0;
        int monthCount = 0;
        String year = "";
        String month = "";
        String day = "";

        int yearValue = 0;
        int dayValue = 0;
        int monthValue = 0;

        String time = "";
        try {
            if(  dayInfo == null )
                time = TimeUtil.getCurrentDateInString1();
            else
                time = dayInfo.getDate();

            StringTokenizer tokenizer = new StringTokenizer(time, "-");
            year = tokenizer.nextToken();
            month = tokenizer.nextToken();
            day = tokenizer.nextToken();


            yearValue = Integer.parseInt(year);
            monthValue = Integer.parseInt(month);
            dayValue = Integer.parseInt(day);

            time = year + "-" + (monthValue-1) + "-" + 1;
            String lastMonth = year + "-" + monthValue + "-" + 1;

            monthCount = DateUtil.getDaysOfMonth(sdf.parse(time));

            dayOfWeek = CalendarUtil.dayOfWeek(yearValue, monthValue, dayValue-1);

            //如果是星期日，则视为0
            if( dayOfWeek == 7 )
                dayOfWeek = 0;

            lastMonthCount = DateUtil.getDaysOfMonth(sdf.parse(lastMonth));


        } catch (ParseException e) {
            e.printStackTrace();
        }

        int j=0;
        int currentWeek = 0;
        int allWeek = 0;

        if(  dayInfo == null )
            time = TimeUtil.getCurrentDateInString1();
        else
            time = dayInfo.getDate();

        String currentMonthLast = year+"-"+monthValue+"-"+monthCount;
        try {
            currentWeek = CalendarUtil.getWeek(time);
            allWeek = CalendarUtil.getWeek(currentMonthLast);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if( currentWeek == 1 ){
            for(int i = dayOfWeek - dayValue; i>=0; i--){
                String week = getWeek(j);
                j++;
                String date = year + "-" + (monthValue - 1) + "-" + (lastMonthCount - i);
                dayInfos.add(new DayInfo(date, (lastMonthCount - i) + "", week, ""));
            }
            for(int i=1; j<7; i++){
                String week = getWeek(j);
                j++;
                String date = year + "-" + monthValue + "-" + i;
                dayInfos.add(new DayInfo(date, i + "", week, ""));
            }
        }

        else if( currentWeek == allWeek ){

            j = 0;

            //获取月尾的星期
            dayOfWeek = CalendarUtil.dayOfWeek(yearValue, monthValue, monthCount);

            for(int i = dayOfWeek; i>0; i--) {
                String week = getWeek(j++);
                String date = year + "-" + monthValue + "-" + ( monthCount - i + 1);
                dayInfos.add(new DayInfo(date, ( monthCount - i + 1) + "", week, ""));
                LogUtil.eLog("dayOfWeek:"+dayOfWeek+", monthCount:"+monthCount+", i:"+i+", date:"+date);
            }

            for(int k=1; j<7; k++, j++){
                String week = getWeek(j);
                String date = year + "-" + (monthValue + 1) + "-" + k;
                dayInfos.add(new DayInfo(date, k + "", week, ""));
            }

        }else{
            j=0;
            for(int i=0; i<dayOfWeek; i++){
                String week = getWeek(j);
                j++;
                String date = year + "-" + monthValue + "-" + (dayValue + i - dayOfWeek);
                dayInfos.add(new DayInfo(date, (dayValue + i - dayOfWeek) + "", week, ""));
            }

            for(int i=0; i<7-dayOfWeek; i++){
                String week = getWeek(j);
                j++;
                String date = year + "-" + monthValue + "-" + (dayValue + i);
                dayInfos.add(new DayInfo(date, (dayValue + i) + "", week, ""));
            }
        }

        mWeekItemView.setData(dayInfos);

        mWeekItemView.setTaskDay(timeList);

        if( dayInfo != null ) {
            mWeekItemView.setSelectItem(dayInfo);
        }

        mWeekItemView.setOnClickListener(new WeekItemView.OnClickListener() {
            @Override
            public void onClick(String day) {

                String time = mToolbar.getDate();
                time += day + "日";
                mTaskAdapter.setDatas(null);
                mTaskAdapter.notifyDataSetChanged();
                mOverdueAdapter.setDatas(null);
                mOverdueAdapter.notifyDataSetChanged();
                date = time;
                presenter.getBacklogInfos(time, data -> {
                    setTodayData(date, data);
                });
            }
        });
    }


    @Override
    public int getLayout() {
        return R.layout.activity_home_01;
    }

    /**
     * 设置备忘录数据
     */
    private void setListData() {

        mNeedAdapter = new BacklogAdapter(this, null);

        mNeedAdapter.setOnItemClickListener( (position) -> {
            Intent intent = new Intent(this, ScheduleDetailCloneActivity.class);
            BacklogInfo backlogInfo = (BacklogInfo)mNeedAdapter.getItem(position);
            intent.putExtra("backlogInfoId", backlogInfo.getBacklogInfoId());
            startActivity(intent);
        });

        mTaskAdapter = new BacklogAdapter(this, null);

        mTaskAdapter.setOnItemClickListener( (position) -> {
                Intent intent = new Intent(this, ScheduleDetailCloneActivity.class);
                BacklogInfo backlogInfo = (BacklogInfo)mTaskAdapter.getItem(position);
                intent.putExtra("backlogInfoId", backlogInfo.getBacklogInfoId());
                startActivity(intent);
        });

        mOverdueAdapter = new BacklogAdapter(this, null);

        mOverdueAdapter.setOnItemClickListener( (position) -> {
            Intent intent = new Intent(this, ScheduleDetailCloneActivity.class);
            BacklogInfo backlogInfo = (BacklogInfo) mOverdueAdapter.getItem(position);
            intent.putExtra("backlogInfoId", backlogInfo.getBacklogInfoId());
            startActivity(intent);
        });


        mNeedLv.setAdapter(mNeedAdapter);

        mTaskLv.setAdapter(mTaskAdapter);

        mOverdueLv.setAdapter(mOverdueAdapter);

        mTaskSv.smoothScrollTo(0, 0);

        String time = TimeUtil.getCurrentDateInString();
        mNeedAdapter.setDatas(null);
        mNeedAdapter.notifyDataSetChanged();
        mTaskAdapter.setDatas(null);
        mTaskAdapter.notifyDataSetChanged();
        mOverdueAdapter.setDatas(null);
        mOverdueAdapter.notifyDataSetChanged();
        date = time;
        presenter.getBacklogInfos(time, data -> setTodayData(date, data));
    }

    @Override
    public void setData(Object data) {

    }

    public void setTodayData(String date, Object data){
        try {
            List<BacklogInfo> backlogInfos = (List<BacklogInfo>) data;
//            mTodayCountTv.setText("("+backlogInfos.size()+")");

            if( backlogInfos.size() > 0 ){
                mTodayTv.setVisibility(View.VISIBLE);
                mTodayTv.setText("今日创建任务("+backlogInfos.size()+")");
            }else{
                mTodayTv.setVisibility(View.GONE);
            }

            mTaskAdapter.setDatas(backlogInfos);
            mTaskAdapter.notifyDataSetChanged();

            presenter.getBacklogInfoNeed(date, data1 -> setNeedData(data1));

        }catch (Exception e){

        }
    }

    @OnClick(R.id.iv_home_calendar)
    public void changeCalendar() {


        mCalendarIv.setEnabled(false);

        //属性动画对象
        ValueAnimator va ;
        va = ValueAnimator.ofInt(calendarHeight,0);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //获取当前的height值
                int h =(Integer)valueAnimator.getAnimatedValue();
                //动态更新view的高度
                mCalendarBgLl.getLayoutParams().height = h;
                mCalendarBgLl.requestLayout();
            }
        });
        va.setDuration(1000);
        //开始动画
        va.start();

        //属性动画对象
        ValueAnimator va1 ;
        va1 = ValueAnimator.ofInt(0, calendarHeight);
        va1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //获取当前的height值
                int h =(Integer)valueAnimator.getAnimatedValue();
                //动态更新view的高度
                mMonthView.getLayoutParams().height = h;
                mMonthView.requestLayout();
            }
        });
        va1.setDuration(1000);
        //开始动画
        va1.start();


    }

    private void detailCalendar() {

        mCalendarIv.setEnabled(true);

        ValueAnimator va ;
        va = ValueAnimator.ofInt(0, calendarHeight);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //获取当前的height值
                int h =(Integer)valueAnimator.getAnimatedValue();
                //动态更新view的高度
                mCalendarBgLl.getLayoutParams().height = h;
                mCalendarBgLl.requestLayout();
            }
        });
        va.setDuration(1000);
        //开始动画
        va.start();

        //属性动画对象
        ValueAnimator va1 ;
        va1 = ValueAnimator.ofInt(calendarHeight, 0);
        va1.addUpdateListener(valueAnimator -> {
                //获取当前的height值
                int h =(Integer)valueAnimator.getAnimatedValue();
                //动态更新view的高度
                mMonthView.getLayoutParams().height = h;
                mMonthView.requestLayout();
        });
        va1.setDuration(1000);
        //开始动画
        va1.start();

    }

    public void setOverdueData(Object overdueData) {
        try {
            List<BacklogInfo> backlogInfos = (List<BacklogInfo>) overdueData;
//            mTodayCountTv.setText("("+backlogInfos.size()+")");

            if( backlogInfos.size() > 0 ){
                mOverdueRl.setVisibility(View.VISIBLE);
                mOverdueTv.setText("已过期任务("+backlogInfos.size()+")");
            }else{
                mOverdueRl.setVisibility(View.GONE);
            }

            mOverdueAdapter.setDatas(backlogInfos);
            mOverdueAdapter.notifyDataSetChanged();

        }catch (Exception e){
            Toast.makeText(this, (String) overdueData, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 忽略过期任务
     */
    @OnClick(R.id.tv_home_task_skip)
    void skipOverdue(){

        if( mOverdueLv.getVisibility() == View.VISIBLE ){
            mOverdueLv.setVisibility(View.GONE);
            mOverdueSkipTv.setText("详情");
        }else{
            mOverdueLv.setVisibility(View.VISIBLE);
            mOverdueSkipTv.setText("忽略");
            mOverdueLv.smoothScrollToPosition(mOverdueAdapter.getCount() - 1);
        }
    }

    public void setNeedData(Object needData) {
        try {
            List<BacklogInfo> backlogInfos = (List<BacklogInfo>) needData;
//            mTodayCountTv.setText("("+backlogInfos.size()+")");

            if( backlogInfos.size() > 0 ){
                mNeedTv.setVisibility(View.VISIBLE);
                mNeedTv.setText("今日需要完成任务("+backlogInfos.size()+")");
            }else{
                mNeedTv.setVisibility(View.GONE);
            }

            mNeedAdapter.setDatas(backlogInfos);
            mNeedAdapter.notifyDataSetChanged();

            presenter.getBackLogInfoOverdue(data1 -> setOverdueData(data1));

        }catch (Exception e){

        }
    }
}
