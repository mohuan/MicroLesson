package com.skyworth.microlesson.model.prefs;

/**
 * @author: skyworth
 * @date: 2017/4/21
 * @description:
 */

public interface PreferencesHelper {

    boolean getNightModeState();

    void setNightModeState(boolean state);

    /**
     * Set login status
     *
     * @param isLogin IsLogin
     */
    void setLoginStatus(boolean isLogin);

    /**
     * Get login status
     *
     * @return login status
     */
    boolean getLoginStatus();

    //ticket
    void setTicket(String ticket);


    //第一次登录
    void setIsFrist(boolean isFrist);

    boolean getIsFrist();

    //登录信息
    void setUserInfo(String userInfo);

    String getUserInfo();

    void clearLoginInfo();
}
