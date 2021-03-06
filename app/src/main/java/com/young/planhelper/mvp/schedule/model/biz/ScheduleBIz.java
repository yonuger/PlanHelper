package com.young.planhelper.mvp.schedule.model.biz;

import android.content.Context;
import android.util.Log;
import android.util.TimeUtils;

import com.young.planhelper.application.AppApplication;
import com.young.planhelper.mvp.base.model.Biz;
import com.young.planhelper.mvp.base.model.IBiz;
import com.young.planhelper.mvp.common.model.NotificationInfo;
import com.young.planhelper.mvp.schedule.model.bean.BacklogInfo;
import com.young.planhelper.util.LogUtil;
import com.young.planhelper.util.TimeUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

import static com.young.planhelper.constant.AppConstant.ADD_FAILED;
import static com.young.planhelper.constant.AppConstant.ADD_SUCCESS;
import static com.young.planhelper.constant.AppConstant.REALM_NOT_INIT;
import static com.young.planhelper.mvp.schedule.model.bean.BacklogInfo.FINISHED;

/**
 * @author: young
 * email:1160415122@qq.com
 * date:16/11/16  20:51
 */


public class ScheduleBIz extends Biz implements IScheduleBiz {

    private Realm mRealm;

    public ScheduleBIz(Context context){
        this.mRealm = AppApplication.get(context).getmAppComponent().getRealm();
    }

    @Override
    public void getData(ICallback callback) {

    }

    @Override
    public void addBacklogInfo(BacklogInfo backlogInfo, final ICallback callback) {

        if( mRealm == null ){
            LogUtil.eLog(REALM_NOT_INIT);
            callback.onResult(ADD_FAILED);
            return;
        }

        mRealm.beginTransaction();
        BacklogInfo object = mRealm.copyToRealm(backlogInfo);

        NotificationInfo notificationInfo = new NotificationInfo();
        notificationInfo.setNotificationInfoId(TimeUtil.getCurrentTimeInLong());
        notificationInfo.setTime(backlogInfo.getToTime());

        NotificationInfo ob = mRealm.copyToRealm(notificationInfo);

        mRealm.commitTransaction();

        callback.onResult(ADD_SUCCESS);
    }

    @Override
    public void getBacklogInfo(ICallback callback) {
        if( mRealm == null ){
            LogUtil.eLog(REALM_NOT_INIT);
            callback.onResult(ADD_FAILED);
            return;
        }

        List<BacklogInfo> backlogInfos = mRealm.where(BacklogInfo.class).findAll();

        callback.onResult(backlogInfos);
    }

