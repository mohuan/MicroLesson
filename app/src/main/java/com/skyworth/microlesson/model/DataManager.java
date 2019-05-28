package com.skyworth.microlesson.model;



import com.skyworth.microlesson.model.http.HttpHelper;
import com.skyworth.microlesson.model.prefs.PreferencesHelper;

import java.util.List;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 作者：skyworth on 2017/7/11 09:55
 * 邮箱：dqwei@iflytek.com
 */

public class DataManager implements HttpHelper, PreferencesHelper {

    HttpHelper mHttpHelper;

    PreferencesHelper mPreferencesHelper;

    public DataManager(HttpHelper httpHelper, PreferencesHelper mPreferencesHelper) {
        mHttpHelper = httpHelper;
        this.mPreferencesHelper = mPreferencesHelper;
    }
    ////////////////////////////////////////HttpHelper//////////////////////////////////////////////


    ////////////////////////////////////////PreferencesHelper///////////////////////////////////////

    @Override
    public void setLoginStatus(boolean isLogin) {
        mPreferencesHelper.setLoginStatus(isLogin);
    }

    @Override
    public boolean getLoginStatus() {
        return mPreferencesHelper.getLoginStatus();
    }


    @Override
    public boolean getNightModeState() {
        return false;
    }

    @Override
    public void setNightModeState(boolean state) {

    }

    @Override
    public void setIsFrist(boolean isFrist) {
        mPreferencesHelper.setIsFrist(isFrist);
    }

    @Override
    public boolean getIsFrist() {
        return mPreferencesHelper.getIsFrist();
    }

    @Override
    public void setTicket(String ticket) {
        mPreferencesHelper.setTicket(ticket);
    }

    @Override
    public void setUserInfo(String userInfo) {
        mPreferencesHelper.setUserInfo(userInfo);
    }

    @Override
    public String getUserInfo() {
        return mPreferencesHelper.getUserInfo();
    }

    @Override
    public void clearLoginInfo() {
        mPreferencesHelper.clearLoginInfo();
    }

}