    @Override
    public void getBackLogInfoByday(String date, ICallback callback) {
        if( mRealm == null ){
            LogUtil.eLog(REALM_NOT_INIT);
            callback.onResult(ADD_FAILED);
            return;
        }

        DateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        Date date1 = new Date();
        Date date2 = new Date();

        try {
            date1 = df.parse(date + " 00:00");
            date2 = df.parse(date + " 23:59");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        long timestamp1 = cal.getTimeInMillis();

        cal.setTime(date2);
        long timestamp2 = cal.getTimeInMillis();

        List<BacklogInfo> result = new ArrayList<>();

        List<BacklogInfo> backlogInfos = mRealm.where(BacklogInfo.class)
                .between("fromTime", timestamp1, timestamp2)
                .notEqualTo("statue", BacklogInfo.OVERDUE).findAll();

        callback.onResult(backlogInfos);
    }


    @Override
    public void getBackLogInfoFuture(ICallback callback) {
        if( mRealm == null ){
            LogUtil.eLog(REALM_NOT_INIT);
            callback.onResult(ADD_FAILED);
            return;
        }

        List<BacklogInfo> backlogInfos = mRealm.where(BacklogInfo.class).greaterThan("toTime", TimeUtil.getTodayEndTime()).findAll();

        callback.onResult(backlogInfos);
    }

    @Override
    public void getBackLogInfoOverdue(ICallback callback) {
        if( mRealm == null ){
            LogUtil.eLog(REALM_NOT_INIT);
            callback.onResult(ADD_FAILED);
            return;
        }

        List<BacklogInfo> backlogInfos = mRealm.where(BacklogInfo.class)
                .equalTo("statue", BacklogInfo.OVERDUE)
                .lessThan("toTime", TimeUtil.getCurrentTimeInLong()).findAll();

        callback.onResult(backlogInfos);
    }

    @Override
    public void getBackLogInfoDetail(long backlogInfoId, ICallback callback) {
        if( mRealm == null ){
            LogUtil.eLog(REALM_NOT_INIT);
            callback.onResult(ADD_FAILED);
            return;
        }

        BacklogInfo backlogInfo = mRealm.where(BacklogInfo.class).equalTo("backlogInfoId", backlogInfoId).findFirst();

        callback.onResult(backlogInfo);
    }

    @Override
    public void queryByMonth(String monthBegin, String monthEnd, ICallback callback) {
        if( mRealm == null ){
            LogUtil.eLog(REALM_NOT_INIT);
            callback.onResult(ADD_FAILED);
            return;
        }

        long begin = 0;
        long end = 0;
        try {
            begin = TimeUtil.dateToStamp(monthBegin);
            end = TimeUtil.dateToStamp(monthEnd);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<BacklogInfo> backlogInfoList = mRealm.where(BacklogInfo.class)
                .greaterThan("fromTime", begin)
                .lessThan("toTime", end)
                .notEqualTo("statue", FINISHED)
                .findAll();

        List<String> dateList = new ArrayList<>();
        if( backlogInfoList.size() == 0)
            callback.onResult(null);
        else{
            dateList.add(TimeUtil.getTime6(backlogInfoList.get(0).getFromTime()));
            for(int i=1, j=0; i<backlogInfoList.size(); i++){
                if( !dateList.get(j).equals( TimeUtil.getTime6(backlogInfoList.get(i).getFromTime()) ) ){
                    dateList.add(TimeUtil.getTime6(backlogInfoList.get(i).getFromTime()));
                    j++;
                }
            }

            for(int i=0, j=0; i<backlogInfoList.size(); i++){
                if( !dateList.get(j).equals( TimeUtil.getTime6(backlogInfoList.get(i).getToTime()) ) ){
                    dateList.add(TimeUtil.getTime6(backlogInfoList.get(i).getToTime()));
                    j++;
                }
            }

            callback.onResult(dateList);
        }

    }

    @Override
    public void modifyBacklogInfo(BacklogInfo mBacklogInfo, BacklogInfo backlogInfo, ICallback callback) {
        if( mRealm == null ){
            LogUtil.eLog("Realm没有初始化");
            callback.onResult("获取失败");
            return;
        }

        mRealm.beginTransaction();

        mBacklogInfo.setContent(backlogInfo.getContent());
        mBacklogInfo.setFromTime(backlogInfo.getFromTime());
        mBacklogInfo.setToTime(backlogInfo.getToTime());
        mBacklogInfo.setRepeatType(backlogInfo.getRepeatType());

        mRealm.commitTransaction();

        callback.onResult("");
    }

    @Override
    public void deleteBacklog(BacklogInfo mBacklogInfo, ICallback callback) {
        if( mRealm == null ){
            LogUtil.eLog("Realm没有初始化");
            callback.onResult("获取失败");
            return;
        }

        mRealm.beginTransaction();

        BacklogInfo backlogInfo = mRealm.where(BacklogInfo.class)
                                    .equalTo("backlogInfoId", mBacklogInfo.getBacklogInfoId())
                                    .findFirst();
        backlogInfo.deleteFromRealm();

        mRealm.commitTransaction();

        callback.onResult("");
    }

    @Override
    public void finishBacklogInfo(BacklogInfo mBacklogInfo, ICallback callback) {
        if( mRealm == null ){
            LogUtil.eLog("Realm没有初始化");
            callback.onResult("获取失败");
            return;
        }

        mRealm.beginTransaction();

        mBacklogInfo.setStatue(FINISHED);

        mRealm.commitTransaction();

        callback.onResult("");

        callback.onResult("");
    }

    @Override
    public void initBacklogInfo(ICallback callback) {

        if( mRealm == null ){
            LogUtil.eLog("Realm没有初始化");
            callback.onResult("获取失败");
            return;
        }


        List<BacklogInfo> backlogInfoList = mRealm.where(BacklogInfo.class)
                .lessThan("toTime", TimeUtil.getCurrentTimeInLong())
                .equalTo("statue", BacklogInfo.UNFINISH)
                .findAll();


        mRealm.beginTransaction();

        int len = backlogInfoList.size();

        for (int i = 0; i < len; i++) {
            backlogInfoList.get(i).setStatue(BacklogInfo.OVERDUE);
        }

        mRealm.commitTransaction();

        callback.onResult("");

    }

    @Override
    public void getBacklogInfoNeed(String date, ICallback callback) {
        if( mRealm == null ){
            LogUtil.eLog(REALM_NOT_INIT);
            callback.onResult(ADD_FAILED);
            return;
        }

        DateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        Date date1 = new Date();
        Date date2 = new Date();

        try {
            date1 = df.parse(date + " 00:00");
            date2 = df.parse(date + " 23:59");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        long timestamp1 = cal.getTimeInMillis();

        cal.setTime(date2);
        long timestamp2 = cal.getTimeInMillis();

        List<BacklogInfo> result = new ArrayList<>();

        List<BacklogInfo> backlogInfos = mRealm.where(BacklogInfo.class)
                .between("toTime", timestamp1, timestamp2)
                .equalTo("statue", BacklogInfo.UNFINISH).findAll();

        callback.onResult(backlogInfos);
    }

}
